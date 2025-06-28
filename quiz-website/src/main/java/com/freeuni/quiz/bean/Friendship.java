package com.freeuni.quiz.bean;

public class Friendship {
    private int id;
    private int friendSenderId;
    private int friendReceiverId;

    public Friendship() {}

    public Friendship(int id, int friendSenderId, int friendReceiverId) {
        this.id = id;
        this.friendSenderId = friendSenderId;
        this.friendReceiverId = friendReceiverId;
    }

    public Friendship(int friendSenderId, int friendReceiverId) {
        this.friendSenderId = friendSenderId;
        this.friendReceiverId = friendReceiverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFriendSenderId() {
        return friendSenderId;
    }

    public void setFriendSenderId(int friendSenderId) {
        this.friendSenderId = friendSenderId;
    }

    public int getFriendReceiverId() {
        return friendReceiverId;
    }

    public void setFriendReceiverId(int friendReceiverId) {
        this.friendReceiverId = friendReceiverId;
    }
}
