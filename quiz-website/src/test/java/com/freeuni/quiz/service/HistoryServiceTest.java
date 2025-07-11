package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.HistoryDAO;
import com.freeuni.quiz.DAO.impl.HistoryDAOImpl;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.UserHistoryDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HistoryServiceTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private HistoryDAO historyDAO;
    
    @Mock
    private QuizService quizService;
    
    private HistoryService historyService;

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
        
        historyService = new HistoryService(dataSource);
    }

    @Test
    void getUserHistory_ShouldReturnUserHistoryDTO() {
        int userId = 123;
        UserDTO user = mock(UserDTO.class);
        
        UserHistoryDTO result = historyService.getUserHistory(userId, user);
        
        assertNotNull(result);
    }

    @Test
    void getUserRecentCompletions_ShouldReturnRecentCompletions() {
        int userId = 123;
        int limit = 5;
        
        List<QuizCompletion> result = historyService.getUserRecentCompletions(userId, limit);
        
        assertNotNull(result);
    }

    @Test
    void getTotalQuizzesTaken_ShouldReturnTotalCount() {
        int userId = 123;
        
        int result = historyService.getTotalQuizzesTaken(userId);
        
        assertTrue(result >= 0);
    }

    @Test
    void getAverageScore_ShouldReturnAverageScore() {
        int userId = 123;
        
        double result = historyService.getAverageScore(userId);
        
        assertTrue(result >= 0);
    }

    @Test
    void getCategoryDistribution_ShouldReturnDistribution() {
        int userId = 123;
        
        Map<String, Integer> result = historyService.getCategoryDistribution(userId);
        
        assertNotNull(result);
    }

    @Test
    void getCompletionQuizMap_ShouldReturnMappedCompletions() {
        List<QuizCompletion> completions = Arrays.asList(
            createCompletion(1L),
            createCompletion(2L)
        );
        
        Map<QuizCompletion, Quiz> result = historyService.getCompletionQuizMap(completions);
        
        assertNotNull(result);
    }

    @Test
    void getQuizCompletionsMap_ShouldReturnMappedCompletions() {
        int userId = 123;
        
        Map<Quiz, List<QuizCompletion>> result = historyService.getQuizCompletionsMap(userId);
        
        assertNotNull(result);
    }

    @Test
    void constructor_ShouldInitializeWithDataSource() {
        HistoryService service = new HistoryService(dataSource);
        
        assertNotNull(service);
    }

    private QuizCompletion createCompletion(Long testId) {
        QuizCompletion completion = new QuizCompletion();
        completion.setTestId(testId);
        return completion;
    }
} 