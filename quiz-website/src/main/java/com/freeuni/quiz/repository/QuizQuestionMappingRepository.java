package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.QuizQuestionMapping;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionMappingRepository {

    boolean addQuestionToQuiz(Long questionId, Long quizId, Long sequenceOrder);

    boolean removeQuestionFromQuiz(Long questionId, Long quizId);

    List<Long> getQuestionIdsByQuizOrdered(Long quizId);

    Optional<Long> getQuestionIdBySequence(Long quizId, Long sequenceOrder);

    Long getNextSequenceOrder(Long quizId, Long currentSequence);

    int getQuestionCount(Long quizId);

    boolean updateQuestionSequence(Long quizId, Long questionId, Long newSequence);

    List<QuizQuestionMapping> findByQuizId(Long quizId);
}
