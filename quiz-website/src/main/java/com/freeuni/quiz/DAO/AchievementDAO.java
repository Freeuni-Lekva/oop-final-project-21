package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Achievement;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {
    private final DataSource dataSource;

    public AchievementDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addAchievement(Achievement achievement) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, type, achieved_at) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, achievement.getUserId());
            stmt.setString(2, achievement.getType());
            stmt.setTimestamp(3, Timestamp.valueOf(achievement.getAchievedAt()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    achievement.setId(generatedKeys.getLong(1));
                }
            }

            return true;
        }
    }

    public List<Achievement> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM achievements WHERE user_id = ?";
        List<Achievement> achievements = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapResultSetToAchievement(rs));
                }
            }
        }

        return achievements;
    }

    public boolean deleteAchievement(Long id) throws SQLException {
        String sql = "DELETE FROM achievements WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private Achievement mapResultSetToAchievement(ResultSet rs) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setId(rs.getLong("id"));
        achievement.setUserId(rs.getInt("user_id"));
        achievement.setType(rs.getString("type"));
        Timestamp timestamp = rs.getTimestamp("achieved_at");
        if (timestamp != null) {
            achievement.setAchievedAt(timestamp.toLocalDateTime());
        }
        return achievement;
    }
}
