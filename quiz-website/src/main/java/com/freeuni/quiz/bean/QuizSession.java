package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class QuizSession {
    private Long id;
    private Long participantUserId;
    private Long testId;
    private Long currentQuestionNum;
    private Long timeAllocated;
    private LocalDateTime sessionStart;

    public QuizSession() {}

    public QuizSession(Long participantUserId, Long testId, Long timeAllocated) {
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.timeAllocated = timeAllocated;
        this.currentQuestionNum = 0L;
    }

    public QuizSession(Long id, Long participantUserId, Long testId, Long currentQuestionNum,
                             Long timeAllocated, LocalDateTime sessionStart) {
        this.id = id;
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.currentQuestionNum = currentQuestionNum;
        this.timeAllocated = timeAllocated;
        this.sessionStart = sessionStart;
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

    public Long getCurrentQuestionNum() {
        return currentQuestionNum;
    }

    public void setCurrentQuestionNum(Long currentQuestionNum) {
        this.currentQuestionNum = currentQuestionNum;
    }

    public Long getTimeAllocated() {
        return timeAllocated;
    }

    public void setTimeAllocated(Long timeAllocated) {
        this.timeAllocated = timeAllocated;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(LocalDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }
}
