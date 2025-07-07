package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.QuizSession;
import com.freeuni.quiz.repository.QuizSessionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class QuizSessionRepositoryImpl implements QuizSessionRepository {
    private final DataSource dataSource;

    public QuizSessionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createSession(QuizSession session) {
        String sql = "INSERT INTO quiz_sessions (participant_id, test_id, current_question_num, " +
                    "time_allocated, session_start) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setLong(1, session.getParticipantUserId());
            statement.setLong(2, session.getTestId());
            statement.setLong(3, session.getCurrentQuestionNum());
            statement.setLong(4, session.getTimeAllocated());
            statement.setTimestamp(5, Timestamp.valueOf(session.getSessionStart()));
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        session.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating quiz session", e);
        }
    }

    @Override
    public Optional<QuizSession> findByParticipant(Long participantId) {
        String sql = "SELECT * FROM quiz_sessions WHERE participant_id = ? ORDER BY session_start DESC LIMIT 1";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuizSession(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz session by participant ID", e);
        }
    }

    @Override
    public boolean updateCurrentQuestion(Long participantId, Long questionNumber) {
        String sql = "UPDATE quiz_sessions SET current_question_num = ? WHERE participant_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, questionNumber);
            statement.setLong(2, participantId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating current question", e);
        }
    }

    @Override
    public boolean deleteSession(Long participantId) {
        String sql = "DELETE FROM quiz_sessions WHERE participant_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quiz session", e);
        }
    }

    @Override
    public boolean hasActiveSession(Long participantId) {
        String sql = "SELECT COUNT(*) FROM quiz_sessions WHERE participant_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking for active session", e);
        }
    }

    private QuizSession mapResultSetToQuizSession(ResultSet resultSet) throws SQLException {
        QuizSession session = new QuizSession();
        session.setId(resultSet.getLong("id"));
        session.setParticipantUserId(resultSet.getLong("participant_id"));
        session.setTestId(resultSet.getLong("test_id"));
        session.setCurrentQuestionNum(resultSet.getLong("current_question_num"));
        session.setTimeAllocated(resultSet.getLong("time_allocated"));
        session.setSessionStart(resultSet.getTimestamp("session_start").toLocalDateTime());
        
        return session;
    }
} 