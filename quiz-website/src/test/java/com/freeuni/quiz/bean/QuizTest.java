package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyQuiz() {
        assertNotNull(quiz);
        assertNull(quiz.getId());
        assertEquals(0, quiz.getCreatorUserId());
        assertNull(quiz.getCategoryId());
        assertNull(quiz.getLastQuestionNumber());
        assertNull(quiz.getCreatedAt());
        assertNull(quiz.getTestTitle());
        assertNull(quiz.getTestDescription());
        assertNull(quiz.getTimeLimitMinutes());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        int creatorUserId = 123;
        Long categoryId = 456L;
        String testTitle = "Test Quiz";
        String testDescription = "This is a test quiz";
        Long timeLimitMinutes = 60L;

        Quiz paramQuiz = new Quiz(creatorUserId, categoryId, testTitle, testDescription, timeLimitMinutes);

        assertEquals(creatorUserId, paramQuiz.getCreatorUserId());
        assertEquals(categoryId, paramQuiz.getCategoryId());
        assertEquals(testTitle, paramQuiz.getTestTitle());
        assertEquals(testDescription, paramQuiz.getTestDescription());
        assertEquals(timeLimitMinutes, paramQuiz.getTimeLimitMinutes());
        assertEquals(Long.valueOf(0), paramQuiz.getLastQuestionNumber());
        assertNull(paramQuiz.getId());
        assertNull(paramQuiz.getCreatedAt());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        int creatorUserId = 111;
        Long categoryId = 222L;
        Long lastQuestionNumber = 5L;
        LocalDateTime createdAt = LocalDateTime.now();
        String testTitle = "Full Quiz";
        String testDescription = "This is a full quiz";
        Long timeLimitMinutes = 90L;

        Quiz fullQuiz = new Quiz(id, creatorUserId, categoryId, lastQuestionNumber, createdAt, testTitle, testDescription, timeLimitMinutes);

        assertEquals(id, fullQuiz.getId());
        assertEquals(creatorUserId, fullQuiz.getCreatorUserId());
        assertEquals(categoryId, fullQuiz.getCategoryId());
        assertEquals(lastQuestionNumber, fullQuiz.getLastQuestionNumber());
        assertEquals(createdAt, fullQuiz.getCreatedAt());
        assertEquals(testTitle, fullQuiz.getTestTitle());
        assertEquals(testDescription, fullQuiz.getTestDescription());
        assertEquals(timeLimitMinutes, fullQuiz.getTimeLimitMinutes());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        quiz.setId(expectedId);
        assertEquals(expectedId, quiz.getId());
    }

    @Test
    void setCreatorUserId_ValidId_ShouldSetCorrectly() {
        int expectedId = 555;
        quiz.setCreatorUserId(expectedId);
        assertEquals(expectedId, quiz.getCreatorUserId());
    }

    @Test
    void setCategoryId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        quiz.setCategoryId(expectedId);
        assertEquals(expectedId, quiz.getCategoryId());
    }

    @Test
    void setLastQuestionNumber_ValidNumber_ShouldSetCorrectly() {
        Long expectedNumber = 10L;
        quiz.setLastQuestionNumber(expectedNumber);
        assertEquals(expectedNumber, quiz.getLastQuestionNumber());
    }

    @Test
    void setCreatedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedDateTime = LocalDateTime.now();
        quiz.setCreatedAt(expectedDateTime);
        assertEquals(expectedDateTime, quiz.getCreatedAt());
    }

    @Test
    void setTestTitle_ValidTitle_ShouldSetCorrectly() {
        String expectedTitle = "New Quiz Title";
        quiz.setTestTitle(expectedTitle);
        assertEquals(expectedTitle, quiz.getTestTitle());
    }

    @Test
    void setTestDescription_ValidDescription_ShouldSetCorrectly() {
        String expectedDescription = "New quiz description";
        quiz.setTestDescription(expectedDescription);
        assertEquals(expectedDescription, quiz.getTestDescription());
    }

    @Test
    void setTimeLimitMinutes_ValidTime_ShouldSetCorrectly() {
        Long expectedTime = 120L;
        quiz.setTimeLimitMinutes(expectedTime);
        assertEquals(expectedTime, quiz.getTimeLimitMinutes());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        quiz.setId(null);
        quiz.setCategoryId(null);
        quiz.setLastQuestionNumber(null);
        quiz.setCreatedAt(null);
        quiz.setTestTitle(null);
        quiz.setTestDescription(null);
        quiz.setTimeLimitMinutes(null);

        assertNull(quiz.getId());
        assertNull(quiz.getCategoryId());
        assertNull(quiz.getLastQuestionNumber());
        assertNull(quiz.getCreatedAt());
        assertNull(quiz.getTestTitle());
        assertNull(quiz.getTestDescription());
        assertNull(quiz.getTimeLimitMinutes());
    }
} 