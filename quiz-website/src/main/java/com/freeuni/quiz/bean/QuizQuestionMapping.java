package com.freeuni.quiz.bean;

public class QuizQuestionMapping {
    private Long id;
    private Long questionId;
    private Long quizId;
    private Long sequenceOrder;

    public QuizQuestionMapping() {}

    public QuizQuestionMapping(Long questionId, Long quizId, Long sequenceOrder) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.sequenceOrder = sequenceOrder;
    }

    public QuizQuestionMapping(Long id, Long questionId, Long quizId, Long sequenceOrder) {
        this.id = id;
        this.questionId = questionId;
        this.quizId = quizId;
        this.sequenceOrder = sequenceOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Long sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}
