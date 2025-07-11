package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipRequestTest {

    private FriendshipRequest friendshipRequest;

    @BeforeEach
    void setUp() {
        friendshipRequest = new FriendshipRequest();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyFriendshipRequest() {
        assertNotNull(friendshipRequest);
        assertEquals(0, friendshipRequest.getId());
        assertEquals(0, friendshipRequest.getRequestSenderId());
        assertEquals(0, friendshipRequest.getRequestReceiverId());
        assertNull(friendshipRequest.getTimestamp());
    }

    @Test
    void parameterizedConstructor_WithSenderAndReceiver_ShouldSetCorrectly() {
        int senderId = 123;
        int receiverId = 456;

        FriendshipRequest paramRequest = new FriendshipRequest(senderId, receiverId);

        assertEquals(senderId, paramRequest.getRequestSenderId());
        assertEquals(receiverId, paramRequest.getRequestReceiverId());
        assertEquals(0, paramRequest.getId());
        assertNull(paramRequest.getTimestamp());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        int id = 789;
        int senderId = 111;
        int receiverId = 222;

        FriendshipRequest fullRequest = new FriendshipRequest(id, senderId, receiverId);

        assertEquals(id, fullRequest.getId());
        assertEquals(senderId, fullRequest.getRequestSenderId());
        assertEquals(receiverId, fullRequest.getRequestReceiverId());
        assertNull(fullRequest.getTimestamp());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        int expectedId = 999;
        friendshipRequest.setId(expectedId);
        assertEquals(expectedId, friendshipRequest.getId());
    }

    @Test
    void setRequestSenderId_ValidId_ShouldSetCorrectly() {
        int expectedId = 555;
        friendshipRequest.setRequestSenderId(expectedId);
        assertEquals(expectedId, friendshipRequest.getRequestSenderId());
    }

    @Test
    void setRequestReceiverId_ValidId_ShouldSetCorrectly() {
        int expectedId = 666;
        friendshipRequest.setRequestReceiverId(expectedId);
        assertEquals(expectedId, friendshipRequest.getRequestReceiverId());
    }

    @Test
    void setTimestamp_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());
        friendshipRequest.setTimestamp(expectedTimestamp);
        assertEquals(expectedTimestamp, friendshipRequest.getTimestamp());
    }

    @Test
    void setTimestamp_NullTimestamp_ShouldSetNull() {
        friendshipRequest.setTimestamp(null);
        assertNull(friendshipRequest.getTimestamp());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        int id = 100;
        int senderId = 200;
        int receiverId = 300;
        Timestamp timestamp = Timestamp.from(Instant.now());

        friendshipRequest.setId(id);
        friendshipRequest.setRequestSenderId(senderId);
        friendshipRequest.setRequestReceiverId(receiverId);
        friendshipRequest.setTimestamp(timestamp);

        assertEquals(id, friendshipRequest.getId());
        assertEquals(senderId, friendshipRequest.getRequestSenderId());
        assertEquals(receiverId, friendshipRequest.getRequestReceiverId());
        assertEquals(timestamp, friendshipRequest.getTimestamp());
    }
} 