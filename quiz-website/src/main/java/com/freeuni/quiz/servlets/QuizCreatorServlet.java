package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.validators.QuizFormValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-creator")
public class QuizCreatorServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        handleCreateForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        handleSaveQuiz(request, response);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Category> categories = categoryService.getAllActiveCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("mode", "create");
            
            request.getRequestDispatcher("/WEB-INF/quiz-creator.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz creation form: " + e.getMessage());
        }
    }

    private void handleSaveQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = getCurrentUser(request);
            
            String validationError = QuizFormValidator.validateQuizForm(request);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                handleCreateForm(request, response);
                return;
            }
            
            Quiz quiz = createQuizFromFormData(request, currentUser);
            
            Long quizId = quizService.createQuiz(quiz);
            
            if (quizId != null) {
                redirectWithSuccess(response, "quiz-editor?quizId=" + quizId, "Quiz created successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to create quiz");
                handleCreateForm(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating quiz: " + e.getMessage());
            handleCreateForm(request, response);
        }
    }

    private Quiz createQuizFromFormData(HttpServletRequest request, UserDTO currentUser) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        
        Quiz quiz = new Quiz();
        quiz.setCreatorUserId(currentUser.getId());
        quiz.setTestTitle(title);
        quiz.setTestDescription(description);
        
        if (isValid(categoryIdStr)) {
            quiz.setCategoryId(Long.parseLong(categoryIdStr));
        }
        
        Long timeLimit = getDoubleParam(request).longValue();
        quiz.setTimeLimitMinutes(timeLimit);
        
        return quiz;
    }
} 