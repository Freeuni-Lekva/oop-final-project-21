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

    public void sendMessage(int senderId, int receiverId, String content) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();
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

    public List<Message> getMessagesBefore(int user1, int user2, LocalDateTime beforeTime) throws SQLException {
        String sql = """
                SELECT * FROM messages 
                WHERE ((sender_id = ? AND receiver_id = ?) 
                   OR  (sender_id = ? AND receiver_id = ?))
                  AND sent_at < ?
                ORDER BY sent_at DESC
                LIMIT ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            stmt.setTimestamp(5, Timestamp.valueOf(beforeTime));
            stmt.setInt(6, PAGE_SIZE);

            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();

            while (rs.next()) {
                messages.add(mapRow(rs));
            }
            Collections.reverse(messages);
            return messages;
        }
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        return new Message(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getString("content"),
                rs.getTimestamp("sent_at").toLocalDateTime()
        );
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }
}
