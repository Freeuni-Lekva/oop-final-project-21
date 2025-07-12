package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DAO.impl.AchievementDAOImpl;
import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.bean.UserAchievement;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class AchievementDAOTest {

    private static BasicDataSource dataSource;
    private AchievementDAO achievementDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP ALL OBJECTS");

            // Create tables
            stmt.execute("""
                CREATE TABLE achievements (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) UNIQUE NOT NULL,
                    description TEXT,
                    icon_url VARCHAR(2083),
                    created_at TIMESTAMP NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE user_achievements (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    achievement_id BIGINT NOT NULL,
                    awarded_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (achievement_id) REFERENCES achievements(id)
                )
            """);
        }
    }

    @Before
    public void setUp() throws SQLException {
        achievementDAO = new AchievementDAOImpl(dataSource);
        // Clear data before each test
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_achievements");
            stmt.execute("DELETE FROM achievements");
        }
    }

    private Achievement createSampleAchievement() {
        return new Achievement(
                "TEST_ACHIEVEMENT",
                "Test description",
                "http://example.com/icon.png",
                LocalDateTime.now()
        );
    }

    @Test
    public void testAddAchievementDefinition() throws SQLException {
        Achievement achievement = createSampleAchievement();
        boolean added = achievementDAO.addAchievementDefinition(achievement);
        assertTrue(added);
        assertNotNull(achievement.getId());
        assertTrue(achievement.getId() > 0);
    }

    @Test
    public void testFindByName() throws SQLException {
        Achievement achievement = createSampleAchievement();
        achievementDAO.addAchievementDefinition(achievement);

        Achievement found = achievementDAO.findByName(achievement.getName());
        assertNotNull(found);
        assertEquals(achievement.getName(), found.getName());
        assertEquals(achievement.getDescription(), found.getDescription());
        assertEquals(achievement.getIconUrl(), found.getIconUrl());

        Achievement notFound = achievementDAO.findByName("NON_EXISTENT");
        assertNull(notFound);
    }

    @Test
    public void testAwardAchievementToUserAndUserHasAchievement() throws SQLException {
        Achievement achievement = createSampleAchievement();
        achievementDAO.addAchievementDefinition(achievement);

        UserAchievement ua = new UserAchievement(1, achievement, LocalDateTime.now());
        boolean awarded = achievementDAO.awardAchievementToUser(ua);
        assertTrue(awarded);
        assertNotNull(ua.getId());
        assertTrue(ua.getId() > 0);

        boolean hasAchievement = achievementDAO.userHasAchievement(1, achievement.getId());
        assertTrue(hasAchievement);

        // Check for a user that does not have the achievement
        assertFalse(achievementDAO.userHasAchievement(2, achievement.getId()));
    }

    @Test
    public void testGetUserAchievements() throws SQLException {
        Achievement achievement1 = createSampleAchievement();
        achievementDAO.addAchievementDefinition(achievement1);

        Achievement achievement2 = new Achievement(
                "ANOTHER_ACHIEVEMENT", "Another desc", "http://example.com/icon2.png", LocalDateTime.now());
        achievementDAO.addAchievementDefinition(achievement2);

        UserAchievement ua1 = new UserAchievement(1, achievement1, LocalDateTime.now().minusDays(1));
        achievementDAO.awardAchievementToUser(ua1);
        UserAchievement ua2 = new UserAchievement(1, achievement2, LocalDateTime.now());
        achievementDAO.awardAchievementToUser(ua2);

        List<UserAchievement> userAchievements = achievementDAO.getUserAchievements(1);
        assertEquals(2, userAchievements.size());

        assertTrue(userAchievements.get(0).getAwardedAt().isAfter(userAchievements.get(1).getAwardedAt()));

        assertEquals("ANOTHER_ACHIEVEMENT", userAchievements.get(0).getAchievement().getName());
    }

    @Test
    public void testGetAllAchievements() throws SQLException {
        Achievement a1 = createSampleAchievement();
        achievementDAO.addAchievementDefinition(a1);

        Achievement a2 = new Achievement(
                "SECOND_ACHIEVEMENT", "Desc 2", "http://example.com/icon2.png", LocalDateTime.now().plusSeconds(1));
        achievementDAO.addAchievementDefinition(a2);

        List<Achievement> all = achievementDAO.getAllAchievements();
        assertEquals(2, all.size());

        assertEquals(a1.getName(), all.get(0).getName());
        assertEquals(a2.getName(), all.get(1).getName());
    }
}
