package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuizCompletionTest {

    private QuizCompletion quizCompletion;

    @BeforeEach
    void setUp() {
        quizCompletion = new QuizCompletion();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyQuizCompletion() {
        assertNotNull(quizCompletion);
        assertNull(quizCompletion.getId());
        assertNull(quizCompletion.getParticipantUserId());
        assertNull(quizCompletion.getTestId());
        assertNull(quizCompletion.getFinalScore());
        assertNull(quizCompletion.getTotalPossible());
        assertNull(quizCompletion.getCompletionPercentage());
        assertNull(quizCompletion.getStartedAt());
        assertNull(quizCompletion.getFinishedAt());
        assertNull(quizCompletion.getTotalTimeMinutes());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFieldsAndCalculatePercentage() {
        Long participantUserId = 123L;
        Long testId = 456L;
        Double finalScore = 80.0;
        Double totalPossible = 100.0;
        LocalDateTime startedAt = LocalDateTime.now();
        Integer totalTimeMinutes = 30;

        QuizCompletion paramCompletion = new QuizCompletion(participantUserId, testId, finalScore, totalPossible, startedAt, totalTimeMinutes);

        assertEquals(participantUserId, paramCompletion.getParticipantUserId());
        assertEquals(testId, paramCompletion.getTestId());
        assertEquals(finalScore, paramCompletion.getFinalScore());
        assertEquals(totalPossible, paramCompletion.getTotalPossible());
        assertEquals(BigDecimal.valueOf(80.0), paramCompletion.getCompletionPercentage());
        assertEquals(startedAt, paramCompletion.getStartedAt());
        assertEquals(totalTimeMinutes, paramCompletion.getTotalTimeMinutes());
        assertNull(paramCompletion.getId());
        assertNull(paramCompletion.getFinishedAt());
    }

    @Test
    void parameterizedConstructor_WithZeroTotalPossible_ShouldSetPercentageToZero() {
        Long participantUserId = 123L;
        Long testId = 456L;
        Double finalScore = 80.0;
        Double totalPossible = 0.0;
        LocalDateTime startedAt = LocalDateTime.now();
        Integer totalTimeMinutes = 30;

        QuizCompletion paramCompletion = new QuizCompletion(participantUserId, testId, finalScore, totalPossible, startedAt, totalTimeMinutes);

        assertEquals(BigDecimal.ZERO, paramCompletion.getCompletionPercentage());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        Long participantUserId = 111L;
        Long testId = 222L;
        Double finalScore = 90.0;
        Double totalPossible = 100.0;
        BigDecimal completionPercentage = BigDecimal.valueOf(90.0);
        LocalDateTime startedAt = LocalDateTime.now().minusMinutes(60);
        LocalDateTime finishedAt = LocalDateTime.now();
        Integer totalTimeMinutes = 45;

        QuizCompletion fullCompletion = new QuizCompletion(id, participantUserId, testId, finalScore, totalPossible, 
                completionPercentage, startedAt, finishedAt, totalTimeMinutes);

        assertEquals(id, fullCompletion.getId());
        assertEquals(participantUserId, fullCompletion.getParticipantUserId());
        assertEquals(testId, fullCompletion.getTestId());
        assertEquals(finalScore, fullCompletion.getFinalScore());
        assertEquals(totalPossible, fullCompletion.getTotalPossible());
        assertEquals(completionPercentage, fullCompletion.getCompletionPercentage());
        assertEquals(startedAt, fullCompletion.getStartedAt());
        assertEquals(finishedAt, fullCompletion.getFinishedAt());
        assertEquals(totalTimeMinutes, fullCompletion.getTotalTimeMinutes());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        quizCompletion.setId(expectedId);
        assertEquals(expectedId, quizCompletion.getId());
    }

    @Test
    void setParticipantUserId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 555L;
        quizCompletion.setParticipantUserId(expectedId);
        assertEquals(expectedId, quizCompletion.getParticipantUserId());
    }

    @Test
    void setTestId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        quizCompletion.setTestId(expectedId);
        assertEquals(expectedId, quizCompletion.getTestId());
    }

    @Test
    void setFinalScore_ValidScore_ShouldSetCorrectly() {
        Double expectedScore = 95.5;
        quizCompletion.setFinalScore(expectedScore);
        assertEquals(expectedScore, quizCompletion.getFinalScore());
    }

    @Test
    void setTotalPossible_ValidTotal_ShouldSetCorrectly() {
        Double expectedTotal = 200.0;
        quizCompletion.setTotalPossible(expectedTotal);
        assertEquals(expectedTotal, quizCompletion.getTotalPossible());
    }

    @Test
    void setCompletionPercentage_ValidPercentage_ShouldSetCorrectly() {
        BigDecimal expectedPercentage = BigDecimal.valueOf(85.7);
        quizCompletion.setCompletionPercentage(expectedPercentage);
        assertEquals(expectedPercentage, quizCompletion.getCompletionPercentage());
    }

    @Test
    void setStartedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedStarted = LocalDateTime.now();
        quizCompletion.setStartedAt(expectedStarted);
        assertEquals(expectedStarted, quizCompletion.getStartedAt());
    }

    @Test
    void setFinishedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedFinished = LocalDateTime.now();
        quizCompletion.setFinishedAt(expectedFinished);
        assertEquals(expectedFinished, quizCompletion.getFinishedAt());
    }

    @Test
    void setTotalTimeMinutes_ValidTime_ShouldSetCorrectly() {
        Integer expectedTime = 120;
        quizCompletion.setTotalTimeMinutes(expectedTime);
        assertEquals(expectedTime, quizCompletion.getTotalTimeMinutes());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        quizCompletion.setId(null);
        quizCompletion.setParticipantUserId(null);
        quizCompletion.setTestId(null);
        quizCompletion.setFinalScore(null);
        quizCompletion.setTotalPossible(null);
        quizCompletion.setCompletionPercentage(null);
        quizCompletion.setStartedAt(null);
        quizCompletion.setFinishedAt(null);
        quizCompletion.setTotalTimeMinutes(null);

        assertNull(quizCompletion.getId());
        assertNull(quizCompletion.getParticipantUserId());
        assertNull(quizCompletion.getTestId());
        assertNull(quizCompletion.getFinalScore());
        assertNull(quizCompletion.getTotalPossible());
        assertNull(quizCompletion.getCompletionPercentage());
        assertNull(quizCompletion.getStartedAt());
        assertNull(quizCompletion.getFinishedAt());
        assertNull(quizCompletion.getTotalTimeMinutes());
    }
} 