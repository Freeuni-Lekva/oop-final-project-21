package com.freeuni.quiz.util;

import com.freeuni.quiz.bean.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SessionManagerTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpSession session;
    
    private SessionManager sessionManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        
        lenient().when(request.getSession()).thenReturn(session);
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(request.getSession(true)).thenReturn(session);
    }

    @Test
    void getCurrentUser_UserInSession_ShouldReturnUser() {
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        User result = sessionManager.getCurrentUser(request);

        assertEquals(testUser, result);
        verify(session).getAttribute("currentUser");
    }

    @Test
    void getCurrentUser_NoUserInSession_ShouldReturnNull() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        User result = sessionManager.getCurrentUser(request);

        assertNull(result);
        verify(session).getAttribute("currentUser");
    }

    @Test
    void getCurrentUser_NoSession_ShouldReturnNull() {
        when(request.getSession(false)).thenReturn(null);

        User result = sessionManager.getCurrentUser(request);

        assertNull(result);
        verify(request).getSession(false);
    }

    @Test
    void setCurrentUser_ValidUser_ShouldSetUserInSession() {
        sessionManager.setCurrentUser(request, testUser);

        verify(request).getSession(true);
        verify(session).setAttribute("currentUser", testUser);
    }

    @Test
    void setCurrentUser_NullUser_ShouldSetNullInSession() {
        sessionManager.setCurrentUser(request, null);

        verify(request).getSession(true);
        verify(session).setAttribute("currentUser", null);
    }

    @Test
    void isUserLoggedIn_UserInSession_ShouldReturnTrue() {
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        boolean result = sessionManager.isUserLoggedIn(request);

        assertTrue(result);
        verify(session).getAttribute("currentUser");
    }

    @Test
    void isUserLoggedIn_NoUserInSession_ShouldReturnFalse() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        boolean result = sessionManager.isUserLoggedIn(request);

        assertFalse(result);
        verify(session).getAttribute("currentUser");
    }

    @Test
    void isUserLoggedIn_NoSession_ShouldReturnFalse() {
        when(request.getSession(false)).thenReturn(null);

        boolean result = sessionManager.isUserLoggedIn(request);

        assertFalse(result);
        verify(request).getSession(false);
    }

    @Test
    void clearSession_ExistingSession_ShouldInvalidateSession() {
        sessionManager.clearSession(request);

        verify(request).getSession(false);
        verify(session).invalidate();
    }

    @Test
    void clearSession_NoSession_ShouldNotThrowException() {
        when(request.getSession(false)).thenReturn(null);

        assertDoesNotThrow(() -> sessionManager.clearSession(request));
        verify(request).getSession(false);
        verify(session, never()).invalidate();
    }

    @Test
    void setCurrentUser_CreatesNewSession_ShouldWork() {
        when(request.getSession(true)).thenReturn(session);

        sessionManager.setCurrentUser(request, testUser);

        verify(request).getSession(true);
        verify(session).setAttribute("currentUser", testUser);
    }

    @Test
    void getCurrentUser_UserWithDifferentId_ShouldReturnCorrectUser() {
        User differentUser = new User();
        differentUser.setId(999);
        differentUser.setUserName("differentuser");
        when(session.getAttribute("currentUser")).thenReturn(differentUser);

        User result = sessionManager.getCurrentUser(request);

        assertEquals(differentUser, result);
        assertEquals(999, result.getId());
        assertEquals("differentuser", result.getUserName());
    }

    @Test
    void isUserLoggedIn_CallsGetCurrentUser() {
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        boolean result = sessionManager.isUserLoggedIn(request);

        assertTrue(result);
        verify(request).getSession(false);
        verify(session).getAttribute("currentUser");
    }

    @Test
    void setCurrentUser_WithCompleteUser_ShouldSetAllProperties() {
        User completeUser = new User();
        completeUser.setId(123);
        completeUser.setUserName("completeuser");
        completeUser.setEmail("complete@example.com");
        completeUser.setFirstName("Complete");
        completeUser.setLastName("User");
        completeUser.setBio("Test bio");

        sessionManager.setCurrentUser(request, completeUser);

        verify(request).getSession(true);
        verify(session).setAttribute("currentUser", completeUser);
    }

    @Test
    void getCurrentUser_ReturnsExactSameObject() {
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        User result = sessionManager.getCurrentUser(request);

        assertSame(testUser, result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUserName(), result.getUserName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }
} 