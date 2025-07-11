package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.repository.HistoryRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class HistoryRepositoryImpl implements HistoryRepository {

    private final DataSource dataSource;

    public HistoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<QuizCompletion> getUserCompletions(int userId) {
        List<QuizCompletion> completions = new ArrayList<>();

        String sql = "SELECT * FROM quiz_completions " +
                "WHERE participant_user_id = ? " +
                "ORDER BY finished_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    completions.add(mapQuizCompletion(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completions;
    }

    @Override
    public List<QuizCompletion> getUserRecentCompletions(int userId, int limit) {
        List<QuizCompletion> completions = new ArrayList<>();

        String sql = "SELECT * FROM quiz_completions " +
                "WHERE participant_user_id = ? " +
                "ORDER BY finished_at DESC LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    completions.add(mapQuizCompletion(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completions;
    }

    @Override
    public List<QuizCompletion> getUserCompletionsByCategory(int userId, Long categoryId) {
        List<QuizCompletion> completions = new ArrayList<>();

        String sql = "SELECT qc.* FROM quiz_completions qc " +
                "JOIN quizzes q ON qc.test_id = q.id " +
                "WHERE qc.participant_user_id = ? AND q.category_id = ? " +
                "ORDER BY qc.finished_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    completions.add(mapQuizCompletion(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completions;
    }

    @Override
    public int getTotalQuizzesTaken(int userId) {
        String sql = "SELECT COUNT(*) FROM quiz_completions WHERE participant_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public double getAverageScore(int userId) {
        String sql = "SELECT AVG(completion_percentage) FROM quiz_completions WHERE participant_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public int getBestScore(int userId) {
        String sql = "SELECT MAX(completion_percentage) FROM quiz_completions WHERE participant_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public String getMostPlayedCategory(int userId) {
        String sql = "SELECT qc.category_name, COUNT(*) as count " +
                "FROM quiz_completions qcomp " +
                "JOIN quizzes q ON qcomp.test_id = q.id " +
                "JOIN quiz_categories qc ON q.category_id = qc.id " +
                "WHERE qcomp.participant_user_id = ? " +
                "GROUP BY qc.category_name " +
                "ORDER BY count DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "None";
    }

    @Override
    public int getTotalTimeTaken(int userId) {
        String sql = "SELECT SUM(total_time_minutes) FROM quiz_completions WHERE participant_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public Map<String, Integer> getCategoryDistribution(int userId) {
        Map<String, Integer> distribution = new LinkedHashMap<>();

        String sql = "SELECT qc.category_name, COUNT(*) as count " +
                "FROM quiz_completions qcomp " +
                "JOIN quizzes q ON qcomp.test_id = q.id " +
                "JOIN quiz_categories qc ON q.category_id = qc.id " +
                "WHERE qcomp.participant_user_id = ? " +
                "GROUP BY qc.category_name " +
                "ORDER BY count DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    distribution.put(rs.getString("category_name"), rs.getInt("count"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return distribution;
    }

    private QuizCompletion mapQuizCompletion(ResultSet rs) throws SQLException {
        QuizCompletion completion = new QuizCompletion();
        completion.setId(rs.getLong("id"));
        completion.setParticipantUserId(rs.getLong("participant_user_id"));
        completion.setTestId(rs.getLong("test_id"));
        completion.setFinalScore(rs.getDouble("final_score"));
        completion.setTotalPossible(rs.getDouble("total_possible"));

        BigDecimal completionPercentage = rs.getBigDecimal("completion_percentage");
        completion.setCompletionPercentage(completionPercentage);

        Timestamp startedTimestamp = rs.getTimestamp("started_at");
        if (startedTimestamp != null) {
            completion.setStartedAt(startedTimestamp.toLocalDateTime());
        }

        Timestamp finishedTimestamp = rs.getTimestamp("finished_at");
        if (finishedTimestamp != null) {
            completion.setFinishedAt(finishedTimestamp.toLocalDateTime());
        }

        completion.setTotalTimeMinutes(rs.getInt("total_time_minutes"));
        return completion;
    }
}