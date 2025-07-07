package com.freeuni.quiz.DTO;

public class FriendshipRequestDTO {
    private int id;
    private UserDTO sender;
    private String timestamp;

    public FriendshipRequestDTO() {}

    public FriendshipRequestDTO(int id, UserDTO sender, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
