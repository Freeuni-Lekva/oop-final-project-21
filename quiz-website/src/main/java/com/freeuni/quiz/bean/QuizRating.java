package com.freeuni.quiz.bean;

import java.sql.Timestamp;

public class QuizRating {
    private Long id;
    private Integer userId;
    private Long quizId;
    private Integer rating;
    private Timestamp createdAt;


    public QuizRating() {
    }

    public QuizRating(Integer userId, Long quizId, Integer rating) {
        this.userId = userId;
        this.quizId = quizId;
        this.rating = rating;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}