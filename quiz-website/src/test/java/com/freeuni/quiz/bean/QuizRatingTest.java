package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QuizRatingTest {

    private QuizRating rating;

    @BeforeEach
    void setUp() {
        rating = new QuizRating();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyRating() {
        assertNotNull(rating);
        assertNull(rating.getId());
        assertNull(rating.getUserId());
        assertNull(rating.getQuizId());
        assertNull(rating.getRating());
        assertNull(rating.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        Integer userId = 1;
        Long quizId = 2L;
        Integer ratingValue = 5;

        QuizRating paramRating = new QuizRating(userId, quizId, ratingValue);

        assertEquals(userId, paramRating.getUserId());
        assertEquals(quizId, paramRating.getQuizId());
        assertEquals(ratingValue, paramRating.getRating());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 123L;

        rating.setId(expectedId);

        assertEquals(expectedId, rating.getId());
    }

    @Test
    void setUserId_ValidUserId_ShouldSetCorrectly() {
        Integer expectedUserId = 456;

        rating.setUserId(expectedUserId);

        assertEquals(expectedUserId, rating.getUserId());
    }

    @Test
    void setQuizId_ValidQuizId_ShouldSetCorrectly() {
        Long expectedQuizId = 789L;

        rating.setQuizId(expectedQuizId);

        assertEquals(expectedQuizId, rating.getQuizId());
    }

    @Test
    void setRating_ValidRating_ShouldSetCorrectly() {
        Integer expectedRating = 4;

        rating.setRating(expectedRating);

        assertEquals(expectedRating, rating.getRating());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        rating.setCreatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, rating.getCreatedAt());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 1L;
        Integer userId = 2;
        Long quizId = 3L;
        Integer ratingValue = 5;
        Timestamp createdAt = Timestamp.from(Instant.now());

        rating.setId(id);
        rating.setUserId(userId);
        rating.setQuizId(quizId);
        rating.setRating(ratingValue);
        rating.setCreatedAt(createdAt);

        assertEquals(id, rating.getId());
        assertEquals(userId, rating.getUserId());
        assertEquals(quizId, rating.getQuizId());
        assertEquals(ratingValue, rating.getRating());
        assertEquals(createdAt, rating.getCreatedAt());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        rating.setId(null);
        rating.setUserId(null);
        rating.setQuizId(null);
        rating.setRating(null);
        rating.setCreatedAt(null);

        assertNull(rating.getId());
        assertNull(rating.getUserId());
        assertNull(rating.getQuizId());
        assertNull(rating.getRating());
        assertNull(rating.getCreatedAt());
    }

    @Test
    void setFields_PositiveAndNegativeValues_ShouldSetCorrectly() {
        rating.setId(100L);
        assertEquals(100L, rating.getId());

        rating.setUserId(200);
        assertEquals(200, rating.getUserId());

        rating.setQuizId(300L);
        assertEquals(300L, rating.getQuizId());

        rating.setRating(5);
        assertEquals(5, rating.getRating());

        rating.setId(-100L);
        assertEquals(-100L, rating.getId());

        rating.setUserId(-200);
        assertEquals(-200, rating.getUserId());

        rating.setQuizId(-300L);
        assertEquals(-300L, rating.getQuizId());

        rating.setRating(-5);
        assertEquals(-5, rating.getRating());
    }

    @Test
    void setFields_ZeroValues_ShouldSetCorrectly() {
        rating.setId(0L);
        rating.setUserId(0);
        rating.setQuizId(0L);
        rating.setRating(0);

        assertEquals(0L, rating.getId());
        assertEquals(0, rating.getUserId());
        assertEquals(0L, rating.getQuizId());
        assertEquals(0, rating.getRating());
    }

    @Test
    void setFields_ExtremeValues_ShouldSetCorrectly() {
        Long maxLong = Long.MAX_VALUE;
        Integer maxInt = Integer.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;
        Integer minInt = Integer.MIN_VALUE;

        rating.setId(maxLong);
        rating.setUserId(maxInt);
        rating.setQuizId(maxLong);
        rating.setRating(maxInt);

        assertEquals(maxLong, rating.getId());
        assertEquals(maxInt, rating.getUserId());
        assertEquals(maxLong, rating.getQuizId());
        assertEquals(maxInt, rating.getRating());

        rating.setId(minLong);
        rating.setUserId(minInt);
        rating.setQuizId(minLong);
        rating.setRating(minInt);

        assertEquals(minLong, rating.getId());
        assertEquals(minInt, rating.getUserId());
        assertEquals(minLong, rating.getQuizId());
        assertEquals(minInt, rating.getRating());
    }
}