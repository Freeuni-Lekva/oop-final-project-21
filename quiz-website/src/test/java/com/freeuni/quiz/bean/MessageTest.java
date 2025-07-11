package com.freeuni.quiz.bean;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void constructor_ShouldSetAllFields() {
        Long id = 123L;
        int senderId = 456;
        int receiverId = 789;
        String content = "Hello, this is a test message";
        LocalDateTime sentAt = LocalDateTime.now();

        Message message = new Message(id, senderId, receiverId, content, sentAt);

        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(content, message.getContent());
        assertEquals(sentAt, message.getSentAt());
    }

    @Test
    void constructor_WithNullValues_ShouldSetNull() {
        Message message = new Message(null, 0, 0, null, null);

        assertNull(message.getId());
        assertEquals(0, message.getSenderId());
        assertEquals(0, message.getReceiverId());
        assertNull(message.getContent());
        assertNull(message.getSentAt());
    }

    @Test
    void constructor_WithNegativeIds_ShouldSetNegativeValues() {
        Long id = -1L;
        int senderId = -2;
        int receiverId = -3;
        String content = "Test message";
        LocalDateTime sentAt = LocalDateTime.now();

        Message message = new Message(id, senderId, receiverId, content, sentAt);

        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(content, message.getContent());
        assertEquals(sentAt, message.getSentAt());
    }

    @Test
    void constructor_WithEmptyContent_ShouldSetEmptyContent() {
        Long id = 100L;
        int senderId = 200;
        int receiverId = 300;
        String content = "";
        LocalDateTime sentAt = LocalDateTime.now();

        Message message = new Message(id, senderId, receiverId, content, sentAt);

        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(content, message.getContent());
        assertEquals(sentAt, message.getSentAt());
    }

    @Test
    void constructor_WithLongContent_ShouldSetLongContent() {
        Long id = 100L;
        int senderId = 200;
        int receiverId = 300;
        String content = "A".repeat(1000);
        LocalDateTime sentAt = LocalDateTime.now();

        Message message = new Message(id, senderId, receiverId, content, sentAt);

        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(content, message.getContent());
        assertEquals(1000, message.getContent().length());
        assertEquals(sentAt, message.getSentAt());
    }

    @Test
    void constructor_WithSpecialCharacters_ShouldSetSpecialCharacters() {
        Long id = 100L;
        int senderId = 200;
        int receiverId = 300;
        String content = "Hello! How are you? éñçåüö #@$%";
        LocalDateTime sentAt = LocalDateTime.now();

        Message message = new Message(id, senderId, receiverId, content, sentAt);

        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(content, message.getContent());
        assertEquals(sentAt, message.getSentAt());
    }
} 