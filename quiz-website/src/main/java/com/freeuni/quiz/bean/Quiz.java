package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class Quiz {
    private Long id;
    private int creatorUserId;
    private Long categoryId;
    private Long lastQuestionNumber;
    private LocalDateTime createdAt;
    private String testTitle;
    private String testDescription;
    private Long timeLimitMinutes;

    public Quiz() {}

    public Quiz(int creatorUserId, Long categoryId, String testTitle, String testDescription, Long timeLimitMinutes) {
        this.creatorUserId = creatorUserId;
        this.categoryId = categoryId;
        this.testTitle = testTitle;
        this.testDescription = testDescription;
        this.timeLimitMinutes = timeLimitMinutes;
        this.lastQuestionNumber = 0L;
    }

    public Quiz(Long id, int creatorUserId, Long categoryId, Long lastQuestionNumber,
                      LocalDateTime createdAt, String testTitle, String testDescription, Long timeLimitMinutes) {
        this.id = id;
        this.creatorUserId = creatorUserId;
        this.categoryId = categoryId;
        this.lastQuestionNumber = lastQuestionNumber;
        this.createdAt = createdAt;
        this.testTitle = testTitle;
        this.testDescription = testDescription;
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(int creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getLastQuestionNumber() {
        return lastQuestionNumber;
    }

    public void setLastQuestionNumber(Long lastQuestionNumber) {
        this.lastQuestionNumber = lastQuestionNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public Long getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Long timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }
}
