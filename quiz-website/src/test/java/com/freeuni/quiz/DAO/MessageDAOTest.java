package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Message;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class MessageDAOTest {
    private static BasicDataSource dataSource;
    private MessageDAO messageDAO;

    @BeforeClass
    public static void setupDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("""
                CREATE TABLE messages (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    sender_id INT NOT NULL,
                    receiver_id INT NOT NULL,
                    content TEXT NOT NULL,
                    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
    }

    @Before
    public void setup() throws SQLException {
        messageDAO = new MessageDAO(dataSource);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM messages");
        }
    }

    @Test
    public void testSendMessageAndGetById() throws SQLException {
        int senderId = 1;
        int receiverId = 2;
        String content = "Hello there!";

        Long messageId = messageDAO.sendMessage(senderId, receiverId, content);
        assertTrue(messageId > 0);

        Message msg = messageDAO.getMessageById(messageId);
        assertEquals(senderId, msg.getSenderId());
        assertEquals(receiverId, msg.getReceiverId());
        assertEquals(content, msg.getContent());
        assertNotNull(msg.getSentAt());
    }

    @Test
    public void testGetRecentMessages() throws SQLException {
        for (int i = 0; i < 5; i++) {
            messageDAO.sendMessage(1, 2, "Message " + i);
        }

        List<Message> messages = messageDAO.getRecentMessages(1, 2);
        assertEquals(5, messages.size());
        assertEquals("Message 0", messages.get(0).getContent());
        assertEquals("Message 4", messages.get(4).getContent());
    }

    @Test
    public void testGetMessagesBefore() throws SQLException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            messageDAO.sendMessage(1, 2, "Msg " + i);
            Thread.sleep(10);
        }

        List<Message> all = messageDAO.getRecentMessages(1, 2);
        Message beforeMessage = all.get(3);
        List<Message> older = messageDAO.getMessagesBefore(1, 2, beforeMessage.getSentAt(), beforeMessage.getId());

        assertEquals(3, older.size());
        assertEquals("Msg 0", older.get(0).getContent());
    }

    @Test
    public void testGetLatestConversations() throws SQLException {
        messageDAO.sendMessage(1, 2, "First");
        messageDAO.sendMessage(2, 3, "Second");
        messageDAO.sendMessage(1, 3, "Third");

        List<Message> convos = messageDAO.getLatestConversations(1);
        assertEquals(2, convos.size());
    }

    @Test(expected = SQLException.class)
    public void testGetMessageByIdThrows() throws SQLException {
        messageDAO.getMessageById(999L);
    }
}

