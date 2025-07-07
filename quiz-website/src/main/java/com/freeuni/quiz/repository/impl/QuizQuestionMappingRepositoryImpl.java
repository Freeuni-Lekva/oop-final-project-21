package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.QuizQuestionMapping;
import com.freeuni.quiz.repository.QuizQuestionMappingRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizQuestionMappingRepositoryImpl implements QuizQuestionMappingRepository {
    private final DataSource dataSource;

    public QuizQuestionMappingRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean addQuestionToQuiz(Long questionId, Long quizId, Long sequenceOrder) {
        String sql = "INSERT INTO quiz_question_mapping (quiz_id, question_id, sequence_order) VALUES (?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            statement.setLong(2, questionId);
            statement.setLong(3, sequenceOrder);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding question to quiz", e);
        }
    }

    @Override
    public boolean removeQuestionFromQuiz(Long questionId, Long quizId) {
        String sql = "DELETE FROM quiz_question_mapping WHERE quiz_id = ? AND question_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            statement.setLong(2, questionId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing question from quiz", e);
        }
    }

    @Override
    public List<Long> getQuestionIdsByQuizOrdered(Long quizId) {
        String sql = "SELECT question_id FROM quiz_question_mapping WHERE quiz_id = ? ORDER BY sequence_order";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            
            List<Long> questionIds = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questionIds.add(resultSet.getLong("question_id"));
                }
            }
            return questionIds;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting question IDs for quiz", e);
        }
    }

    @Override
    public Optional<Long> getQuestionIdBySequence(Long quizId, Long sequenceOrder) {
        String sql = "SELECT question_id FROM quiz_question_mapping WHERE quiz_id = ? AND sequence_order = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            statement.setLong(2, sequenceOrder);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getLong("question_id"));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting question ID by sequence", e);
        }
    }

    @Override
    public Long getNextSequenceOrder(Long quizId, Long currentSequence) {
        String sql = "SELECT MIN(sequence_order) FROM quiz_question_mapping WHERE quiz_id = ? AND sequence_order > ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            statement.setLong(2, currentSequence);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting next sequence order", e);
        }
    }

    @Override
    public int getQuestionCount(Long quizId) {
        String sql = "SELECT COUNT(*) FROM quiz_question_mapping WHERE quiz_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting question count", e);
        }
    }

    @Override
    public boolean updateQuestionSequence(Long quizId, Long questionId, Long newSequence) {
        String sql = "UPDATE quiz_question_mapping SET sequence_order = ? WHERE quiz_id = ? AND question_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, newSequence);
            statement.setLong(2, quizId);
            statement.setLong(3, questionId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating question sequence", e);
        }
    }

    @Override
    public List<QuizQuestionMapping> findByQuizId(Long quizId) {
        String sql = "SELECT * FROM quiz_question_mapping WHERE quiz_id = ? ORDER BY sequence_order";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            
            List<QuizQuestionMapping> mappings = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mappings.add(mapResultSetToQuizQuestionMapping(resultSet));
                }
            }
            return mappings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz question mappings", e);
        }
    }

    private QuizQuestionMapping mapResultSetToQuizQuestionMapping(ResultSet resultSet) throws SQLException {
        QuizQuestionMapping mapping = new QuizQuestionMapping();
        mapping.setId(resultSet.getLong("id"));
        mapping.setQuizId(resultSet.getLong("quiz_id"));
        mapping.setQuestionId(resultSet.getLong("question_id"));
        mapping.setSequenceOrder(resultSet.getLong("sequence_order"));
        
        return mapping;
    }
} 