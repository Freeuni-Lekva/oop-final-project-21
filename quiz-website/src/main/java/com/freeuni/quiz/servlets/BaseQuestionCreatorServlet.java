package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseQuestionCreatorServlet extends HttpServlet {

    protected QuestionService questionService;
    protected CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.questionService = (QuestionService) getServletContext().getAttribute("questionService");
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
        handleSaveQuestion(request, response);
    }

    protected void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = ServletUtils.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            ServletUtils.setupCategoriesAttribute(request, categoryService);
            request.setAttribute("questionType", getQuestionType());
            request.setAttribute("mode", "create");
            
            request.getRequestDispatcher(getJspPage()).forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading question creation form: " + e.getMessage());
        }
    }

    protected void handleSaveQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = ServletUtils.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String validationError = validateFormData(request);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                handleCreateForm(request, response);
                return;
            }
            
            Question question = createQuestionFromForm(request, currentUser);
            if (question == null) {
                request.setAttribute("errorMessage", "Failed to create question - invalid data");
                handleCreateForm(request, response);
                return;
            }
            
            Long questionId = questionService.createQuestion(question);
            
            if (questionId != null) {
                response.sendRedirect("question-editor?questionId=" + questionId + "&message=Question created successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to create question");
                handleCreateForm(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating question: " + e.getMessage());
            handleCreateForm(request, response);
        }
    }

    protected Question createBaseQuestion(HttpServletRequest request, UserDTO currentUser) {
        try {
            Question question = new Question();
            question.setAuthorUserId(currentUser.getId());
            question.setQuestionTitle(request.getParameter("title"));
            question.setQuestionType(QuestionType.valueOf(getQuestionType()));
            
            String categoryIdStr = request.getParameter("categoryId");
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                question.setCategoryId(Long.parseLong(categoryIdStr));
            }
            
            return question;
        } catch (Exception e) {
            return null;
        }
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }


    protected abstract String getQuestionType();
    protected abstract String getJspPage();
    protected abstract String validateFormData(HttpServletRequest request);
    protected abstract Question createQuestionFromForm(HttpServletRequest request, UserDTO currentUser);
} 