package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.UserDTO;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class UserServiceTest {

    private static DataSource dataSource;
    private UserService userService;

    @BeforeClass
    public static void setupClass() throws Exception {
        // Setup H2 in-memory database
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"); // keep DB until JVM stops
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        // Initialize DB schema for users table
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            String createTableSQL = "CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "hashPassword VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "firstName VARCHAR(255) NOT NULL," +
                    "lastName VARCHAR(255) NOT NULL," +
                    "userName VARCHAR(255) UNIQUE NOT NULL," +
                    "email VARCHAR(255) NOT NULL," +
                    "imageURL VARCHAR(255)," +
                    "bio TEXT," +
                    "isAdmin BOOLEAN NOT NULL DEFAULT FALSE" +
                    ");";

            stmt.execute(createTableSQL);
        }
    }

    @Before
    public void setup() {
        // Create new UserService instance before each test
        userService = new UserService(dataSource);
    }

    @After
    public void cleanup() throws Exception {
        // Clear users table after each test for isolation
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        boolean registered = userService.registerUser(
                "testuser",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                null,
                "Hello, I am John!"
        );
        assertTrue("User should be registered successfully", registered);
    }

    @Test
    public void testRegisterUser_DuplicateUsername() throws Exception {
        // First registration
        boolean first = userService.registerUser(
                "duplicateUser",
                "pass1",
                "Jane",
                "Doe",
                "jane@example.com",
                null,
                null
        );
        assertTrue(first);

        // Try to register again with same username
        boolean second = userService.registerUser(
                "duplicateUser",
                "pass2",
                "Janet",
                "Smith",
                "janet@example.com",
                null,
                null
        );
        assertFalse("Duplicate username should not be allowed", second);
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        String username = "authUser";
        String password = "secret";

        userService.registerUser(username, password, "Auth", "User", "auth@example.com", null, null);

        UserDTO userDTO = userService.authenticateUser(username, password);
        assertNotNull("Authentication should succeed with correct credentials", userDTO);
        assertEquals(username, userDTO.getUserName());
    }

    @Test
    public void testAuthenticateUser_WrongPassword() throws Exception {
        String username = "authUser2";
        String password = "correctPassword";

        userService.registerUser(username, password, "Auth", "User2", "auth2@example.com", null, null);

        UserDTO userDTO = userService.authenticateUser(username, "wrongPassword");
        assertNull("Authentication should fail with wrong password", userDTO);
    }

    @Test
    public void testAuthenticateUser_NonExistingUser() throws Exception {
        UserDTO userDTO = userService.authenticateUser("nonExisting", "anyPassword");
        assertNull("Authentication should fail for non-existing user", userDTO);
    }
    @Test
    public void testUpdateUserInfo() throws Exception {
        // First register a user
        userService.registerUser("johnny", "pass", "John", "Doe", "jd@example.com", null, "original bio");

        UserDTO userBefore = userService.findByUsername("johnny");
        assertEquals("original bio", userBefore.getBio());

        boolean updated = userService.updateUserInfo(
                userBefore.getId(),
                "Johnny",
                "Updated",
                "johnny.new@example.com",
                "http://image.com/johnny.jpg",
                "Updated bio"
        );

        assertTrue(updated);

        UserDTO userAfter = userService.findByUsername("johnny");
        assertEquals("Johnny", userAfter.getFirstName());
        assertEquals("Updated", userAfter.getLastName());
        assertEquals("johnny.new@example.com", userAfter.getEmail());
        assertEquals("Updated bio", userAfter.getBio());
        assertEquals("http://image.com/johnny.jpg", userAfter.getImageURL());
    }
    @Test
    public void testRegisterUser_InvalidImageURL_ShouldUsePlaceholder() throws Exception {
        String badImageURL = "https://example.com/not-a-real-image.jpg"; // should fallback
        userService.registerUser("badimage", "pass", "Fake", "User", "badimg@example.com", badImageURL, null);

        UserDTO user = userService.findByUsername("badimage");

        assertNotNull(user);
        assertTrue(user.getImageURL().startsWith("https://t3.ftcdn.net"));
    }

    @Test
    public void testRegisterUser_EmptyImageURL_ShouldUsePlaceholder() throws Exception {
        userService.registerUser("noimage", "pass", "No", "Image", "noimg@example.com", "", null);

        UserDTO user = userService.findByUsername("noimage");

        assertNotNull(user);
        assertTrue(user.getImageURL().startsWith("https://t3.ftcdn.net"));
    }

    @Test
    public void testSearchUsers() throws Exception {
        userService.registerUser("alex01", "pass", "Alex", "Miller", "alex@example.com", null, null);
        userService.registerUser("alexa", "pass", "Alexa", "Stone", "alexa@example.com", null, null);
        userService.registerUser("bob", "pass", "Bob", "Builder", "bob@example.com", null, null);

        // Search by username
        assertEquals(1, userService.searchUsers("bob").size());

        // Search by first name fragment
        assertEquals(2, userService.searchUsers("Alex").size());

        // Search by last name fragment
        assertEquals(1, userService.searchUsers("Build").size());

        // No results
        assertTrue(userService.searchUsers("xyz").isEmpty());
    }
    @Test
    public void testFindByUsername() throws Exception {
        userService.registerUser("searchable", "pass", "Sarah", "Connor", "sarah@example.com", null, null);

        UserDTO user = userService.findByUsername("searchable");
        assertNotNull(user);
        assertEquals("Sarah", user.getFirstName());
        assertEquals("sarah@example.com", user.getEmail());

        // Non-existing
        UserDTO notFound = userService.findByUsername("ghost");
        assertNull(notFound);
    }

    @Test
    public void testFindById() throws Exception {
        userService.registerUser("singleUser", "pass", "Lara", "Croft", "lara@example.com", null, null);
        UserDTO user = userService.findByUsername("singleUser");

        assertNotNull(user);
        UserDTO foundById = userService.findById(user.getId());

        assertNotNull("User should be found by ID", foundById);
        assertEquals(user.getId(), foundById.getId());
        assertEquals("Lara", foundById.getFirstName());
    }

    @Test
    public void testFindUsersByIds() throws Exception {
        userService.registerUser("u1", "pass", "Alice", "One", "alice@example.com", null, null);
        userService.registerUser("u2", "pass", "Bob", "Two", "bob@example.com", null, null);
        userService.registerUser("u3", "pass", "Charlie", "Three", "charlie@example.com", null, null);

        UserDTO user1 = userService.findByUsername("u1");
        UserDTO user2 = userService.findByUsername("u2");

        Set<Integer> ids = new HashSet<>();
        ids.add(user1.getId());
        ids.add(user2.getId());

        Map<Integer, UserDTO> userMap = userService.findUsersByIds(ids);

        assertEquals(2, userMap.size());
        assertTrue(userMap.containsKey(user1.getId()));
        assertTrue(userMap.containsKey(user2.getId()));
        assertEquals("Alice", userMap.get(user1.getId()).getFirstName());
        assertEquals("Bob", userMap.get(user2.getId()).getFirstName());
    }


}

