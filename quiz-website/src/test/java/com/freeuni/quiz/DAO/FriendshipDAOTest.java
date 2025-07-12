package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DAO.impl.FriendshipDAOImpl;
import com.freeuni.quiz.bean.Friendship;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.*;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class FriendshipDAOTest {
    private static BasicDataSource dataSource;
    private FriendshipDAOImpl friendshipDAOImpl;


    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE friendships (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "friendSenderId INT NOT NULL," +
                    "friendReceiverId INT NOT NULL" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        friendshipDAOImpl = new FriendshipDAOImpl(dataSource);

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
        boolean added = friendshipDAOImpl.addFriendship(friendship);
        assertTrue(added);
        assertTrue(friendship.getId() > 0);

        Friendship fetched = friendshipDAOImpl.findById(friendship.getId());
        assertNotNull(fetched);
        assertEquals(1, fetched.getFriendSenderId());
        assertEquals(2, fetched.getFriendReceiverId());

        assertNull(friendshipDAOImpl.findById(999));
    }

    @Test
    public void testFindAll() throws SQLException {
        friendshipDAOImpl.addFriendship(new Friendship(1, 2));
        friendshipDAOImpl.addFriendship(new Friendship(3, 4));

        List<Friendship> all = friendshipDAOImpl.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testDeleteFriendship() throws SQLException {
        Friendship friendship = createSampleFriendship();
        friendshipDAOImpl.addFriendship(friendship);

        boolean deleted = friendshipDAOImpl.deleteFriendship(friendship.getId());
        assertTrue(deleted);

        assertNull(friendshipDAOImpl.findById(friendship.getId()));
        assertFalse(friendshipDAOImpl.deleteFriendship(999));
    }

    @Test
    public void testUpdateFriendship() throws SQLException {
        Friendship friendship = createSampleFriendship();
        friendshipDAOImpl.addFriendship(friendship);

        friendship.setFriendSenderId(10);
        friendship.setFriendReceiverId(20);
        boolean updated = friendshipDAOImpl.updateFriendship(friendship);
        assertTrue(updated);

        Friendship updatedFriendship = friendshipDAOImpl.findById(friendship.getId());
        assertEquals(10, updatedFriendship.getFriendSenderId());
        assertEquals(20, updatedFriendship.getFriendReceiverId());

        friendship.setId(999);
        assertFalse(friendshipDAOImpl.updateFriendship(friendship));
    }

    @Test
    public void testExists() throws SQLException {
        friendshipDAOImpl.addFriendship(new Friendship(1, 2));

        assertTrue(friendshipDAOImpl.exists(1, 2));
        assertTrue(friendshipDAOImpl.exists(2, 1));
        assertFalse(friendshipDAOImpl.exists(1, 3));
    }

    @Test
    public void testFindFriendIdsByUserId() throws SQLException {
        friendshipDAOImpl.addFriendship(new Friendship(1, 2));
        friendshipDAOImpl.addFriendship(new Friendship(3, 1));

        List<Integer> friends = friendshipDAOImpl.findFriendIdsByUserId(1);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(2));
        assertTrue(friends.contains(3));
    }

    @Test
    public void testFindFriendshipId() throws SQLException {
        Friendship friendship = new Friendship(4, 5);
        friendshipDAOImpl.addFriendship(friendship);

        Integer id = friendshipDAOImpl.findFriendshipId(4, 5);
        assertNotNull(id);
        assertEquals((int)friendship.getId(), (int)id);

        Integer reverseId = friendshipDAOImpl.findFriendshipId(5, 4);
        assertEquals(id, reverseId);

        assertNull(friendshipDAOImpl.findFriendshipId(1, 99));
    }
}
