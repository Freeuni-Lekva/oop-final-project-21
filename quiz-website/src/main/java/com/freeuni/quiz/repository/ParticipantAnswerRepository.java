package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.ParticipantAnswer;

import java.util.List;
import java.util.Optional;

public interface ParticipantAnswerRepository {

    boolean saveAnswer(ParticipantAnswer answer);

    Optional<Double> getAnswerScore(Long participantUserId, Long testId, Long questionNumber);

    List<Long> getAnsweredQuestionNumbers(Long participantUserId, Long testId);

    List<ParticipantAnswer> getAllAnswers(Long participantUserId, Long testId);

    boolean deleteAnswer(Long participantUserId, Long testId, Long questionNumber);

    int deleteAllAnswers(Long participantUserId, Long testId);
}
