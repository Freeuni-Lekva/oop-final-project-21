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
        // Assert
        assertNotNull(review);
        assertNull(review.getId());
        assertNull(review.getUserId());
        assertNull(review.getQuizId());
        assertNull(review.getReviewText());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
        assertNull(review.getUserName());
        assertNull(review.getUserImageURL());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // Arrange
        Integer userId = 1;
        Long quizId = 2L;
        String reviewText = "Great quiz!";

        // Act
        QuizReview paramReview = new QuizReview(userId, quizId, reviewText);

        // Assert
        assertEquals(userId, paramReview.getUserId());
        assertEquals(quizId, paramReview.getQuizId());
        assertEquals(reviewText, paramReview.getReviewText());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        // Arrange
        Long expectedId = 123L;

        // Act
        review.setId(expectedId);

        // Assert
        assertEquals(expectedId, review.getId());
    }

    @Test
    void setUserId_ValidUserId_ShouldSetCorrectly() {
        // Arrange
        Integer expectedUserId = 456;

        // Act
        review.setUserId(expectedUserId);

        // Assert
        assertEquals(expectedUserId, review.getUserId());
    }

    @Test
    void setQuizId_ValidQuizId_ShouldSetCorrectly() {
        // Arrange
        Long expectedQuizId = 789L;

        // Act
        review.setQuizId(expectedQuizId);

        // Assert
        assertEquals(expectedQuizId, review.getQuizId());
    }

    @Test
    void setReviewText_ValidText_ShouldSetCorrectly() {
        // Arrange
        String expectedText = "This quiz was very informative!";

        // Act
        review.setReviewText(expectedText);

        // Assert
        assertEquals(expectedText, review.getReviewText());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        // Arrange
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        // Act
        review.setCreatedAt(expectedTimestamp);

        // Assert
        assertEquals(expectedTimestamp, review.getCreatedAt());
    }

    @Test
    void setUpdatedAt_ValidTimestamp_ShouldSetCorrectly() {
        // Arrange
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        // Act
        review.setUpdatedAt(expectedTimestamp);

        // Assert
        assertEquals(expectedTimestamp, review.getUpdatedAt());
    }

    @Test
    void setUserName_ValidName_ShouldSetCorrectly() {
        // Arrange
        String expectedName = "JohnDoe";

        // Act
        review.setUserName(expectedName);

        // Assert
        assertEquals(expectedName, review.getUserName());
    }

    @Test
    void setUserImageURL_ValidURL_ShouldSetCorrectly() {
        // Arrange
        String expectedURL = "http://example.com/image.jpg";

        // Act
        review.setUserImageURL(expectedURL);

        // Assert
        assertEquals(expectedURL, review.getUserImageURL());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        Integer userId = 2;
        Long quizId = 3L;
        String reviewText = "Excellent quiz with challenging questions.";
        Timestamp createdAt = Timestamp.from(Instant.now());
        Timestamp updatedAt = Timestamp.from(Instant.now());
        String userName = "JaneDoe";
        String userImageURL = "http://example.com/jane.jpg";

        // Act
        review.setId(id);
        review.setUserId(userId);
        review.setQuizId(quizId);
        review.setReviewText(reviewText);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(updatedAt);
        review.setUserName(userName);
        review.setUserImageURL(userImageURL);

        // Assert
        assertEquals(id, review.getId());
        assertEquals(userId, review.getUserId());
        assertEquals(quizId, review.getQuizId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
        assertEquals(userName, review.getUserName());
        assertEquals(userImageURL, review.getUserImageURL());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        // Act
        review.setId(null);
        review.setUserId(null);
        review.setQuizId(null);
        review.setReviewText(null);
        review.setCreatedAt(null);
        review.setUpdatedAt(null);
        review.setUserName(null);
        review.setUserImageURL(null);

        // Assert
        assertNull(review.getId());
        assertNull(review.getUserId());
        assertNull(review.getQuizId());
        assertNull(review.getReviewText());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
        assertNull(review.getUserName());
        assertNull(review.getUserImageURL());
    }

    @Test
    void setFields_EmptyStrings_ShouldSetEmpty() {
        // Act
        review.setReviewText("");
        review.setUserName("");
        review.setUserImageURL("");

        // Assert
        assertEquals("", review.getReviewText());
        assertEquals("", review.getUserName());
        assertEquals("", review.getUserImageURL());
    }

    @Test
    void setReviewText_LongText_ShouldSetCorrectly() {
        // Arrange
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is a very long review text that should be stored correctly. ");
        }
        String veryLongText = longText.toString();

        // Act
        review.setReviewText(veryLongText);

        // Assert
        assertEquals(veryLongText, review.getReviewText());
    }

    @Test
    void setFields_SpecialCharacters_ShouldSetCorrectly() {
        // Arrange
        String specialText = "Review with special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";
        String specialUserName = "user-name_123!";
        String specialURL = "http://example.com/image?id=123&type=profile";

        // Act
        review.setReviewText(specialText);
        review.setUserName(specialUserName);
        review.setUserImageURL(specialURL);

        // Assert
        assertEquals(specialText, review.getReviewText());
        assertEquals(specialUserName, review.getUserName());
        assertEquals(specialURL, review.getUserImageURL());
    }

    @Test
    void setFields_NonAsciiCharacters_ShouldSetCorrectly() {
        // Arrange
        String nonAsciiText = "Review with non-ASCII characters: éñçåüö";
        String nonAsciiUserName = "José_García";
        String nonAsciiURL = "http://example.com/résumé.jpg";

        // Act
        review.setReviewText(nonAsciiText);
        review.setUserName(nonAsciiUserName);
        review.setUserImageURL(nonAsciiURL);

        // Assert
        assertEquals(nonAsciiText, review.getReviewText());
        assertEquals(nonAsciiUserName, review.getUserName());
        assertEquals(nonAsciiURL, review.getUserImageURL());
    }

    @Test
    void setFields_SameValueMultipleTimes_ShouldRetainValue() {
        // Arrange
        String text = "This is a review.";

        // Act
        review.setReviewText(text);
        String firstGet = review.getReviewText();
        review.setReviewText(text);
        String secondGet = review.getReviewText();

        // Assert
        assertEquals(text, firstGet);
        assertEquals(text, secondGet);
    }
}