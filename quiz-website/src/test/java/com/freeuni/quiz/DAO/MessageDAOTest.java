package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Message;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageDAOTest {
    private static BasicDataSource dataSource;
    private MessageDAO messageDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("CREATE TABLE messages (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "sender_id INT NOT NULL," +
                    "receiver_id INT NOT NULL," +
                    "content TEXT NOT NULL," +
                    "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        messageDAO = new MessageDAO(dataSource);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM messages");
        }
    }

    @Test
    public void testSendMessageAndGetRecent() throws SQLException, InterruptedException {
        messageDAO.sendMessage(1, 2, "Hello");
        messageDAO.sendMessage(2, 1, "Hi");
        messageDAO.sendMessage(1, 2, "How are you?");

        List<Message> recent = messageDAO.getRecentMessages(1, 2);
        assertEquals(3, recent.size());
        assertEquals("Hello", recent.get(0).getContent());
        assertEquals("Hi", recent.get(1).getContent());
        assertEquals("How are you?", recent.get(2).getContent());
    }

    @Test
    public void testGetMessagesBefore() throws SQLException, InterruptedException {
        messageDAO.sendMessage(1, 2, "First");
        Thread.sleep(1000);
        messageDAO.sendMessage(2, 1, "Second");
        Thread.sleep(1000);
        messageDAO.sendMessage(1, 2, "Third");

        List<Message> recent = messageDAO.getRecentMessages(1, 2);
        assertEquals(3, recent.size());

        LocalDateTime before = recent.get(1).getSentAt();

        List<Message> older = messageDAO.getMessagesBefore(1, 2, before);
        assertEquals(1, older.size());
        assertEquals("First", older.get(0).getContent());
    }

    @Test
    public void testGetMessagesBetweenUsersOnly() throws SQLException {
        messageDAO.sendMessage(1, 2, "User1->User2");
        messageDAO.sendMessage(2, 3, "User2->User3");
        messageDAO.sendMessage(3, 1, "User3->User1");

        List<Message> between12 = messageDAO.getRecentMessages(1, 2);
        assertEquals(1, between12.size());
        assertEquals("User1->User2", between12.get(0).getContent());
    }

    @Test
    public void testPageSizeLimit() throws SQLException {
        for (int i = 0; i < 25; i++) {
            messageDAO.sendMessage(1, 2, "Msg " + i);
        }
        List<Message> recent = messageDAO.getRecentMessages(1, 2);
        assertEquals(messageDAO.getPageSize(), recent.size());
        assertEquals("Msg 5", recent.get(0).getContent());
    }
}
