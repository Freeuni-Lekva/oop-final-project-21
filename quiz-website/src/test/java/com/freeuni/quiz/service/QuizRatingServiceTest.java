package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.impl.QuizRatingDAOImpl;
import com.freeuni.quiz.DTO.QuizRatingDTO;
import com.freeuni.quiz.bean.QuizRating;
import com.freeuni.quiz.converter.QuizRatingConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QuizRatingServiceTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private QuizRatingDAOImpl ratingDAO;
    
    private QuizRatingService ratingService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(resultSet.next()).thenReturn(false);
        
        ratingService = new QuizRatingService(dataSource);
    }

    @Test
    void rateQuiz_WithRatingTooLow_ShouldReturnFalse() throws SQLException {
        int userId = 123;
        long quizId = 456L;
        int rating = 0;
        
        boolean result = ratingService.rateQuiz(userId, quizId, rating);
        
        assertFalse(result);
    }

    @Test
    void rateQuiz_WithRatingTooHigh_ShouldReturnFalse() throws SQLException {
        int userId = 123;
        long quizId = 456L;
        int rating = 6;
        
        boolean result = ratingService.rateQuiz(userId, quizId, rating);
        
        assertFalse(result);
    }

    @Test
    void rateQuiz_WithNegativeRating_ShouldReturnFalse() throws SQLException {
        int userId = 123;
        long quizId = 456L;
        int rating = -1;
        
        boolean result = ratingService.rateQuiz(userId, quizId, rating);
        
        assertFalse(result);
    }

    @Test
    void rateQuiz_WithVeryHighRating_ShouldReturnFalse() throws SQLException {
        int userId = 123;
        long quizId = 456L;
        int rating = 10;
        
        boolean result = ratingService.rateQuiz(userId, quizId, rating);
        
        assertFalse(result);
    }

    @Test
    void constructor_ShouldInitializeWithDataSource() {
        QuizRatingService service = new QuizRatingService(dataSource);
        
        assertNotNull(service);
    }
} 