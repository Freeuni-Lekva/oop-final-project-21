package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Friendship;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.*;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class FriendshipDAOTest {
    private static BasicDataSource dataSource;
    private FriendshipDAO friendshipDAO;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE friendships (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "friendSenderId INT NOT NULL," +
                    "friendReceiverId INT NOT NULL" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        friendshipDAO = new FriendshipDAO(dataSource);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM friendships");
        }
    }

    private Friendship createSampleFriendship() {
        return new Friendship(1, 2);
    }

    @Test
    public void testAddAndFindById() throws SQLException {
        Friendship friendship = createSampleFriendship();
        boolean added = friendshipDAO.addFriendship(friendship);
        assertTrue(added);
        assertTrue(friendship.getId() > 0);

        Friendship fetched = friendshipDAO.findById(friendship.getId());
        assertNotNull(fetched);
        assertEquals(1, fetched.getFriendSenderId());
        assertEquals(2, fetched.getFriendReceiverId());

        assertNull(friendshipDAO.findById(999));
    }

    @Test
    public void testFindAll() throws SQLException {
        friendshipDAO.addFriendship(new Friendship(1, 2));
        friendshipDAO.addFriendship(new Friendship(3, 4));

        List<Friendship> all = friendshipDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testDeleteFriendship() throws SQLException {
        Friendship friendship = createSampleFriendship();
        friendshipDAO.addFriendship(friendship);

        boolean deleted = friendshipDAO.deleteFriendship(friendship.getId());
        assertTrue(deleted);

        assertNull(friendshipDAO.findById(friendship.getId()));
        assertFalse(friendshipDAO.deleteFriendship(999));
    }

    @Test
    public void testUpdateFriendship() throws SQLException {
        Friendship friendship = createSampleFriendship();
        friendshipDAO.addFriendship(friendship);

        friendship.setFriendSenderId(10);
        friendship.setFriendReceiverId(20);
        boolean updated = friendshipDAO.updateFriendship(friendship);
        assertTrue(updated);

        Friendship updatedFriendship = friendshipDAO.findById(friendship.getId());
        assertEquals(10, updatedFriendship.getFriendSenderId());
        assertEquals(20, updatedFriendship.getFriendReceiverId());

        friendship.setId(999);
        assertFalse(friendshipDAO.updateFriendship(friendship));
    }

    @Test
    public void testExists() throws SQLException {
        friendshipDAO.addFriendship(new Friendship(1, 2));

        assertTrue(friendshipDAO.exists(1, 2));
        assertTrue(friendshipDAO.exists(2, 1));
        assertFalse(friendshipDAO.exists(1, 3));
    }

    @Test
    public void testFindFriendIdsByUserId() throws SQLException {
        friendshipDAO.addFriendship(new Friendship(1, 2));
        friendshipDAO.addFriendship(new Friendship(3, 1));

        List<Integer> friends = friendshipDAO.findFriendIdsByUserId(1);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(2));
        assertTrue(friends.contains(3));
    }

    @Test
    public void testFindFriendshipId() throws SQLException {
        Friendship friendship = new Friendship(4, 5);
        friendshipDAO.addFriendship(friendship);

        Integer id = friendshipDAO.findFriendshipId(4, 5);
        assertNotNull(id);
        assertEquals((int)friendship.getId(), (int)id);

        Integer reverseId = friendshipDAO.findFriendshipId(5, 4);
        assertEquals(id, reverseId);

        assertNull(friendshipDAO.findFriendshipId(1, 99));
    }
}
