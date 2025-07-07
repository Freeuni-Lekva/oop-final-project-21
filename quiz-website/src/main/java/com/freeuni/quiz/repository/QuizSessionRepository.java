package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.QuizSession;

import java.util.Optional;

public interface QuizSessionRepository {
    boolean createSession(QuizSession session);

    Optional<QuizSession> findByParticipant(Long participantUserId);

    boolean updateCurrentQuestion(Long participantUserId, Long questionNumber);

    boolean deleteSession(Long participantUserId);

    boolean hasActiveSession(Long participantUserId);
}
