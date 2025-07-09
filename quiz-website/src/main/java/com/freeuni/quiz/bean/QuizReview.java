package com.freeuni.quiz.bean;

import java.sql.Timestamp;

public class QuizReview {
    private Long id;
    private Integer userId;
    private Long quizId;
    private String reviewText;
    private Timestamp createdAt;
    private Timestamp updatedAt;


    public QuizReview() {
    }

    public QuizReview(Integer userId, Long quizId, String reviewText) {
        this.userId = userId;
        this.quizId = quizId;
        this.reviewText = reviewText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

}