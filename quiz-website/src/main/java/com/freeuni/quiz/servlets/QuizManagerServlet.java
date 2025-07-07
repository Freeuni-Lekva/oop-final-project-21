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
import java.util.List;

@WebServlet("/quiz-manager")
public class QuizManagerServlet extends HttpServlet {

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
        
        String action = request.getParameter("action");
        if (action == null) action = "dashboard";
        
        if ("dashboard".equals(action)) {
            handleDashboard(request, response);
        } else {
            response.sendRedirect("quiz-manager");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("quiz-manager");
            return;
        }
        
        switch (action) {
            case "delete":
                handleDeleteQuiz(request, response);
                break;
            default:
                response.sendRedirect("quiz-manager");
                break;
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            List<Quiz> createdQuizzes = quizService.getQuizzesByCreator((long) currentUser.getId(), 0, 20);
            
            Long participantId = (long) currentUser.getId();
            boolean hasActiveSession = sessionService.hasActiveSession(participantId);
            
            request.setAttribute("createdQuizzes", createdQuizzes);
            request.setAttribute("hasActiveSession", hasActiveSession);
            
            request.getRequestDispatcher("/WEB-INF/quiz-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading dashboard: " + e.getMessage());
        }
    }

    private void handleDeleteQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to delete this quiz");
                return;
            }
            
            boolean deleted = quizService.deleteQuiz(quizId);
            
            if (deleted) {
                response.sendRedirect("quiz-manager?action=dashboard&message=Quiz deleted successfully");
            } else {
                response.sendRedirect("quiz-manager?action=dashboard&error=Failed to delete quiz");
            }
            
        } catch (Exception e) {
            handleError(request, response, "Error deleting quiz: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
} 