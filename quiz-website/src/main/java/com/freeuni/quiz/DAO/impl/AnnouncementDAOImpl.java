package com.freeuni.quiz.DAO.impl;

import com.freeuni.quiz.DAO.AnnouncementDAO;
import com.freeuni.quiz.bean.Announcement;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAOImpl implements AnnouncementDAO {
    private final DataSource dataSource;

    public AnnouncementDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean addAnnouncement(Announcement announcement) throws SQLException {
        String sql = "INSERT INTO announcements (title, content, author_id, is_active) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getContent());
            stmt.setInt(3, announcement.getAuthorId());
            stmt.setBoolean(4, announcement.isActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    announcement.setId(generatedKeys.getLong(1));
                }
            }
            return true;
        }
    }

    @Override
    public List<Announcement> getActiveAnnouncements() throws SQLException {
        String sql = "SELECT * FROM announcements WHERE is_active = true ORDER BY created_at DESC";

        List<Announcement> announcements = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = mapResultSetToAnnouncement(rs);
                    announcements.add(announcement);
                }
            }
        }
        return announcements;
    }

    @Override
    public List<Announcement> getRecentAnnouncements(int limit) throws SQLException {
        String sql = "SELECT * FROM announcements WHERE is_active = true ORDER BY created_at DESC LIMIT ?";

        List<Announcement> announcements = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = mapResultSetToAnnouncement(rs);
                    announcements.add(announcement);
                }
            }
        }
        return announcements;
    }

    @Override
    public List<Announcement> getAllAnnouncements() throws SQLException {
        String sql = "SELECT * FROM announcements ORDER BY created_at DESC";

        List<Announcement> announcements = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = mapResultSetToAnnouncement(rs);
                    announcements.add(announcement);
                }
            }
        }
        return announcements;
    }

    @Override
    public Announcement findById(Long id) throws SQLException {
        String sql = "SELECT * FROM announcements WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnnouncement(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) throws SQLException {
        String sql = "UPDATE announcements SET title = ?, content = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getContent());
            stmt.setBoolean(3, announcement.isActive());
            stmt.setLong(4, announcement.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteAnnouncement(Long id) throws SQLException {
        String sql = "DELETE FROM announcements WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deactivateAnnouncement(Long id) throws SQLException {
        String sql = "UPDATE announcements SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setId(rs.getLong("id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setAuthorId(rs.getInt("author_id"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        announcement.setUpdatedAt(rs.getTimestamp("updated_at"));
        announcement.setActive(rs.getBoolean("is_active"));
        return announcement;
    }
} 