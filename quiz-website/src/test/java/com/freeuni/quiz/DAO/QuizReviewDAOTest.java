package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizReview;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class QuizReviewDAOTest {
    private static BasicDataSource dataSource;
    private QuizReviewDAO reviewDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE quiz_reviews (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "quiz_id BIGINT NOT NULL," +
                    "review_text TEXT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(user_id, quiz_id)" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        reviewDAO = new QuizReviewDAO(dataSource);

        // Clear reviews table before each test
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM quiz_reviews");
        }
    }

    private QuizReview createSampleReview(int userId, long quizId, String reviewText) {
        QuizReview review = new QuizReview();
        review.setUserId(userId);
        review.setQuizId(quizId);
        review.setReviewText(reviewText);
        return review;
    }

    @Test
    public void testAddReview() throws SQLException {
        // Create a sample review
        QuizReview review = createSampleReview(1, 1, "Great quiz!");

        // Add the review
        boolean added = reviewDAO.addReview(review);

        // Verify
        assertTrue(added);
        assertTrue(review.getId() > 0);

        // Verify the review is retrievable
        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Great quiz!", retrievedReview.getReviewText());
    }

    @Test
    public void testAddReviewUpdate() throws SQLException {
        // Create and add a sample review
        QuizReview review = createSampleReview(1, 1, "Initial review.");
        reviewDAO.addReview(review);

        // Update the review
        QuizReview updatedReview = createSampleReview(1, 1, "Updated review!");
        boolean updated = reviewDAO.addReview(updatedReview);

        // Verify
        assertTrue(updated);

        // Verify the review was updated
        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Updated review!", retrievedReview.getReviewText());
    }

    @Test
    public void testFindByUserAndQuiz() throws SQLException {
        // Create and add sample reviews
        QuizReview review1 = createSampleReview(1, 1, "Review 1");
        QuizReview review2 = createSampleReview(1, 2, "Review 2");
        QuizReview review3 = createSampleReview(2, 1, "Review 3");

        reviewDAO.addReview(review1);
        reviewDAO.addReview(review2);
        reviewDAO.addReview(review3);

        // Find reviews
        QuizReview found1 = reviewDAO.findByUserAndQuiz(1, 1);
        QuizReview found2 = reviewDAO.findByUserAndQuiz(1, 2);
        QuizReview found3 = reviewDAO.findByUserAndQuiz(2, 1);
        QuizReview notFound = reviewDAO.findByUserAndQuiz(3, 3);

        // Verify
        assertNotNull(found1);
        assertEquals("Review 1", found1.getReviewText());

        assertNotNull(found2);
        assertEquals("Review 2", found2.getReviewText());

        assertNotNull(found3);
        assertEquals("Review 3", found3.getReviewText());

        assertNull(notFound);
    }

    @Test
    public void testFindByQuiz() throws SQLException {
        // Create and add sample reviews
        QuizReview review1 = createSampleReview(1, 1, "First review");
        QuizReview review2 = createSampleReview(2, 1, "Second review");
        QuizReview review3 = createSampleReview(3, 1, "Third review");
        QuizReview review4 = createSampleReview(1, 2, "Another quiz review");

        reviewDAO.addReview(review1);
        reviewDAO.addReview(review2);
        reviewDAO.addReview(review3);
        reviewDAO.addReview(review4);

        // Find reviews for quiz 1
        List<QuizReview> reviewsQuiz1 = reviewDAO.findByQuiz(1);
        List<QuizReview> reviewsQuiz2 = reviewDAO.findByQuiz(2);
        List<QuizReview> reviewsQuiz3 = reviewDAO.findByQuiz(3);

        // Verify
        assertEquals(3, reviewsQuiz1.size());
        assertEquals(1, reviewsQuiz2.size());
        assertEquals(0, reviewsQuiz3.size());
    }

    @Test
    public void testUpdateReview() throws SQLException {
        // Create and add a sample review
        QuizReview review = createSampleReview(1, 1, "Initial review text");
        reviewDAO.addReview(review);

        // Update the review
        review.setReviewText("Updated review text");
        boolean updated = reviewDAO.updateReview(review);

        // Verify
        assertTrue(updated);

        // Verify the review was updated
        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Updated review text", retrievedReview.getReviewText());
    }

    @Test
    public void testDeleteReview() throws SQLException {
        // Create and add a sample review
        QuizReview review = createSampleReview(1, 1, "Review to be deleted");
        reviewDAO.addReview(review);

        // Verify it exists
        QuizReview beforeDelete = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(beforeDelete);

        // Delete the review
        boolean deleted = reviewDAO.deleteReview(1, 1);

        // Verify
        assertTrue(deleted);

        // Verify it no longer exists
        QuizReview afterDelete = reviewDAO.findByUserAndQuiz(1, 1);
        assertNull(afterDelete);
    }

    @Test
    public void testDeleteReviewNonExistent() throws SQLException {
        // Try to delete a non-existent review
        boolean deleted = reviewDAO.deleteReview(999, 999);

        // Verify
        assertFalse(deleted);
    }

    @Test
    public void testLongReviewText() throws SQLException {
        // Create a very long review text
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is a long review text that should be stored properly. ");
        }
        String veryLongText = longText.toString();

        // Create and add a review with long text
        QuizReview review = createSampleReview(1, 1, veryLongText);
        reviewDAO.addReview(review);

        // Retrieve the review
        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);

        // Verify the long text was stored correctly
        assertEquals(veryLongText, retrievedReview.getReviewText());
    }
}