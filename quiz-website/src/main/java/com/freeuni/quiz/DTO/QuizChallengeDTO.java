package com.freeuni.quiz.DTO;

import com.freeuni.quiz.bean.Quiz;
import java.sql.Timestamp;

public class QuizChallengeDTO {
    private Long id;
    private UserDTO challenger;
    private UserDTO challenged;
    private Quiz quiz;
    private String message;
    private Timestamp createdAt;
    private String status;
    private String quizUrl;

    public QuizChallengeDTO() {}

    public QuizChallengeDTO(Long id, UserDTO challenger, UserDTO challenged,
                            Quiz quiz, String message, Timestamp createdAt, String status) {
        this.id = id;
        this.challenger = challenger;
        this.challenged = challenged;
        this.quiz = quiz;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
        this.quizUrl = "/quiz?id=" + quiz.getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getChallenger() { return challenger; }
    public void setChallenger(UserDTO challenger) { this.challenger = challenger; }

    public UserDTO getChallenged() { return challenged; }
    public void setChallenged(UserDTO challenged) { this.challenged = challenged; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQuizUrl() { return quizUrl; }
    public void setQuizUrl(String quizUrl) { this.quizUrl = quizUrl; }
}
