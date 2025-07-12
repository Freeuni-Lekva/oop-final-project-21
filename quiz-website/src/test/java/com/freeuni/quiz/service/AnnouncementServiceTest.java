package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.AnnouncementDAO;
import com.freeuni.quiz.DAO.impl.UserDAOImpl;
import com.freeuni.quiz.DTO.AnnouncementDTO;
import com.freeuni.quiz.bean.Announcement;
import com.freeuni.quiz.bean.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnnouncementServiceTest {

    @Mock
    private AnnouncementDAO announcementDAO;

    @Mock
    private UserDAOImpl userDAO;

    @Mock
    private DataSource dataSource;

    private AnnouncementService announcementService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        announcementService = new AnnouncementService(dataSource);

        try {
            java.lang.reflect.Field announcementDAOField = AnnouncementService.class.getDeclaredField("announcementDAO");
            announcementDAOField.setAccessible(true);
            announcementDAOField.set(announcementService, announcementDAO);

            java.lang.reflect.Field userDAOField = AnnouncementService.class.getDeclaredField("userDAO");
            userDAOField.setAccessible(true);
            userDAOField.set(announcementService, userDAO);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to inject mocked DAOs: " + e.getMessage());
        }
    }

    private User createAdminUser(int id, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdmin(true);
        return user;
    }

    private User createRegularUser(int id, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdmin(false);
        return user;
    }

    private Announcement createSampleAnnouncement(Long id, String title, String content, Integer authorId) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setAuthorId(authorId);
        announcement.setCreatedAt(Timestamp.from(Instant.now()));
        announcement.setUpdatedAt(Timestamp.from(Instant.now()));
        announcement.setActive(true);
        return announcement;
    }

    @Test
    public void testCreateAnnouncement_ValidAdminUser() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.addAnnouncement(any(Announcement.class))).thenReturn(true);

        boolean result = announcementService.createAnnouncement("Test Title", "Test Content", 1);

        assertTrue(result);
        verify(userDAO).findById(1);
        verify(announcementDAO).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_EmptyTitle() throws SQLException {
        try {
            announcementService.createAnnouncement("", "Test Content", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Title cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_NullTitle() throws SQLException {
        try {
            announcementService.createAnnouncement(null, "Test Content", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Title cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_WhitespaceTitle() throws SQLException {
        try {
            announcementService.createAnnouncement("   ", "Test Content", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Title cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_EmptyContent() throws SQLException {
        try {
            announcementService.createAnnouncement("Test Title", "", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Content cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_NullContent() throws SQLException {
        try {
            announcementService.createAnnouncement("Test Title", null, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Content cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_WhitespaceContent() throws SQLException {
        try {
            announcementService.createAnnouncement("Test Title", "   ", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Content cannot be empty", e.getMessage());
        }

        verify(userDAO, never()).findById(anyInt());
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_UserNotFound() throws SQLException {
        when(userDAO.findById(1)).thenReturn(null);

        try {
            announcementService.createAnnouncement("Test Title", "Test Content", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can create announcements", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_RegularUser() throws SQLException {
        User regularUser = createRegularUser(1, "Regular", "User");
        when(userDAO.findById(1)).thenReturn(regularUser);

        try {
            announcementService.createAnnouncement("Test Title", "Test Content", 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can create announcements", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).addAnnouncement(any(Announcement.class));
    }

    @Test
    public void testCreateAnnouncement_TrimmedValues() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.addAnnouncement(any(Announcement.class))).thenReturn(true);

        boolean result = announcementService.createAnnouncement("  Test Title  ", "  Test Content  ", 1);

        assertTrue(result);
        verify(announcementDAO).addAnnouncement(argThat(announcement -> 
            "Test Title".equals(announcement.getTitle()) && 
            "Test Content".equals(announcement.getContent())
        ));
    }

    @Test
    public void testGetRecentAnnouncements() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(createSampleAnnouncement(1L, "Title 1", "Content 1", 1));
        announcements.add(createSampleAnnouncement(2L, "Title 2", "Content 2", 1));

        when(announcementDAO.getRecentAnnouncements(5)).thenReturn(announcements);
        when(userDAO.findById(1)).thenReturn(createAdminUser(1, "Admin", "User"));

        List<AnnouncementDTO> result = announcementService.getRecentAnnouncements(5);

        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        assertEquals("Admin User", result.get(0).getAuthorName());
        verify(announcementDAO).getRecentAnnouncements(5);
    }

    @Test
    public void testGetRecentAnnouncements_Empty() throws SQLException {
        when(announcementDAO.getRecentAnnouncements(5)).thenReturn(new ArrayList<>());

        List<AnnouncementDTO> result = announcementService.getRecentAnnouncements(5);

        assertTrue(result.isEmpty());
        verify(announcementDAO).getRecentAnnouncements(5);
    }

    @Test
    public void testGetAllAnnouncements() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(createSampleAnnouncement(1L, "Title 1", "Content 1", 1));
        announcements.add(createSampleAnnouncement(2L, "Title 2", "Content 2", 2));

        when(announcementDAO.getAllAnnouncements()).thenReturn(announcements);
        when(userDAO.findById(1)).thenReturn(createAdminUser(1, "Admin", "One"));
        when(userDAO.findById(2)).thenReturn(createAdminUser(2, "Admin", "Two"));

        List<AnnouncementDTO> result = announcementService.getAllAnnouncements();

        assertEquals(2, result.size());
        assertEquals("Admin One", result.get(0).getAuthorName());
        assertEquals("Admin Two", result.get(1).getAuthorName());
        verify(announcementDAO).getAllAnnouncements();
    }

    @Test
    public void testGetActiveAnnouncements() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(createSampleAnnouncement(1L, "Active 1", "Content 1", 1));
        announcements.add(createSampleAnnouncement(2L, "Active 2", "Content 2", 1));

        when(announcementDAO.getActiveAnnouncements()).thenReturn(announcements);
        when(userDAO.findById(1)).thenReturn(createAdminUser(1, "Admin", "User"));

        List<AnnouncementDTO> result = announcementService.getActiveAnnouncements();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(AnnouncementDTO::isActive));
        verify(announcementDAO).getActiveAnnouncements();
    }

    @Test
    public void testUpdateAnnouncement_ValidAdmin() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        Announcement existingAnnouncement = createSampleAnnouncement(1L, "Old Title", "Old Content", 1);
        
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.findById(1L)).thenReturn(existingAnnouncement);
        when(announcementDAO.updateAnnouncement(any(Announcement.class))).thenReturn(true);

        boolean result = announcementService.updateAnnouncement(1L, "New Title", "New Content", true, 1);

        assertTrue(result);
        verify(userDAO).findById(1);
        verify(announcementDAO).findById(1L);
        verify(announcementDAO).updateAnnouncement(any(Announcement.class));
    }

    @Test
    public void testUpdateAnnouncement_NonAdminUser() throws SQLException {
        User regularUser = createRegularUser(1, "Regular", "User");
        when(userDAO.findById(1)).thenReturn(regularUser);

        try {
            announcementService.updateAnnouncement(1L, "New Title", "New Content", true, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can update announcements", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).findById(anyLong());
        verify(announcementDAO, never()).updateAnnouncement(any(Announcement.class));
    }

    @Test
    public void testUpdateAnnouncement_EmptyTitle() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);

        try {
            announcementService.updateAnnouncement(1L, "", "New Content", true, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Title cannot be empty", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).findById(anyLong());
    }

    @Test
    public void testUpdateAnnouncement_EmptyContent() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);

        try {
            announcementService.updateAnnouncement(1L, "New Title", "", true, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Content cannot be empty", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).findById(anyLong());
    }

    @Test
    public void testUpdateAnnouncement_AnnouncementNotFound() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.findById(1L)).thenReturn(null);

        boolean result = announcementService.updateAnnouncement(1L, "New Title", "New Content", true, 1);

        assertFalse(result);
        verify(userDAO).findById(1);
        verify(announcementDAO).findById(1L);
        verify(announcementDAO, never()).updateAnnouncement(any(Announcement.class));
    }

    @Test
    public void testDeleteAnnouncement_ValidAdmin() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.deleteAnnouncement(1L)).thenReturn(true);

        boolean result = announcementService.deleteAnnouncement(1L, 1);

        assertTrue(result);
        verify(userDAO).findById(1);
        verify(announcementDAO).deleteAnnouncement(1L);
    }

    @Test
    public void testDeleteAnnouncement_NonAdminUser() throws SQLException {
        User regularUser = createRegularUser(1, "Regular", "User");
        when(userDAO.findById(1)).thenReturn(regularUser);

        try {
            announcementService.deleteAnnouncement(1L, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can delete announcements", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).deleteAnnouncement(anyLong());
    }

    @Test
    public void testDeactivateAnnouncement_ValidAdmin() throws SQLException {
        User adminUser = createAdminUser(1, "Admin", "User");
        when(userDAO.findById(1)).thenReturn(adminUser);
        when(announcementDAO.deactivateAnnouncement(1L)).thenReturn(true);

        boolean result = announcementService.deactivateAnnouncement(1L, 1);

        assertTrue(result);
        verify(userDAO).findById(1);
        verify(announcementDAO).deactivateAnnouncement(1L);
    }

    @Test
    public void testDeactivateAnnouncement_NonAdminUser() throws SQLException {
        User regularUser = createRegularUser(1, "Regular", "User");
        when(userDAO.findById(1)).thenReturn(regularUser);

        try {
            announcementService.deactivateAnnouncement(1L, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can deactivate announcements", e.getMessage());
        }

        verify(userDAO).findById(1);
        verify(announcementDAO, never()).deactivateAnnouncement(anyLong());
    }

    @Test
    public void testConvertToDTO_WithAuthor() throws SQLException {
        Announcement announcement = createSampleAnnouncement(1L, "Test Title", "Test Content", 1);
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(announcement);

        when(announcementDAO.getRecentAnnouncements(1)).thenReturn(announcements);
        when(userDAO.findById(1)).thenReturn(createAdminUser(1, "John", "Doe"));

        List<AnnouncementDTO> result = announcementService.getRecentAnnouncements(1);

        assertEquals(1, result.size());
        AnnouncementDTO dto = result.get(0);
        assertEquals(announcement.getId(), dto.getId());
        assertEquals(announcement.getTitle(), dto.getTitle());
        assertEquals(announcement.getContent(), dto.getContent());
        assertEquals(announcement.getAuthorId(), dto.getAuthorId());
        assertEquals("John Doe", dto.getAuthorName());
        assertEquals(announcement.isActive(), dto.isActive());
    }

    @Test
    public void testConvertToDTO_AuthorNotFound() throws SQLException {
        Announcement announcement = createSampleAnnouncement(1L, "Test Title", "Test Content", 999);
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(announcement);

        when(announcementDAO.getRecentAnnouncements(1)).thenReturn(announcements);
        when(userDAO.findById(999)).thenReturn(null);

        List<AnnouncementDTO> result = announcementService.getRecentAnnouncements(1);

        assertEquals(1, result.size());
        assertEquals(null, result.getFirst().getAuthorName());
    }

    @Test
    public void testConvertToDTO_SQLException() throws SQLException {
        Announcement announcement = createSampleAnnouncement(1L, "Test Title", "Test Content", 1);
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(announcement);

        when(announcementDAO.getRecentAnnouncements(1)).thenReturn(announcements);
        when(userDAO.findById(1)).thenThrow(new SQLException("Database error"));

        List<AnnouncementDTO> result = announcementService.getRecentAnnouncements(1);

        assertEquals(1, result.size());
        assertEquals("Unknown", result.get(0).getAuthorName());
    }

    @Test
    public void testUserNotFound_InAllOperations() throws SQLException {
        when(userDAO.findById(999)).thenReturn(null);

        try {
            announcementService.createAnnouncement("Title", "Content", 999);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can create announcements", e.getMessage());
        }

        try {
            announcementService.updateAnnouncement(1L, "Title", "Content", true, 999);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can update announcements", e.getMessage());
        }

        try {
            announcementService.deleteAnnouncement(1L, 999);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can delete announcements", e.getMessage());
        }

        try {
            announcementService.deactivateAnnouncement(1L, 999);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Only admins can deactivate announcements", e.getMessage());
        }
    }
} 