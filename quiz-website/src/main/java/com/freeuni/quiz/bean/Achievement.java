package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class Achievement {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private LocalDateTime createdAt;

    public Achievement() {}

    public Achievement(Long id) {
        this.id = id;
    }

    public Achievement(String name, String description, String iconUrl, LocalDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

