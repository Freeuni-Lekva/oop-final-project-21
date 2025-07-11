package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.bean.Friendship;
import com.freeuni.quiz.bean.User;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class FriendshipServiceTest {

    private static DataSource dataSource;
    private FriendshipService friendshipService;
    private UserDAO userDAO;

    @BeforeClass
    public static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "hashPassword VARCHAR(255)," +
                    "salt VARCHAR(255)," +
                    "firstName VARCHAR(255)," +
                    "lastName VARCHAR(255)," +
                    "userName VARCHAR(255) UNIQUE," +
                    "email VARCHAR(255) UNIQUE," +
                    "imageURL VARCHAR(255)," +
                    "bio TEXT)");

            stmt.execute("CREATE TABLE friendships (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "friendSenderId INT," +
                    "friendReceiverId INT," +
                    "FOREIGN KEY (friendSenderId) REFERENCES users(id)," +
                    "FOREIGN KEY (friendReceiverId) REFERENCES users(id))");
        }
    }

    @Before
    public void setup() throws Exception {
        friendshipService = new FriendshipService(dataSource);
        userDAO = new UserDAO(dataSource);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM friendships");
            stmt.execute("DELETE FROM users");
        }
    }

    private User createAndAddUser(String username, String email) throws Exception {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setHashPassword("hashed");
        user.setSalt("salt");
        userDAO.addUser(user);
        return user;
    }

    @Test
    public void testAddFriendship() throws Exception {
        User u1 = createAndAddUser("u1", "u1@example.com");
        User u2 = createAndAddUser("u2", "u2@example.com");

        boolean result = friendshipService.addFriendship(u1.getId(), u2.getId());
        assertTrue(result);
        assertTrue(friendshipService.areFriends(u1.getId(), u2.getId()));
    }

    @Test
    public void testAreFriendsFalse() throws Exception {
        User u1 = createAndAddUser("u3", "u3@example.com");
        User u2 = createAndAddUser("u4", "u4@example.com");

        assertFalse(friendshipService.areFriends(u1.getId(), u2.getId()));
    }

    @Test
    public void testRemoveFriendship() throws Exception {
        User u1 = createAndAddUser("u5", "u5@example.com");
        User u2 = createAndAddUser("u6", "u6@example.com");

        friendshipService.addFriendship(u1.getId(), u2.getId());
        assertTrue(friendshipService.areFriends(u1.getId(), u2.getId()));

        boolean deleted = friendshipService.removeFriendshipByUserIds(u1.getId(), u2.getId());
        assertTrue(deleted);
        assertFalse(friendshipService.areFriends(u1.getId(), u2.getId()));
    }

    @Test
    public void testGetFriendsOfUser() throws Exception {
        User u1 = createAndAddUser("u7", "u7@example.com");
        User u2 = createAndAddUser("u8", "u8@example.com");
        User u3 = createAndAddUser("u9", "u9@example.com");

        friendshipService.addFriendship(u1.getId(), u2.getId());
        friendshipService.addFriendship(u1.getId(), u3.getId());

        List<Integer> friends = friendshipService.getFriendsOfUser(u1.getId());
        assertEquals(2, friends.size());
        assertTrue(friends.contains(u2.getId()));
        assertTrue(friends.contains(u3.getId()));
    }

    @Test
    public void testGetMutualFriends() throws Exception {
        User u1 = createAndAddUser("u10", "u10@example.com");
        User u2 = createAndAddUser("u11", "u11@example.com");
        User u3 = createAndAddUser("u12", "u12@example.com");

        friendshipService.addFriendship(u1.getId(), u3.getId());
        friendshipService.addFriendship(u2.getId(), u3.getId());

        List<Integer> mutual = friendshipService.getMutualFriends(u1.getId(), u2.getId());
        assertEquals(1, mutual.size());
        assertEquals(Integer.valueOf(u3.getId()), mutual.get(0));
    }
    @Test
    public void testGetAllFriendships() throws Exception {
        User user1 = createAndAddUser("all1", "all1@example.com");
        User user2 = createAndAddUser("all2", "all2@example.com");
        User user3 = createAndAddUser("all3", "all3@example.com");

        friendshipService.addFriendship(user1.getId(), user2.getId());
        friendshipService.addFriendship(user1.getId(), user3.getId());

        List<Friendship> allFriendships = friendshipService.getAllFriendships();

        assertEquals(2, allFriendships.size());

        boolean containsFirst = allFriendships.stream()
                .anyMatch(f -> f.getFriendSenderId() == user1.getId() && f.getFriendReceiverId() == user2.getId());
        boolean containsSecond = allFriendships.stream()
                .anyMatch(f -> f.getFriendSenderId() == user1.getId() && f.getFriendReceiverId() == user3.getId());

        assertTrue(containsFirst);
        assertTrue(containsSecond);
    }
}
