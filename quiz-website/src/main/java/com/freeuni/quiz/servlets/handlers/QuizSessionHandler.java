package com.freeuni.quiz.servlets.handlers;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class QuizSessionHandler {

    private final QuizService quizService;
    private final CategoryService categoryService;

    public QuizSessionHandler(QuizService quizService, CategoryService categoryService) {
        this.quizService = quizService;
        this.categoryService = categoryService;
    }

    public void handleQuizSession(HttpServletRequest request, HttpServletResponse response, 
                                 UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
        if (quizOpt.isEmpty()) {
            request.setAttribute("errorMessage", "Quiz not found");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        Quiz quiz = quizOpt.get();
        
        List<Question> questions = quizService.getQuizQuestions(quizId);
        if (questions.isEmpty()) {
            request.setAttribute("errorMessage", "This quiz has no questions");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        HttpSession session = request.getSession();
        Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");
        
        if (currentQuestionIndex == null) {
            currentQuestionIndex = 0;
            Long startTime = System.currentTimeMillis();
            session.setAttribute("quizStartTime", startTime);
            session.setAttribute("currentQuestionIndex", currentQuestionIndex);
            System.out.println("DEBUG: Quiz started at " + startTime + " for user " + currentUser.getId());
        }
        
        setupQuizSessionAttributes(request, quiz, questions, currentQuestionIndex);
        setupTimeAttributes(request, quiz, session);
        setupAnswerAttributes(request, currentQuestionIndex);
        
        request.getRequestDispatcher("/WEB-INF/quiz-session.jsp").forward(request, response);
    }

    private void setupQuizSessionAttributes(HttpServletRequest request, Quiz quiz, 
                                          List<Question> questions, Integer currentQuestionIndex) {
        request.setAttribute("quiz", quiz);
        request.setAttribute("questions", questions);
        request.setAttribute("totalQuestions", questions.size());
        request.setAttribute("currentQuestionIndex", currentQuestionIndex);
        request.setAttribute("currentQuestion", questions.get(currentQuestionIndex));
        request.setAttribute("reviewMode", false);
        
        if (categoryService != null) {
            try {
                List<Category> categories = categoryService.getAllActiveCategories();
                request.setAttribute("categories", categories);
            } catch (Exception ignored) {
            }
        }
    }

    private void setupTimeAttributes(HttpServletRequest request, Quiz quiz, HttpSession session) {
        if (quiz.getTimeLimitMinutes() != null && quiz.getTimeLimitMinutes() > 0) {
            Long quizStartTime = (Long) session.getAttribute("quizStartTime");
            if (quizStartTime != null) {
                long elapsedSeconds = (System.currentTimeMillis() - quizStartTime) / 1000;
                long totalTimeSeconds = quiz.getTimeLimitMinutes() * 60;
                long remainingSeconds = Math.max(0, totalTimeSeconds - elapsedSeconds);
                request.setAttribute("remainingTimeSeconds", remainingSeconds);
                request.setAttribute("quizStartTime", quizStartTime);
            } else {
                long remainingSeconds = quiz.getTimeLimitMinutes() * 60;
                request.setAttribute("remainingTimeSeconds", remainingSeconds);
                request.setAttribute("quizStartTime", System.currentTimeMillis());
            }
        }
    }

    private void setupAnswerAttributes(HttpServletRequest request, Integer currentQuestionIndex) {
        HttpSession session = request.getSession();
        
        String questionTimeKey = "questionStartTime_" + currentQuestionIndex;
        if (session.getAttribute(questionTimeKey) == null) {
            session.setAttribute(questionTimeKey, System.currentTimeMillis());
        }
        
        Map<Integer, String> answers = getSessionAnswers(request);
        String currentAnswer = answers.get(currentQuestionIndex);
        request.setAttribute("currentAnswer", currentAnswer);
    }

    public void handleNavigation(HttpServletRequest request, HttpServletResponse response, 
                                UserDTO currentUser, Long quizId, String action) 
            throws ServletException, IOException {
        
        String answer = request.getParameter("answer");
        if (answer != null && !answer.trim().isEmpty()) {
            saveCurrentAnswerToSession(request);
        }
        
        HttpSession session = request.getSession();
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        if (currentIndex == null) currentIndex = 0;
        
        if ("next".equals(action)) {
            currentIndex++;
        } else if ("previous".equals(action) && currentIndex > 0) {
            currentIndex--;
        }
        
        session.setAttribute("currentQuestionIndex", currentIndex);
        
        handleQuizSession(request, response, currentUser, quizId);
    }

    public void handleReviewMode(HttpServletRequest request, HttpServletResponse response, 
                                UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        request.setAttribute("reviewMode", true);
        
        Map<Integer, String> answers = getSessionAnswers(request);
        request.setAttribute("answeredQuestions", answers.size());
        
        handleQuizSession(request, response, currentUser, quizId);
    }

    private void saveCurrentAnswerToSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String answer = request.getParameter("answer");
        Integer questionIndex = (Integer) session.getAttribute("currentQuestionIndex");
        
        if (answer != null && questionIndex != null) {
            Map<Integer, String> answers = getSessionAnswers(request);
            answers.put(questionIndex, answer);
            session.setAttribute("quizAnswers", answers);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> getSessionAnswers(HttpServletRequest request) {
        Map<Integer, String> answers = (Map<Integer, String>) request.getSession().getAttribute("quizAnswers");
        if (answers == null) {
            answers = new HashMap<>();
            request.getSession().setAttribute("quizAnswers", answers);
        }
        return answers;
    }
} 