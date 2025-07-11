package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.bean.UserAchievement;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {
    private final DataSource dataSource;

    public AchievementDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // (admin side)
    public boolean addAchievementDefinition(Achievement achievement) throws SQLException {
        String sql = "INSERT INTO achievements (name, description, icon_url, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, achievement.getName());
            stmt.setString(2, achievement.getDescription());
            stmt.setString(3, achievement.getIconUrl());
            stmt.setTimestamp(4, Timestamp.valueOf(achievement.getCreatedAt()));

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    achievement.setId(keys.getLong(1));
                }
            }
            return true;
        }
    }

    public boolean awardAchievementToUser(UserAchievement userAchievement) throws SQLException {
        String sql = "INSERT INTO user_achievements (user_id, achievement_id, awarded_at) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userAchievement.getUserId());
            stmt.setLong(2, userAchievement.getAchievement().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(userAchievement.getAwardedAt()));

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    userAchievement.setId(keys.getLong(1));
                }
            }

            return true;
        }
    }

    public boolean userHasAchievement(int userId, long achievementId) throws SQLException {
        String sql = "SELECT 1 FROM user_achievements WHERE user_id = ? AND achievement_id = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setLong(2, achievementId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<UserAchievement> getUserAchievements(int userId) throws SQLException {
        String sql = """
            SELECT ua.id as ua_id, ua.awarded_at, a.id as a_id, a.name, a.description, a.icon_url, a.created_at
            FROM user_achievements ua
            JOIN achievements a ON ua.achievement_id = a.id
            WHERE ua.user_id = ?
            ORDER BY ua.awarded_at DESC
        """;

        List<UserAchievement> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Achievement achievement = new Achievement();
                    achievement.setId(rs.getLong("a_id"));
                    achievement.setName(rs.getString("name"));
                    achievement.setDescription(rs.getString("description"));
                    achievement.setIconUrl(rs.getString("icon_url"));
                    achievement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    UserAchievement userAchievement = new UserAchievement();
                    userAchievement.setId(rs.getLong("ua_id"));
                    userAchievement.setUserId(userId);
                    userAchievement.setAchievement(achievement);
                    userAchievement.setAwardedAt(rs.getTimestamp("awarded_at").toLocalDateTime());

                    result.add(userAchievement);
                }
            }
        }
        return result;
    }

    public List<Achievement> getAllAchievements() throws SQLException {
        String sql = "SELECT * FROM achievements ORDER BY created_at ASC";
        List<Achievement> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Achievement a = new Achievement();
                a.setId(rs.getLong("id"));
                a.setName(rs.getString("name"));
                a.setDescription(rs.getString("description"));
                a.setIconUrl(rs.getString("icon_url"));
                a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(a);
            }
        }
        return list;
    }

    public Achievement findByName(String name) throws SQLException {
        String sql = "SELECT * FROM achievements WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Achievement achievement = new Achievement();
                    achievement.setId(rs.getLong("id"));
                    achievement.setName(rs.getString("name"));
                    achievement.setDescription(rs.getString("description"));
                    achievement.setIconUrl(rs.getString("icon_url"));
                    achievement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return achievement;
                }
            }
        }
        return null;
    }

}


