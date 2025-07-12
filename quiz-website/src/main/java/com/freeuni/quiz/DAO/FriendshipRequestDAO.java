package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.FriendshipRequest;

import java.sql.SQLException;
import java.util.List;

public interface FriendshipRequestDAO {
    
    boolean addFriendshipRequest(FriendshipRequest request) throws SQLException;
    
    FriendshipRequest findById(int id) throws SQLException;
    
    boolean exists(int senderId, int receiverId) throws SQLException;
    
    List<FriendshipRequest> findRequestsBySenderId(int senderId) throws SQLException;
    
    List<FriendshipRequest> findRequestsByReceiverId(int receiverId) throws SQLException;
    
    List<FriendshipRequest> findAll() throws SQLException;
    
    boolean deleteRequest(int id) throws SQLException;
    
    boolean updateRequest(FriendshipRequest request) throws SQLException;
    
    FriendshipRequest getFriendshipRequest(int senderId, int receiverId) throws SQLException;
}