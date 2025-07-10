package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QuizChallengeTest {

    private QuizChallenge challenge;

    @BeforeEach
    void setUp() {
        challenge = new QuizChallenge();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyChallenge() {
        assertNotNull(challenge);
        assertNull(challenge.getId());
        assertNull(challenge.getMessage());
        assertNull(challenge.getCreatedAt());
        assertNull(challenge.getStatus());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        int challengerId = 1;
        int challengedId = 2;
        Long quizId = 3L;
        String message = "Challenge you to this quiz!";

        QuizChallenge paramChallenge = new QuizChallenge(challengerId, challengedId, quizId, message);

        assertEquals(challengerId, paramChallenge.getChallengerUserId());
        assertEquals(challengedId, paramChallenge.getChallengedUserId());
        assertEquals(quizId, paramChallenge.getQuizId());
        assertEquals(message, paramChallenge.getMessage());
        assertEquals("PENDING", paramChallenge.getStatus());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 123L;

        challenge.setId(expectedId);

        assertEquals(expectedId, challenge.getId());
    }

    @Test
    void setChallengerUserId_ValidUserId_ShouldSetCorrectly() {
        int expectedUserId = 456;

        challenge.setChallengerUserId(expectedUserId);

        assertEquals(expectedUserId, challenge.getChallengerUserId());
    }

    @Test
    void setChallengedUserId_ValidUserId_ShouldSetCorrectly() {
        int expectedUserId = 789;

        challenge.setChallengedUserId(expectedUserId);

        assertEquals(expectedUserId, challenge.getChallengedUserId());
    }

    @Test
    void setQuizId_ValidQuizId_ShouldSetCorrectly() {
        Long expectedQuizId = 999L;

        challenge.setQuizId(expectedQuizId);

        assertEquals(expectedQuizId, challenge.getQuizId());
    }

    @Test
    void setMessage_ValidMessage_ShouldSetCorrectly() {
        String expectedMessage = "I challenge you to beat my score!";

        challenge.setMessage(expectedMessage);

        assertEquals(expectedMessage, challenge.getMessage());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        challenge.setCreatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, challenge.getCreatedAt());
    }

    @Test
    void setStatus_ValidStatus_ShouldSetCorrectly() {
        String expectedStatus = "ACCEPTED";

        challenge.setStatus(expectedStatus);

        assertEquals(expectedStatus, challenge.getStatus());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 1L;
        int challengerId = 2;
        int challengedId = 3;
        Long quizId = 4L;
        String message = "Test challenge message";
        Timestamp createdAt = Timestamp.from(Instant.now());
        String status = "COMPLETED";

        challenge.setId(id);
        challenge.setChallengerUserId(challengerId);
        challenge.setChallengedUserId(challengedId);
        challenge.setQuizId(quizId);
        challenge.setMessage(message);
        challenge.setCreatedAt(createdAt);
        challenge.setStatus(status);

        assertEquals(id, challenge.getId());
        assertEquals(challengerId, challenge.getChallengerUserId());
        assertEquals(challengedId, challenge.getChallengedUserId());
        assertEquals(quizId, challenge.getQuizId());
        assertEquals(message, challenge.getMessage());
        assertEquals(createdAt, challenge.getCreatedAt());
        assertEquals(status, challenge.getStatus());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        challenge.setId(null);
        challenge.setQuizId(null);
        challenge.setMessage(null);
        challenge.setCreatedAt(null);
        challenge.setStatus(null);

        assertNull(challenge.getId());
        assertNull(challenge.getQuizId());
        assertNull(challenge.getMessage());
        assertNull(challenge.getCreatedAt());
        assertNull(challenge.getStatus());
    }

    @Test
    void setMessage_EmptyString_ShouldSetEmpty() {
        challenge.setMessage("");

        assertEquals("", challenge.getMessage());
    }

    @Test
    void setMessage_LongText_ShouldSetCorrectly() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            longText.append("This is a very long challenge message. ");
        }
        String veryLongMessage = longText.toString();

        challenge.setMessage(veryLongMessage);

        assertEquals(veryLongMessage, challenge.getMessage());
    }

    @Test
    void setFields_SpecialCharacters_ShouldSetCorrectly() {
        String specialMessage = "Challenge with special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";
        String specialStatus = "PENDING-REVIEW";

        challenge.setMessage(specialMessage);
        challenge.setStatus(specialStatus);

        assertEquals(specialMessage, challenge.getMessage());
        assertEquals(specialStatus, challenge.getStatus());
    }

    @Test
    void setFields_NonAsciiCharacters_ShouldSetCorrectly() {
        String nonAsciiMessage = "Challenge with non-ASCII: éñçåüö გამარჯობა";
        String nonAsciiStatus = "დასრულებული";

        challenge.setMessage(nonAsciiMessage);
        challenge.setStatus(nonAsciiStatus);

        assertEquals(nonAsciiMessage, challenge.getMessage());
        assertEquals(nonAsciiStatus, challenge.getStatus());
    }

    @Test
    void setFields_SameValueMultipleTimes_ShouldRetainValue() {
        String message = "Test challenge";
        String status = "PENDING";

        challenge.setMessage(message);
        String firstGet = challenge.getMessage();
        challenge.setMessage(message);
        String secondGet = challenge.getMessage();

        challenge.setStatus(status);
        String firstStatus = challenge.getStatus();
        challenge.setStatus(status);
        String secondStatus = challenge.getStatus();

        assertEquals(message, firstGet);
        assertEquals(message, secondGet);
        assertEquals(status, firstStatus);
        assertEquals(status, secondStatus);
    }

    @Test
    void setUserIds_ValidValues_ShouldSetCorrectly() {
        int challengerId = Integer.MAX_VALUE;
        int challengedId = Integer.MIN_VALUE;

        challenge.setChallengerUserId(challengerId);
        challenge.setChallengedUserId(challengedId);

        assertEquals(challengerId, challenge.getChallengerUserId());
        assertEquals(challengedId, challenge.getChallengedUserId());
    }

    @Test
    void setQuizId_LargeValue_ShouldSetCorrectly() {
        Long largeQuizId = Long.MAX_VALUE;

        challenge.setQuizId(largeQuizId);

        assertEquals(largeQuizId, challenge.getQuizId());
    }
}