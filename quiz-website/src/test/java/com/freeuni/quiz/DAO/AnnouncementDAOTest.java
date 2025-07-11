package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DAO.impl.AnnouncementDAOImpl;
import com.freeuni.quiz.bean.Announcement;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class AnnouncementDAOTest {
    private static BasicDataSource dataSource;
    private AnnouncementDAOImpl announcementDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE announcements (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "content TEXT NOT NULL," +
                    "author_id INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "is_active BOOLEAN DEFAULT TRUE" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        announcementDAO = new AnnouncementDAOImpl(dataSource);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM announcements");
        }
    }

    private Announcement createSampleAnnouncement(String title, String content, Integer authorId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setAuthorId(authorId);
        announcement.setActive(true);
        return announcement;
    }

    @Test
    public void testAddAnnouncement() throws SQLException {
        Announcement announcement = createSampleAnnouncement("Test Title", "Test Content", 1);

        boolean added = announcementDAO.addAnnouncement(announcement);

        assertTrue(added);
        assertTrue(announcement.getId() > 0);

        Announcement retrievedAnnouncement = announcementDAO.findById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertEquals("Test Title", retrievedAnnouncement.getTitle());
        assertEquals("Test Content", retrievedAnnouncement.getContent());
        assertEquals(Integer.valueOf(1), retrievedAnnouncement.getAuthorId());
        assertTrue(retrievedAnnouncement.isActive());
    }

    @Test
    public void testAddAnnouncementInactive() throws SQLException {
        Announcement announcement = createSampleAnnouncement("Inactive Title", "Inactive Content", 2);
        announcement.setActive(false);

        boolean added = announcementDAO.addAnnouncement(announcement);

        assertTrue(added);
        
        Announcement retrievedAnnouncement = announcementDAO.findById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertFalse(retrievedAnnouncement.isActive());
    }

    @Test
    public void testGetActiveAnnouncements() throws SQLException {
        Announcement active1 = createSampleAnnouncement("Active 1", "Content 1", 1);
        Announcement active2 = createSampleAnnouncement("Active 2", "Content 2", 1);
        Announcement inactive = createSampleAnnouncement("Inactive", "Content 3", 1);
        inactive.setActive(false);

        announcementDAO.addAnnouncement(active1);
        announcementDAO.addAnnouncement(active2);
        announcementDAO.addAnnouncement(inactive);

        List<Announcement> activeAnnouncements = announcementDAO.getActiveAnnouncements();

        assertEquals(2, activeAnnouncements.size());
        assertTrue(activeAnnouncements.stream().allMatch(Announcement::isActive));
    }

    @Test
    public void testGetRecentAnnouncements() throws SQLException {
        for (int i = 1; i <= 5; i++) {
            Announcement announcement = createSampleAnnouncement("Title " + i, "Content " + i, 1);
            announcementDAO.addAnnouncement(announcement);
        }

        List<Announcement> recentAnnouncements = announcementDAO.getRecentAnnouncements(3);

        assertEquals(3, recentAnnouncements.size());
        assertTrue(recentAnnouncements.stream().allMatch(Announcement::isActive));
    }

    @Test
    public void testGetRecentAnnouncementsWithInactive() throws SQLException {
        for (int i = 1; i <= 3; i++) {
            Announcement announcement = createSampleAnnouncement("Title " + i, "Content " + i, 1);
            announcementDAO.addAnnouncement(announcement);
        }

        Announcement inactive = createSampleAnnouncement("Inactive Title", "Inactive Content", 1);
        inactive.setActive(false);
        announcementDAO.addAnnouncement(inactive);

        List<Announcement> recentAnnouncements = announcementDAO.getRecentAnnouncements(5);

        assertEquals(3, recentAnnouncements.size());
        assertTrue(recentAnnouncements.stream().allMatch(Announcement::isActive));
    }

    @Test
    public void testGetAllAnnouncements() throws SQLException {
        Announcement active = createSampleAnnouncement("Active", "Active Content", 1);
        Announcement inactive = createSampleAnnouncement("Inactive", "Inactive Content", 1);
        inactive.setActive(false);

        announcementDAO.addAnnouncement(active);
        announcementDAO.addAnnouncement(inactive);

        List<Announcement> allAnnouncements = announcementDAO.getAllAnnouncements();

        assertEquals(2, allAnnouncements.size());
    }

    @Test
    public void testFindById() throws SQLException {
        Announcement announcement = createSampleAnnouncement("Find Me", "Content to find", 1);
        announcementDAO.addAnnouncement(announcement);

        Announcement foundAnnouncement = announcementDAO.findById(announcement.getId());

        assertNotNull(foundAnnouncement);
        assertEquals("Find Me", foundAnnouncement.getTitle());
        assertEquals("Content to find", foundAnnouncement.getContent());
        assertEquals(Integer.valueOf(1), foundAnnouncement.getAuthorId());
        assertTrue(foundAnnouncement.isActive());
    }

    @Test
    public void testFindByIdNotFound() throws SQLException {
        Announcement foundAnnouncement = announcementDAO.findById(999L);

        assertNull(foundAnnouncement);
    }

    @Test
    public void testUpdateAnnouncement() throws SQLException {
        Announcement announcement = createSampleAnnouncement("Original Title", "Original Content", 1);
        announcementDAO.addAnnouncement(announcement);

        announcement.setTitle("Updated Title");
        announcement.setContent("Updated Content");
        announcement.setActive(false);

        boolean updated = announcementDAO.updateAnnouncement(announcement);

        assertTrue(updated);

        Announcement retrievedAnnouncement = announcementDAO.findById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertEquals("Updated Title", retrievedAnnouncement.getTitle());
        assertEquals("Updated Content", retrievedAnnouncement.getContent());
        assertFalse(retrievedAnnouncement.isActive());
    }

    @Test
    public void testUpdateAnnouncementNotFound() throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setId(999L);
        announcement.setTitle("Not Found");
        announcement.setContent("Content");
        announcement.setActive(true);

        boolean updated = announcementDAO.updateAnnouncement(announcement);

        assertFalse(updated);
    }

    @Test
    public void testDeleteAnnouncement() throws SQLException {
        Announcement announcement = createSampleAnnouncement("To Delete", "Content to delete", 1);
        announcementDAO.addAnnouncement(announcement);

        Announcement beforeDelete = announcementDAO.findById(announcement.getId());
        assertNotNull(beforeDelete);

        boolean deleted = announcementDAO.deleteAnnouncement(announcement.getId());

        assertTrue(deleted);

        Announcement afterDelete = announcementDAO.findById(announcement.getId());
        assertNull(afterDelete);
    }

    @Test
    public void testDeleteAnnouncementNotFound() throws SQLException {
        boolean deleted = announcementDAO.deleteAnnouncement(999L);

        assertFalse(deleted);
    }

    @Test
    public void testDeactivateAnnouncement() throws SQLException {
        Announcement announcement = createSampleAnnouncement("To Deactivate", "Content to deactivate", 1);
        announcementDAO.addAnnouncement(announcement);

        assertTrue(announcement.isActive());

        boolean deactivated = announcementDAO.deactivateAnnouncement(announcement.getId());

        assertTrue(deactivated);

        Announcement retrievedAnnouncement = announcementDAO.findById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertFalse(retrievedAnnouncement.isActive());
    }

    @Test
    public void testDeactivateAnnouncementNotFound() throws SQLException {
        boolean deactivated = announcementDAO.deactivateAnnouncement(999L);

        assertFalse(deactivated);
    }

    @Test
    public void testSpecialCharacters() throws SQLException {
        String specialTitle = "Title with special chars: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";
        String specialContent = "Content with special chars: éñçåüö";

        Announcement announcement = createSampleAnnouncement(specialTitle, specialContent, 1);
        announcementDAO.addAnnouncement(announcement);

        Announcement retrievedAnnouncement = announcementDAO.findById(announcement.getId());

        assertEquals(specialTitle, retrievedAnnouncement.getTitle());
        assertEquals(specialContent, retrievedAnnouncement.getContent());
    }

    @Test
    public void testMultipleAuthors() throws SQLException {
        Announcement announcement1 = createSampleAnnouncement("Author 1", "Content 1", 1);
        Announcement announcement2 = createSampleAnnouncement("Author 2", "Content 2", 2);
        Announcement announcement3 = createSampleAnnouncement("Author 1 Again", "Content 3", 1);

        announcementDAO.addAnnouncement(announcement1);
        announcementDAO.addAnnouncement(announcement2);
        announcementDAO.addAnnouncement(announcement3);

        List<Announcement> allAnnouncements = announcementDAO.getAllAnnouncements();

        assertEquals(3, allAnnouncements.size());
        
        long author1Count = allAnnouncements.stream()
                .filter(a -> a.getAuthorId().equals(1))
                .count();
        long author2Count = allAnnouncements.stream()
                .filter(a -> a.getAuthorId().equals(2))
                .count();

        assertEquals(2, author1Count);
        assertEquals(1, author2Count);
    }

    @Test
    public void testEmptyResults() throws SQLException {
        List<Announcement> activeAnnouncements = announcementDAO.getActiveAnnouncements();
        List<Announcement> recentAnnouncements = announcementDAO.getRecentAnnouncements(10);
        List<Announcement> allAnnouncements = announcementDAO.getAllAnnouncements();

        assertTrue(activeAnnouncements.isEmpty());
        assertTrue(recentAnnouncements.isEmpty());
        assertTrue(allAnnouncements.isEmpty());
    }

    @Test
    public void testOrderingByCreatedAt() throws SQLException {
        Announcement first = createSampleAnnouncement("First", "First Content", 1);
        Announcement second = createSampleAnnouncement("Second", "Second Content", 1);
        Announcement third = createSampleAnnouncement("Third", "Third Content", 1);

        announcementDAO.addAnnouncement(first);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        announcementDAO.addAnnouncement(second);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        announcementDAO.addAnnouncement(third);

        List<Announcement> allAnnouncements = announcementDAO.getAllAnnouncements();

        assertEquals(3, allAnnouncements.size());
        assertEquals("Third", allAnnouncements.get(0).getTitle());
        assertEquals("Second", allAnnouncements.get(1).getTitle());
        assertEquals("First", allAnnouncements.get(2).getTitle());
    }
} 