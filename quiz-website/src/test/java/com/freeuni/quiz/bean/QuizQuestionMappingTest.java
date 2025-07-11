package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizQuestionMappingTest {

    private QuizQuestionMapping mapping;

    @BeforeEach
    void setUp() {
        mapping = new QuizQuestionMapping();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyMapping() {
        assertNotNull(mapping);
        assertNull(mapping.getId());
        assertNull(mapping.getQuestionId());
        assertNull(mapping.getQuizId());
        assertNull(mapping.getSequenceOrder());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        Long questionId = 123L;
        Long quizId = 456L;
        Long sequenceOrder = 1L;

        QuizQuestionMapping paramMapping = new QuizQuestionMapping(questionId, quizId, sequenceOrder);

        assertEquals(questionId, paramMapping.getQuestionId());
        assertEquals(quizId, paramMapping.getQuizId());
        assertEquals(sequenceOrder, paramMapping.getSequenceOrder());
        assertNull(paramMapping.getId());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 789L;
        Long questionId = 111L;
        Long quizId = 222L;
        Long sequenceOrder = 3L;

        QuizQuestionMapping fullMapping = new QuizQuestionMapping(id, questionId, quizId, sequenceOrder);

        assertEquals(id, fullMapping.getId());
        assertEquals(questionId, fullMapping.getQuestionId());
        assertEquals(quizId, fullMapping.getQuizId());
        assertEquals(sequenceOrder, fullMapping.getSequenceOrder());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        mapping.setId(expectedId);
        assertEquals(expectedId, mapping.getId());
    }

    @Test
    void setQuestionId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 555L;
        mapping.setQuestionId(expectedId);
        assertEquals(expectedId, mapping.getQuestionId());
    }

    @Test
    void setQuizId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 666L;
        mapping.setQuizId(expectedId);
        assertEquals(expectedId, mapping.getQuizId());
    }

    @Test
    void setSequenceOrder_ValidOrder_ShouldSetCorrectly() {
        Long expectedOrder = 5L;
        mapping.setSequenceOrder(expectedOrder);
        assertEquals(expectedOrder, mapping.getSequenceOrder());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        mapping.setId(null);
        mapping.setQuestionId(null);
        mapping.setQuizId(null);
        mapping.setSequenceOrder(null);

        assertNull(mapping.getId());
        assertNull(mapping.getQuestionId());
        assertNull(mapping.getQuizId());
        assertNull(mapping.getSequenceOrder());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 100L;
        Long questionId = 200L;
        Long quizId = 300L;
        Long sequenceOrder = 4L;

        mapping.setId(id);
        mapping.setQuestionId(questionId);
        mapping.setQuizId(quizId);
        mapping.setSequenceOrder(sequenceOrder);

        assertEquals(id, mapping.getId());
        assertEquals(questionId, mapping.getQuestionId());
        assertEquals(quizId, mapping.getQuizId());
        assertEquals(sequenceOrder, mapping.getSequenceOrder());
    }
} 