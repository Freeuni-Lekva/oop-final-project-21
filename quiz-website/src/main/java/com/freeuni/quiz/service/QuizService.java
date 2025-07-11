package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.DTO.PopularQuizDTO;
import com.freeuni.quiz.DAO.QuestionDAO;
import com.freeuni.quiz.DAO.QuizCompletionDAO;
import com.freeuni.quiz.DAO.QuizQuestionMappingDAO;
import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;
import com.freeuni.quiz.DAO.impl.QuestionDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizQuestionMappingDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizCompletionDAOImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuizService {
    private final QuizDAO quizRepository;
    private final QuestionDAO questionDAO;
    private final QuizQuestionMappingDAO quizQuestionMappingRepository;
    private final QuizCompletionDAO quizCompletionRepository;

    public QuizService(DataSource dataSource) {
        this.quizRepository = new QuizDAOImpl(dataSource);
        this.questionDAO = new QuestionDAOImpl(dataSource);
        this.quizQuestionMappingRepository = new QuizQuestionMappingDAOImpl(dataSource);
        this.quizCompletionRepository = new QuizCompletionDAOImpl(dataSource);
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
        
        Optional<Question> questionOpt = questionDAO.findById(questionId);
        if (questionOpt.isEmpty()) {
            return false;
        }
        
        boolean success = quizQuestionMappingRepository.addQuestionToQuiz(questionId, quizId, questionNumber);
        
        if (success) {
            Quiz quiz = quizOpt.get();
            if (questionNumber > quiz.getLastQuestionNumber()) {
                quizRepository.updateLastQuestionNumber(quizId, questionNumber);
            }
        }
        
        return success;
    }

    public boolean removeQuestionFromQuiz(Long quizId, Long questionId) {
        return quizQuestionMappingRepository.removeQuestionFromQuiz(questionId, quizId);
    }

    public List<Question> getQuizQuestions(Long quizId) {
        List<Long> questionIds = quizQuestionMappingRepository.getQuestionIdsByQuizOrdered(quizId);
        return questionIds.stream()
            .map(questionDAO::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public int getQuizQuestionCount(Long quizId) {
        return quizQuestionMappingRepository.getQuestionCount(quizId);
    }

    public boolean isQuizOwner(Long quizId, Long userId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        return quizOpt.isPresent() && quizOpt.get().getCreatorUserId() == userId;
    }

    public Map<Integer, Integer> getCompletionCountsForQuizzes(List<Quiz> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            return new HashMap<>();
        }
        
        List<Long> quizIds = quizzes.stream()
            .map(Quiz::getId)
            .collect(Collectors.toList());
        
        Map<Long, Integer> longResults = quizCompletionRepository.getCompletionCountsByQuizzes(quizIds);
        
        Map<Integer, Integer> results = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : longResults.entrySet()) {
            results.put(entry.getKey().intValue(), entry.getValue());
        }
        
        return results;
    }

    public Map<Integer, Double> getAverageScoresForQuizzes(List<Quiz> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            return new HashMap<>();
        }
        
        List<Long> quizIds = quizzes.stream()
            .map(Quiz::getId)
            .collect(Collectors.toList());
        
        Map<Long, Double> longResults = quizCompletionRepository.getAverageScoresByQuizzes(quizIds);
        
        Map<Integer, Double> results = new HashMap<>();
        for (Map.Entry<Long, Double> entry : longResults.entrySet()) {
            results.put(entry.getKey().intValue(), entry.getValue());
        }
        
        return results;
    }

    public int getCompletionCountForQuiz(Long quizId) {
        return quizCompletionRepository.getCompletionCountByQuiz(quizId);
    }

    public Double getAverageScoreForQuiz(Long quizId) {
        return quizCompletionRepository.getAverageScoreByQuiz(quizId);
    }

    public List<PopularQuizDTO> getPopularQuizzesWithCompletionCount(int limit) {
        return quizRepository.findPopularQuizzesWithCompletionCount(limit);
    }

    public List<Quiz> getRecentlyCreatedQuizzes(int limit) {
        return quizRepository.findRecentlyCreatedQuizzes(limit);
    }

    public List<Quiz> getRecentlyCreatedByUser(Long userId, int limit) {
        return quizRepository.findRecentlyCreatedByUser(userId, limit);
    }

    public List<QuizCompletion> getRecentCompletionsByUser(Long userId, int limit) {
        return quizCompletionRepository.findRecentCompletionsByUser(userId, limit);
    }

    public List<QuizCompletion> getRecentCompletionsByFriends(Long userId, int limit) {
        return quizCompletionRepository.findRecentCompletionsByFriends(userId, limit);
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