package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.impl.QuizReviewDAOImpl;
import com.freeuni.quiz.DTO.QuizReviewDTO;
import com.freeuni.quiz.bean.QuizReview;
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
    private QuizReviewDAOImpl reviewDAO;

    @Mock
    private DataSource dataSource;

    private QuizReviewService reviewService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reviewService = new QuizReviewService(dataSource);

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
        when(reviewDAO.addReview(any(QuizReview.class))).thenReturn(true);

        boolean result = reviewService.addOrUpdateReview(1, 1, "This is a test review");

        assertTrue(result);
        verify(reviewDAO).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_NullReviewText() throws SQLException {
        boolean result = reviewService.addOrUpdateReview(1, 1, null);

        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_EmptyReviewText() throws SQLException {
        boolean result = reviewService.addOrUpdateReview(1, 1, "");

        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testAddOrUpdateReview_WhitespaceReviewText() throws SQLException {
        boolean result = reviewService.addOrUpdateReview(1, 1, "   ");

        assertFalse(result);
        verify(reviewDAO, never()).addReview(any(QuizReview.class));
    }

    @Test
    public void testGetUserReview() throws SQLException {
        QuizReview review = new QuizReview(1, 1L, "Test review");
        review.setId(1L);
        review.setCreatedAt(Timestamp.from(Instant.now()));
        review.setUpdatedAt(Timestamp.from(Instant.now()));

        when(reviewDAO.findByUserAndQuiz(1, 1)).thenReturn(review);

        QuizReviewDTO result = reviewService.getUserReview(1, 1);

        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getUserId());
        assertEquals(Long.valueOf(1), result.getQuizId());
        assertEquals("Test review", result.getReviewText());
        verify(reviewDAO).findByUserAndQuiz(1, 1);
    }

    @Test
    public void testGetUserReview_NotFound() throws SQLException {
        when(reviewDAO.findByUserAndQuiz(1, 1)).thenReturn(null);

        QuizReviewDTO result = reviewService.getUserReview(1, 1);

        assertNull(result);
        verify(reviewDAO).findByUserAndQuiz(1, 1);
    }

    @Test
    public void testGetQuizReviews() throws SQLException {
        List<QuizReview> reviews = new ArrayList<>();

        QuizReview review1 = new QuizReview(1, 1L, "Review 1");
        review1.setId(1L);
        review1.setCreatedAt(Timestamp.from(Instant.now()));
        review1.setUpdatedAt(Timestamp.from(Instant.now()));

        QuizReview review2 = new QuizReview(2, 1L, "Review 2");
        review2.setId(2L);
        review2.setCreatedAt(Timestamp.from(Instant.now()));
        review2.setUpdatedAt(Timestamp.from(Instant.now()));

        reviews.add(review1);
        reviews.add(review2);

        when(reviewDAO.findByQuiz(1L)).thenReturn(reviews);

        List<QuizReviewDTO> result = reviewService.getQuizReviews(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Review 1", result.get(0).getReviewText());
        assertEquals("Review 2", result.get(1).getReviewText());
        verify(reviewDAO).findByQuiz(1L);
    }

    @Test
    public void testGetQuizReviews_NoReviews() throws SQLException {
        when(reviewDAO.findByQuiz(1L)).thenReturn(new ArrayList<>());

        List<QuizReviewDTO> result = reviewService.getQuizReviews(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewDAO).findByQuiz(1L);
    }

    @Test
    public void testDeleteReview() throws SQLException {
        when(reviewDAO.deleteReview(1, 1)).thenReturn(true);

        boolean result = reviewService.deleteReview(1, 1);

        assertTrue(result);
        verify(reviewDAO).deleteReview(1, 1);
    }

    @Test
    public void testDeleteReview_NotFound() throws SQLException {
        when(reviewDAO.deleteReview(1, 1)).thenReturn(false);

        boolean result = reviewService.deleteReview(1, 1);

        assertFalse(result);
        verify(reviewDAO).deleteReview(1, 1);
    }
}