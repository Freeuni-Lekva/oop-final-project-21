package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-browser")
public class QuizBrowserServlet extends HttpServlet {

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
        
        try {
            String categoryIdStr = request.getParameter("categoryId");
            String pageStr = request.getParameter("page");
            
            int page = parsePageNumber(pageStr);
            int size = 10;
            
            List<Quiz> quizzes = getFilteredQuizzes(categoryIdStr, page, size);
            List<Category> categories = categoryService.getAllActiveCategories();
            
            request.setAttribute("quizzes", quizzes);
            request.setAttribute("categories", categories);
            request.setAttribute("currentCategory", categoryIdStr);
            request.setAttribute("currentPage", page);
            
            request.getRequestDispatcher("/WEB-INF/quiz-browse.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quizzes: " + e.getMessage());
        }
    }

    private int parsePageNumber(String pageStr) {
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                return Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private List<Quiz> getFilteredQuizzes(String categoryIdStr, int page, int size) {
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            Long categoryId = Long.parseLong(categoryIdStr);
            return quizService.getQuizzesByCategory(categoryId, page, size);
        } else {
            return quizService.getAllQuizzes(page, size);
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
} 