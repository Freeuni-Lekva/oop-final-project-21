package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizRating;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class QuizRatingDAOTest {
    private static BasicDataSource dataSource;
    private QuizRatingDAO ratingDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE quiz_ratings (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "quiz_id BIGINT NOT NULL," +
                    "rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(user_id, quiz_id)" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        ratingDAO = new QuizRatingDAO(dataSource);

        // Clear ratings table before each test
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM quiz_ratings");
        }
    }

    private QuizRating createSampleRating(int userId, long quizId, int rating) {
        QuizRating quizRating = new QuizRating();
        quizRating.setUserId(userId);
        quizRating.setQuizId(quizId);
        quizRating.setRating(rating);
        return quizRating;
    }

    @Test
    public void testAddRating() throws SQLException {
        // Create a sample rating
        QuizRating rating = createSampleRating(1, 1, 4);

        // Add the rating
        boolean added = ratingDAO.addRating(rating);

        // Verify
        assertTrue(added);
        assertTrue(rating.getId() > 0);

        // Verify the rating is retrievable
        QuizRating retrievedRating = ratingDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedRating);
        assertEquals(Integer.valueOf(4), retrievedRating.getRating());
    }

    @Test
    public void testAddRatingUpdate() throws SQLException {
        // Create and add a sample rating
        QuizRating rating = createSampleRating(1, 1, 3);
        ratingDAO.addRating(rating);

        // Update the rating
        QuizRating updatedRating = createSampleRating(1, 1, 5);
        boolean updated = ratingDAO.addRating(updatedRating);

        // Verify
        assertTrue(updated);

        // Verify the rating was updated
        QuizRating retrievedRating = ratingDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedRating);
        assertEquals(Integer.valueOf(5), retrievedRating.getRating());
    }

    @Test
    public void testFindByUserAndQuiz() throws SQLException {
        // Create and add sample ratings
        QuizRating rating1 = createSampleRating(1, 1, 4);
        QuizRating rating2 = createSampleRating(1, 2, 5);
        QuizRating rating3 = createSampleRating(2, 1, 3);

        ratingDAO.addRating(rating1);
        ratingDAO.addRating(rating2);
        ratingDAO.addRating(rating3);

        // Find ratings
        QuizRating found1 = ratingDAO.findByUserAndQuiz(1, 1);
        QuizRating found2 = ratingDAO.findByUserAndQuiz(1, 2);
        QuizRating found3 = ratingDAO.findByUserAndQuiz(2, 1);
        QuizRating notFound = ratingDAO.findByUserAndQuiz(3, 3);

        // Verify
        assertNotNull(found1);
        assertEquals(Integer.valueOf(4), found1.getRating());

        assertNotNull(found2);
        assertEquals(Integer.valueOf(5), found2.getRating());

        assertNotNull(found3);
        assertEquals(Integer.valueOf(3), found3.getRating());

        assertNull(notFound);
    }

    @Test
    public void testFindByQuiz() throws SQLException {
        // Create and add sample ratings
        QuizRating rating1 = createSampleRating(1, 1, 4);
        QuizRating rating2 = createSampleRating(2, 1, 5);
        QuizRating rating3 = createSampleRating(3, 1, 3);
        QuizRating rating4 = createSampleRating(1, 2, 4);

        ratingDAO.addRating(rating1);
        ratingDAO.addRating(rating2);
        ratingDAO.addRating(rating3);
        ratingDAO.addRating(rating4);

        // Find ratings for quiz 1
        List<QuizRating> ratingsQuiz1 = ratingDAO.findByQuiz(1);
        List<QuizRating> ratingsQuiz2 = ratingDAO.findByQuiz(2);
        List<QuizRating> ratingsQuiz3 = ratingDAO.findByQuiz(3);

        // Verify
        assertEquals(3, ratingsQuiz1.size());
        assertEquals(1, ratingsQuiz2.size());
        assertEquals(0, ratingsQuiz3.size());
    }

    @Test
    public void testGetAverageRating() throws SQLException {
        // Create and add sample ratings
        QuizRating rating1 = createSampleRating(1, 1, 3);
        QuizRating rating2 = createSampleRating(2, 1, 4);
        QuizRating rating3 = createSampleRating(3, 1, 5);
        QuizRating rating4 = createSampleRating(1, 2, 2);

        ratingDAO.addRating(rating1);
        ratingDAO.addRating(rating2);
        ratingDAO.addRating(rating3);
        ratingDAO.addRating(rating4);

        // Get average ratings
        double avgQuiz1 = ratingDAO.getAverageRating(1);
        double avgQuiz2 = ratingDAO.getAverageRating(2);
        double avgQuiz3 = ratingDAO.getAverageRating(3);

        // Verify
        assertEquals(4.0, avgQuiz1, 0.001); // (3+4+5)/3 = 4
        assertEquals(2.0, avgQuiz2, 0.001);
        assertEquals(0.0, avgQuiz3, 0.001); // No ratings for quiz 3
    }

    @Test
    public void testGetRatingCount() throws SQLException {
        // Create and add sample ratings
        QuizRating rating1 = createSampleRating(1, 1, 4);
        QuizRating rating2 = createSampleRating(2, 1, 5);
        QuizRating rating3 = createSampleRating(3, 1, 3);
        QuizRating rating4 = createSampleRating(1, 2, 4);

        ratingDAO.addRating(rating1);
        ratingDAO.addRating(rating2);
        ratingDAO.addRating(rating3);
        ratingDAO.addRating(rating4);

        // Get rating counts
        int countQuiz1 = ratingDAO.getRatingCount(1);
        int countQuiz2 = ratingDAO.getRatingCount(2);
        int countQuiz3 = ratingDAO.getRatingCount(3);

        // Verify
        assertEquals(3, countQuiz1);
        assertEquals(1, countQuiz2);
        assertEquals(0, countQuiz3);
    }

    @Test
    public void testGetPopularQuizzes() throws SQLException {
        // Create and add sample ratings
        QuizRating rating1 = createSampleRating(1, 1, 3);
        QuizRating rating2 = createSampleRating(2, 1, 4);
        QuizRating rating3 = createSampleRating(3, 2, 5);
        QuizRating rating4 = createSampleRating(4, 2, 5);
        QuizRating rating5 = createSampleRating(5, 3, 2);

        ratingDAO.addRating(rating1);
        ratingDAO.addRating(rating2);
        ratingDAO.addRating(rating3);
        ratingDAO.addRating(rating4);
        ratingDAO.addRating(rating5);

        // Get popular quizzes (limited to 2)
        Map<Long, Double> popularQuizzes = ratingDAO.getPopularQuizzes(2);

        // Verify
        assertEquals(2, popularQuizzes.size());
        assertTrue(popularQuizzes.containsKey(2L)); // Quiz 2 has highest average (5)
        assertTrue(popularQuizzes.containsKey(1L) || popularQuizzes.containsKey(3L)); // Either quiz 1 (3.5) or quiz 3 (2)
    }

    @Test
    public void testDeleteRating() throws SQLException {
        // Create and add a sample rating
        QuizRating rating = createSampleRating(1, 1, 4);
        ratingDAO.addRating(rating);

        // Verify it exists
        QuizRating beforeDelete = ratingDAO.findByUserAndQuiz(1, 1);
        assertNotNull(beforeDelete);

        // Delete the rating
        boolean deleted = ratingDAO.deleteRating(1, 1);

        // Verify
        assertTrue(deleted);

        // Verify it no longer exists
        QuizRating afterDelete = ratingDAO.findByUserAndQuiz(1, 1);
        assertNull(afterDelete);
    }

    @Test
    public void testDeleteRatingNonExistent() throws SQLException {
        // Try to delete a non-existent rating
        boolean deleted = ratingDAO.deleteRating(999, 999);

        // Verify
        assertFalse(deleted);
    }
}