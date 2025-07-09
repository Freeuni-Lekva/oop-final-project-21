package com.freeuni.quiz.servlets;

import com.freeuni.quiz.service.QuizRatingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/popular-quizzes")
public class PopularQuizzesServlet extends HttpServlet {
    private QuizRatingService ratingService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        ratingService = new QuizRatingService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String limitParam = request.getParameter("limit");
        int limit = 10;

        if (limitParam != null && !limitParam.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit <= 0) {
                    limit = 10;
                }
            } catch (NumberFormatException e) {
            }
        }

        try {
            Map<Long, Double> popularQuizzes = ratingService.getPopularQuizzes(limit);
            request.setAttribute("popularQuizzes", popularQuizzes);
            request.getRequestDispatcher("/popular-quizzes.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
}