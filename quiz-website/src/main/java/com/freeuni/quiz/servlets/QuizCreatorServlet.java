package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-creator")
public class QuizCreatorServlet extends HttpServlet {

    private QuizService quizService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        this.quizService = new QuizService(dataSource);
        this.categoryService = new CategoryService(dataSource);
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
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
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
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String categoryIdStr = request.getParameter("categoryId");
            String timeLimitStr = request.getParameter("timeLimit");
            
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Quiz title is required");
                handleCreateForm(request, response);
                return;
            }
            
            if (description == null || description.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Quiz description is required");
                handleCreateForm(request, response);
                return;
            }
            
            Quiz quiz = createQuizFromFormData(title, description, categoryIdStr, timeLimitStr, currentUser);
            
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

    private Quiz createQuizFromFormData(String title, String description, String categoryIdStr, String timeLimitStr, UserDTO currentUser) {
        Quiz quiz = new Quiz();
        quiz.setCreatorUserId(currentUser.getId());
        quiz.setTestTitle(title);
        quiz.setTestDescription(description);
        
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            quiz.setCategoryId(Long.parseLong(categoryIdStr));
        }
        
        long timeLimit = 10L;
        if (timeLimitStr != null && !timeLimitStr.isEmpty()) {
            try {
                timeLimit = Long.parseLong(timeLimitStr);
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