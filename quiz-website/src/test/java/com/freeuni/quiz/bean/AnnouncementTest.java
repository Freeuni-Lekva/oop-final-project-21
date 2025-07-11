package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AnnouncementTest {

    private Announcement announcement;

    @BeforeEach
    void setUp() {
        announcement = new Announcement();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyAnnouncement() {
        assertNotNull(announcement);
        assertNull(announcement.getId());
        assertNull(announcement.getTitle());
        assertNull(announcement.getContent());
        assertNull(announcement.getAuthorId());
        assertNull(announcement.getCreatedAt());
        assertNull(announcement.getUpdatedAt());
        assertFalse(announcement.isActive());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        String title = "Test Announcement";
        String content = "This is a test announcement content";
        Integer authorId = 1;

        Announcement paramAnnouncement = new Announcement(title, content, authorId);

        assertEquals(title, paramAnnouncement.getTitle());
        assertEquals(content, paramAnnouncement.getContent());
        assertEquals(authorId, paramAnnouncement.getAuthorId());
        assertTrue(paramAnnouncement.isActive());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 123L;

        announcement.setId(expectedId);

        assertEquals(expectedId, announcement.getId());
    }

    @Test
    void setTitle_ValidTitle_ShouldSetCorrectly() {
        String expectedTitle = "Important News";

        announcement.setTitle(expectedTitle);

        assertEquals(expectedTitle, announcement.getTitle());
    }

    @Test
    void setContent_ValidContent_ShouldSetCorrectly() {
        String expectedContent = "This is an important announcement for all users.";

        announcement.setContent(expectedContent);

        assertEquals(expectedContent, announcement.getContent());
    }

    @Test
    void setAuthorId_ValidAuthorId_ShouldSetCorrectly() {
        Integer expectedAuthorId = 456;

        announcement.setAuthorId(expectedAuthorId);

        assertEquals(expectedAuthorId, announcement.getAuthorId());
    }

    @Test
    void setCreatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        announcement.setCreatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, announcement.getCreatedAt());
    }

    @Test
    void setUpdatedAt_ValidTimestamp_ShouldSetCorrectly() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        announcement.setUpdatedAt(expectedTimestamp);

        assertEquals(expectedTimestamp, announcement.getUpdatedAt());
    }

    @Test
    void setActive_ValidBoolean_ShouldSetCorrectly() {
        announcement.setActive(true);

        assertTrue(announcement.isActive());

        announcement.setActive(false);

        assertFalse(announcement.isActive());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 1L;
        String title = "System Maintenance";
        String content = "The system will be down for maintenance on Sunday.";
        Integer authorId = 2;
        Timestamp createdAt = Timestamp.from(Instant.now());
        Timestamp updatedAt = Timestamp.from(Instant.now());
        boolean isActive = true;

        announcement.setId(id);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setAuthorId(authorId);
        announcement.setCreatedAt(createdAt);
        announcement.setUpdatedAt(updatedAt);
        announcement.setActive(isActive);

        assertEquals(id, announcement.getId());
        assertEquals(title, announcement.getTitle());
        assertEquals(content, announcement.getContent());
        assertEquals(authorId, announcement.getAuthorId());
        assertEquals(createdAt, announcement.getCreatedAt());
        assertEquals(updatedAt, announcement.getUpdatedAt());
        assertEquals(isActive, announcement.isActive());
    }

    @Test
    void setFields_NullValues_ShouldSetNull() {
        announcement.setId(null);
        announcement.setTitle(null);
        announcement.setContent(null);
        announcement.setAuthorId(null);
        announcement.setCreatedAt(null);
        announcement.setUpdatedAt(null);

        assertNull(announcement.getId());
        assertNull(announcement.getTitle());
        assertNull(announcement.getContent());
        assertNull(announcement.getAuthorId());
        assertNull(announcement.getCreatedAt());
        assertNull(announcement.getUpdatedAt());
    }

    @Test
    void setFields_EmptyStrings_ShouldSetEmpty() {
        announcement.setTitle("");
        announcement.setContent("");

        assertEquals("", announcement.getTitle());
        assertEquals("", announcement.getContent());
    }

    @Test
    void setTitle_LongTitle_ShouldSetCorrectly() {
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longTitle.append("Very Long Title ");
        }
        String veryLongTitle = longTitle.toString();

        announcement.setTitle(veryLongTitle);

        assertEquals(veryLongTitle, announcement.getTitle());
    }

    @Test
    void setContent_LongContent_ShouldSetCorrectly() {
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("This is a very long announcement content that should be stored correctly. ");
        }
        String veryLongContent = longContent.toString();

        announcement.setContent(veryLongContent);

        assertEquals(veryLongContent, announcement.getContent());
    }

    @Test
    void setFields_SpecialCharacters_ShouldSetCorrectly() {
        String specialTitle = "Announcement with special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";
        String specialContent = "Content with special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./";

        announcement.setTitle(specialTitle);
        announcement.setContent(specialContent);

        assertEquals(specialTitle, announcement.getTitle());
        assertEquals(specialContent, announcement.getContent());
    }

    @Test
    void setFields_NonAsciiCharacters_ShouldSetCorrectly() {
        String nonAsciiTitle = "Announcement with non-ASCII characters: éñçåüö";
        String nonAsciiContent = "Content with non-ASCII characters: éñçåüö";

        announcement.setTitle(nonAsciiTitle);
        announcement.setContent(nonAsciiContent);

        assertEquals(nonAsciiTitle, announcement.getTitle());
        assertEquals(nonAsciiContent, announcement.getContent());
    }

    @Test
    void setFields_SameValueMultipleTimes_ShouldRetainValue() {
        String title = "Test Title";
        String content = "Test Content";

        announcement.setTitle(title);
        String firstTitleGet = announcement.getTitle();
        announcement.setTitle(title);
        String secondTitleGet = announcement.getTitle();

        announcement.setContent(content);
        String firstContentGet = announcement.getContent();
        announcement.setContent(content);
        String secondContentGet = announcement.getContent();

        assertEquals(title, firstTitleGet);
        assertEquals(title, secondTitleGet);
        assertEquals(content, firstContentGet);
        assertEquals(content, secondContentGet);
    }

    @Test
    void setActive_ToggleStates_ShouldWorkCorrectly() {
        assertFalse(announcement.isActive());

        announcement.setActive(true);
        assertTrue(announcement.isActive());

        announcement.setActive(false);
        assertFalse(announcement.isActive());

        announcement.setActive(true);
        assertTrue(announcement.isActive());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        Announcement nullTitle = new Announcement(null, "content", 1);
        Announcement nullContent = new Announcement("title", null, 1);
        Announcement nullAuthor = new Announcement("title", "content", null);

        assertNull(nullTitle.getTitle());
        assertEquals("content", nullTitle.getContent());
        assertEquals(Integer.valueOf(1), nullTitle.getAuthorId());

        assertEquals("title", nullContent.getTitle());
        assertNull(nullContent.getContent());
        assertEquals(Integer.valueOf(1), nullContent.getAuthorId());

        assertEquals("title", nullAuthor.getTitle());
        assertEquals("content", nullAuthor.getContent());
        assertNull(nullAuthor.getAuthorId());
    }
} 