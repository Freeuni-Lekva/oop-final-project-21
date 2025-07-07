package com.freeuni.quiz.bean;

public class ParticipantAnswer {
    private Long id;
    private Long participantUserId;
    private Long testId;
    private Long questionNumber;
    private Double pointsEarned;
    private Integer timeSpentSeconds;
    private String answerText;

    public ParticipantAnswer() {}

    public ParticipantAnswer(Long participantUserId, Long testId, Long questionNumber,
                                   Double pointsEarned, Integer timeSpentSeconds, String answerText) {
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.questionNumber = questionNumber;
        this.pointsEarned = pointsEarned;
        this.timeSpentSeconds = timeSpentSeconds;
        this.answerText = answerText;
    }

    public ParticipantAnswer(Long id, Long participantUserId, Long testId, Long questionNumber,
                                   Double pointsEarned, Integer timeSpentSeconds, String answerText) {
        this.id = id;
        this.participantUserId = participantUserId;
        this.testId = testId;
        this.questionNumber = questionNumber;
        this.pointsEarned = pointsEarned;
        this.timeSpentSeconds = timeSpentSeconds;
        this.answerText = answerText;
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

    public Long getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Long questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Double getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
