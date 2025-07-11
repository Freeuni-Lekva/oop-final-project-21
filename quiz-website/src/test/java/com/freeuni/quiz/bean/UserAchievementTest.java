package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementTest {

    private UserAchievement userAchievement;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        userAchievement = new UserAchievement();
        achievement = new Achievement();
        achievement.setId(100L);
        achievement.setName("Test Achievement");
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyUserAchievement() {
        assertNotNull(userAchievement);
        assertNull(userAchievement.getId());
        assertEquals(0, userAchievement.getUserId());
        assertNull(userAchievement.getAchievement());
        assertNull(userAchievement.getAwardedAt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        int userId = 123;
        LocalDateTime awardedAt = LocalDateTime.now();

        UserAchievement paramUserAchievement = new UserAchievement(userId, achievement, awardedAt);

        assertEquals(userId, paramUserAchievement.getUserId());
        assertEquals(achievement, paramUserAchievement.getAchievement());
        assertEquals(awardedAt, paramUserAchievement.getAwardedAt());
        assertNull(paramUserAchievement.getId());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        userAchievement.setId(expectedId);
        assertEquals(expectedId, userAchievement.getId());
    }

    @Test
    void setUserId_ValidUserId_ShouldSetCorrectly() {
        int expectedUserId = 456;
        userAchievement.setUserId(expectedUserId);
        assertEquals(expectedUserId, userAchievement.getUserId());
    }

    @Test
    void setAchievement_ValidAchievement_ShouldSetCorrectly() {
        userAchievement.setAchievement(achievement);
        assertEquals(achievement, userAchievement.getAchievement());
    }

    @Test
    void setAwardedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedDateTime = LocalDateTime.now();
        userAchievement.setAwardedAt(expectedDateTime);
        assertEquals(expectedDateTime, userAchievement.getAwardedAt());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        userAchievement.setId(null);
        userAchievement.setAchievement(null);
        userAchievement.setAwardedAt(null);

        assertNull(userAchievement.getId());
        assertNull(userAchievement.getAchievement());
        assertNull(userAchievement.getAwardedAt());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 100L;
        int userId = 200;
        LocalDateTime awardedAt = LocalDateTime.now();

        userAchievement.setId(id);
        userAchievement.setUserId(userId);
        userAchievement.setAchievement(achievement);
        userAchievement.setAwardedAt(awardedAt);

        assertEquals(id, userAchievement.getId());
        assertEquals(userId, userAchievement.getUserId());
        assertEquals(achievement, userAchievement.getAchievement());
        assertEquals(awardedAt, userAchievement.getAwardedAt());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        UserAchievement nullAchievement = new UserAchievement(123, null, null);

        assertEquals(123, nullAchievement.getUserId());
        assertNull(nullAchievement.getAchievement());
        assertNull(nullAchievement.getAwardedAt());
        assertNull(nullAchievement.getId());
    }

    @Test
    void setUserId_NegativeId_ShouldSetNegativeValue() {
        int expectedUserId = -1;
        userAchievement.setUserId(expectedUserId);
        assertEquals(expectedUserId, userAchievement.getUserId());
    }
} 