package com.freeuni.quiz.bean;

import com.freeuni.quiz.quiz_util.AbstractQuestionHandler;

import java.time.LocalDateTime;

public class Question {
    private Long id;
    private int authorUserId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private AbstractQuestionHandler questionHandler;
    private String questionTitle;
    private QuestionType questionType;

    public Question() {}

    public Question(int authorUserId, Long categoryId, AbstractQuestionHandler questionHandler,
                          String questionTitle, QuestionType questionType) {
        this.authorUserId = authorUserId;
        this.categoryId = categoryId;
        this.questionHandler = questionHandler;
        this.questionTitle = questionTitle;
        this.questionType = questionType;
    }

    public Question(Long id, int authorUserId, Long categoryId, LocalDateTime createdAt,
                          AbstractQuestionHandler questionHandler, String questionTitle, QuestionType questionType) {
        this.id = id;
        this.authorUserId = authorUserId;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.questionHandler = questionHandler;
        this.questionTitle = questionTitle;
        this.questionType = questionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(int authorUserId) {
        this.authorUserId = authorUserId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AbstractQuestionHandler getQuestionHandler() {
        return questionHandler;
    }

    public void setQuestionHandler(AbstractQuestionHandler questionHandler) {
        this.questionHandler = questionHandler;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}
