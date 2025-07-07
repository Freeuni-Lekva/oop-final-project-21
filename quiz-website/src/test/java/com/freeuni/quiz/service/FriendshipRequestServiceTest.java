package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.FriendshipRequest;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class FriendshipRequestServiceTest {

    private static DataSource dataSource;
    private FriendshipRequestService service;

    @BeforeClass
    public static void setupDatabase() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

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
    public void setup() {
        service = new FriendshipRequestService(dataSource);
    }

    @After
    public void cleanup() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM friendship_requests");
        }
    }

    @Test
    public void testSendRequestSuccess() throws Exception {
        boolean result = service.sendRequest(1, 2);
        assertTrue(result);
        assertTrue(service.requestExists(1, 2));
    }

    @Test
    public void testSendRequestToSelf() throws Exception {
        boolean result = service.sendRequest(5, 5);
        assertFalse(result);
    }

    @Test
    public void testSendDuplicateRequest() throws Exception {
        service.sendRequest(10, 11);
        boolean secondAttempt = service.sendRequest(10, 11);
        assertFalse(secondAttempt);
    }

    @Test
    public void testCancelRequest() throws Exception {
        service.sendRequest(20, 21);
        List<FriendshipRequest> requests = service.getRequestsSentByUser(20);
        assertFalse(requests.isEmpty());

        int requestId = requests.get(0).getId();
        boolean deleted = service.cancelRequest(requestId);
        assertTrue(deleted);

        FriendshipRequest shouldBeGone = service.findRequestById(requestId);
        assertNull(shouldBeGone);
    }

    @Test
    public void testGetRequestsSentByUser() throws Exception {
        service.sendRequest(30, 31);
        service.sendRequest(30, 32);
        List<FriendshipRequest> sent = service.getRequestsSentByUser(30);
        assertEquals(2, sent.size());
    }

    @Test
    public void testGetRequestsReceivedByUser() throws Exception {
        service.sendRequest(40, 50);
        service.sendRequest(41, 50);
        List<FriendshipRequest> received = service.getRequestsReceivedByUser(50);
        assertEquals(2, received.size());
    }

    @Test
    public void testRequestExists() throws Exception {
        service.sendRequest(60, 70);
        assertTrue(service.requestExists(60, 70));
        assertFalse(service.requestExists(70, 60));
    }

    @Test
    public void testFindRequestById() throws Exception {
        service.sendRequest(80, 81);
        List<FriendshipRequest> requests = service.getRequestsSentByUser(80);
        assertFalse(requests.isEmpty());

        int requestId = requests.get(0).getId();
        FriendshipRequest request = service.findRequestById(requestId);
        assertNotNull(request);
        assertEquals((Integer) request.getRequestSenderId(), Integer.valueOf(80));
        assertEquals((Integer) request.getRequestReceiverId(), Integer.valueOf(81));
    }
}
