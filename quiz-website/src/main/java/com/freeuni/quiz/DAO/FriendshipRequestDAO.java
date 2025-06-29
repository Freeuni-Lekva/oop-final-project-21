package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.FriendshipRequest;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestDAO {
    private final DataSource dataSource;

    public FriendshipRequestDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addFriendshipRequest(FriendshipRequest request) throws SQLException {
        String sql = "INSERT INTO friendship_requests (requestSender_id, requestReceiver_id, timestamp) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getRequestSenderId());
            stmt.setInt(2, request.getRequestReceiverId());
            stmt.setTimestamp(3, request.getTimestamp());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getInt(1));
                }
            }

            return true;
        }
    }

    public FriendshipRequest findById(int id) throws SQLException {
        String sql = "SELECT * FROM friendship_requests WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        }
        return null;
    }

    public boolean exists(int senderId, int receiverId) throws SQLException {
        String sql = "SELECT id FROM friendship_requests WHERE requestSender_id = ? AND requestReceiver_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<FriendshipRequest> findRequestsBySenderId(int senderId) throws SQLException {
        String sql = "SELECT * FROM friendship_requests WHERE requestSender_id = ?";
        List<FriendshipRequest> requests = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        }
        return requests;
    }

    public List<FriendshipRequest> findRequestsByReceiverId(int receiverId) throws SQLException {
        String sql = "SELECT * FROM friendship_requests WHERE requestReceiver_id = ?";
        List<FriendshipRequest> requests = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        }
        return requests;
    }

    public List<FriendshipRequest> findAll() throws SQLException {
        String sql = "SELECT * FROM friendship_requests";
        List<FriendshipRequest> requests = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }

        return requests;
    }

    public boolean deleteRequest(int id) throws SQLException {
        String sql = "DELETE FROM friendship_requests WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateRequest(FriendshipRequest request) throws SQLException {
        String sql = "UPDATE friendship_requests SET requestSender_id = ?, requestReceiver_id = ?, timestamp = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, request.getRequestSenderId());
            stmt.setInt(2, request.getRequestReceiverId());
            stmt.setTimestamp(3, request.getTimestamp());
            stmt.setInt(4, request.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private FriendshipRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        FriendshipRequest request = new FriendshipRequest();
        request.setId(rs.getInt("id"));
        request.setRequestSenderId(rs.getInt("requestSender_id"));
        request.setRequestReceiverId(rs.getInt("requestReceiver_id"));
        request.setTimestamp(rs.getTimestamp("timestamp"));
        return request;
    }
}
