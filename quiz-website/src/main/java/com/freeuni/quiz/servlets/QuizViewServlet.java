package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/quiz-view")
public class QuizViewServlet extends HttpServlet {

    private QuizService quizService;
    private QuizSessionService sessionService;
    private QuizCompletionRepositoryImpl quizCompletionRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        this.quizService = new QuizService(dataSource);
        this.sessionService = new QuizSessionService(dataSource);
        this.quizCompletionRepository = new QuizCompletionRepositoryImpl(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleViewQuiz(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("start".equals(action)) {
            handleStartQuiz(request, response);
        } else {
            response.sendRedirect("quiz-browser");
        }
    }

    private void handleViewQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-browser");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            int totalQuestions = quizService.getQuizQuestionCount(quizId);
            
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            boolean hasCompleted = false;
            QuizCompletion bestCompletion = null;
            
            if (currentUser != null) {
                Optional<QuizCompletion> fastestTime = quizCompletionRepository.findFastestTime((long) currentUser.getId(), quizId);
                if (fastestTime.isPresent()) {
                    hasCompleted = true;
                    bestCompletion = fastestTime.get();
                }
            }
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("hasCompleted", hasCompleted);
            request.setAttribute("bestCompletion", bestCompletion);
            
            request.getRequestDispatcher("/WEB-INF/quiz-view.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz: " + e.getMessage());
        }
    }

    private void handleStartQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
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
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            Long participantId = (long) currentUser.getId();
            if (sessionService.hasActiveSession(participantId)) {
                handleError(request, response, "You already have an active quiz session");
                return;
            }
            
            boolean sessionStarted = sessionService.startQuizSession(participantId, quizId);
            
            if (!sessionStarted) {
                handleError(request, response, "Failed to start quiz session");
                return;
            }
            
            response.sendRedirect("quiz-session?quizId=" + quizId);
            
        } catch (Exception e) {
            handleError(request, response, "Error starting quiz: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
} 