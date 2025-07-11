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
        User u1 = createUser("john", "John", "Doe", "john@example.com");
        User u2 = createUser("alice", "Alice", "Smith", "alice@example.com");
        User u3 = createUser("bob", "Bob", "Builder", "bob@example.com");

        Long m1Id = messageDAO.sendMessage(u1.getId(), u2.getId(), "Hi Alice");
        Long m2Id = messageDAO.sendMessage(u3.getId(), u1.getId(), "Hey John");

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
    @Test
    public void testGetRecentConversationsWithProfileDetails() throws SQLException {
        User u1 = createUser("john_limit", "John", "Doe", "john_limit@example.com");
        User u2 = createUser("alice_limit", "Alice", "Smith", "alice_limit@example.com");
        User u3 = createUser("bob_limit", "Bob", "Builder", "bob_limit@example.com");

        messageDAO.sendMessage(u1.getId(), u2.getId(), "Message 1 to Alice");
        messageDAO.sendMessage(u3.getId(), u1.getId(), "Message 2 from Bob");
        messageDAO.sendMessage(u2.getId(), u1.getId(), "Message 3 from Alice");

        LinkedHashMap<Message, UserDTO> result = messageService.getRecentConversationsWithProfileDetails(u1.getId(), 2);

        assertEquals(2, result.size());

        Message[] messages = result.keySet().toArray(new Message[0]);
        UserDTO[] userDTOs = result.values().toArray(new UserDTO[0]);

        assertTrue(messages[0].getContent().contains("Message 3 from Alice") || messages[0].getContent().contains("Message 2 from Bob"));
        assertTrue(messages[1].getContent().contains("Message 3 from Alice") || messages[1].getContent().contains("Message 2 from Bob"));
        assertNotEquals(messages[0].getContent(), messages[1].getContent());

        assertTrue(userDTOs[0].getUserName().equals("alice_limit") || userDTOs[0].getUserName().equals("bob_limit"));
        assertTrue(userDTOs[1].getUserName().equals("alice_limit") || userDTOs[1].getUserName().equals("bob_limit"));
        assertNotEquals(userDTOs[0].getUserName(), userDTOs[1].getUserName());
    }

}
