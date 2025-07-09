package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.FriendshipRequestDAO;
import com.freeuni.quiz.bean.FriendshipRequest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class FriendshipRequestService {
    private final FriendshipRequestDAO requestDAO;

    public FriendshipRequestService(DataSource dataSource) {
        this.requestDAO = new FriendshipRequestDAO(dataSource);
    }

    public boolean sendRequest(int senderId, int receiverId) throws SQLException {
        if (senderId == receiverId) return false;
        if (requestExists(senderId, receiverId)) return false;

        FriendshipRequest request = new FriendshipRequest(senderId, receiverId);
        return requestDAO.addFriendshipRequest(request);
    }

    public boolean cancelRequest(int requestId) throws SQLException {
        return requestDAO.deleteRequest(requestId);
    }

    public List<FriendshipRequest> getRequestsSentByUser(int senderId) throws SQLException {
        return requestDAO.findRequestsBySenderId(senderId);
    }

    public List<FriendshipRequest> getRequestsReceivedByUser(int receiverId) throws SQLException {
        return requestDAO.findRequestsByReceiverId(receiverId);
    }

    public boolean requestExists(int senderId, int receiverId) throws SQLException {
        return requestDAO.exists(senderId, receiverId);
    }

    public FriendshipRequest findRequestById(int requestId) throws SQLException {
        return requestDAO.findById(requestId);
    }

    public FriendshipRequest getRequest(int senderId, int receiverId) throws SQLException {
        return requestDAO.getFriendshipRequest(senderId, receiverId);
    }
}
