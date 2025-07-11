package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.User;
import com.freeuni.quiz.DAO.UserDAO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class UserDAOTest {
    private static BasicDataSource basicDataSource;
    private UserDAO userDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "hashPassword VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "firstName VARCHAR(100) NOT NULL," +
                    "lastName VARCHAR(100) NOT NULL," +
                    "userName VARCHAR(100) UNIQUE NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "imageURL VARCHAR(2083)," +
                    "bio TEXT," +
                    "isAdmin BOOLEAN NOT NULL DEFAULT FALSE" +
                    ")");

            // Optional seed data can go here, or skip if you want clean tests
        }
    }

    @Before
    public void setUp() throws SQLException {
        userDAO = new UserDAO(basicDataSource);

        // Clear users table before each test
        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM users");
        }
    }

    private User createSampleUser() {
        User user = new User();
        user.setHashPassword("hashedPass");
        user.setFirstName("Alice");
        user.setLastName("Wonderland");
        user.setUserName("alice");
        user.setSalt("randomSalt");
        user.setEmail("alice@example.com");
        user.setImageURL("http://example.com/alice.jpg");
        user.setBio("Sample bio");
        return user;
    }

    @Test
    public void testAddUserAndFindById() throws SQLException {
        User user = createSampleUser();
        boolean added = userDAO.addUser(user);
        assertTrue(added);
        assertTrue(user.getId() > 0);

        User fetched = userDAO.findById(user.getId());
        assertNotNull(fetched);
        assertEquals("alice", fetched.getUserName());

        user = userDAO.findById(999);
        assertNull(user);
    }

    @Test
    public void testFindByUsername() throws SQLException {
        User user = createSampleUser();
        userDAO.addUser(user);

        User fetched = userDAO.findByUsername("alice");
        assertNotNull(fetched);
        assertEquals(user.getEmail(), fetched.getEmail());

        user = userDAO.findByUsername("nonexistent");
        assertNull(user);
    }

    @Test
    public void testFindAll() throws SQLException {
        User user1 = createSampleUser();
        userDAO.addUser(user1);

        User user2 = createSampleUser();
        user2.setUserName("bob");
        user2.setEmail("bob@example.com");
        userDAO.addUser(user2);

        List<User> allUsers = userDAO.findAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = createSampleUser();
        userDAO.addUser(user);

        user.setFirstName("Alicia");
        user.setBio("Updated bio");
        boolean updated = userDAO.updateUser(user);
        assertTrue(updated);

        User fetched = userDAO.findById(user.getId());
        assertEquals("Alicia", fetched.getFirstName());
        assertEquals("Updated bio", fetched.getBio());

        user = createSampleUser();
        user.setId(999);
        boolean result = userDAO.updateUser(user);
        assertFalse(result);
    }

    @Test
    public void testDeleteUser() throws SQLException {
        User user = createSampleUser();
        userDAO.addUser(user);

        boolean deleted = userDAO.deleteUser(user.getId());
        assertTrue(deleted);

        User fetched = userDAO.findById(user.getId());
        assertNull(fetched);

        boolean result = userDAO.deleteUser(999);
        assertFalse(result);
    }

    @Test
    public void testAddUserWithDuplicateUsernameOrEmail() throws SQLException {
        User user1 = createSampleUser();
        User user2 = createSampleUser();
        user2.setEmail("different@example.com"); // keep username same

        assertTrue(userDAO.addUser(user1));

        try {
            userDAO.addUser(user2);
            fail("Expected SQLException due to duplicate username");
        } catch (SQLException e) {
            //pass
        }

        user2.setUserName("differentUser");
        user2.setEmail(user1.getEmail()); // now duplicate email

        try {
            userDAO.addUser(user2);
            fail("Expected SQLException due to duplicate email");
        } catch (SQLException e) {
            //pass
        }
    }

    @Test
    public void testFindUsersByPartialMatch() throws SQLException {
        // Add multiple users
        User user1 = createSampleUser(); // "alice", "Alice", "Wonderland"
        userDAO.addUser(user1);

        User user2 = createSampleUser();
        user2.setUserName("bob123");
        user2.setFirstName("Bob");
        user2.setLastName("Builder");
        user2.setEmail("bob@example.com");
        userDAO.addUser(user2);

        User user3 = createSampleUser();
        user3.setUserName("charlie99");
        user3.setFirstName("Charles");
        user3.setLastName("Chaplin");
        user3.setEmail("charlie@example.com");
        userDAO.addUser(user3);

        List<User> result1 = userDAO.findUsers("Ali");
        assertEquals(1, result1.size());
        assertEquals("alice", result1.get(0).getUserName());

        List<User> result2 = userDAO.findUsers("Build");
        assertEquals(1, result2.size());
        assertEquals("bob123", result2.get(0).getUserName());

        List<User> result3 = userDAO.findUsers("charlie");
        assertEquals(1, result3.size());
        assertEquals("charlie99", result3.get(0).getUserName());

        List<User> result4 = userDAO.findUsers("zzzz");
        assertTrue(result4.isEmpty());

        List<User> result5 = userDAO.findUsers("a");
        assertEquals(2, result5.size());
    }

    @Test
    public void testFindUsersByIds() throws SQLException {

        User user1 = createSampleUser();
        userDAO.addUser(user1);

        User user2 = createSampleUser();
        user2.setUserName("bob");
        user2.setEmail("bob@example.com");
        userDAO.addUser(user2);

        User user3 = createSampleUser();
        user3.setUserName("charlie");
        user3.setEmail("charlie@example.com");
        userDAO.addUser(user3);

        Set<Integer> idsToFetch = Set.of(user1.getId(), user2.getId());

        Map<Integer, UserDTO> usersMap = userDAO.findUsersByIds(idsToFetch);

        assertEquals(2, usersMap.size());
        assertTrue(usersMap.containsKey(user1.getId()));
        assertTrue(usersMap.containsKey(user2.getId()));
        assertFalse(usersMap.containsKey(user3.getId()));

        assertEquals("alice", usersMap.get(user1.getId()).getUserName());
        assertEquals("bob", usersMap.get(user2.getId()).getUserName());

        Map<Integer, UserDTO> emptyResult = userDAO.findUsersByIds(Set.of());
        assertTrue(emptyResult.isEmpty());
    }

}
