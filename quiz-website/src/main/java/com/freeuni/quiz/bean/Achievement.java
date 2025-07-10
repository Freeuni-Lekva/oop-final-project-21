package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class Achievement {
    private Long id;
    private int userId;
    private String type;
    private LocalDateTime achievedAt;

    public Achievement() {}

    public Achievement(int userId, String type, LocalDateTime achievedAt) {
        this.userId = userId;
        this.type = type;
        this.achievedAt = achievedAt;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public LocalDateTime getAchievedAt() { return achievedAt; }

    public void setAchievedAt(LocalDateTime achievedAt) { this.achievedAt = achievedAt; }
}
