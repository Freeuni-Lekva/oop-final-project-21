package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.bean.QuestionType;
import com.freeuni.quiz.repository.QuestionRepository;
import com.freeuni.quiz.quiz_util.AbstractQuestionHandler;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionRepositoryImpl implements QuestionRepository {
    private final DataSource dataSource;

    public QuestionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long saveQuestion(Question question) {
        String sql = "INSERT INTO test_questions (author_user_id, category_id, question_title, question_type, " +
                    "question_data, created_at, points) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setLong(1, question.getAuthorUserId());
            statement.setLong(2, question.getCategoryId());
            statement.setString(3, question.getQuestionTitle());
            statement.setString(4, question.getQuestionType().name());
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(question.getQuestionHandler());
            out.flush();
            statement.setBlob(5, new ByteArrayInputStream(bos.toByteArray()));
            
            statement.setTimestamp(6, Timestamp.valueOf(question.getCreatedAt()));
            statement.setDouble(7, question.getPoints());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                throw new SQLException("Failed to insert question, no ID obtained");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving question", e);
        }
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        String sql = "SELECT * FROM test_questions WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, questionId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToQuestion(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding question by ID", e);
        }
    }

    @Override
    public List<Question> findByAuthor(Long authorId, int offset, int limit) {
        String sql = "SELECT * FROM test_questions WHERE author_user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, authorId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuestionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding questions by author", e);
        }
    }

    @Override
    public List<Question> findByCategory(Long categoryId, int offset, int limit) {
        String sql = "SELECT * FROM test_questions WHERE category_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, categoryId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuestionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding questions by category", e);
        }
    }

    @Override
    public List<Question> findByType(QuestionType type, int offset, int limit) {
        String sql = "SELECT * FROM test_questions WHERE question_type = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, type.name());
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuestionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding questions by type", e);
        }
    }

    @Override
    public List<Question> searchByTitle(String searchTerm, int offset, int limit) {
        String sql = "SELECT * FROM test_questions WHERE question_title LIKE ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + searchTerm + "%");
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            
            return executeQuestionQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching questions by title", e);
        }
    }

    @Override
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE test_questions SET question_title = ?, question_type = ?, question_data = ?, points = ? WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, question.getQuestionTitle());
            statement.setString(2, question.getQuestionType().name());
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(question.getQuestionHandler());
            out.flush();
            statement.setBlob(3, new ByteArrayInputStream(bos.toByteArray()));
            
            statement.setDouble(4, question.getPoints());
            statement.setLong(5, question.getId());
            
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error updating question", e);
        }
    }

    @Override
    public boolean deleteQuestion(Long questionId) {
        String sql = "DELETE FROM test_questions WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, questionId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting question", e);
        }
    }

    private List<Question> executeQuestionQuery(PreparedStatement statement) throws SQLException {
        List<Question> questions = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                questions.add(mapResultSetToQuestion(resultSet));
            }
        }
        
        return questions;
    }

    private Question mapResultSetToQuestion(ResultSet resultSet) throws SQLException {
        Question question = new Question();
        question.setId(resultSet.getLong("id"));
        question.setAuthorUserId(resultSet.getInt("author_user_id"));
        question.setCategoryId(resultSet.getLong("category_id"));
        question.setQuestionTitle(resultSet.getString("question_title"));
        question.setQuestionType(QuestionType.valueOf(resultSet.getString("question_type")));
        question.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        question.setPoints(resultSet.getDouble("points"));
        
        try {
            Blob blob = resultSet.getBlob("question_data");
            if (blob != null) {
                ObjectInputStream in = new ObjectInputStream(blob.getBinaryStream());
                question.setQuestionHandler((AbstractQuestionHandler) in.readObject());
            }
        } catch (Exception e) {
            System.err.println("Error deserializing question handler: " + e.getMessage());
        }
        
        return question;
    }
} 