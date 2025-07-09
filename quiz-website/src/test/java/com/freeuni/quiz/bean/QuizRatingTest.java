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
        // Assert
        assertNotNull(rating);
        assertNull(rating.getId());
        assertNull(rating.getUserId());
        assertNull(rating.getQuizId());
        assertNull(rating.getRating());
        assertNull(rating.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // Arrange
        Integer userId = 1;
        Long quizId = 2L;
        Integer ratingValue = 5;

        // Act
        QuizRating paramRating = new QuizRating(userId, quizId, ratingValue);

        // Assert
        assertEquals(userId, paramRating.getUserId());
        assertEquals(quizId, paramRating.getQuizId());
        assertEquals(ratingValue, paramRating.getRating());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        // Arrange
        Long expectedId = 123L;

        // Act
        rating.setId(expectedId);

        // Assert
        assertEquals(expectedId, rating.getId());
    }

    @Test
    void setUserId_ValidUserId_ShouldSetCorrectly() {
        // Arrange
        Integer expectedUserId = 456;

        // Act
        rating.setUserId(expectedUserId);

        // Assert
        assertEquals(expectedUserId, rating.getUserId());
    }

    @Test
    void setQuizId_ValidQuizId_ShouldSetCorrectly() {
        // Arrange
        Long expectedQuizId = 789L;

        // Act
        rating.setQuizId(expectedQuizId);

        // Assert
        assertEquals(expectedQuizId, rating.getQuizId());
    }

    @Test
    void setRating_ValidRating_ShouldSetCorrectly() {
        // Arrange
        Integer expectedRating = 4;

        // Act
        rating.setRating(expectedRating);

        // Assert
        assertEquals(expectedRating, rating.getRating());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        // Arrange
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        // Act
        rating.setCreatedAt(expectedTimestamp);

        // Assert
        assertEquals(expectedTimestamp, rating.getCreatedAt());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        Integer userId = 2;
        Long quizId = 3L;
        Integer ratingValue = 5;
        Timestamp createdAt = Timestamp.from(Instant.now());

        // Act
        rating.setId(id);
        rating.setUserId(userId);
        rating.setQuizId(quizId);
        rating.setRating(ratingValue);
        rating.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, rating.getId());
        assertEquals(userId, rating.getUserId());
        assertEquals(quizId, rating.getQuizId());
        assertEquals(ratingValue, rating.getRating());
        assertEquals(createdAt, rating.getCreatedAt());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        // Act
        rating.setId(null);
        rating.setUserId(null);
        rating.setQuizId(null);
        rating.setRating(null);
        rating.setCreatedAt(null);

        // Assert
        assertNull(rating.getId());
        assertNull(rating.getUserId());
        assertNull(rating.getQuizId());
        assertNull(rating.getRating());
        assertNull(rating.getCreatedAt());
    }

    @Test
    void setFields_PositiveAndNegativeValues_ShouldSetCorrectly() {
        // Act & Assert - Positive values
        rating.setId(100L);
        assertEquals(100L, rating.getId());

        rating.setUserId(200);
        assertEquals(200, rating.getUserId());

        rating.setQuizId(300L);
        assertEquals(300L, rating.getQuizId());

        rating.setRating(5);
        assertEquals(5, rating.getRating());

        // Act & Assert - Negative values (for IDs, which might not be valid in production)
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
        // Act
        rating.setId(0L);
        rating.setUserId(0);
        rating.setQuizId(0L);
        rating.setRating(0);

        // Assert
        assertEquals(0L, rating.getId());
        assertEquals(0, rating.getUserId());
        assertEquals(0L, rating.getQuizId());
        assertEquals(0, rating.getRating());
    }

    @Test
    void setFields_ExtremeValues_ShouldSetCorrectly() {
        // Arrange
        Long maxLong = Long.MAX_VALUE;
        Integer maxInt = Integer.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;
        Integer minInt = Integer.MIN_VALUE;

        // Act
        rating.setId(maxLong);
        rating.setUserId(maxInt);
        rating.setQuizId(maxLong);
        rating.setRating(maxInt);

        // Assert
        assertEquals(maxLong, rating.getId());
        assertEquals(maxInt, rating.getUserId());
        assertEquals(maxLong, rating.getQuizId());
        assertEquals(maxInt, rating.getRating());

        // Act
        rating.setId(minLong);
        rating.setUserId(minInt);
        rating.setQuizId(minLong);
        rating.setRating(minInt);

        // Assert
        assertEquals(minLong, rating.getId());
        assertEquals(minInt, rating.getUserId());
        assertEquals(minLong, rating.getQuizId());
        assertEquals(minInt, rating.getRating());
    }
}