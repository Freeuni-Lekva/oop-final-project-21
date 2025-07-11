package com.freeuni.quiz.bean;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sentAt;

    public Message(Long id, int senderId, int receiverId, String content, LocalDateTime sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

}

