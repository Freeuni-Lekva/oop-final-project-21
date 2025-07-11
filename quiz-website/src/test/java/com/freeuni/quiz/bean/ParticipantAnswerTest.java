package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantAnswerTest {

    private ParticipantAnswer participantAnswer;

    @BeforeEach
    void setUp() {
        participantAnswer = new ParticipantAnswer();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyParticipantAnswer() {
        assertNotNull(participantAnswer);
        assertNull(participantAnswer.getId());
        assertNull(participantAnswer.getParticipantUserId());
        assertNull(participantAnswer.getTestId());
        assertNull(participantAnswer.getQuestionNumber());
        assertNull(participantAnswer.getPointsEarned());
        assertNull(participantAnswer.getTimeSpentSeconds());
        assertNull(participantAnswer.getAnswerText());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        Long participantUserId = 123L;
        Long testId = 456L;
        Long questionNumber = 1L;
        Double pointsEarned = 5.5;
        Integer timeSpentSeconds = 30;
        String answerText = "Test answer";

        ParticipantAnswer paramAnswer = new ParticipantAnswer(participantUserId, testId, questionNumber, pointsEarned, timeSpentSeconds, answerText);

        assertEquals(participantUserId, paramAnswer.getParticipantUserId());
        assertEquals(testId, paramAnswer.getTestId());
        assertEquals(questionNumber, paramAnswer.getQuestionNumber());
        assertEquals(pointsEarned, paramAnswer.getPointsEarned());
        assertEquals(timeSpentSeconds, paramAnswer.getTimeSpentSeconds());
        assertEquals(answerText, paramAnswer.getAnswerText());
        assertNull(paramAnswer.getId());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        Long participantUserId = 111L;
        Long testId = 222L;
        Long questionNumber = 3L;
        Double pointsEarned = 8.0;
        Integer timeSpentSeconds = 45;
        String answerText = "Full answer";

        ParticipantAnswer fullAnswer = new ParticipantAnswer(id, participantUserId, testId, questionNumber, pointsEarned, timeSpentSeconds, answerText);

        assertEquals(id, fullAnswer.getId());
        assertEquals(participantUserId, fullAnswer.getParticipantUserId());
        assertEquals(testId, fullAnswer.getTestId());
        assertEquals(questionNumber, fullAnswer.getQuestionNumber());
        assertEquals(pointsEarned, fullAnswer.getPointsEarned());
        assertEquals(timeSpentSeconds, fullAnswer.getTimeSpentSeconds());
        assertEquals(answerText, fullAnswer.getAnswerText());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        participantAnswer.setId(expectedId);
        assertEquals(expectedId, participantAnswer.getId());
    }

    @Test
    void setParticipantUserId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 555L;
        participantAnswer.setParticipantUserId(expectedId);
        assertEquals(expectedId, participantAnswer.getParticipantUserId());
    }

    @Test
    void setTestId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        participantAnswer.setTestId(expectedId);
        assertEquals(expectedId, participantAnswer.getTestId());
    }

    @Test
    void setQuestionNumber_ValidNumber_ShouldSetCorrectly() {
        Long expectedNumber = 5L;
        participantAnswer.setQuestionNumber(expectedNumber);
        assertEquals(expectedNumber, participantAnswer.getQuestionNumber());
    }

    @Test
    void setPointsEarned_ValidPoints_ShouldSetCorrectly() {
        Double expectedPoints = 7.5;
        participantAnswer.setPointsEarned(expectedPoints);
        assertEquals(expectedPoints, participantAnswer.getPointsEarned());
    }

    @Test
    void setTimeSpentSeconds_ValidTime_ShouldSetCorrectly() {
        Integer expectedTime = 120;
        participantAnswer.setTimeSpentSeconds(expectedTime);
        assertEquals(expectedTime, participantAnswer.getTimeSpentSeconds());
    }

    @Test
    void setAnswerText_ValidText_ShouldSetCorrectly() {
        String expectedText = "My answer";
        participantAnswer.setAnswerText(expectedText);
        assertEquals(expectedText, participantAnswer.getAnswerText());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        participantAnswer.setId(null);
        participantAnswer.setParticipantUserId(null);
        participantAnswer.setTestId(null);
        participantAnswer.setQuestionNumber(null);
        participantAnswer.setPointsEarned(null);
        participantAnswer.setTimeSpentSeconds(null);
        participantAnswer.setAnswerText(null);

        assertNull(participantAnswer.getId());
        assertNull(participantAnswer.getParticipantUserId());
        assertNull(participantAnswer.getTestId());
        assertNull(participantAnswer.getQuestionNumber());
        assertNull(participantAnswer.getPointsEarned());
        assertNull(participantAnswer.getTimeSpentSeconds());
        assertNull(participantAnswer.getAnswerText());
    }
} 