package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.QuizFormDto;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/quiz-creator")
public class QuizCreatorServlet extends HttpServlet {

    private QuizService quizService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.quizService = (QuizService) getServletContext().getAttribute("quizService");
        this.categoryService = (CategoryService) getServletContext().getAttribute("categoryService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleCreateForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleSaveQuiz(request, response);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = ServletUtils.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            ServletUtils.setupCategoriesAttribute(request, categoryService);
            request.setAttribute("mode", "create");
            
            request.getRequestDispatcher("/WEB-INF/quiz-creator.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz creation form: " + e.getMessage());
        }
    }

    private void handleSaveQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = ServletUtils.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            QuizFormDto formData = ServletUtils.extractQuizFormData(request);
            
            String validationError = ServletUtils.validateQuizForm(formData);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                handleCreateForm(request, response);
                return;
            }
            
            Quiz quiz = createQuizFromFormData(formData, currentUser);
            
            Long quizId = quizService.createQuiz(quiz);
            
            if (quizId != null) {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Quiz created successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to create quiz");
                handleCreateForm(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating quiz: " + e.getMessage());
            handleCreateForm(request, response);
        }
    }

    private Quiz createQuizFromFormData(QuizFormDto formData, UserDTO currentUser) {
        Quiz quiz = new Quiz();
        quiz.setCreatorUserId(currentUser.getId());
        quiz.setTestTitle(formData.getTitle());
        quiz.setTestDescription(formData.getDescription());
        
        if (formData.getCategoryIdStr() != null && !formData.getCategoryIdStr().isEmpty()) {
            quiz.setCategoryId(Long.parseLong(formData.getCategoryIdStr()));
        }
        
        long timeLimit = 10L;
        if (formData.getTimeLimitStr() != null && !formData.getTimeLimitStr().isEmpty()) {
            try {
                timeLimit = Long.parseLong(formData.getTimeLimitStr());
            } catch (NumberFormatException ignored) {
            }
        }
        quiz.setTimeLimitMinutes(timeLimit);
        
        return quiz;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
} 