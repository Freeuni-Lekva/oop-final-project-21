package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.repository.QuizRepository;
import com.freeuni.quiz.repository.QuizQuestionMappingRepository;
import com.freeuni.quiz.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizQuestionMappingRepository quizQuestionMappingRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, 
                      QuizQuestionMappingRepository quizQuestionMappingRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizQuestionMappingRepository = quizQuestionMappingRepository;
    }

    public Long createQuiz(Quiz quiz) {
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setLastQuestionNumber(0L);
        
        validateQuizData(quiz);
        
        return quizRepository.saveQuiz(quiz);
    }

    public Optional<Quiz> getQuizById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    public List<Quiz> getQuizzesByCreator(Long creatorId, int page, int size) {
        int offset = page * size;
        return quizRepository.findByCreator(creatorId, offset, size);
    }

    public List<Quiz> getQuizzesByCategory(Long categoryId, int page, int size) {
        int offset = page * size;
        return quizRepository.findByCategory(categoryId, offset, size);
    }

    public List<Quiz> getAllQuizzes(int page, int size) {
        int offset = page * size;
        return quizRepository.findAll(offset, size);
    }

    public boolean updateQuiz(Quiz quiz) {
        validateQuizData(quiz);
        
        return quizRepository.updateQuiz(quiz);
    }

    public boolean deleteQuiz(Long quizId) {
        return quizRepository.deleteQuiz(quizId);
    }

    public boolean addQuestionToQuiz(Long quizId, Long questionId, Long questionNumber) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            return false;
        }
        
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return false;
        }
        
        boolean success = quizQuestionMappingRepository.addQuestionToQuiz(quizId, questionId, questionNumber);
        
        if (success) {
            Quiz quiz = quizOpt.get();
            if (questionNumber > quiz.getLastQuestionNumber()) {
                quizRepository.updateLastQuestionNumber(quizId, questionNumber);
            }
        }
        
        return success;
    }

    public boolean removeQuestionFromQuiz(Long quizId, Long questionId) {
        return quizQuestionMappingRepository.removeQuestionFromQuiz(quizId, questionId);
    }

    public List<Question> getQuizQuestions(Long quizId) {
        List<Long> questionIds = quizQuestionMappingRepository.getQuestionIdsByQuizOrdered(quizId);
        return questionIds.stream()
            .map(questionRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(java.util.stream.Collectors.toList());
    }

    public Optional<Question> getQuizQuestion(Long quizId, Long questionNumber) {
        Optional<Long> questionIdOpt = quizQuestionMappingRepository.getQuestionIdBySequence(quizId, questionNumber);
        if (questionIdOpt.isPresent()) {
            return questionRepository.findById(questionIdOpt.get());
        }
        return Optional.empty();
    }

    public int getQuizQuestionCount(Long quizId) {
        return quizQuestionMappingRepository.getQuestionCount(quizId);
    }

    public boolean isQuizOwner(Long quizId, Long userId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        return quizOpt.isPresent() && quizOpt.get().getCreatorUserId() == userId;
    }

    private void validateQuizData(Quiz quiz) {
        if (quiz.getTestTitle() == null || quiz.getTestTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title is required");
        }
        
        if (quiz.getTestDescription() == null || quiz.getTestDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz description is required");
        }
        
        if (quiz.getTimeLimitMinutes() == null || quiz.getTimeLimitMinutes() <= 0) {
            throw new IllegalArgumentException("Time limit must be positive");
        }
        
        if (quiz.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
    }
} 