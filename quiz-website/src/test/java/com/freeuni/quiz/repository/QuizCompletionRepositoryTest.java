package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class QuizCompletionRepositoryTest {
    private static BasicDataSource basicDataSource;
    private QuizCompletionRepository completionRepository;

    @BeforeClass
    public static void setUpDatabase() throws SQLException {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:h2:mem:testdb_completion;DB_CLOSE_DELAY=-1");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");

        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("CREATE TABLE quiz_completions (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "participant_user_id BIGINT NOT NULL," +
                    "test_id BIGINT NOT NULL," +
                    "final_score DOUBLE NOT NULL," +
                    "total_possible DOUBLE NOT NULL," +
                    "completion_percentage DECIMAL(5,2) NOT NULL," +
                    "started_at TIMESTAMP NOT NULL," +
                    "finished_at TIMESTAMP," +
                    "total_time_minutes INT NOT NULL" +
                    ")");
        }
    }

    @Before
    public void setUp() throws SQLException {
        completionRepository = new QuizCompletionRepositoryImpl(basicDataSource);

        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM quiz_completions");
        }
    }

    private QuizCompletion createSampleCompletion(Long userId, Long quizId, Double score, Double totalPossible) {
        QuizCompletion completion = new QuizCompletion();
        completion.setParticipantUserId(userId);
        completion.setTestId(quizId);
        completion.setFinalScore(score);
        completion.setTotalPossible(totalPossible);
        completion.setCompletionPercentage(BigDecimal.valueOf((score / totalPossible) * 100));
        completion.setStartedAt(LocalDateTime.now().minusMinutes(30));
        completion.setFinishedAt(LocalDateTime.now());
        completion.setTotalTimeMinutes(25);
        return completion;
    }

    @Test
    public void testFindFastestTime() {
        QuizCompletion slow = createSampleCompletion(1L, 1L, 7.0, 10.0);
        slow.setTotalTimeMinutes(30);
        completionRepository.saveCompletion(slow);

        QuizCompletion fast = createSampleCompletion(1L, 1L, 9.0, 10.0);
        fast.setTotalTimeMinutes(15);
        completionRepository.saveCompletion(fast);

        Optional<QuizCompletion> result = completionRepository.findFastestTime(1L, 1L);
        assertTrue(result.isPresent());
        assertEquals(15, result.get().getTotalTimeMinutes().intValue());
    }

    @Test
    public void testGetAverageScoreByQuiz() {
        completionRepository.saveCompletion(createSampleCompletion(1L, 1L, 8.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(2L, 1L, 6.0, 10.0));

        Double avg = completionRepository.getAverageScoreByQuiz(1L);
        assertNotNull(avg);
        assertEquals(70.0, avg, 0.01);
    }

    @Test
    public void testGetCompletionCountsByQuizzes() {
        completionRepository.saveCompletion(createSampleCompletion(1L, 1L, 8.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(2L, 2L, 7.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(3L, 1L, 9.0, 10.0));

        Map<Long, Integer> counts = completionRepository.getCompletionCountsByQuizzes(Arrays.asList(1L, 2L, 3L));
        assertEquals(2, (int) counts.get(1L));
        assertEquals(1, (int) counts.get(2L));
        assertEquals(0, (int) counts.get(3L));
    }

    @Test
    public void testGetAverageScoresByQuizzes() {
        completionRepository.saveCompletion(createSampleCompletion(1L, 1L, 8.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(2L, 1L, 6.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(3L, 2L, 7.0, 10.0));

        Map<Long, Double> averages = completionRepository.getAverageScoresByQuizzes(Arrays.asList(1L, 2L, 3L));
        assertEquals(70.0, averages.get(1L), 0.01);
        assertEquals(70.0, averages.get(2L), 0.01);
        assertNull(averages.get(3L));
    }

    @Test
    public void testFindRecentCompletionsByUser() {
        completionRepository.saveCompletion(createSampleCompletion(1L, 1L, 9.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(1L, 2L, 8.0, 10.0));

        List<QuizCompletion> recents = completionRepository.findRecentCompletionsByUser(1L, 5);
        assertEquals(2, recents.size());
    }

    @Test
    public void testGetCompletionCountByUser() {
        completionRepository.saveCompletion(createSampleCompletion(1L, 1L, 9.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(1L, 2L, 8.0, 10.0));
        completionRepository.saveCompletion(createSampleCompletion(2L, 2L, 7.0, 10.0));

        int count = completionRepository.getCompletionCountByUser(1);
        assertEquals(2, count);
    }

    @Test
    public void testFindByIdAndFindByQuiz() {
        QuizCompletion c1 = createSampleCompletion(10L, 99L, 5.0, 10.0);
        Long id = completionRepository.saveCompletion(c1);
        Optional<QuizCompletion> byId = completionRepository.findById(id);
        assertTrue(byId.isPresent());
        assertEquals(c1.getParticipantUserId(), byId.get().getParticipantUserId());

        List<QuizCompletion> byQuiz = completionRepository.findByQuiz(99L);
        assertEquals(1, byQuiz.size());
    }

    @Test
    public void testFindUserCompletionForQuizOnlyFinished() {
        QuizCompletion unfinished = createSampleCompletion(1L, 1L, 4.0, 10.0);
        unfinished.setFinishedAt(null);
        completionRepository.saveCompletion(unfinished);

        Optional<QuizCompletion> shouldBeEmpty = completionRepository.findUserCompletionForQuiz(1L, 1L);
        assertFalse(shouldBeEmpty.isPresent());

        QuizCompletion finished = createSampleCompletion(1L, 1L, 9.0, 10.0);
        completionRepository.saveCompletion(finished);

        Optional<QuizCompletion> shouldExist = completionRepository.findUserCompletionForQuiz(1L, 1L);
        assertTrue(shouldExist.isPresent());
        assertEquals(9.0, shouldExist.get().getFinalScore(), 0.01);
    }

    @Test
    public void testEmptyGetters() {
        assertTrue(completionRepository.findByQuiz(1000L).isEmpty());
        assertTrue(completionRepository.findRecentCompletionsByUser(1000L, 5).isEmpty());
        assertTrue(completionRepository.getAverageScoresByQuizzes(Collections.emptyList()).isEmpty());
        assertTrue(completionRepository.getCompletionCountsByQuizzes(Collections.emptyList()).isEmpty());
        assertNull(completionRepository.getAverageScoreByQuiz(999L));
        assertEquals(0, completionRepository.getCompletionCountByQuiz(999L));
    }
    @Test
    public void testFindRecentCompletionsByFriends() throws SQLException {
        try (Connection connection = basicDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE friendships (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "friendSenderId BIGINT NOT NULL," +
                    "friendReceiverId BIGINT NOT NULL" +
                    ")");
            statement.execute("INSERT INTO friendships (friendSenderId, friendReceiverId) VALUES (1, 2)");
            statement.execute("INSERT INTO friendships (friendSenderId, friendReceiverId) VALUES (3, 1)");
        }

        // User 2 and 3 are friends with user 1
        QuizCompletion completionFromFriend2 = createSampleCompletion(2L, 1L, 7.0, 10.0);
        completionRepository.saveCompletion(completionFromFriend2);

        QuizCompletion completionFromFriend3 = createSampleCompletion(3L, 2L, 8.0, 10.0);
        completionRepository.saveCompletion(completionFromFriend3);

        QuizCompletion selfCompletion = createSampleCompletion(1L, 3L, 9.0, 10.0); // should be excluded
        completionRepository.saveCompletion(selfCompletion);

        List<QuizCompletion> completions = completionRepository.findRecentCompletionsByFriends(1L, 10);
        assertEquals(2, completions.size());
        for (QuizCompletion qc : completions) {
            assertNotEquals(Long.valueOf(1L), qc.getParticipantUserId()); // user 1's own completion should not appear
        }
    }

    @Test(expected = RuntimeException.class)
    public void testFindRecentCompletionsByFriendsThrowsException() {
        QuizCompletionRepository brokenRepo = new QuizCompletionRepositoryImpl(new BasicDataSource() {{
            setUrl("jdbc:h2:mem:broken;INIT=RUNSCRIPT FROM 'non_existing.sql'");
            setUsername("sa");
            setPassword("");
        }});

        brokenRepo.findRecentCompletionsByFriends(1L, 5);
    }

    @Test(expected = RuntimeException.class)
    public void testSaveCompletionThrowsException() {
        QuizCompletionRepository brokenRepo = new QuizCompletionRepositoryImpl(new BasicDataSource() {{
            setUrl("jdbc:h2:mem:broken2;INIT=RUNSCRIPT FROM 'non_existing.sql'");
            setUsername("sa");
            setPassword("");
        }});

        QuizCompletion badCompletion = createSampleCompletion(1L, 1L, 9.0, 10.0);
        brokenRepo.saveCompletion(badCompletion);
    }

    @Test(expected = RuntimeException.class)
    public void testFindByIdThrowsException() {
        QuizCompletionRepository brokenRepo = new QuizCompletionRepositoryImpl(new BasicDataSource() {{
            setUrl("jdbc:h2:mem:broken3;INIT=RUNSCRIPT FROM 'non_existing.sql'");
            setUsername("sa");
            setPassword("");
        }});

        brokenRepo.findById(1L);
    }


}