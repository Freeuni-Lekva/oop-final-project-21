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
import java.util.List;
import java.util.Optional;

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

        // Clear completions table before each test
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
    public void testSaveAndFindById() {
        QuizCompletion completion = createSampleCompletion(100L, 200L, 8.5, 10.0);
        
        Long savedId = completionRepository.saveCompletion(completion);
        assertNotNull(savedId);
        assertTrue(savedId > 0);

        Optional<QuizCompletion> retrieved = completionRepository.findById(savedId);
        assertTrue(retrieved.isPresent());
        assertEquals(completion.getParticipantUserId(), retrieved.get().getParticipantUserId());
        assertEquals(completion.getTestId(), retrieved.get().getTestId());
        assertEquals(completion.getFinalScore(), retrieved.get().getFinalScore(), 0.01);
    }

    @Test
    public void testFindUserCompletionForQuiz_ExistingCompletion() {
        // Create and save a completion
        QuizCompletion completion = createSampleCompletion(100L, 200L, 7.5, 10.0);
        completionRepository.saveCompletion(completion);

        // Test finding the completion
        Optional<QuizCompletion> found = completionRepository.findUserCompletionForQuiz(100L, 200L);
        
        assertTrue(found.isPresent());
        assertEquals(Long.valueOf(100), found.get().getParticipantUserId());
        assertEquals(Long.valueOf(200), found.get().getTestId());
        assertEquals(7.5, found.get().getFinalScore(), 0.01);
        assertEquals(10.0, found.get().getTotalPossible(), 0.01);
        assertEquals(75.0, found.get().getCompletionPercentage().doubleValue(), 0.01);
    }

    @Test
    public void testFindUserCompletionForQuiz_NonExistingCompletion() {
        // Try to find a completion that doesn't exist
        Optional<QuizCompletion> found = completionRepository.findUserCompletionForQuiz(999L, 888L);
        
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindUserCompletionForQuiz_MultipleCompletions_ReturnsLatest() {
        // Create multiple completions for same user and quiz
        QuizCompletion completion1 = createSampleCompletion(100L, 200L, 6.0, 10.0);
        completion1.setFinishedAt(LocalDateTime.now().minusHours(2));
        completionRepository.saveCompletion(completion1);

        QuizCompletion completion2 = createSampleCompletion(100L, 200L, 8.5, 10.0);
        completion2.setFinishedAt(LocalDateTime.now().minusHours(1));
        completionRepository.saveCompletion(completion2);

        QuizCompletion completion3 = createSampleCompletion(100L, 200L, 9.0, 10.0);
        completion3.setFinishedAt(LocalDateTime.now());
        completionRepository.saveCompletion(completion3);

        // Should return the most recent completion (completion3)
        Optional<QuizCompletion> found = completionRepository.findUserCompletionForQuiz(100L, 200L);
        
        assertTrue(found.isPresent());
        assertEquals(9.0, found.get().getFinalScore(), 0.01);
    }

    @Test
    public void testFindUserCompletionForQuiz_DifferentUsersAndQuizzes() {
        // Create completions for different users and quizzes
        QuizCompletion completion1 = createSampleCompletion(100L, 200L, 8.0, 10.0);
        completionRepository.saveCompletion(completion1);

        QuizCompletion completion2 = createSampleCompletion(101L, 200L, 7.5, 10.0);
        completionRepository.saveCompletion(completion2);

        QuizCompletion completion3 = createSampleCompletion(100L, 201L, 9.0, 10.0);
        completionRepository.saveCompletion(completion3);

        // Test finding specific combinations
        Optional<QuizCompletion> found1 = completionRepository.findUserCompletionForQuiz(100L, 200L);
        assertTrue(found1.isPresent());
        assertEquals(8.0, found1.get().getFinalScore(), 0.01);

        Optional<QuizCompletion> found2 = completionRepository.findUserCompletionForQuiz(101L, 200L);
        assertTrue(found2.isPresent());
        assertEquals(7.5, found2.get().getFinalScore(), 0.01);

        Optional<QuizCompletion> found3 = completionRepository.findUserCompletionForQuiz(100L, 201L);
        assertTrue(found3.isPresent());
        assertEquals(9.0, found3.get().getFinalScore(), 0.01);

        // Test non-existing combination
        Optional<QuizCompletion> notFound = completionRepository.findUserCompletionForQuiz(101L, 201L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindUserCompletionForQuiz_OnlyFinishedCompletions() {
        // Create an unfinished completion (finished_at is null)
        QuizCompletion unfinishedCompletion = createSampleCompletion(100L, 200L, 5.0, 10.0);
        unfinishedCompletion.setFinishedAt(null);
        completionRepository.saveCompletion(unfinishedCompletion);

        // Should not find unfinished completion
        Optional<QuizCompletion> found = completionRepository.findUserCompletionForQuiz(100L, 200L);
        assertFalse(found.isPresent());

        // Create a finished completion
        QuizCompletion finishedCompletion = createSampleCompletion(100L, 200L, 8.0, 10.0);
        completionRepository.saveCompletion(finishedCompletion);

        // Should find the finished completion
        found = completionRepository.findUserCompletionForQuiz(100L, 200L);
        assertTrue(found.isPresent());
        assertEquals(8.0, found.get().getFinalScore(), 0.01);
    }

    @Test
    public void testFindByQuiz() {
        // Create multiple completions for the same quiz
        QuizCompletion completion1 = createSampleCompletion(100L, 200L, 8.0, 10.0);
        completionRepository.saveCompletion(completion1);

        QuizCompletion completion2 = createSampleCompletion(101L, 200L, 7.5, 10.0);
        completionRepository.saveCompletion(completion2);

        QuizCompletion completion3 = createSampleCompletion(102L, 201L, 9.0, 10.0);
        completionRepository.saveCompletion(completion3);

        List<QuizCompletion> completions = completionRepository.findByQuiz(200L);
        assertEquals(2, completions.size());

        List<QuizCompletion> singleCompletion = completionRepository.findByQuiz(201L);
        assertEquals(1, singleCompletion.size());

        List<QuizCompletion> noCompletions = completionRepository.findByQuiz(999L);
        assertEquals(0, noCompletions.size());
    }

    @Test
    public void testGetCompletionCountByQuiz() {
        // Create multiple completions for different quizzes
        QuizCompletion completion1 = createSampleCompletion(100L, 200L, 8.0, 10.0);
        completionRepository.saveCompletion(completion1);

        QuizCompletion completion2 = createSampleCompletion(101L, 200L, 7.5, 10.0);
        completionRepository.saveCompletion(completion2);

        QuizCompletion completion3 = createSampleCompletion(102L, 201L, 9.0, 10.0);
        completionRepository.saveCompletion(completion3);

        int count200 = completionRepository.getCompletionCountByQuiz(200L);
        assertEquals(2, count200);

        int count201 = completionRepository.getCompletionCountByQuiz(201L);
        assertEquals(1, count201);

        int countNone = completionRepository.getCompletionCountByQuiz(999L);
        assertEquals(0, countNone);
    }
} 