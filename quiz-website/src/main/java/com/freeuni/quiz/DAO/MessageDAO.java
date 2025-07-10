package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Message;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDAO {
    private static final int PAGE_SIZE = 20;
    private final DataSource dataSource;

    public MessageDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Message getMessageById(Long id) throws SQLException {
        String sql = "SELECT * FROM messages WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            } else {
                throw new SQLException("Message not found.");
            }
        }
    }

    public Long sendMessage(int senderId, int receiverId, String content) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();


            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Could not get generated ID");
            }
        }
    }

    public List<Message> getRecentMessages(int user1, int user2) throws SQLException {
        String sql = """
                SELECT * FROM messages 
                WHERE (sender_id = ? AND receiver_id = ?) 
                   OR (sender_id = ? AND receiver_id = ?)
                ORDER BY sent_at DESC, id DESC
                LIMIT ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            stmt.setInt(5, PAGE_SIZE);

            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();

            while (rs.next()) {
                messages.add(mapRow(rs));
            }
            Collections.reverse(messages);
            return messages;
        }
    }

    public List<Message> getMessagesBefore(int user1, int user2, LocalDateTime beforeTime, Long beforeId) throws SQLException {
        String sql = """
        SELECT * FROM messages 
        WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))
          AND ((sent_at < ?) OR (sent_at = ? AND id < ?))
        ORDER BY sent_at DESC, id DESC
        LIMIT ?
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            stmt.setTimestamp(5, Timestamp.valueOf(beforeTime));
            stmt.setTimestamp(6, Timestamp.valueOf(beforeTime));
            stmt.setLong(7, beforeId);
            stmt.setInt(8, PAGE_SIZE);

            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (rs.next()) messages.add(mapRow(rs));
            Collections.reverse(messages);
            return messages;
        }
    }


    private Message mapRow(ResultSet rs) throws SQLException {
        return new Message(
                rs.getLong("id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getString("content"),
                rs.getTimestamp("sent_at").toLocalDateTime()
        );
    }

    public List<Message> getLatestConversations(int userId) throws SQLException {
        String sql = """
        SELECT m.*
        FROM messages m
        INNER JOIN (
            SELECT 
                LEAST(sender_id, receiver_id) AS user1,
                GREATEST(sender_id, receiver_id) AS user2,
                MAX(sent_at) AS latest_time
            FROM messages
            WHERE sender_id = ? OR receiver_id = ?
            GROUP BY user1, user2
        ) sub ON
            LEAST(m.sender_id, m.receiver_id) = sub.user1 AND
            GREATEST(m.sender_id, m.receiver_id) = sub.user2 AND
            m.sent_at = sub.latest_time
        ORDER BY m.sent_at DESC
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();

            while (rs.next()) {
                messages.add(mapRow(rs));
            }

            return messages;
        }
    }


    public int getPageSize() {
        return PAGE_SIZE;
    }
}
