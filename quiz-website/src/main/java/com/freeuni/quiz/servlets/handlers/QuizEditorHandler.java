package com.freeuni.quiz.servlets.handlers;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.validators.QuizFormValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class QuizEditorHandler {

    private final QuizService quizService;
    private final CategoryService categoryService;

    public QuizEditorHandler(QuizService quizService, CategoryService categoryService) {
        this.quizService = quizService;
        this.categoryService = categoryService;
    }

    public void handleEditForm(HttpServletRequest request, HttpServletResponse response, 
                              UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        
        if (quiz == null) {
            request.setAttribute("errorMessage", "Quiz not found");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
            request.setAttribute("errorMessage", "You don't have permission to edit this quiz");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        List<Category> categories = categoryService.getAllActiveCategories();
        List<Question> questions = quizService.getQuizQuestions(quizId);
        
        request.setAttribute("quiz", quiz);
        request.setAttribute("categories", categories);
        request.setAttribute("questions", questions);
        request.setAttribute("mode", "edit");
        
        request.getRequestDispatcher("/WEB-INF/quiz-editor.jsp").forward(request, response);
    }

    public void handleUpdateQuiz(HttpServletRequest request, HttpServletResponse response, 
                                UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
            request.setAttribute("errorMessage", "You don't have permission to update this quiz");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        String validationError = QuizFormValidator.validateQuizForm(request);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            handleEditForm(request, response, currentUser, quizId);
            return;
        }
        
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        if (quiz == null) {
            request.setAttribute("errorMessage", "Quiz not found");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        updateQuizFromForm(quiz, request);
        
        boolean updated = quizService.updateQuiz(quiz);
        
        if (updated) {
            response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Quiz updated successfully");
        } else {
            request.setAttribute("errorMessage", "Failed to update quiz");
            handleEditForm(request, response, currentUser, quizId);
        }
    }

    public void handlePreviewQuiz(HttpServletRequest request, HttpServletResponse response, 
                                 UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        
        if (quiz == null) {
            request.setAttribute("errorMessage", "Quiz not found");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
            request.setAttribute("errorMessage", "You don't have permission to preview this quiz");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        List<Question> questions = quizService.getQuizQuestions(quizId);
        
        request.setAttribute("quiz", quiz);
        request.setAttribute("questions", questions);
        request.setAttribute("isPreview", true);
        
        request.getRequestDispatcher("/WEB-INF/quiz-preview.jsp").forward(request, response);
    }

    private void updateQuizFromForm(Quiz quiz, HttpServletRequest request) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String timeLimitStr = request.getParameter("timeLimit");
        
        quiz.setTestTitle(title);
        quiz.setTestDescription(description);
        
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            quiz.setCategoryId(Long.parseLong(categoryIdStr));
        }
        
        if (timeLimitStr != null && !timeLimitStr.trim().isEmpty()) {
            try {
                quiz.setTimeLimitMinutes(Long.parseLong(timeLimitStr));
            } catch (NumberFormatException ignored) {
            }
        }
    }
} 