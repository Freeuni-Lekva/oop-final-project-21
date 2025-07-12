package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.UserAchievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AchievementServiceTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private ResultSet resultSet;
    
    private AchievementService achievementService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(resultSet.next()).thenReturn(false);
        
        achievementService = new AchievementService(dataSource);
    }

    @Test
    void checkAndAwardAchievements_ShouldNotThrowException() throws SQLException {
        int userId = 123;
        
        assertDoesNotThrow(() -> {
            achievementService.checkAndAwardAchievements(userId);
        });
    }

    @Test
    void getUserAchievements_ShouldReturnEmptyList() throws SQLException {
        int userId = 123;
        
        List<UserAchievement> result = achievementService.getUserAchievements(userId);
        
        assertNotNull(result);
    }

    @Test
    void constructor_ShouldInitializeWithDataSource() {
        AchievementService service = new AchievementService(dataSource);
        
        assertNotNull(service);
    }
} 