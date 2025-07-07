package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.UserDTO;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

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
                    "username VARCHAR(255) UNIQUE NOT NULL," +
                    "hashPassword VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "firstName VARCHAR(255) NOT NULL," +
                    "lastName VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL," +
                    "imageUrl VARCHAR(255)," +
                    "bio TEXT" +
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
}

