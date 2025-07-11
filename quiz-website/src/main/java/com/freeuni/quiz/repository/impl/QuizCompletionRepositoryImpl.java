package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.repository.QuizCompletionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizCompletionRepositoryImpl implements QuizCompletionRepository {
    private final DataSource dataSource;

    public QuizCompletionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long saveCompletion(QuizCompletion completion) {
        String sql = "INSERT INTO quiz_completions (test_id, participant_user_id, started_at, finished_at, " +
                    "final_score, total_possible, completion_percentage, total_time_minutes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setLong(1, completion.getTestId());
            statement.setLong(2, completion.getParticipantUserId());
            statement.setTimestamp(3, Timestamp.valueOf(completion.getStartedAt()));
            if (completion.getFinishedAt() != null) {
                statement.setTimestamp(4, Timestamp.valueOf(completion.getFinishedAt()));
            } else {
                statement.setNull(4, Types.TIMESTAMP);
            }
            statement.setDouble(5, completion.getFinalScore());
            statement.setDouble(6, completion.getTotalPossible());
            statement.setBigDecimal(7, completion.getCompletionPercentage());
            statement.setInt(8, completion.getTotalTimeMinutes());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                throw new SQLException("Failed to insert quiz completion, no ID obtained");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz completion", e);
        }
    }

    @Override
    public Optional<QuizCompletion> findById(Long completionId) {
        String sql = "SELECT * FROM quiz_completions WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, completionId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuizCompletion(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz completion by ID", e);
        }
    }

    @Override
    public List<QuizCompletion> findByQuiz(Long testId) {
        String sql = "SELECT * FROM quiz_completions WHERE test_id = ? ORDER BY started_at DESC";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, testId);
            
            return executeQuizCompletionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz completions by quiz", e);
        }
    }

    @Override
    public Optional<QuizCompletion> findFastestTime(Long participantUserId, Long testId) {
        String sql = "SELECT * FROM quiz_completions WHERE participant_user_id = ? AND test_id = ? ORDER BY total_time_minutes LIMIT 1";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuizCompletion(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding fastest time by participant and quiz", e);
        }
    }


    @Override
    public int getCompletionCountByQuiz(Long testId) {
        String sql = "SELECT COUNT(*) FROM quiz_completions WHERE test_id = ? AND finished_at IS NOT NULL";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, testId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting completion count for quiz", e);
        }
    }

    @Override
    public Double getAverageScoreByQuiz(Long testId) {
        String sql = "SELECT AVG(completion_percentage) FROM quiz_completions WHERE test_id = ? AND finished_at IS NOT NULL";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, testId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double avgScore = resultSet.getDouble(1);
                    return resultSet.wasNull() ? null : avgScore;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting average score for quiz", e);
        }
    }

    @Override
    public java.util.Map<Long, Integer> getCompletionCountsByQuizzes(java.util.List<Long> testIds) {
        java.util.Map<Long, Integer> results = new java.util.HashMap<>();
        
        if (testIds == null || testIds.isEmpty()) {
            return results;
        }
        
        String placeholders = testIds.stream().map(id -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT test_id, COUNT(*) as completion_count FROM quiz_completions " +
                    "WHERE test_id IN (" + placeholders + ") AND finished_at IS NOT NULL " +
                    "GROUP BY test_id";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < testIds.size(); i++) {
                statement.setLong(i + 1, testIds.get(i));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long testId = resultSet.getLong("test_id");
                    Integer count = resultSet.getInt("completion_count");
                    results.put(testId, count);
                }
            }
            
            for (Long testId : testIds) {
                if (!results.containsKey(testId)) {
                    results.put(testId, 0);
                }
            }
            
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting completion counts for quizzes", e);
        }
    }

    @Override
    public java.util.Map<Long, Double> getAverageScoresByQuizzes(java.util.List<Long> testIds) {
        java.util.Map<Long, Double> results = new java.util.HashMap<>();
        
        if (testIds == null || testIds.isEmpty()) {
            return results;
        }
        
        String placeholders = testIds.stream().map(id -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT test_id, AVG(completion_percentage) as avg_score FROM quiz_completions " +
                    "WHERE test_id IN (" + placeholders + ") AND finished_at IS NOT NULL " +
                    "GROUP BY test_id";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < testIds.size(); i++) {
                statement.setLong(i + 1, testIds.get(i));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long testId = resultSet.getLong("test_id");
                    Double avgScore = resultSet.getDouble("avg_score");
                    if (!resultSet.wasNull()) {
                        results.put(testId, avgScore);
                    }
                }
            }
            
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting average scores for quizzes", e);
        }
    }

    @Override
    public List<QuizCompletion> findRecentCompletionsByUser(Long userId, int limit) {
        String sql = "SELECT * FROM quiz_completions WHERE participant_user_id = ? " +
                    "AND finished_at IS NOT NULL ORDER BY finished_at DESC LIMIT ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            statement.setInt(2, limit);
            
            return executeQuizCompletionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent completions by user", e);
        }
    }

    @Override
    public List<QuizCompletion> findRecentCompletionsByFriends(Long userId, int limit) {
        String sql = "SELECT qc.* FROM quiz_completions qc " +
                    "JOIN friendships f ON (qc.participant_user_id = f.friendSenderId OR qc.participant_user_id = f.friendReceiverId) " +
                    "WHERE (f.friendSenderId = ? OR f.friendReceiverId = ?) " +
                    "AND qc.participant_user_id != ? " +
                    "AND qc.finished_at IS NOT NULL " +
                    "ORDER BY qc.finished_at DESC LIMIT ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            statement.setInt(4, limit);
            
            return executeQuizCompletionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent completions by friends", e);
        }
    }

    private List<QuizCompletion> executeQuizCompletionQuery(PreparedStatement statement) throws SQLException {
        List<QuizCompletion> completions = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                completions.add(mapResultSetToQuizCompletion(resultSet));
            }
        }
        
        return completions;
    }

    private QuizCompletion mapResultSetToQuizCompletion(ResultSet resultSet) throws SQLException {
        QuizCompletion completion = new QuizCompletion();
        completion.setId(resultSet.getLong("id"));
        completion.setTestId(resultSet.getLong("test_id"));
        completion.setParticipantUserId(resultSet.getLong("participant_user_id"));
        completion.setStartedAt(resultSet.getTimestamp("started_at").toLocalDateTime());
        
        Timestamp finishedAt = resultSet.getTimestamp("finished_at");
        if (finishedAt != null) {
            completion.setFinishedAt(finishedAt.toLocalDateTime());
        }
        
        completion.setFinalScore(resultSet.getDouble("final_score"));
        completion.setTotalPossible(resultSet.getDouble("total_possible"));
        completion.setCompletionPercentage(resultSet.getBigDecimal("completion_percentage"));
        completion.setTotalTimeMinutes(resultSet.getInt("total_time_minutes"));
        
        return completion;
    }

    @Override
    public Optional<QuizCompletion> findUserCompletionForQuiz(Long userId, Long quizId) {
        String sql = "SELECT * FROM quiz_completions WHERE participant_user_id = ? AND test_id = ? " +
                    "AND finished_at IS NOT NULL ORDER BY finished_at DESC LIMIT 1";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            statement.setLong(2, quizId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuizCompletion(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user completion for quiz", e);
        }
    }
} 