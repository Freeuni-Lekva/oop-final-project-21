package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.ParticipantAnswer;
import com.freeuni.quiz.repository.ParticipantAnswerRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantAnswerRepositoryImpl implements ParticipantAnswerRepository {
    private final DataSource dataSource;

    public ParticipantAnswerRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean saveAnswer(ParticipantAnswer answer) {
        String sql = "INSERT INTO participant_answers (participant_id, test_id, question_number, " +
                    "points_earned, time_spent_seconds, answer_text) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setLong(1, answer.getParticipantUserId());
            statement.setLong(2, answer.getTestId());
            statement.setLong(3, answer.getQuestionNumber());
            statement.setDouble(4, answer.getPointsEarned());
            statement.setInt(5, answer.getTimeSpentSeconds());
            statement.setString(6, answer.getAnswerText());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        answer.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving participant answer", e);
        }
    }

    @Override
    public Optional<Double> getAnswerScore(Long participantUserId, Long testId, Long questionNumber) {
        String sql = "SELECT points_earned FROM participant_answers WHERE participant_id = ? AND test_id = ? AND question_number = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            statement.setLong(3, questionNumber);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getDouble("points_earned"));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting answer score", e);
        }
    }

    @Override
    public List<Long> getAnsweredQuestionNumbers(Long participantUserId, Long testId) {
        String sql = "SELECT question_number FROM participant_answers WHERE participant_id = ? AND test_id = ? ORDER BY question_number";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            
            List<Long> questionNumbers = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questionNumbers.add(resultSet.getLong("question_number"));
                }
            }
            return questionNumbers;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting answered question numbers", e);
        }
    }

    @Override
    public List<ParticipantAnswer> getAllAnswers(Long participantUserId, Long testId) {
        String sql = "SELECT * FROM participant_answers WHERE participant_id = ? AND test_id = ? ORDER BY question_number";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            
            return executeParticipantAnswerQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all answers", e);
        }
    }

    @Override
    public boolean deleteAnswer(Long participantUserId, Long testId, Long questionNumber) {
        String sql = "DELETE FROM participant_answers WHERE participant_id = ? AND test_id = ? AND question_number = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            statement.setLong(3, questionNumber);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting participant answer", e);
        }
    }

    @Override
    public int deleteAllAnswers(Long participantUserId, Long testId) {
        String sql = "DELETE FROM participant_answers WHERE participant_id = ? AND test_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, participantUserId);
            statement.setLong(2, testId);
            
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all participant answers", e);
        }
    }

    private List<ParticipantAnswer> executeParticipantAnswerQuery(PreparedStatement statement) throws SQLException {
        List<ParticipantAnswer> answers = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                answers.add(mapResultSetToParticipantAnswer(resultSet));
            }
        }
        
        return answers;
    }

    private ParticipantAnswer mapResultSetToParticipantAnswer(ResultSet resultSet) throws SQLException {
        ParticipantAnswer answer = new ParticipantAnswer();
        answer.setId(resultSet.getLong("id"));
        answer.setParticipantUserId(resultSet.getLong("participant_id"));
        answer.setTestId(resultSet.getLong("test_id"));
        answer.setQuestionNumber(resultSet.getLong("question_number"));
        answer.setPointsEarned(resultSet.getDouble("points_earned"));
        answer.setTimeSpentSeconds(resultSet.getInt("time_spent_seconds"));
        answer.setAnswerText(resultSet.getString("answer_text"));
        
        return answer;
    }
} 