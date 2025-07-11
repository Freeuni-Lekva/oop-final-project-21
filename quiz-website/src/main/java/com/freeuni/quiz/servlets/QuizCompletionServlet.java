package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.QuizRatingDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.QuizRatingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/quiz-completion")
public class QuizCompletionServlet extends HttpServlet {
    private QuizRatingService ratingService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        ratingService = new QuizRatingService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        String quizIdParam = request.getParameter("quizId");
        String scoreParam = request.getParameter("score");
        String maxScoreParam = request.getParameter("maxScore");

        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID is required");
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);

            // Get rating data for the completion page
            QuizRatingDTO userRating = null;
            double averageRating = 0.0;
            int ratingCount = 0;

            try {
                userRating = ratingService.getUserRating(user.getId(), quizId);
                averageRating = ratingService.getAverageRating(quizId);
                ratingCount = ratingService.getRatingCount(quizId);
            } catch (SQLException e) {
                System.err.println("Error loading rating data: " + e.getMessage());
                e.printStackTrace();
            }

            // Set attributes
            request.setAttribute("quizId", quizId);
            request.setAttribute("score", scoreParam);
            request.setAttribute("maxScore", maxScoreParam);
            request.setAttribute("userRating", userRating);
            request.setAttribute("averageRating", averageRating);
            request.setAttribute("ratingCount", ratingCount);

            // Forward to a simple completion page
            request.getRequestDispatcher("/quiz-completion.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Quiz ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }
}