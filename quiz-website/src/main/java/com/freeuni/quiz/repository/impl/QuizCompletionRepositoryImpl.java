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
    public List<QuizCompletion> findByParticipant(Long participantUserId) {
        String sql = "SELECT * FROM quiz_completions WHERE participant_user_id = ? ORDER BY started_at DESC";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            
            return executeQuizCompletionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz completions by participant", e);
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
    public Optional<QuizCompletion> findBestScore(Long participantUserId, Long testId) {
        String sql = "SELECT * FROM quiz_completions WHERE participant_user_id = ? AND test_id = ? ORDER BY final_score DESC LIMIT 1";
        
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
            throw new RuntimeException("Error finding best score by participant and quiz", e);
        }
    }



    @Override
    public boolean deleteCompletion(Long completionId) {
        String sql = "DELETE FROM quiz_completions WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, completionId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quiz completion", e);
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
} 