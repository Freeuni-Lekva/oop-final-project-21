package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Announcement;

import java.sql.SQLException;
import java.util.List;

public interface AnnouncementDAO {

    boolean addAnnouncement(Announcement announcement) throws SQLException;

    List<Announcement> getActiveAnnouncements() throws SQLException;

    List<Announcement> getRecentAnnouncements(int limit) throws SQLException;

    List<Announcement> getAllAnnouncements() throws SQLException;

    Announcement findById(Long id) throws SQLException;

    boolean updateAnnouncement(Announcement announcement) throws SQLException;

    boolean deleteAnnouncement(Long id) throws SQLException;

    boolean deactivateAnnouncement(Long id) throws SQLException;
} 