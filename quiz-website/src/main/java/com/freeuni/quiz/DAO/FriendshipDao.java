package com.freeuni.quiz.DAO;
import com.freeuni.quiz.bean.Friendship;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDao {
    private final DataSource dataSource;

    public FriendshipDao(DataSource dataSource) {
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

    private Friendship mapResultSetToFriendship(ResultSet rs) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getInt("id"));
        friendship.setFriendSenderId(rs.getInt("friendSenderId"));
        friendship.setFriendReceiverId(rs.getInt("friendReceiverId"));
        return friendship;
    }
}
