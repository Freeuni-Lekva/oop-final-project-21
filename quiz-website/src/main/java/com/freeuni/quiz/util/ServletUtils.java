package com.freeuni.quiz.util;

import com.freeuni.quiz.DTO.QuestionFormDto;
import com.freeuni.quiz.DTO.QuizFormDto;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.CategoryService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ServletUtils {
    
    public static UserDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserDTO) session.getAttribute("user");
    }
    
    public static boolean isUserLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
    
    public static void redirectToLoginIfNotAuthenticated(HttpServletRequest request, 
                                                       HttpServletResponse response) 
            throws IOException {
        if (!isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
    
    public static void forwardWithError(HttpServletRequest request, HttpServletResponse response, 
                                      String jspPage, String errorMessage) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPage);
        dispatcher.forward(request, response);
    }
    
    public static void setupCategoriesAttribute(HttpServletRequest request, CategoryService categoryService) {
        request.setAttribute("categories", categoryService.getAllActiveCategories());
    }
    
    public static QuizFormDto extractQuizFormData(HttpServletRequest request) {
        return new QuizFormDto(
            request.getParameter("title"),
            request.getParameter("description"),
            request.getParameter("categoryId"),
            request.getParameter("timeLimit")
        );
    }
    
    public static QuestionFormDto extractQuestionFormData(HttpServletRequest request) {
        return new QuestionFormDto(
            request.getParameter("title"),
            request.getParameter("questionType"),
            request.getParameter("categoryId"),
            request.getParameter("questionText")
        );
    }
    
    public static String validateQuizForm(QuizFormDto formData) {
        if (formData.getTitle() == null || formData.getTitle().trim().isEmpty()) {
            return "Quiz title is required";
        }
        
        if (formData.getDescription() == null || formData.getDescription().trim().isEmpty()) {
            return "Quiz description is required";
        }
        
        if (formData.getCategoryIdStr() == null || formData.getCategoryIdStr().trim().isEmpty()) {
            return "Category is required";
        }
        
        try {
            Integer.parseInt(formData.getCategoryIdStr());
        } catch (NumberFormatException e) {
            return "Invalid category selected";
        }
        
        if (formData.getTimeLimitStr() != null && !formData.getTimeLimitStr().trim().isEmpty()) {
            try {
                int timeLimit = Integer.parseInt(formData.getTimeLimitStr());
                if (timeLimit <= 0) {
                    return "Time limit must be positive";
                }
            } catch (NumberFormatException e) {
                return "Invalid time limit format";
            }
        }
        
        return null;
    }
    
    public static String validateQuestionForm(QuestionFormDto formData) {
        if (formData.getTitle() == null || formData.getTitle().trim().isEmpty()) {
            return "Question title is required";
        }
        
        if (formData.getQuestionText() == null || formData.getQuestionText().trim().isEmpty()) {
            return "Question text is required";
        }
        
        if (formData.getCategoryIdStr() == null || formData.getCategoryIdStr().trim().isEmpty()) {
            return "Category is required";
        }
        
        try {
            Integer.parseInt(formData.getCategoryIdStr());
        } catch (NumberFormatException e) {
            return "Invalid category selected";
        }
        
        return null;
    }
    
    public static boolean isValidInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidPositiveInteger(String value) {
        if (!isValidInteger(value)) {
            return false;
        }
        return Integer.parseInt(value) > 0;
    }
    
    public static String getStringParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return value != null ? value.trim() : null;
    }
    
    public static String[] getStringArrayParameter(HttpServletRequest request, String paramName) {
        return request.getParameterValues(paramName);
    }
    
    public static void setSuccessMessage(HttpServletRequest request, String message) {
        request.setAttribute("successMessage", message);
    }
    
    public static void setErrorMessage(HttpServletRequest request, String message) {
        request.setAttribute("errorMessage", message);
    }
} 