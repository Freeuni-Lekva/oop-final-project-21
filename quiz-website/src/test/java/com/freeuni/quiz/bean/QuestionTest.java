package com.freeuni.quiz.bean;

import com.freeuni.quiz.quiz_util.AbstractQuestionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    private Question question;
    @Mock
    private AbstractQuestionHandler mockHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        question = new Question();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyQuestion() {
        assertNotNull(question);
        assertNull(question.getId());
        assertEquals(0, question.getAuthorUserId());
        assertNull(question.getCategoryId());
        assertNull(question.getCreatedAt());
        assertNull(question.getQuestionHandler());
        assertNull(question.getQuestionTitle());
        assertNull(question.getQuestionType());
        assertEquals(10.0, question.getPoints());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        int authorUserId = 123;
        Long categoryId = 456L;
        String questionTitle = "Test Question";
        QuestionType questionType = QuestionType.TEXT;

        Question paramQuestion = new Question(authorUserId, categoryId, mockHandler, questionTitle, questionType);

        assertEquals(authorUserId, paramQuestion.getAuthorUserId());
        assertEquals(categoryId, paramQuestion.getCategoryId());
        assertEquals(mockHandler, paramQuestion.getQuestionHandler());
        assertEquals(questionTitle, paramQuestion.getQuestionTitle());
        assertEquals(questionType, paramQuestion.getQuestionType());
        assertNull(paramQuestion.getId());
        assertNull(paramQuestion.getCreatedAt());
        assertEquals(10.0, paramQuestion.getPoints());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        int authorUserId = 111;
        Long categoryId = 222L;
        LocalDateTime createdAt = LocalDateTime.now();
        String questionTitle = "Full Question";
        QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

        Question fullQuestion = new Question(id, authorUserId, categoryId, createdAt, mockHandler, questionTitle, questionType);

        assertEquals(id, fullQuestion.getId());
        assertEquals(authorUserId, fullQuestion.getAuthorUserId());
        assertEquals(categoryId, fullQuestion.getCategoryId());
        assertEquals(createdAt, fullQuestion.getCreatedAt());
        assertEquals(mockHandler, fullQuestion.getQuestionHandler());
        assertEquals(questionTitle, fullQuestion.getQuestionTitle());
        assertEquals(questionType, fullQuestion.getQuestionType());
        assertEquals(10.0, fullQuestion.getPoints());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        question.setId(expectedId);
        assertEquals(expectedId, question.getId());
    }

    @Test
    void setAuthorUserId_ValidId_ShouldSetCorrectly() {
        int expectedId = 555;
        question.setAuthorUserId(expectedId);
        assertEquals(expectedId, question.getAuthorUserId());
    }

    @Test
    void setCategoryId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        question.setCategoryId(expectedId);
        assertEquals(expectedId, question.getCategoryId());
    }

    @Test
    void setCreatedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedDateTime = LocalDateTime.now();
        question.setCreatedAt(expectedDateTime);
        assertEquals(expectedDateTime, question.getCreatedAt());
    }

    @Test
    void setQuestionHandler_ValidHandler_ShouldSetCorrectly() {
        question.setQuestionHandler(mockHandler);
        assertEquals(mockHandler, question.getQuestionHandler());
    }

    @Test
    void setQuestionTitle_ValidTitle_ShouldSetCorrectly() {
        String expectedTitle = "New Question Title";
        question.setQuestionTitle(expectedTitle);
        assertEquals(expectedTitle, question.getQuestionTitle());
    }

    @Test
    void setQuestionType_ValidType_ShouldSetCorrectly() {
        QuestionType expectedType = QuestionType.IMAGE;
        question.setQuestionType(expectedType);
        assertEquals(expectedType, question.getQuestionType());
    }

    @Test
    void setPoints_ValidPoints_ShouldSetCorrectly() {
        Double expectedPoints = 15.5;
        question.setPoints(expectedPoints);
        assertEquals(expectedPoints, question.getPoints());
    }

    @Test
    void getPoints_WhenNull_ShouldReturnDefaultValue() {
        question.setPoints(null);
        assertEquals(10.0, question.getPoints());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        question.setId(null);
        question.setCategoryId(null);
        question.setCreatedAt(null);
        question.setQuestionHandler(null);
        question.setQuestionTitle(null);
        question.setQuestionType(null);
        question.setPoints(null);

        assertNull(question.getId());
        assertNull(question.getCategoryId());
        assertNull(question.getCreatedAt());
        assertNull(question.getQuestionHandler());
        assertNull(question.getQuestionTitle());
        assertNull(question.getQuestionType());
        assertEquals(10.0, question.getPoints());
    }
} 