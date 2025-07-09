package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DAO.impl.QuizReviewDAOImpl;
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
    private QuizReviewDAOImpl reviewDAO;

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
        reviewDAO = new QuizReviewDAOImpl(dataSource);

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
        QuizReview review = createSampleReview(1, 1, "Great quiz!");

        boolean added = reviewDAO.addReview(review);

        assertTrue(added);
        assertTrue(review.getId() > 0);

        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Great quiz!", retrievedReview.getReviewText());
    }

    @Test
    public void testAddReviewUpdate() throws SQLException {
        QuizReview review = createSampleReview(1, 1, "Initial review.");
        reviewDAO.addReview(review);

        QuizReview updatedReview = createSampleReview(1, 1, "Updated review!");
        boolean updated = reviewDAO.addReview(updatedReview);

        assertTrue(updated);

        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Updated review!", retrievedReview.getReviewText());
    }

    @Test
    public void testFindByUserAndQuiz() throws SQLException {
        QuizReview review1 = createSampleReview(1, 1, "Review 1");
        QuizReview review2 = createSampleReview(1, 2, "Review 2");
        QuizReview review3 = createSampleReview(2, 1, "Review 3");

        reviewDAO.addReview(review1);
        reviewDAO.addReview(review2);
        reviewDAO.addReview(review3);

        QuizReview found1 = reviewDAO.findByUserAndQuiz(1, 1);
        QuizReview found2 = reviewDAO.findByUserAndQuiz(1, 2);
        QuizReview found3 = reviewDAO.findByUserAndQuiz(2, 1);
        QuizReview notFound = reviewDAO.findByUserAndQuiz(3, 3);

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
        QuizReview review1 = createSampleReview(1, 1, "First review");
        QuizReview review2 = createSampleReview(2, 1, "Second review");
        QuizReview review3 = createSampleReview(3, 1, "Third review");
        QuizReview review4 = createSampleReview(1, 2, "Another quiz review");

        reviewDAO.addReview(review1);
        reviewDAO.addReview(review2);
        reviewDAO.addReview(review3);
        reviewDAO.addReview(review4);

        List<QuizReview> reviewsQuiz1 = reviewDAO.findByQuiz(1);
        List<QuizReview> reviewsQuiz2 = reviewDAO.findByQuiz(2);
        List<QuizReview> reviewsQuiz3 = reviewDAO.findByQuiz(3);

        assertEquals(3, reviewsQuiz1.size());
        assertEquals(1, reviewsQuiz2.size());
        assertEquals(0, reviewsQuiz3.size());
    }

    @Test
    public void testUpdateReview() throws SQLException {
        QuizReview review = createSampleReview(1, 1, "Initial review text");
        reviewDAO.addReview(review);

        review.setReviewText("Updated review text");
        boolean updated = reviewDAO.updateReview(review);

        assertTrue(updated);

        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(retrievedReview);
        assertEquals("Updated review text", retrievedReview.getReviewText());
    }

    @Test
    public void testDeleteReview() throws SQLException {
        QuizReview review = createSampleReview(1, 1, "Review to be deleted");
        reviewDAO.addReview(review);

        QuizReview beforeDelete = reviewDAO.findByUserAndQuiz(1, 1);
        assertNotNull(beforeDelete);

        boolean deleted = reviewDAO.deleteReview(1, 1);

        assertTrue(deleted);

        QuizReview afterDelete = reviewDAO.findByUserAndQuiz(1, 1);
        assertNull(afterDelete);
    }

    @Test
    public void testDeleteReviewNonExistent() throws SQLException {
        boolean deleted = reviewDAO.deleteReview(999, 999);

        assertFalse(deleted);
    }

    @Test
    public void testLongReviewText() throws SQLException {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is a long review text that should be stored properly. ");
        }
        String veryLongText = longText.toString();

        QuizReview review = createSampleReview(1, 1, veryLongText);
        reviewDAO.addReview(review);

        QuizReview retrievedReview = reviewDAO.findByUserAndQuiz(1, 1);

        assertEquals(veryLongText, retrievedReview.getReviewText());
    }
}