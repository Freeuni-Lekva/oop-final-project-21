package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.QuizCompletion;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuizCompletionRepository {

    Long saveCompletion(QuizCompletion completion);

    Optional<QuizCompletion> findById(Long completionId);

    List<QuizCompletion> findByQuiz(Long testId);

    Optional<QuizCompletion> findFastestTime(Long participantUserId, Long testId);

    int getCompletionCountByQuiz(Long testId);

    Double getAverageScoreByQuiz(Long testId);

    Map<Long, Integer> getCompletionCountsByQuizzes(List<Long> testIds);

    Map<Long, Double> getAverageScoresByQuizzes(List<Long> testIds);
}
