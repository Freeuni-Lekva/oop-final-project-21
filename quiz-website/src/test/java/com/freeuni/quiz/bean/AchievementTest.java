package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {

    private Achievement achievement;

    @BeforeEach
    void setUp() {
        achievement = new Achievement();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyAchievement() {
        assertNotNull(achievement);
        assertNull(achievement.getId());
        assertNull(achievement.getName());
        assertNull(achievement.getDescription());
        assertNull(achievement.getIconUrl());
        assertNull(achievement.getCreatedAt());
    }

    @Test
    void idConstructor_ShouldSetIdOnly() {
        Long expectedId = 123L;
        Achievement idAchievement = new Achievement(expectedId);
        assertEquals(expectedId, idAchievement.getId());
        assertNull(idAchievement.getName());
        assertNull(idAchievement.getDescription());
        assertNull(idAchievement.getIconUrl());
        assertNull(idAchievement.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        String name = "First Quiz";
        String description = "Created your first quiz";
        String iconUrl = "https://example.com/icon.png";
        LocalDateTime createdAt = LocalDateTime.now();

        Achievement paramAchievement = new Achievement(name, description, iconUrl, createdAt);

        assertEquals(name, paramAchievement.getName());
        assertEquals(description, paramAchievement.getDescription());
        assertEquals(iconUrl, paramAchievement.getIconUrl());
        assertEquals(createdAt, paramAchievement.getCreatedAt());
        assertNull(paramAchievement.getId());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 999L;
        achievement.setId(expectedId);
        assertEquals(expectedId, achievement.getId());
    }

    @Test
    void setName_ValidName_ShouldSetCorrectly() {
        String expectedName = "Quiz Master";
        achievement.setName(expectedName);
        assertEquals(expectedName, achievement.getName());
    }

    @Test
    void setDescription_ValidDescription_ShouldSetCorrectly() {
        String expectedDescription = "Completed 10 quizzes";
        achievement.setDescription(expectedDescription);
        assertEquals(expectedDescription, achievement.getDescription());
    }

    @Test
    void setIconUrl_ValidUrl_ShouldSetCorrectly() {
        String expectedUrl = "https://example.com/master-icon.png";
        achievement.setIconUrl(expectedUrl);
        assertEquals(expectedUrl, achievement.getIconUrl());
    }

    @Test
    void setCreatedAt_ValidDateTime_ShouldSetCorrectly() {
        LocalDateTime expectedDateTime = LocalDateTime.now();
        achievement.setCreatedAt(expectedDateTime);
        assertEquals(expectedDateTime, achievement.getCreatedAt());
    }

    @Test
    void setFields_WithNullValues_ShouldSetNull() {
        achievement.setId(null);
        achievement.setName(null);
        achievement.setDescription(null);
        achievement.setIconUrl(null);
        achievement.setCreatedAt(null);

        assertNull(achievement.getId());
        assertNull(achievement.getName());
        assertNull(achievement.getDescription());
        assertNull(achievement.getIconUrl());
        assertNull(achievement.getCreatedAt());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 100L;
        String name = "Achievement Name";
        String description = "Achievement Description";
        String iconUrl = "https://example.com/icon.png";
        LocalDateTime createdAt = LocalDateTime.now();

        achievement.setId(id);
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setIconUrl(iconUrl);
        achievement.setCreatedAt(createdAt);

        assertEquals(id, achievement.getId());
        assertEquals(name, achievement.getName());
        assertEquals(description, achievement.getDescription());
        assertEquals(iconUrl, achievement.getIconUrl());
        assertEquals(createdAt, achievement.getCreatedAt());
    }
} 