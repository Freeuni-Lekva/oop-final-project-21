package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseServlet extends HttpServlet {

    protected QuizService quizService;
    protected CategoryService categoryService;
    protected QuestionService questionService;
    protected QuizSessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        
        this.quizService = new QuizService(dataSource);
        this.categoryService = new CategoryService(dataSource);
        this.questionService = new QuestionService(dataSource);
        this.sessionService = new QuizSessionService(dataSource);
    }

    protected UserDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (UserDTO) session.getAttribute("user") : null;
    }

    protected boolean requireAuth(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        if (getCurrentUser(request) == null) {
            response.sendRedirect("login.jsp");
            return true;
        }
        return false;
    }

    protected boolean isQuizOwner(HttpServletRequest request, Long quizId) {
        UserDTO user = getCurrentUser(request);
        return user == null || !quizService.isQuizOwner(quizId, (long) user.getId());
    }

    protected Long getLongParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Double getDoubleParam(HttpServletRequest request) {
        String value = request.getParameter("timeLimit");
        if (value == null || value.trim().isEmpty()) {
            return 10.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 10.0;
        }
    }

    protected boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }


    protected void redirectWithSuccess(HttpServletResponse response, String url, String message) 
            throws IOException {
        response.sendRedirect(url + (url.contains("?") ? "&" : "?") + "message=" + 
                            java.net.URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    protected void redirectWithError(HttpServletResponse response, String url, String error) 
            throws IOException {
        response.sendRedirect(url + (url.contains("?") ? "&" : "?") + "error=" + 
                            java.net.URLEncoder.encode(error, StandardCharsets.UTF_8));
    }
} 