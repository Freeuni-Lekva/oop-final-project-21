package com.freeuni.quiz.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuizCompletion {

    private Long id;
    private Long participantUserId;
    private Long testId;
    private Double finalScore;
    private Double totalPossible;
    private BigDecimal completionPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer totalTimeMinutes;

    public QuizCompletion() {}

    public QuizCompletion(Long participantUserId, Long testId, Double finalScore, Double totalPossible,
                                LocalDateTime startedAt, Integer totalTimeMinutes) {
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.finalScore = finalScore;
        this.totalPossible = totalPossible;
        this.completionPercentage = totalPossible > 0 ?
                BigDecimal.valueOf((finalScore / totalPossible) * 100) : BigDecimal.ZERO;
        this.startedAt = startedAt;
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public QuizCompletion(Long id, Long participantUserId, Long testId, Double finalScore, Double totalPossible,
                                BigDecimal completionPercentage, LocalDateTime startedAt, LocalDateTime finishedAt,
                                Integer totalTimeMinutes) {
        this.id = id;
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.finalScore = finalScore;
        this.totalPossible = totalPossible;
        this.completionPercentage = completionPercentage;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipantUserId() {
        return participantUserId;
    }

    public void setParticipantUserId(Long participantUserId) {
        this.participantUserId = participantUserId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getTotalPossible() {
        return totalPossible;
    }

    public void setTotalPossible(Double totalPossible) {
        this.totalPossible = totalPossible;
    }

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(Integer totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }
}
