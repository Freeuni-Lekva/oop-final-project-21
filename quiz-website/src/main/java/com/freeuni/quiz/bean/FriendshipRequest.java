package com.freeuni.quiz.bean;

import java.sql.Timestamp;

public class FriendshipRequest {
    private int id;
    private int requestSenderId;
    private int requestReceiverId;
    private Timestamp timestamp;

    public FriendshipRequest() {}

    public FriendshipRequest(int requestSenderId, int requestReceiverId, Timestamp timestamp) {
        this.requestSenderId = requestSenderId;
        this.requestReceiverId = requestReceiverId;
        this.timestamp = timestamp;
    }

    public FriendshipRequest(int id, int requestSenderId, int requestReceiverId, Timestamp timestamp) {
        this.id = id;
        this.requestSenderId = requestSenderId;
        this.requestReceiverId = requestReceiverId;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getRequestSenderId() {
        return requestSenderId;
    }

    public int getRequestReceiverId() {
        return requestReceiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRequestSenderId(int requestSenderId) {
        this.requestSenderId = requestSenderId;
    }

    public void setRequestReceiverId(int requestReceiverId) {
        this.requestReceiverId = requestReceiverId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

