package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipTest {

    private Friendship friendship;

    @BeforeEach
    void setUp() {
        friendship = new Friendship();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyFriendship() {
        assertNotNull(friendship);
        assertEquals(0, friendship.getId());
        assertEquals(0, friendship.getFriendSenderId());
        assertEquals(0, friendship.getFriendReceiverId());
    }

    @Test
    void parameterizedConstructor_WithSenderAndReceiver_ShouldSetCorrectly() {
        int senderId = 123;
        int receiverId = 456;

        Friendship paramFriendship = new Friendship(senderId, receiverId);

        assertEquals(senderId, paramFriendship.getFriendSenderId());
        assertEquals(receiverId, paramFriendship.getFriendReceiverId());
        assertEquals(0, paramFriendship.getId());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        int id = 789;
        int senderId = 111;
        int receiverId = 222;

        Friendship fullFriendship = new Friendship(id, senderId, receiverId);

        assertEquals(id, fullFriendship.getId());
        assertEquals(senderId, fullFriendship.getFriendSenderId());
        assertEquals(receiverId, fullFriendship.getFriendReceiverId());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        int expectedId = 999;

        friendship.setId(expectedId);

        assertEquals(expectedId, friendship.getId());
    }

    @Test
    void setId_ZeroId_ShouldSetCorrectly() {
        int expectedId = 0;

        friendship.setId(expectedId);

        assertEquals(expectedId, friendship.getId());
    }

    @Test
    void setId_NegativeId_ShouldSetCorrectly() {
        int expectedId = -1;

        friendship.setId(expectedId);

        assertEquals(expectedId, friendship.getId());
    }

    @Test
    void setFriendSenderId_ValidId_ShouldSetCorrectly() {
        int expectedId = 555;

        friendship.setFriendSenderId(expectedId);

        assertEquals(expectedId, friendship.getFriendSenderId());
    }

    @Test
    void setFriendSenderId_ZeroId_ShouldSetCorrectly() {
        int expectedId = 0;

        friendship.setFriendSenderId(expectedId);

        assertEquals(expectedId, friendship.getFriendSenderId());
    }

    @Test
    void setFriendSenderId_NegativeId_ShouldSetCorrectly() {
        int expectedId = -10;

        friendship.setFriendSenderId(expectedId);

        assertEquals(expectedId, friendship.getFriendSenderId());
    }

    @Test
    void setFriendReceiverId_ValidId_ShouldSetCorrectly() {
        int expectedId = 666;

        friendship.setFriendReceiverId(expectedId);

        assertEquals(expectedId, friendship.getFriendReceiverId());
    }

    @Test
    void setFriendReceiverId_ZeroId_ShouldSetCorrectly() {
        int expectedId = 0;

        friendship.setFriendReceiverId(expectedId);

        assertEquals(expectedId, friendship.getFriendReceiverId());
    }

    @Test
    void setFriendReceiverId_NegativeId_ShouldSetCorrectly() {
        int expectedId = -20;

        friendship.setFriendReceiverId(expectedId);

        assertEquals(expectedId, friendship.getFriendReceiverId());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        int id = 100;
        int senderId = 200;
        int receiverId = 300;

        friendship.setId(id);
        friendship.setFriendSenderId(senderId);
        friendship.setFriendReceiverId(receiverId);

        assertEquals(id, friendship.getId());
        assertEquals(senderId, friendship.getFriendSenderId());
        assertEquals(receiverId, friendship.getFriendReceiverId());
    }

    @Test
    void parameterizedConstructor_WithZeroValues_ShouldSetCorrectly() {
        Friendship zeroFriendship = new Friendship(0, 0);

        assertEquals(0, zeroFriendship.getFriendSenderId());
        assertEquals(0, zeroFriendship.getFriendReceiverId());
        assertEquals(0, zeroFriendship.getId());
    }

    @Test
    void parameterizedConstructor_WithNegativeValues_ShouldSetCorrectly() {
        Friendship negativeFriendship = new Friendship(-1, -2);

        assertEquals(-1, negativeFriendship.getFriendSenderId());
        assertEquals(-2, negativeFriendship.getFriendReceiverId());
        assertEquals(0, negativeFriendship.getId());
    }

    @Test
    void fullParameterizedConstructor_WithNegativeValues_ShouldSetCorrectly() {
        Friendship negativeFullFriendship = new Friendship(-10, -20, -30);

        assertEquals(-10, negativeFullFriendship.getId());
        assertEquals(-20, negativeFullFriendship.getFriendSenderId());
        assertEquals(-30, negativeFullFriendship.getFriendReceiverId());
    }

    @Test
    void setFields_LargeValues_ShouldHandleCorrectly() {
        int largeId = Integer.MAX_VALUE;
        int largeSenderId = Integer.MAX_VALUE - 1;
        int largeReceiverId = Integer.MAX_VALUE - 2;

        friendship.setId(largeId);
        friendship.setFriendSenderId(largeSenderId);
        friendship.setFriendReceiverId(largeReceiverId);

        assertEquals(largeId, friendship.getId());
        assertEquals(largeSenderId, friendship.getFriendSenderId());
        assertEquals(largeReceiverId, friendship.getFriendReceiverId());
    }

    @Test
    void setFields_SmallValues_ShouldHandleCorrectly() {
        int smallId = Integer.MIN_VALUE;
        int smallSenderId = Integer.MIN_VALUE + 1;
        int smallReceiverId = Integer.MIN_VALUE + 2;

        friendship.setId(smallId);
        friendship.setFriendSenderId(smallSenderId);
        friendship.setFriendReceiverId(smallReceiverId);

        assertEquals(smallId, friendship.getId());
        assertEquals(smallSenderId, friendship.getFriendSenderId());
        assertEquals(smallReceiverId, friendship.getFriendReceiverId());
    }

    @Test
    void setFields_MultipleChanges_ShouldRetainLatestValues() {
        friendship.setId(1);
        friendship.setId(2);
        friendship.setId(3);

        friendship.setFriendSenderId(10);
        friendship.setFriendSenderId(20);
        friendship.setFriendSenderId(30);

        friendship.setFriendReceiverId(100);
        friendship.setFriendReceiverId(200);
        friendship.setFriendReceiverId(300);

        assertEquals(3, friendship.getId());
        assertEquals(30, friendship.getFriendSenderId());
        assertEquals(300, friendship.getFriendReceiverId());
    }

    @Test
    void setFields_SameValues_ShouldRetainValues() {
        int id = 50;
        int senderId = 60;
        int receiverId = 70;

        friendship.setId(id);
        friendship.setFriendSenderId(senderId);
        friendship.setFriendReceiverId(receiverId);

        // Set same values again
        friendship.setId(id);
        friendship.setFriendSenderId(senderId);
        friendship.setFriendReceiverId(receiverId);

        assertEquals(id, friendship.getId());
        assertEquals(senderId, friendship.getFriendSenderId());
        assertEquals(receiverId, friendship.getFriendReceiverId());
    }
} 