package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QuizReviewTest {

    private QuizReview review;

    @BeforeEach
    void setUp() {
        review = new QuizReview();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyReview() {
        assertNotNull(review);
        assertNull(review.getId());
        assertNull(review.getUserId());
        assertNull(review.getQuizId());
        assertNull(review.getReviewText());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        Integer userId = 1;
        Long quizId = 2L;
        String reviewText = "Great quiz!";

        QuizReview paramReview = new QuizReview(userId, quizId, reviewText);

        assertEquals(userId, paramReview.getUserId());
        assertEquals(quizId, paramReview.getQuizId());
        assertEquals(reviewText, paramReview.getReviewText());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 123L;

        review.setId(expectedId);

        assertEquals(expectedId, review.getId());
    }

    @Test
    void setUserId_ValidUserId_ShouldSetCorrectly() {
        Integer expectedUserId = 456;

        review.setUserId(expectedUserId);

        assertEquals(expectedUserId, review.getUserId());
    }

    @Test
    void setQuizId_ValidQuizId_ShouldSetCorrectly() {
        Long expectedQuizId = 789L;

        review.setQuizId(expectedQuizId);

        assertEquals(expectedQuizId, review.getQuizId());
    }

    @Test
    void setReviewText_ValidText_ShouldSetCorrectly() {
        String expectedText = "This quiz was very informative!";

        review.setReviewText(expectedText);

        assertEquals(expectedText, review.getReviewText());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        review.setCreatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, review.getCreatedAt());
    }

    @Test
    void setUpdatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        review.setUpdatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, review.getUpdatedAt());
    }


    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 1L;
        Integer userId = 2;
        Long quizId = 3L;
        String reviewText = "Excellent quiz with challenging questions.";
        Timestamp createdAt = Timestamp.from(Instant.now());
        Timestamp updatedAt = Timestamp.from(Instant.now());
        String userName = "JaneDoe";
        String userImageURL = "http://example.com/jane.jpg";

        review.setId(id);
        review.setUserId(userId);
        review.setQuizId(quizId);
        review.setReviewText(reviewText);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(updatedAt);
        assertEquals(id, review.getId());
        assertEquals(userId, review.getUserId());
        assertEquals(quizId, review.getQuizId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        review.setId(null);
        review.setUserId(null);
        review.setQuizId(null);
        review.setReviewText(null);
        review.setCreatedAt(null);
        review.setUpdatedAt(null);

        assertNull(review.getId());
        assertNull(review.getUserId());
        assertNull(review.getQuizId());
        assertNull(review.getReviewText());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }

    @Test
    void setFields_EmptyStrings_ShouldSetEmpty() {
        review.setReviewText("");

        assertEquals("", review.getReviewText());
    }

    @Test
    void setReviewText_LongText_ShouldSetCorrectly() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is a very long review text that should be stored correctly. ");
        }
        String veryLongText = longText.toString();

        review.setReviewText(veryLongText);

        assertEquals(veryLongText, review.getReviewText());
    }

    @Test
    void setFields_SpecialCharacters_ShouldSetCorrectly() {
        String specialText = "Review with special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";
        String specialUserName = "user-name_123!";
        String specialURL = "http://example.com/image?id=123&type=profile";

        review.setReviewText(specialText);

        assertEquals(specialText, review.getReviewText());
    }

    @Test
    void setFields_NonAsciiCharacters_ShouldSetCorrectly() {
        String nonAsciiText = "Review with non-ASCII characters: éñçåüö";
        String nonAsciiUserName = "José_García";
        String nonAsciiURL = "http://example.com/résumé.jpg";

        review.setReviewText(nonAsciiText);

        assertEquals(nonAsciiText, review.getReviewText());
    }

    @Test
    void setFields_SameValueMultipleTimes_ShouldRetainValue() {
        String text = "This is a review.";

        review.setReviewText(text);
        String firstGet = review.getReviewText();
        review.setReviewText(text);
        String secondGet = review.getReviewText();

        assertEquals(text, firstGet);
        assertEquals(text, secondGet);
    }
}