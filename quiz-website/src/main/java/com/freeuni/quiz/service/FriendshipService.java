package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.impl.FriendshipDAOImpl;
import com.freeuni.quiz.bean.Friendship;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendshipService {
    private final FriendshipDAOImpl friendshipDAOImpl;

    public FriendshipService(DataSource dataSource) {
        this.friendshipDAOImpl = new FriendshipDAOImpl(dataSource);
    }

    public boolean addFriendship(int senderId, int receiverId) throws SQLException {
        if (senderId == receiverId) return false;
        if (areFriends(senderId, receiverId)) return false;

        Friendship friendship = new Friendship(senderId, receiverId);
        return friendshipDAOImpl.addFriendship(friendship);
    }

    public boolean removeFriendshipByUserIds(int userId1, int userId2) throws SQLException {
        Integer friendshipId = friendshipDAOImpl.findFriendshipId(userId1, userId2);
        if (friendshipId != null) {
            return friendshipDAOImpl.deleteFriendship(friendshipId);
        }
        return false;
    }

    public List<Friendship> getAllFriendships() throws SQLException {
        return friendshipDAOImpl.findAll();
    }

    public List<Integer> getFriendsOfUser(int userId) throws SQLException {
        return friendshipDAOImpl.findFriendIdsByUserId(userId);
    }

    public boolean areFriends(int userId1, int userId2) throws SQLException {
        return friendshipDAOImpl.exists(userId1, userId2);
    }

    public List<Integer> getMutualFriends(int userId1, int userId2) throws SQLException {
        Set<Integer> friendsOfUser1 = new HashSet<>(getFriendsOfUser(userId1));
        Set<Integer> friendsOfUser2 = new HashSet<>(getFriendsOfUser(userId2));

        Set<Integer> mutualFriends = new HashSet<>(friendsOfUser1);
        mutualFriends.retainAll(friendsOfUser2);

        return mutualFriends.stream().collect(Collectors.toList());
    }
}
