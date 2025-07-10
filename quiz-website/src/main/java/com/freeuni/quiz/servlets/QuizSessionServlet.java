package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.repository.impl.ParticipantAnswerRepositoryImpl;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@WebServlet("/quiz-session")
public class QuizSessionServlet extends HttpServlet {

    private QuizService quizService;
    private CategoryService categoryService;
    private ParticipantAnswerRepositoryImpl participantAnswerRepository;
    private QuizCompletionRepositoryImpl quizCompletionRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) getServletContext().getAttribute("dataSource");
        this.quizService = new QuizService(dataSource);
        this.categoryService = new CategoryService(dataSource);
        this.participantAnswerRepository = new ParticipantAnswerRepositoryImpl(dataSource);
        this.quizCompletionRepository = new QuizCompletionRepositoryImpl(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleQuizSession(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "answer";
        
        switch (action) {
            case "answer":
                handleAnswerSubmission(request, response);
                break;
            case "next":
                handleNextQuestion(request, response);
                break;
            case "previous":
                handlePreviousQuestion(request, response);
                break;
            case "review":
                handleReviewMode(request, response);
                break;
            case "submit":
            case "finish":
                handleQuizSubmission(request, response);
                break;
            default:
                response.sendRedirect("quiz-browser");
                break;
        }
    }

    private void handleQuizSession(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-browser");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);
            
            Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
            if (quizOpt.isEmpty()) {
                handleError(request, response, "Quiz not found");
                return;
            }
            Quiz quiz = quizOpt.get();
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            
            if (questions.isEmpty()) {
                handleError(request, response, "This quiz has no questions");
                return;
            }
            
            Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");
            if (currentQuestionIndex == null) {
                currentQuestionIndex = 0;
                Long startTime = System.currentTimeMillis();
                session.setAttribute("quizStartTime", startTime);
                System.out.println("DEBUG: Quiz started at " + startTime + " for user " + currentUser.getId());
            }
            
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
            
            String questionTimeKey = "questionStartTime_" + currentQuestionIndex;
            if (session.getAttribute(questionTimeKey) == null) {
                session.setAttribute(questionTimeKey, System.currentTimeMillis());
            }
            
            Map<Integer, String> answers = getSessionAnswers(request);
            String currentAnswer = answers.get(currentQuestionIndex);
            request.setAttribute("currentAnswer", currentAnswer);
            
            request.getRequestDispatcher("/WEB-INF/quiz-session.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz session: " + e.getMessage());
        }
    }

    private void handleAnswerSubmission(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String answer = request.getParameter("answer");
            if (answer != null && !answer.trim().isEmpty()) {
                saveCurrentAnswer(request);
            }
            
            String nextAction = request.getParameter("nextAction");
            if ("next".equals(nextAction)) {
                handleNextQuestion(request, response);
            } else if ("review".equals(nextAction)) {
                handleReviewMode(request, response);
            } else {
                handleQuizSession(request, response);
            }
            
        } catch (Exception e) {
            handleError(request, response, "Error saving answer: " + e.getMessage());
        }
    }

    private void handleNextQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String answer = request.getParameter("answer");
            if (answer != null && !answer.trim().isEmpty()) {
                saveCurrentAnswer(request);
            }
            
            Integer currentIndex = (Integer) request.getSession().getAttribute("currentQuestionIndex");
            if (currentIndex == null) currentIndex = 0;
            
            currentIndex++;
            request.getSession().setAttribute("currentQuestionIndex", currentIndex);
            
            handleQuizSession(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error moving to next question: " + e.getMessage());
        }
    }

    private void handlePreviousQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String answer = request.getParameter("answer");
            if (answer != null && !answer.trim().isEmpty()) {
                saveCurrentAnswer(request);
            }
            
            Integer currentIndex = (Integer) request.getSession().getAttribute("currentQuestionIndex");
            if (currentIndex == null) currentIndex = 0;
            
            if (currentIndex > 0) {
                currentIndex--;
                request.getSession().setAttribute("currentQuestionIndex", currentIndex);
            }
            
            handleQuizSession(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error moving to previous question: " + e.getMessage());
        }
    }

    private void saveCurrentAnswer(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("user");
            if (currentUser == null) return;
            
            String answer = request.getParameter("answer");
            String quizIdStr = request.getParameter("quizId");
            
            if (answer == null || answer.trim().isEmpty() || quizIdStr == null) return;
            
            Long quizId = Long.parseLong(quizIdStr);
            Integer questionIndex = (Integer) session.getAttribute("currentQuestionIndex");
            if (questionIndex == null) questionIndex = 0;
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            if (questionIndex >= questions.size()) return;
            
            Question currentQuestion = questions.get(questionIndex);
            
            double pointsEarned = 0.0;
            if (currentQuestion.getQuestionHandler() != null) {
                try {
                    java.util.Map<String, String> userInputs = new java.util.HashMap<>();
                    if (currentQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                        try {
                            int choiceIndex = Integer.parseInt(answer);
                            java.util.Map<String, Object> viewData = currentQuestion.getQuestionHandler().getViewData();
                            @SuppressWarnings("unchecked")
                            java.util.List<String> options = (java.util.List<String>) viewData.get("choiceOptions");
                            if (options != null && choiceIndex >= 0 && choiceIndex < options.size()) {
                                userInputs.put("choiceSelection", options.get(choiceIndex));
                            }
                        } catch (NumberFormatException e) {
                            userInputs.put("choiceSelection", answer);
                        }
                                         } else {
                         userInputs.put("textInput", answer);
                     }
                    
                    double score = currentQuestion.getQuestionHandler().evaluateUserResponse(userInputs);
                    pointsEarned = score * currentQuestion.getPoints();
                } catch (Exception e) {
                    System.err.println("Error evaluating answer: " + e.getMessage());
                    pointsEarned = 0.0;
                }
            }
            
            Long questionStartTime = (Long) session.getAttribute("questionStartTime_" + questionIndex);
            int timeSpentSeconds = 0;
            if (questionStartTime != null) {
                timeSpentSeconds = (int) ((System.currentTimeMillis() - questionStartTime) / 1000);
            }
            
            Map<Integer, String> answers = getSessionAnswers(request);
            answers.put(questionIndex, answer);
            session.setAttribute("quizAnswers", answers);
            
            ParticipantAnswer participantAnswer = new ParticipantAnswer(
                (long) currentUser.getId(),
                quizId,
                (long) (questionIndex + 1),
                pointsEarned,
                timeSpentSeconds,
                answer
            );
            
            boolean saved = participantAnswerRepository.saveAnswer(participantAnswer);
            if (!saved) {
                System.err.println("Failed to save answer to database for user " + currentUser.getId() + 
                                 ", quiz " + quizId + ", question " + (questionIndex + 1));
            }
            
        } catch (Exception e) {
            System.err.println("Error in saveCurrentAnswer: " + e.getMessage());
        }
    }

    private void handleReviewMode(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("reviewMode", true);
        
        Map<Integer, String> answers = getSessionAnswers(request);
        request.setAttribute("answeredQuestions", answers.size());
        
        handleQuizSession(request, response);
    }

    private void handleQuizSubmission(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                handleError(request, response, "Quiz ID is required");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);
            
            String currentAnswer = request.getParameter("answer");
            if (currentAnswer != null && !currentAnswer.trim().isEmpty()) {
                saveCurrentAnswer(request);
            }
            
            Long quizStartTime = (Long) session.getAttribute("quizStartTime");
            LocalDateTime startedAt;
            int totalTimeSeconds;
            
            if (quizStartTime != null) {
                long secondsAgo = (System.currentTimeMillis() - quizStartTime) / 1000;
                startedAt = LocalDateTime.now().minusSeconds(secondsAgo);
                totalTimeSeconds = (int) secondsAgo;
                System.out.println("DEBUG: Quiz completion - Start time: " + quizStartTime + ", Current time: " + System.currentTimeMillis() + ", Total seconds: " + totalTimeSeconds + " for user " + currentUser.getId());
            } else {
                System.out.println("DEBUG: WARNING - No quiz start time found for user " + currentUser.getId() + ", using fallback");
                startedAt = LocalDateTime.now().minusMinutes(10);
                totalTimeSeconds = 600;
            }
            
            List<ParticipantAnswer> allAnswers = participantAnswerRepository.getAllAnswers((long) currentUser.getId(), quizId);
            
            double totalScore = 0.0;
            double maxPossibleScore = 0.0;
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            for (Question question : questions) {
                maxPossibleScore += question.getPoints();
            }
            
            for (ParticipantAnswer answer : allAnswers) {
                totalScore += answer.getPointsEarned();
            }
            
            QuizCompletion completion = new QuizCompletion(
                (long) currentUser.getId(),
                quizId,
                totalScore,
                maxPossibleScore,
                startedAt,
                totalTimeSeconds
            );
            
            completion.setFinishedAt(LocalDateTime.now());
            
            Long completionId = quizCompletionRepository.saveCompletion(completion);
            
            if (completionId != null) {
                session.removeAttribute("quizAnswers");
                session.removeAttribute("currentQuestionIndex");
                session.removeAttribute("quizStartTime");
                
                for (int i = 0; i < questions.size(); i++) {
                    session.removeAttribute("questionStartTime_" + i);
                }
                
                response.sendRedirect("quiz-view?quizId=" + quizId + "&completed=true&score=" + totalScore + "&maxScore=" + maxPossibleScore);
            } else {
                handleError(request, response, "Failed to save quiz completion. Please try again.");
            }
            
        } catch (Exception e) {
            handleError(request, response, "Error submitting quiz: " + e.getMessage());
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

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
} 