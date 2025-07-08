package com.freeuni.quiz.service;

public class FriendshipManager {
    private final FriendshipRequestService friendshipRequestService;
    private final FriendshipService friendshipService;

    public FriendshipManager(FriendshipRequestService friendRequestService,
                             FriendshipService friendshipService) {
        this.friendshipRequestService = friendRequestService;
        this.friendshipService = friendshipService;
    }

    public boolean acceptFriendRequest(int senderId, int receiverId, int friendshipRequestId) throws Exception {
        if (!friendshipRequestService.requestExists(senderId, receiverId)) {
            return false;
        }
        friendshipRequestService.cancelRequest(friendshipRequestId);

        return friendshipService.addFriendship(senderId, receiverId);
    }

    public boolean declineFriendRequest(int senderId, int receiverId, int friendshipRequestId) throws Exception {
        // Just delete the friend request
        if (!friendshipRequestService.requestExists(senderId, receiverId)) {
            return false;
        }

        friendshipRequestService.cancelRequest(friendshipRequestId);
        return true;
    }
}
