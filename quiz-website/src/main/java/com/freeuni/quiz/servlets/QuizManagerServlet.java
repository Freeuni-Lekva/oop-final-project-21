package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/quiz-manager")
public class QuizManagerServlet extends BaseServlet {

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

        if (action.equals("delete")) {
            handleDeleteQuiz(request, response);
        } else {
            response.sendRedirect("quiz-manager");
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            List<Quiz> createdQuizzes = quizService.getQuizzesByCreator((long) currentUser.getId(), 0, 20);
            
            Map<Integer, Integer> completionCounts = quizService.getCompletionCountsForQuizzes(createdQuizzes);
            Map<Integer, Double> averageScores = quizService.getAverageScoresForQuizzes(createdQuizzes);
            
            Long participantId = (long) currentUser.getId();
            boolean hasActiveSession = sessionService.hasActiveSession(participantId);
            
            request.setAttribute("createdQuizzes", createdQuizzes);
            request.setAttribute("completionCounts", completionCounts);
            request.setAttribute("averageScores", averageScores);
            request.setAttribute("hasActiveSession", hasActiveSession);
            
            request.getRequestDispatcher("/WEB-INF/quiz-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading dashboard: " + e.getMessage());
        }
    }

    private void handleDeleteQuiz(HttpServletRequest request, HttpServletResponse response) 
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
} 