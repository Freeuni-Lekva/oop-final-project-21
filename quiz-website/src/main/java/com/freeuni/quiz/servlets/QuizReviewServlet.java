package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.QuizReviewDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.QuizReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/quiz-reviews")
public class QuizReviewServlet extends HttpServlet {
    private QuizReviewService reviewService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        reviewService = new QuizReviewService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String quizIdParam = request.getParameter("quizId");

        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID is required");
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);
            List<QuizReviewDTO> reviews = reviewService.getQuizReviews(quizId);

            request.setAttribute("reviews", reviews);
            request.setAttribute("quizId", quizId);

            // Check if user is logged in and has already submitted a review
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                UserDTO user = (UserDTO) session.getAttribute("user");
                QuizReviewDTO userReview = reviewService.getUserReview(user.getId(), quizId);

                if (userReview != null) {
                    request.setAttribute("userReview", userReview);
                }
            }

            request.getRequestDispatcher("/quiz-reviews.jsp").forward(request, response);

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
        String reviewText = request.getParameter("reviewText");

        if (quizIdParam == null || quizIdParam.trim().isEmpty() || reviewText == null || reviewText.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID and review text are required");
            return;
        }

        try {
            long quizId = Long.parseLong(quizIdParam);
            boolean success = reviewService.addOrUpdateReview(user.getId(), quizId, reviewText);

            if (success) {
                response.sendRedirect("quiz-reviews?quizId=" + quizId);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save review");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Quiz ID");
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
            boolean success = reviewService.deleteReview(user.getId(), quizId);

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