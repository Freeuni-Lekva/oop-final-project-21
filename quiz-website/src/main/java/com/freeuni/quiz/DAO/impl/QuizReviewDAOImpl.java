package com.freeuni.quiz.DAO.impl;

import com.freeuni.quiz.DAO.QuizReviewDAO;
import com.freeuni.quiz.bean.QuizReview;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizReviewDAOImpl implements QuizReviewDAO {
    private final DataSource dataSource;

    public QuizReviewDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addReview(QuizReview review) throws SQLException {
        QuizReview existing = findByUserAndQuiz(review.getUserId(), review.getQuizId());

        if (existing == null) {
            String sql = "INSERT INTO quiz_reviews (user_id, quiz_id, review_text) VALUES (?, ?, ?)";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, review.getUserId());
                stmt.setLong(2, review.getQuizId());
                stmt.setString(3, review.getReviewText());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    return false;
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        review.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } else {
            String sql = "UPDATE quiz_reviews SET review_text = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND quiz_id = ?";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, review.getReviewText());
                stmt.setInt(2, review.getUserId());
                stmt.setLong(3, review.getQuizId());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    return false;
                }

                review.setId(existing.getId());
                return true;
            }
        }
    }

    public QuizReview findByUserAndQuiz(int userId, long quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_reviews WHERE user_id = ? AND quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        return null;
    }

    public List<QuizReview> findByQuiz(long quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_reviews WHERE quiz_id = ? ORDER BY created_at DESC";

        List<QuizReview> reviews = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    QuizReview review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    public boolean updateReview(QuizReview review) throws SQLException {
        String sql = "UPDATE quiz_reviews SET review_text = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ? AND quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, review.getReviewText());
            stmt.setInt(2, review.getUserId());
            stmt.setLong(3, review.getQuizId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteReview(int userId, long quizId) throws SQLException {
        String sql = "DELETE FROM quiz_reviews WHERE user_id = ? AND quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, quizId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private QuizReview mapResultSetToReview(ResultSet rs) throws SQLException {
        QuizReview review = new QuizReview();
        review.setId(rs.getLong("id"));
        review.setUserId(rs.getInt("user_id"));
        review.setQuizId(rs.getLong("quiz_id"));
        review.setReviewText(rs.getString("review_text"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        review.setUpdatedAt(rs.getTimestamp("updated_at"));
        return review;
    }
}