package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.bean.QuestionType;
import com.freeuni.quiz.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Long createQuestion(Question question) {
        question.setCreatedAt(LocalDateTime.now());
        
        validateQuestionData(question);
        
        return questionRepository.saveQuestion(question);
    }

    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Question> getQuestionsByAuthor(Long authorId, int page, int size) {
        int offset = page * size;
        return questionRepository.findByAuthor(authorId, offset, size);
    }

    public List<Question> getQuestionsByCategory(Long categoryId, int page, int size) {
        int offset = page * size;
        return questionRepository.findByCategory(categoryId, offset, size);
    }

    public List<Question> getQuestionsByType(QuestionType type, int page, int size) {
        int offset = page * size;
        return questionRepository.findByType(type, offset, size);
    }

    public List<Question> searchQuestionsByTitle(String searchTerm, int page, int size) {
        int offset = page * size;
        return questionRepository.searchByTitle(searchTerm, offset, size);
    }

    public boolean updateQuestion(Question question) {
        validateQuestionData(question);
        
        return questionRepository.updateQuestion(question);
    }

    public boolean deleteQuestion(Long questionId) {
        return questionRepository.deleteQuestion(questionId);
    }

    public boolean isQuestionOwner(Long questionId, Long userId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
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