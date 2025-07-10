package com.freeuni.quiz.service;
import com.freeuni.quiz.DAO.MessageDAO;
import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Message;
import com.freeuni.quiz.bean.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;
public class MessageServiceTest {

    private static BasicDataSource dataSource;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private MessageService messageService;

    @BeforeClass
    public static void initDB() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");

            stmt.execute("""
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    hashPassword VARCHAR(255) NOT NULL,
                    salt VARCHAR(255) NOT NULL,
                    firstName VARCHAR(100) NOT NULL,
                    lastName VARCHAR(100) NOT NULL,
                    userName VARCHAR(100) UNIQUE NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    imageURL VARCHAR(2083),
                    bio TEXT
                )
            """);

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
    public void setUp() throws SQLException {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM messages");
            stmt.execute("DELETE FROM users");
        }

        userDAO = new UserDAO(dataSource);
        messageDAO = new MessageDAO(dataSource);
        messageService = new MessageService(dataSource);
    }
    @Test
    public void testGetConversationsWithProfileDetails() throws SQLException {
        // Create 3 users
        User u1 = createUser("john", "John", "Doe", "john@example.com");
        User u2 = createUser("alice", "Alice", "Smith", "alice@example.com");
        User u3 = createUser("bob", "Bob", "Builder", "bob@example.com");

        // Send messages
        int m1Id = messageDAO.sendMessage(u1.getId(), u2.getId(), "Hi Alice");
        int m2Id = messageDAO.sendMessage(u3.getId(), u1.getId(), "Hey John");

        // Fetch the message-user map
        LinkedHashMap<Message, UserDTO> result = messageService.getConversationsWithProfileDetails(u1.getId());

        assertEquals(2, result.size());

        Message[] messages = result.keySet().toArray(new Message[0]);
        UserDTO[] userDTOs = result.values().toArray(new UserDTO[0]);

        assertEquals("Hey John", messages[0].getContent());
        assertEquals("bob", userDTOs[0].getUserName());

        assertEquals("Hi Alice", messages[1].getContent());
        assertEquals("alice", userDTOs[1].getUserName());

    }

    private User createUser(String username, String firstName, String lastName, String email) throws SQLException {
        User u = new User();
        u.setUserName(username);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setHashPassword("hash");
        u.setSalt("salt");
        u.setBio("");
        u.setImageURL(null);
        userDAO.addUser(u);
        return u;
    }
}
