package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizReviewDAO;
import com.freeuni.quiz.DTO.QuizReviewDTO;
import com.freeuni.quiz.bean.QuizReview;
import com.freeuni.quiz.converter.QuizReviewConverter;
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

public class QuizReviewServiceTest {

    @Mock
    private QuizReviewDAO reviewDAO;

    @Mock
    private DataSource dataSource;

    private QuizReviewService reviewService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reviewService = new QuizReviewService(dataSource);

        // Inject mocked DAO using reflection
        try {
            java.lang.reflect.Field field = QuizReviewService.class.getDeclaredField("reviewDAO");
            field.setAccessible(true);
            field.set(reviewService, reviewDAO);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to inject mocked DAO: " + e.getMessage());
        }
    }

    @Test
    public void testAddOrUpdateReview_ValidReview() throws SQLException {
        // Arrange
        when(reviewDAO.addReview(any(QuizReview.class))).thenReturn(true);

        // Act
        boolean result = reviewService.addOrUpdateReview(1, 1, "This is a test review");

        // Assert
        assertTrue(result);
        verify(reviewDAO).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_NullReviewText() throws SQLException {
        // Act
        boolean result = reviewService.addOrUpdateReview(1, 1, null);

        // Assert
        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_EmptyReviewText() throws SQLException {
        // Act
        boolean result = reviewService.addOrUpdateReview(1, 1, "");

        // Assert
        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_WhitespaceReviewText() throws SQLException {
        // Act
        boolean result = reviewService.addOrUpdateReview(1, 1, "   ");

        // Assert
        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testGetUserReview() throws SQLException {
        // Arrange
        QuizReview review = new QuizReview(1, 1L, "Test review");
        review.setId(1L);
        review.setCreatedAt(Timestamp.from(Instant.now()));
        review.setUpdatedAt(Timestamp.from(Instant.now()));

        when(reviewDAO.findByUserAndQuiz(1, 1)).thenReturn(review);

        // Act
        QuizReviewDTO result = reviewService.getUserReview(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getUserId());
        assertEquals(Long.valueOf(1), result.getQuizId());
        assertEquals("Test review", result.getReviewText());
        verify(reviewDAO).findByUserAndQuiz(1, 1);
    }

    @Test
    public void testGetUserReview_NotFound() throws SQLException {
        // Arrange
        when(reviewDAO.findByUserAndQuiz(1, 1)).thenReturn(null);

        // Act
        QuizReviewDTO result = reviewService.getUserReview(1, 1);

        // Assert
        assertNull(result);
        verify(reviewDAO).findByUserAndQuiz(1, 1);
    }

    @Test
    public void testGetQuizReviews() throws SQLException {
        // Arrange
        List<QuizReview> reviews = new ArrayList<>();

        QuizReview review1 = new QuizReview(1, 1L, "Review 1");
        review1.setId(1L);
        review1.setCreatedAt(Timestamp.from(Instant.now()));
        review1.setUpdatedAt(Timestamp.from(Instant.now()));
        review1.setUserName("User1");
        review1.setUserImageURL("http://example.com/user1.jpg");

        QuizReview review2 = new QuizReview(2, 1L, "Review 2");
        review2.setId(2L);
        review2.setCreatedAt(Timestamp.from(Instant.now()));
        review2.setUpdatedAt(Timestamp.from(Instant.now()));
        review2.setUserName("User2");
        review2.setUserImageURL("http://example.com/user2.jpg");

        reviews.add(review1);
        reviews.add(review2);

        when(reviewDAO.findByQuiz(1L)).thenReturn(reviews);

        // Act
        List<QuizReviewDTO> result = reviewService.getQuizReviews(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Review 1", result.get(0).getReviewText());
        assertEquals("User1", result.get(0).getUserName());
        assertEquals("Review 2", result.get(1).getReviewText());
        assertEquals("User2", result.get(1).getUserName());
        verify(reviewDAO).findByQuiz(1L);
    }

    @Test
    public void testGetQuizReviews_NoReviews() throws SQLException {
        // Arrange
        when(reviewDAO.findByQuiz(1L)).thenReturn(new ArrayList<>());

        // Act
        List<QuizReviewDTO> result = reviewService.getQuizReviews(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewDAO).findByQuiz(1L);
    }

    @Test
    public void testDeleteReview() throws SQLException {
        // Arrange
        when(reviewDAO.deleteReview(1, 1)).thenReturn(true);

        // Act
        boolean result = reviewService.deleteReview(1, 1);

        // Assert
        assertTrue(result);
        verify(reviewDAO).deleteReview(1, 1);
    }

    @Test
    public void testDeleteReview_NotFound() throws SQLException {
        // Arrange
        when(reviewDAO.deleteReview(1, 1)).thenReturn(false);

        // Act
        boolean result = reviewService.deleteReview(1, 1);

        // Assert
        assertFalse(result);
        verify(reviewDAO).deleteReview(1, 1);
    }
}