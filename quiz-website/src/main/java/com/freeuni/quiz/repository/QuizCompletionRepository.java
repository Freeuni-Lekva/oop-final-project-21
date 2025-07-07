package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.QuizCompletion;

import java.util.List;
import java.util.Optional;

public interface QuizCompletionRepository {

    Long saveCompletion(QuizCompletion completion);

    Optional<QuizCompletion> findById(Long completionId);

    List<QuizCompletion> findByParticipant(Long participantUserId);

    List<QuizCompletion> findByQuiz(Long testId);

    Optional<QuizCompletion> findBestScore(Long participantUserId, Long testId);

    boolean deleteCompletion(Long completionId);
}
