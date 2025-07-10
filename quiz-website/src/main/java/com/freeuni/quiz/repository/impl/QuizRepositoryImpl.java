package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.repository.QuizRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizRepositoryImpl implements QuizRepository {
    private final DataSource dataSource;

    public QuizRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long saveQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (creator_user_id, category_id, test_title, test_description, time_limit_minutes, " +
                    "created_at, last_question_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setLong(1, quiz.getCreatorUserId());
            statement.setLong(2, quiz.getCategoryId());
            statement.setString(3, quiz.getTestTitle());
            statement.setString(4, quiz.getTestDescription());
            statement.setLong(5, quiz.getTimeLimitMinutes());
            statement.setTimestamp(6, Timestamp.valueOf(quiz.getCreatedAt()));
            statement.setLong(7, quiz.getLastQuestionNumber());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                throw new SQLException("Failed to insert quiz, no ID obtained");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz", e);
        }
    }

    @Override
    public Optional<Quiz> findById(Long quizId) {
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuiz(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz by ID", e);
        }
    }

    @Override
    public List<Quiz> findByCreator(Long creatorUserId, int offset, int limit) {
        String sql = "SELECT * FROM quizzes WHERE creator_user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, creatorUserId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuizQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quizzes by creator", e);
        }
    }

    @Override
    public List<Quiz> findByCategory(Long categoryId, int offset, int limit) {
        String sql = "SELECT * FROM quizzes WHERE category_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, categoryId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuizQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quizzes by category", e);
        }
    }

    @Override
    public List<Quiz> findAll(int offset, int limit) {
        String sql = "SELECT * FROM quizzes ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            
            return executeQuizQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all quizzes", e);
        }
    }

    @Override
    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET test_title = ?, test_description = ?, time_limit_minutes = ?, " +
                    "last_question_number = ? WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, quiz.getTestTitle());
            statement.setString(2, quiz.getTestDescription());
            statement.setLong(3, quiz.getTimeLimitMinutes());
            statement.setLong(4, quiz.getLastQuestionNumber());
            statement.setLong(5, quiz.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz", e);
        }
    }

    @Override
    public boolean updateLastQuestionNumber(Long quizId, Long questionNumber) {
        String sql = "UPDATE quizzes SET last_question_number = ? WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, questionNumber);
            statement.setLong(2, quizId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating last question number", e);
        }
    }

    @Override
    public boolean deleteQuiz(Long quizId) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, quizId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quiz", e);
        }
    }

    private List<Quiz> executeQuizQuery(PreparedStatement statement) throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                quizzes.add(mapResultSetToQuiz(resultSet));
            }
        }
        
        return quizzes;
    }

    private Quiz mapResultSetToQuiz(ResultSet resultSet) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(resultSet.getLong("id"));
        quiz.setCreatorUserId(resultSet.getInt("creator_user_id"));
        quiz.setCategoryId(resultSet.getLong("category_id"));
        quiz.setTestTitle(resultSet.getString("test_title"));
        quiz.setTestDescription(resultSet.getString("test_description"));
        quiz.setTimeLimitMinutes(resultSet.getLong("time_limit_minutes"));
        quiz.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        quiz.setLastQuestionNumber(resultSet.getLong("last_question_number"));
        
        return quiz;
    }
} 