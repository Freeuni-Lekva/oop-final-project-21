package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.util.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/quiz-view")
public class QuizViewServlet extends HttpServlet {

    private QuizService quizService;
    private QuizSessionService sessionService;
    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        super.init();
        this.quizService = (QuizService) getServletContext().getAttribute("quizService");
        this.sessionService = (QuizSessionService) getServletContext().getAttribute("sessionService");
        this.sessionManager = new SessionManager();
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
            
            User currentUser = sessionManager.getCurrentUser(request);
            boolean hasAttempted = false;
            if (currentUser != null) {
                hasAttempted = sessionService.hasActiveSession((long) currentUser.getId());
            }
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("hasAttempted", hasAttempted);
            
            request.getRequestDispatcher("/WEB-INF/quiz-view.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz: " + e.getMessage());
        }
    }

    private void handleStartQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            User currentUser = sessionManager.getCurrentUser(request);
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