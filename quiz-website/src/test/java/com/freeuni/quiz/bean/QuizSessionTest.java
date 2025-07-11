package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuizSessionTest {

    private QuizSession quizSession;

    @BeforeEach
    void setUp() {
        quizSession = new QuizSession();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyQuizSession() {
        assertNotNull(quizSession);
        assertNull(quizSession.getId());
        assertNull(quizSession.getParticipantUserId());
        assertNull(quizSession.getTestId());
        assertNull(quizSession.getCurrentQuestionNum());
        assertNull(quizSession.getTimeAllocated());
        assertNull(quizSession.getSessionStart());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        Long participantUserId = 123L;
        Long testId = 456L;
        Long timeAllocated = 60L;

        QuizSession paramSession = new QuizSession(participantUserId, testId, timeAllocated);

        assertEquals(participantUserId, paramSession.getParticipantUserId());
        assertEquals(testId, paramSession.getTestId());
        assertEquals(timeAllocated, paramSession.getTimeAllocated());
        assertEquals(Long.valueOf(0), paramSession.getCurrentQuestionNum());
        assertNull(paramSession.getId());
        assertNull(paramSession.getSessionStart());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        Long participantUserId = 111L;
        Long testId = 222L;
        Long currentQuestionNum = 3L;
        Long timeAllocated = 90L;
        LocalDateTime sessionStart = LocalDateTime.now();

        QuizSession fullSession = new QuizSession(id, participantUserId, testId, currentQuestionNum, timeAllocated, sessionStart);

        assertEquals(id, fullSession.getId());
        assertEquals(participantUserId, fullSession.getParticipantUserId());
        assertEquals(testId, fullSession.getTestId());
        assertEquals(currentQuestionNum, fullSession.getCurrentQuestionNum());
        assertEquals(timeAllocated, fullSession.getTimeAllocated());
        assertEquals(sessionStart, fullSession.getSessionStart());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        quizSession.setId(expectedId);
        assertEquals(expectedId, quizSession.getId());
    }

    @Test
    void setParticipantUserId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 555L;
        quizSession.setParticipantUserId(expectedId);
        assertEquals(expectedId, quizSession.getParticipantUserId());
    }

    @Test
    void setTestId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        quizSession.setTestId(expectedId);
        assertEquals(expectedId, quizSession.getTestId());
    }

    @Test
    void setCurrentQuestionNum_ValidNum_ShouldSetCorrectly() {
        Long expectedNum = 5L;
        quizSession.setCurrentQuestionNum(expectedNum);
        assertEquals(expectedNum, quizSession.getCurrentQuestionNum());
    }

    @Test
    void setTimeAllocated_ValidTime_ShouldSetCorrectly() {
        Long expectedTime = 120L;
        quizSession.setTimeAllocated(expectedTime);
        assertEquals(expectedTime, quizSession.getTimeAllocated());
    }

    @Test
    void setSessionStart_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedStart = LocalDateTime.now();
        quizSession.setSessionStart(expectedStart);
        assertEquals(expectedStart, quizSession.getSessionStart());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        quizSession.setId(null);
        quizSession.setParticipantUserId(null);
        quizSession.setTestId(null);
        quizSession.setCurrentQuestionNum(null);
        quizSession.setTimeAllocated(null);
        quizSession.setSessionStart(null);

        assertNull(quizSession.getId());
        assertNull(quizSession.getParticipantUserId());
        assertNull(quizSession.getTestId());
        assertNull(quizSession.getCurrentQuestionNum());
        assertNull(quizSession.getTimeAllocated());
        assertNull(quizSession.getSessionStart());
    }
} 