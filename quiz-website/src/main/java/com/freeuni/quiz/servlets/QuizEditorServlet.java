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

@WebServlet("/quiz-editor")
public class QuizEditorServlet extends HttpServlet {

    private QuizService quizService;
    private CategoryService categoryService;
    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        super.init();
        this.quizService = (QuizService) getServletContext().getAttribute("quizService");
        this.categoryService = (CategoryService) getServletContext().getAttribute("categoryService");
        this.sessionManager = new SessionManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("preview".equals(action)) {
            handlePreviewQuiz(request, response);
        } else {
            handleEditForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleUpdateQuiz(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response) 
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
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to edit this quiz");
                return;
            }
            
            List<Category> categories = categoryService.getAllActiveCategories();
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("categories", categories);
            request.setAttribute("questions", questions);
            request.setAttribute("mode", "edit");
            
            request.getRequestDispatcher("/WEB-INF/quiz-editor.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz for editing: " + e.getMessage());
        }
    }

    private void handleUpdateQuiz(HttpServletRequest request, HttpServletResponse response) 
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
                handleError(request, response, "You don't have permission to update this quiz");
                return;
            }
            
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            QuizUpdateData updateData = extractUpdateData(request);
            
            if (!isValidUpdateData(updateData)) {
                request.setAttribute("errorMessage", updateData.getValidationError());
                handleEditForm(request, response);
                return;
            }
            
            updateQuizFromData(quiz, updateData);
            
            boolean updated = quizService.updateQuiz(quiz);
            
            if (updated) {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Quiz updated successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to update quiz");
                handleEditForm(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating quiz: " + e.getMessage());
            handleEditForm(request, response);
        }
    }

    private void handlePreviewQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }

            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            Long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to preview this quiz");
                return;
            }
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);
            request.setAttribute("isPreview", true);
            
            request.getRequestDispatcher("/WEB-INF/quiz-preview.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz preview: " + e.getMessage());
        }
    }

    private QuizUpdateData extractUpdateData(HttpServletRequest request) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String timeLimitStr = request.getParameter("timeLimit");
        
        QuizUpdateData updateData = new QuizUpdateData();
        updateData.setTitle(title);
        updateData.setDescription(description);
        updateData.setCategoryIdStr(categoryIdStr);
        updateData.setTimeLimitStr(timeLimitStr);
        
        return updateData;
    }

    private boolean isValidUpdateData(QuizUpdateData updateData) {
        if (updateData.getTitle() == null || updateData.getTitle().trim().isEmpty()) {
            updateData.setValidationError("Quiz title is required");
            return false;
        }
        
        if (updateData.getDescription() == null || updateData.getDescription().trim().isEmpty()) {
            updateData.setValidationError("Quiz description is required");
            return false;
        }
        
        return true;
    }

    private void updateQuizFromData(Quiz quiz, QuizUpdateData updateData) {
        quiz.setTestTitle(updateData.getTitle());
        quiz.setTestDescription(updateData.getDescription());
        
        if (updateData.getCategoryIdStr() != null && !updateData.getCategoryIdStr().isEmpty()) {
            quiz.setCategoryId(Long.parseLong(updateData.getCategoryIdStr()));
        }
        
        if (updateData.getTimeLimitStr() != null && !updateData.getTimeLimitStr().isEmpty()) {
            try {
                quiz.setTimeLimitMinutes(Long.parseLong(updateData.getTimeLimitStr()));
            } catch (NumberFormatException e) {
                // Keep existing value
            }
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }

    // Inner class for update data
    private static class QuizUpdateData {
        private String title;
        private String description;
        private String categoryIdStr;
        private String timeLimitStr;
        private String validationError;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategoryIdStr() { return categoryIdStr; }
        public void setCategoryIdStr(String categoryIdStr) { this.categoryIdStr = categoryIdStr; }
        
        public String getTimeLimitStr() { return timeLimitStr; }
        public void setTimeLimitStr(String timeLimitStr) { this.timeLimitStr = timeLimitStr; }
        
        public String getValidationError() { return validationError; }
        public void setValidationError(String validationError) { this.validationError = validationError; }
    }
} 