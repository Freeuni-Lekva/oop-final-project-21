package com.freeuni.quiz.DTO;

import java.time.LocalDateTime;

public class AchievementDTO {
    private Long id;
    private int userId;
    private String type;
    private LocalDateTime achievedAt;

    public AchievementDTO() {}

    public AchievementDTO(Long id, int userId, String type, LocalDateTime achievedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.achievedAt = achievedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }
}
