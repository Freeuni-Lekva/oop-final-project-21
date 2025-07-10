package com.freeuni.quiz.bean;

import java.sql.Timestamp;

public class QuizChallenge {
    private Long id;
    private int challengerUserId;
    private int challengedUserId;
    private Long quizId;
    private String message;
    private Timestamp createdAt;
    private String status;

    public QuizChallenge() {}

    public QuizChallenge(int challengerUserId, int challengedUserId, Long quizId, String message) {
        this.challengerUserId = challengerUserId;
        this.challengedUserId = challengedUserId;
        this.quizId = quizId;
        this.message = message;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getChallengerUserId() { return challengerUserId; }
    public void setChallengerUserId(int challengerUserId) { this.challengerUserId = challengerUserId; }

    public int getChallengedUserId() { return challengedUserId; }
    public void setChallengedUserId(int challengedUserId) { this.challengedUserId = challengedUserId; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
