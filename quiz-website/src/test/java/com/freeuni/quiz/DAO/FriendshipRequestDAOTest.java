package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.FriendshipRequest;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class FriendshipRequestDAOTest {

    private static BasicDataSource dataSource;
    private FriendshipRequestDAO dao;

    @BeforeClass
    public static void initDb() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("CREATE TABLE friendship_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "requestSender_id INT NOT NULL," +
                    "requestReceiver_id INT NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        dao = new FriendshipRequestDAO(dataSource);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM friendship_requests");
        }
    }

    private FriendshipRequest createSampleRequest() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return new FriendshipRequest(1, 2, now);
    }

    @Test
    public void testAddAndFindById() throws SQLException {
        FriendshipRequest request = createSampleRequest();
        assertTrue(dao.addFriendshipRequest(request));
        assertTrue(request.getId() > 0);

        FriendshipRequest fetched = dao.findById(request.getId());
        assertNotNull(fetched);
        assertEquals((Integer) request.getId(), (Integer) fetched.getId());
        assertEquals(request.getRequestSenderId(), fetched.getRequestSenderId());
    }

    @Test
    public void testExists() throws SQLException {
        FriendshipRequest request = createSampleRequest();
        dao.addFriendshipRequest(request);

        assertTrue(dao.exists(1, 2));
        assertFalse(dao.exists(2, 1));
    }

    @Test
    public void testFindBySenderIdAndReceiverId() throws SQLException {
        FriendshipRequest request = createSampleRequest();
        dao.addFriendshipRequest(request);

        List<FriendshipRequest> bySender = dao.findRequestsBySenderId(1);
        assertEquals(1, bySender.size());

        List<FriendshipRequest> byReceiver = dao.findRequestsByReceiverId(2);
        assertEquals(1, byReceiver.size());
    }

    @Test
    public void testFindAll() throws SQLException {
        dao.addFriendshipRequest(new FriendshipRequest(1, 2, new Timestamp(System.currentTimeMillis())));
        dao.addFriendshipRequest(new FriendshipRequest(3, 4, new Timestamp(System.currentTimeMillis())));

        List<FriendshipRequest> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testDelete() throws SQLException {
        FriendshipRequest request = createSampleRequest();
        dao.addFriendshipRequest(request);

        assertTrue(dao.deleteRequest(request.getId()));
        assertNull(dao.findById(request.getId()));
        assertFalse(dao.deleteRequest(999));
    }

    @Test
    public void testUpdate() throws SQLException {
        FriendshipRequest request = createSampleRequest();
        dao.addFriendshipRequest(request);

        request.setRequestSenderId(5);
        request.setRequestReceiverId(6);
        Timestamp newTime = new Timestamp(System.currentTimeMillis() + 10000);
        request.setTimestamp(newTime);

        assertTrue(dao.updateRequest(request));
        FriendshipRequest updated = dao.findById(request.getId());
        assertEquals(5, updated.getRequestSenderId());
        assertEquals(6, updated.getRequestReceiverId());
        assertEquals(newTime, updated.getTimestamp());
    }
}
