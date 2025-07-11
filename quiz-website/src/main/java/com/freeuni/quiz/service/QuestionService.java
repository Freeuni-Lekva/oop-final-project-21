package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.bean.QuestionType;
import com.freeuni.quiz.DAO.QuestionDAO;
import com.freeuni.quiz.DAO.impl.QuestionDAOImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class QuestionService {
    private final QuestionDAO questionDAO;

    public QuestionService(DataSource dataSource) {
        this.questionDAO = new QuestionDAOImpl(dataSource);
    }

    public Long createQuestion(Question question) {
        question.setCreatedAt(LocalDateTime.now());
        
        validateQuestionData(question);
        
        return questionDAO.saveQuestion(question);
    }

    public Optional<Question> getQuestionById(Long questionId) {
        return questionDAO.findById(questionId);
    }

    public List<Question> getQuestionsByAuthor(Long authorId, int page, int size) {
        int offset = page * size;
        return questionDAO.findByAuthor(authorId, offset, size);
    }

    public List<Question> getQuestionsByCategory(Long categoryId, int page, int size) {
        int offset = page * size;
        return questionDAO.findByCategory(categoryId, offset, size);
    }

    public List<Question> getQuestionsByType(QuestionType type, int page, int size) {
        int offset = page * size;
        return questionDAO.findByType(type, offset, size);
    }

    public List<Question> searchQuestionsByTitle(String searchTerm, int page, int size) {
        int offset = page * size;
        return questionDAO.searchByTitle(searchTerm, offset, size);
    }

    public boolean updateQuestion(Question question) {
        validateQuestionData(question);
        
        return questionDAO.updateQuestion(question);
    }

    public boolean deleteQuestion(Long questionId) {
        return questionDAO.deleteQuestion(questionId);
    }

    public boolean isQuestionOwner(Long questionId, Long userId) {
        Optional<Question> questionOpt = questionDAO.findById(questionId);
        return questionOpt.isPresent() && questionOpt.get().getAuthorUserId() == userId;
    }

    private void validateQuestionData(Question question) {
        if (question.getQuestionTitle() == null || question.getQuestionTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Question title is required");
        }
        
        if (question.getQuestionType() == null) {
            throw new IllegalArgumentException("Question type is required");
        }
        
        if (question.getQuestionHandler() == null) {
            throw new IllegalArgumentException("Question handler is required");
        }
        
        if (question.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
    }
} 