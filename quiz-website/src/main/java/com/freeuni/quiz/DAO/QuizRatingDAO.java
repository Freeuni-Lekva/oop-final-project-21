package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizRating;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizRatingDAO {
    private final DataSource dataSource;

    public QuizRatingDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addRating(QuizRating rating) throws SQLException {
        String sql = "INSERT INTO quiz_ratings (user_id, quiz_id, rating) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating = ?, created_at = CURRENT_TIMESTAMP";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, rating.getUserId());
            stmt.setLong(2, rating.getQuizId());
            stmt.setInt(3, rating.getRating());
            stmt.setInt(4, rating.getRating());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rating.setId(generatedKeys.getLong(1));
                }
            }
            return true;
        }
    }

    public QuizRating findByUserAndQuiz(int userId, long quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_ratings WHERE user_id = ? AND quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRating(rs);
                }
            }
        }
        return null;
    }

    public List<QuizRating> findByQuiz(long quizId) throws SQLException {
        String sql = "SELECT qr.*, u.userName FROM quiz_ratings qr " +
                "JOIN users u ON qr.user_id = u.id " +
                "WHERE qr.quiz_id = ?";

        List<QuizRating> ratings = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        }
        return ratings;
    }

    public double getAverageRating(long quizId) throws SQLException {
        String sql = "SELECT AVG(rating) as avg_rating FROM quiz_ratings WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    public int getRatingCount(long quizId) throws SQLException {
        String sql = "SELECT COUNT(*) as rating_count FROM quiz_ratings WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating_count");
                }
            }
        }
        return 0;
    }

    public Map<Long, Double> getPopularQuizzes(int limit) throws SQLException {
        String sql = "SELECT quiz_id, AVG(rating) as avg_rating FROM quiz_ratings " +
                "GROUP BY quiz_id ORDER BY avg_rating DESC, COUNT(*) DESC LIMIT ?";

        Map<Long, Double> popularQuizzes = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    popularQuizzes.put(rs.getLong("quiz_id"), rs.getDouble("avg_rating"));
                }
            }
        }
        return popularQuizzes;
    }

    public boolean deleteRating(int userId, long quizId) throws SQLException {
        String sql = "DELETE FROM quiz_ratings WHERE user_id = ? AND quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, quizId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private QuizRating mapResultSetToRating(ResultSet rs) throws SQLException {
        QuizRating rating = new QuizRating();
        rating.setId(rs.getLong("id"));
        rating.setUserId(rs.getInt("user_id"));
        rating.setQuizId(rs.getLong("quiz_id"));
        rating.setRating(rs.getInt("rating"));
        rating.setCreatedAt(rs.getTimestamp("created_at"));
        return rating;
    }
}