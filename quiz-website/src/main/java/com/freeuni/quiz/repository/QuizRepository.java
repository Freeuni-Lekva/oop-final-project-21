package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    Long saveQuiz(Quiz quiz);

    Optional<Quiz> findById(Long quizId);

    List<Quiz> findByCreator(Long creatorUserId, int offset, int limit);

    List<Quiz> findByCategory(Long categoryId, int offset, int limit);

    List<Quiz> findAll(int offset, int limit);

    boolean updateQuiz(Quiz quiz);

    boolean updateLastQuestionNumber(Long quizId, Long questionNumber);

    boolean deleteQuiz(Long quizId);
}
