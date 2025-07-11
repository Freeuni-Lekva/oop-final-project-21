package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.QuizChallengeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QuizChallengeManagerTest {

    @Mock
    private QuizChallengeService challengeService;
    
    @Mock
    private FriendshipService friendshipService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private QuizService quizService;
    
    private QuizChallengeManager challengeManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        challengeManager = new QuizChallengeManager(challengeService, friendshipService);
    }

    @Test
    void sendChallengeToFriend_WhenUsersAreFriends_ShouldSendChallenge() throws SQLException {
        int challengerId = 123;
        int friendId = 456;
        Long quizId = 789L;
        String message = "Test challenge";
        
        when(friendshipService.areFriends(challengerId, friendId)).thenReturn(true);
        when(challengeService.sendChallenge(challengerId, friendId, quizId, message)).thenReturn(true);
        
        boolean result = challengeManager.sendChallengeToFriend(challengerId, friendId, quizId, message);
        
        assertTrue(result);
        verify(friendshipService).areFriends(challengerId, friendId);
        verify(challengeService).sendChallenge(challengerId, friendId, quizId, message);
    }

    @Test
    void sendChallengeToFriend_WhenUsersAreNotFriends_ShouldReturnFalse() throws SQLException {
        int challengerId = 123;
        int friendId = 456;
        Long quizId = 789L;
        String message = "Test challenge";
        
        when(friendshipService.areFriends(challengerId, friendId)).thenReturn(false);
        
        boolean result = challengeManager.sendChallengeToFriend(challengerId, friendId, quizId, message);
        
        assertFalse(result);
        verify(friendshipService).areFriends(challengerId, friendId);
        verify(challengeService, never()).sendChallenge(anyInt(), anyInt(), anyLong(), anyString());
    }

    @Test
    void sendChallengeToFriend_WhenChallengeServiceFails_ShouldReturnFalse() throws SQLException {
        int challengerId = 123;
        int friendId = 456;
        Long quizId = 789L;
        String message = "Test challenge";
        
        when(friendshipService.areFriends(challengerId, friendId)).thenReturn(true);
        when(challengeService.sendChallenge(challengerId, friendId, quizId, message)).thenReturn(false);
        
        boolean result = challengeManager.sendChallengeToFriend(challengerId, friendId, quizId, message);
        
        assertFalse(result);
        verify(friendshipService).areFriends(challengerId, friendId);
        verify(challengeService).sendChallenge(challengerId, friendId, quizId, message);
    }

    @Test
    void sendChallengeToFriend_WhenSQLExceptionThrown_ShouldThrowRuntimeException() throws SQLException {
        int challengerId = 123;
        int friendId = 456;
        Long quizId = 789L;
        String message = "Test challenge";
        
        when(friendshipService.areFriends(challengerId, friendId)).thenThrow(new SQLException("Database error"));
        
        assertThrows(RuntimeException.class, () -> {
            challengeManager.sendChallengeToFriend(challengerId, friendId, quizId, message);
        });
        
        verify(friendshipService).areFriends(challengerId, friendId);
        verify(challengeService, never()).sendChallenge(anyInt(), anyInt(), anyLong(), anyString());
    }

    @Test
    void acceptChallengeAndGetQuizUrl_WhenChallengeAccepted_ShouldReturnQuizUrl() {
        Long challengeId = 123L;
        String expectedUrl = "https://example.com/quiz/789";
        QuizChallengeDTO challengeDTO = mock(QuizChallengeDTO.class);
        
        when(challengeService.acceptChallenge(challengeId)).thenReturn(true);
        when(challengeService.getChallengeById(challengeId, userService, quizService)).thenReturn(Optional.of(challengeDTO));
        when(challengeDTO.getQuizUrl()).thenReturn(expectedUrl);
        
        String result = challengeManager.acceptChallengeAndGetQuizUrl(challengeId, userService, quizService);
        
        assertEquals(expectedUrl, result);
        verify(challengeService).acceptChallenge(challengeId);
        verify(challengeService).getChallengeById(challengeId, userService, quizService);
    }

    @Test
    void acceptChallengeAndGetQuizUrl_WhenChallengeNotFound_ShouldReturnNull() {
        Long challengeId = 123L;
        
        when(challengeService.acceptChallenge(challengeId)).thenReturn(true);
        when(challengeService.getChallengeById(challengeId, userService, quizService)).thenReturn(Optional.empty());
        
        String result = challengeManager.acceptChallengeAndGetQuizUrl(challengeId, userService, quizService);
        
        assertNull(result);
        verify(challengeService).acceptChallenge(challengeId);
        verify(challengeService).getChallengeById(challengeId, userService, quizService);
    }

    @Test
    void acceptChallengeAndGetQuizUrl_WhenChallengeNotAccepted_ShouldReturnNull() {
        Long challengeId = 123L;
        
        when(challengeService.acceptChallenge(challengeId)).thenReturn(false);
        
        String result = challengeManager.acceptChallengeAndGetQuizUrl(challengeId, userService, quizService);
        
        assertNull(result);
        verify(challengeService).acceptChallenge(challengeId);
        verify(challengeService, never()).getChallengeById(anyLong(), any(), any());
    }

    @Test
    void constructor_ShouldInitializeWithServices() {
        QuizChallengeManager manager = new QuizChallengeManager(challengeService, friendshipService);
        
        assertNotNull(manager);
    }
} 