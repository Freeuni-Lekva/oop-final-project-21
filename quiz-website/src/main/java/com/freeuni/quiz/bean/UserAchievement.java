package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class UserAchievement {
    private Long id;
    private int userId;
    private Achievement achievement;
    private LocalDateTime awardedAt;

    public UserAchievement() {}

    public UserAchievement(int userId, Achievement achievement, LocalDateTime awardedAt) {
        this.userId = userId;
        this.achievement = achievement;
        this.awardedAt = awardedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }

    public LocalDateTime getAwardedAt() { return awardedAt; }
    public void setAwardedAt(LocalDateTime awardedAt) { this.awardedAt = awardedAt; }
}
