package com.freeuni.quiz.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendshipManagerTest {

    private FriendshipRequestService mockRequestService;
    private FriendshipService mockFriendshipService;
    private FriendshipManager friendshipManager;

    @BeforeEach
    void setUp() {
        mockRequestService = mock(FriendshipRequestService.class);
        mockFriendshipService = mock(FriendshipService.class);
        friendshipManager = new FriendshipManager(mockRequestService, mockFriendshipService);
    }

    @Test
    void testPrivateFieldInjectionUsingReflection() throws Exception {
        Field requestServiceField = FriendshipManager.class.getDeclaredField("friendshipRequestService");
        requestServiceField.setAccessible(true);
        Object requestServiceValue = requestServiceField.get(friendshipManager);
        assertEquals(mockRequestService, requestServiceValue);

        Field serviceField = FriendshipManager.class.getDeclaredField("friendshipService");
        serviceField.setAccessible(true);
        Object friendshipServiceValue = serviceField.get(friendshipManager);
        assertEquals(mockFriendshipService, friendshipServiceValue);
    }

    @Test
    void testAcceptFriendRequest_RequestExists_ShouldSucceed() throws Exception {
        when(mockRequestService.requestExists(1, 2)).thenReturn(true);
        when(mockFriendshipService.addFriendship(1, 2)).thenReturn(true);

        boolean result = friendshipManager.acceptFriendRequest(1, 2, 10);

        assertTrue(result);
        verify(mockRequestService).cancelRequest(10);
        verify(mockFriendshipService).addFriendship(1, 2);
    }

    @Test
    void testAcceptFriendRequest_RequestDoesNotExist_ShouldFail() throws Exception {
        when(mockRequestService.requestExists(1, 2)).thenReturn(false);

        boolean result = friendshipManager.acceptFriendRequest(1, 2, 10);

        assertFalse(result);
        verify(mockRequestService, never()).cancelRequest(anyInt());
        verify(mockFriendshipService, never()).addFriendship(anyInt(), anyInt());
    }

    @Test
    void testDeclineFriendRequest_RequestExists_ShouldSucceed() throws Exception {
        when(mockRequestService.requestExists(3, 4)).thenReturn(true);

        boolean result = friendshipManager.declineFriendRequest(3, 4, 20);

        assertTrue(result);
        verify(mockRequestService).cancelRequest(20);
    }

    @Test
    void testDeclineFriendRequest_RequestDoesNotExist_ShouldFail() throws Exception {
        when(mockRequestService.requestExists(3, 4)).thenReturn(false);

        boolean result = friendshipManager.declineFriendRequest(3, 4, 20);

        assertFalse(result);
        verify(mockRequestService, never()).cancelRequest(anyInt());
    }
}
