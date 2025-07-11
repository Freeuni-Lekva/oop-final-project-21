package com.freeuni.quiz.DTO;

public class FriendshipDTO {
    private int id;
    private UserDTO friendUser;

    public FriendshipDTO() {}

    public FriendshipDTO(int id, UserDTO friend) {
        this.id = id;
        this.friendUser = friend;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDTO getFriend() {
        return friendUser;
    }

    public void setFriend(UserDTO friend) {
        this.friendUser = friend;
    }
}
