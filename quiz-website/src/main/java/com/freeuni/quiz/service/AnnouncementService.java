package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.AnnouncementDAO;
import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.DAO.impl.AnnouncementDAOImpl;
import com.freeuni.quiz.DTO.AnnouncementDTO;
import com.freeuni.quiz.bean.Announcement;
import com.freeuni.quiz.bean.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementService {
    private final AnnouncementDAO announcementDAO;
    private final UserDAO userDAO;

    public AnnouncementService(DataSource dataSource) {
        this.announcementDAO = new AnnouncementDAOImpl(dataSource);
        this.userDAO = new UserDAO(dataSource);
    }

    public boolean createAnnouncement(String title, String content, Integer authorId) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        User author = userDAO.findById(authorId);
        if (author == null || !author.isAdmin()) {
            throw new IllegalArgumentException("Only admins can create announcements");
        }

        Announcement announcement = new Announcement(title.trim(), content.trim(), authorId);
        return announcementDAO.addAnnouncement(announcement);
    }

    public List<AnnouncementDTO> getRecentAnnouncements(int limit) throws SQLException {
        List<Announcement> announcements = announcementDAO.getRecentAnnouncements(limit);
        List<AnnouncementDTO> announcementDTOs = new ArrayList<>();

        for (Announcement announcement : announcements) {
            AnnouncementDTO dto = convertToDTO(announcement);
            announcementDTOs.add(dto);
        }

        return announcementDTOs;
    }

    public List<AnnouncementDTO> getAllAnnouncements() throws SQLException {
        List<Announcement> announcements = announcementDAO.getAllAnnouncements();
        List<AnnouncementDTO> announcementDTOs = new ArrayList<>();

        for (Announcement announcement : announcements) {
            AnnouncementDTO dto = convertToDTO(announcement);
            announcementDTOs.add(dto);
        }

        return announcementDTOs;
    }

    public List<AnnouncementDTO> getActiveAnnouncements() throws SQLException {
        List<Announcement> announcements = announcementDAO.getActiveAnnouncements();
        List<AnnouncementDTO> announcementDTOs = new ArrayList<>();

        for (Announcement announcement : announcements) {
            AnnouncementDTO dto = convertToDTO(announcement);
            announcementDTOs.add(dto);
        }

        return announcementDTOs;
    }

    public boolean updateAnnouncement(Long id, String title, String content, boolean isActive, Integer adminId) throws SQLException {
        User admin = userDAO.findById(adminId);
        if (admin == null || !admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can update announcements");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        Announcement announcement = announcementDAO.findById(id);
        if (announcement == null) {
            return false;
        }

        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setActive(isActive);

        return announcementDAO.updateAnnouncement(announcement);
    }

    public boolean deleteAnnouncement(Long id, Integer adminId) throws SQLException {
        User admin = userDAO.findById(adminId);
        if (admin == null || !admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can delete announcements");
        }

        return announcementDAO.deleteAnnouncement(id);
    }

    public boolean deactivateAnnouncement(Long id, Integer adminId) throws SQLException {
        User admin = userDAO.findById(adminId);
        if (admin == null || !admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can deactivate announcements");
        }

        return announcementDAO.deactivateAnnouncement(id);
    }

    private AnnouncementDTO convertToDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setAuthorId(announcement.getAuthorId());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        dto.setActive(announcement.isActive());

        try {
            User author = userDAO.findById(announcement.getAuthorId());
            if (author != null) {
                dto.setAuthorName(author.getFirstName() + " " + author.getLastName());
            }
        } catch (SQLException e) {
            dto.setAuthorName("Unknown");
        }

        return dto;
    }
} 