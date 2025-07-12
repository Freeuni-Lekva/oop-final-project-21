package com.freeuni.quiz.DAO.impl;
import com.freeuni.quiz.bean.Friendship;
import com.freeuni.quiz.DAO.FriendshipDAO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAOImpl implements FriendshipDAO {
    private final DataSource dataSource;

    public FriendshipDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addFriendship(Friendship friendship) throws SQLException {
        String sql = "INSERT INTO friendships (friendSenderId, friendReceiverId) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, friendship.getFriendSenderId());
            stmt.setInt(2, friendship.getFriendReceiverId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    friendship.setId(generatedKeys.getInt(1));
                }
            }

            return true;
        }
    }

    public Friendship findById(int id) throws SQLException {
        String sql = "SELECT * FROM friendships WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFriendship(rs);
                }
            }
        }
        return null;
    }

    public List<Friendship> findAll() throws SQLException {
        String sql = "SELECT * FROM friendships";
        List<Friendship> friendships = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                friendships.add(mapResultSetToFriendship(rs));
            }
        }

        return friendships;
    }

    public boolean deleteFriendship(int id) throws SQLException {
        String sql = "DELETE FROM friendships WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateFriendship(Friendship friendship) throws SQLException {
        String sql = "UPDATE friendships SET friendSenderId = ?, friendReceiverId = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, friendship.getFriendSenderId());
            stmt.setInt(2, friendship.getFriendReceiverId());
            stmt.setInt(3, friendship.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean exists(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id FROM friendships WHERE " +
                "(friendSenderId = ? AND friendReceiverId = ?) OR " +
                "(friendSenderId = ? AND friendReceiverId = ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Integer> findFriendIdsByUserId(int userId) throws SQLException {
        String sql = "SELECT friendSenderId, friendReceiverId FROM friendships WHERE " +
                "friendSenderId = ? OR friendReceiverId = ?";

        List<Integer> friendIds = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int senderId = rs.getInt("friendSenderId");
                    int receiverId = rs.getInt("friendReceiverId");

                    if (senderId != userId) friendIds.add(senderId);
                    if (receiverId != userId) friendIds.add(receiverId);
                }
            }
        }

        return friendIds;
    }
    public Integer findFriendshipId(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id FROM friendships WHERE " +
                "(friendSenderId = ? AND friendReceiverId = ?) OR " +
                "(friendSenderId = ? AND friendReceiverId = ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    private Friendship mapResultSetToFriendship(ResultSet rs) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getInt("id"));
        friendship.setFriendSenderId(rs.getInt("friendSenderId"));
        friendship.setFriendReceiverId(rs.getInt("friendReceiverId"));
        return friendship;
    }
}
