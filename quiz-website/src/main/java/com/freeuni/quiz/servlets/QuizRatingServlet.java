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

@WebServlet("/rate-quiz")
public class QuizRatingServlet extends HttpServlet {
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

        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID is required");
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);
            QuizRatingDTO userRating = ratingService.getUserRating(user.getId(), quizId);

            if (userRating != null) {
                request.setAttribute("userRating", userRating);
            }

            double averageRating = ratingService.getAverageRating(quizId);
            int ratingCount = ratingService.getRatingCount(quizId);

            request.setAttribute("averageRating", averageRating);
            request.setAttribute("ratingCount", ratingCount);
            request.setAttribute("quizId", quizId);

            request.getRequestDispatcher("/rate-quiz.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Quiz ID");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        String quizIdParam = request.getParameter("quizId");
        String ratingParam = request.getParameter("rating");

        if (quizIdParam == null || quizIdParam.trim().isEmpty() || ratingParam == null || ratingParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID and rating are required");
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);
            int rating = Integer.parseInt(ratingParam);

            if (rating < 1 || rating > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Rating must be between 1 and 5");
                return;
            }

            boolean success = ratingService.rateQuiz(user.getId(), quizId, rating);

            if (success) {
                response.sendRedirect("quiz-details?id=" + quizId);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save rating");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Quiz ID or rating");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        String quizIdParam = request.getParameter("quizId");

        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);
            boolean success = ratingService.deleteRating(user.getId(), quizId);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}