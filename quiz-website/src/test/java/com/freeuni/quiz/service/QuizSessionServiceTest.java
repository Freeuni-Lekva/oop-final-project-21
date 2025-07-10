package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.service.QuizSessionService.QuizResults;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class QuizSessionServiceTest {

    private static DataSource dataSource;
    private QuizSessionService quizSessionService;
    private Quiz testQuiz;
    private QuizSession testSession;

    @BeforeAll
    static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:quizsessiontest;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=FALSE;DEFAULT_NULL_ORDERING=HIGH");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            
            stmt.execute("CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "hashPassword VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "firstName VARCHAR(100) NOT NULL," +
                    "lastName VARCHAR(100) NOT NULL," +
                    "userName VARCHAR(100) UNIQUE NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "imageURL VARCHAR(2083)," +
                    "bio TEXT" +
                    ")");
            
            stmt.execute("CREATE TABLE quiz_categories (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "category_name VARCHAR(64) NOT NULL," +
                    "description TEXT," +
                    "is_active BOOLEAN DEFAULT TRUE" +
                    ")");
                    
            stmt.execute("CREATE TABLE quizzes (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "creator_user_id INT NOT NULL," +
                    "category_id BIGINT," +
                    "last_question_number BIGINT DEFAULT 0," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "test_title VARCHAR(128) NOT NULL," +
                    "test_description VARCHAR(256)," +
                    "time_limit_minutes BIGINT DEFAULT 10," +
                    "FOREIGN KEY (creator_user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (category_id) REFERENCES quiz_categories(id) ON DELETE CASCADE" +
                    ")");
            
            stmt.execute("CREATE TABLE quiz_sessions (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "participant_user_id INT UNIQUE NOT NULL," +
                    "test_id BIGINT NOT NULL," +
                    "current_question_num BIGINT DEFAULT 0," +
                    "time_allocated BIGINT," +
                    "session_start DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
                    ")");
                    
            stmt.execute("CREATE TABLE participant_answers (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "participant_user_id INT NOT NULL," +
                    "test_id BIGINT NOT NULL," +
                    "question_number BIGINT NOT NULL," +
                    "points_earned DOUBLE DEFAULT 0," +
                    "time_spent_seconds INT," +
                    "answer_text TEXT," +
                    "UNIQUE(participant_user_id, test_id, question_number)," +
                    "FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
                    ")");
            
            stmt.execute("INSERT INTO users (id, hashPassword, salt, firstName, lastName, userName, email) VALUES " +
                    "(100, 'hash', 'salt', 'Test', 'User', 'testuser', 'test@example.com')");
            stmt.execute("INSERT INTO users (id, hashPassword, salt, firstName, lastName, userName, email) VALUES " +
                    "(101, 'hash2', 'salt2', 'Another', 'User', 'anotheruser', 'another@example.com')");
            stmt.execute("INSERT INTO quiz_categories (id, category_name, description) VALUES " +
                    "(10, 'Mathematics', 'Math questions')");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        quizSessionService = new QuizSessionService(dataSource);
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM participant_answers");
            stmt.execute("DELETE FROM quiz_sessions");
            stmt.execute("DELETE FROM quizzes");
        }
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO quizzes (id, creator_user_id, category_id, test_title, test_description, time_limit_minutes) VALUES " +
                    "(1, 100, 10, 'Test Quiz', 'A test quiz', 30)");
        }
        
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTimeLimitMinutes(30L);
        
        testSession = new QuizSession();
        testSession.setParticipantUserId(100L);
        testSession.setTestId(1L);
        testSession.setTimeAllocated(30L);
        testSession.setCurrentQuestionNum(0L);
    }

    @Test
    void startQuizSession_ValidParameters_ShouldReturnTrue() {
        Long participantId = 100L;
        Long quizId = 1L;
        
        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertTrue(result);
        
        Optional<QuizSession> session = quizSessionService.getActiveSession(participantId);
        assertTrue(session.isPresent());
        assertEquals(participantId, session.get().getParticipantUserId());
        assertEquals(quizId, session.get().getTestId());
    }

    @Test
    void startQuizSession_NonExistentQuiz_ShouldReturnFalse() {
        Long participantId = 100L;
        Long quizId = 999L;
        
        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertFalse(result);
    }

    @Test
    void startQuizSession_ParticipantHasActiveSession_ShouldReturnFalse() {
        Long participantId = 100L;
        Long quizId = 1L;
        
        quizSessionService.startQuizSession(participantId, quizId);
        
        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertFalse(result);
    }

    @Test
    void getActiveSession_ExistingSession_ShouldReturnSession() {
        Long participantId = 100L;
        quizSessionService.startQuizSession(participantId, 1L);

        Optional<QuizSession> result = quizSessionService.getActiveSession(participantId);

        assertTrue(result.isPresent());
        assertEquals(participantId, result.get().getParticipantUserId());
    }

    @Test
    void getActiveSession_NoSession_ShouldReturnEmpty() {
        Optional<QuizSession> result = quizSessionService.getActiveSession(100L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateCurrentQuestion_ValidParameters_ShouldReturnTrue() {
        Long participantId = 100L;
        quizSessionService.startQuizSession(participantId, 1L);

        boolean result = quizSessionService.updateCurrentQuestion(participantId, 5L);

        assertTrue(result);
    }

    @Test
    void updateCurrentQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        boolean result = quizSessionService.updateCurrentQuestion(999L, 5L);

        assertFalse(result);
    }

    @Test
    void submitAnswer_ValidAnswer_ShouldReturnTrue() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 3L;
        String answerText = "Paris";
        Double pointsEarned = 1.0;
        Integer timeSpent = 30;

        quizSessionService.startQuizSession(participantId, testId);

        boolean result = quizSessionService.submitAnswer(
            participantId, testId, questionNumber, answerText, pointsEarned, timeSpent);

        assertTrue(result);

        List<Long> answeredQuestions = quizSessionService.getAnsweredQuestions(participantId, testId);
        assertTrue(answeredQuestions.contains(questionNumber));
    }

    @Test
    void getQuizResults_WithAnswers_ShouldReturnCorrectResults() {
        Long participantId = 100L;
        Long testId = 1L;
        
        quizSessionService.startQuizSession(participantId, testId);
        
        quizSessionService.submitAnswer(participantId, testId, 1L, "Answer 1", 1.0, 30);
        quizSessionService.submitAnswer(participantId, testId, 2L, "Answer 2", 0.5, 25);
        quizSessionService.submitAnswer(participantId, testId, 3L, "Answer 3", 1.0, 35);

        QuizResults result = quizSessionService.getQuizResults(participantId, testId);

        assertNotNull(result);
        assertEquals(participantId, result.participantId());
        assertEquals(testId, result.testId());
        assertEquals(2.5, result.totalScore(), 0.001);
        assertEquals(3, result.totalQuestions());
        assertEquals(3, result.answers().size());
        assertEquals(2.5 / 3, result.getAverageScore(), 0.001);
    }

    @Test
    void getQuizResults_NoAnswers_ShouldReturnEmptyResults() {
        Long participantId = 100L;
        Long testId = 1L;

        QuizResults result = quizSessionService.getQuizResults(participantId, testId);

        assertNotNull(result);
        assertEquals(participantId, result.participantId());
        assertEquals(testId, result.testId());
        assertEquals(0.0, result.totalScore(), 0.001);
        assertEquals(0, result.totalQuestions());
        assertTrue(result.answers().isEmpty());
        assertEquals(0.0, result.getAverageScore(), 0.001);
    }

    @Test
    void getAnsweredQuestions_ShouldReturnRepositoryResult() {
        Long participantId = 100L;
        Long testId = 1L;
        
        quizSessionService.startQuizSession(participantId, testId);
        
        quizSessionService.submitAnswer(participantId, testId, 1L, "Answer 1", 1.0, 30);
        quizSessionService.submitAnswer(participantId, testId, 2L, "Answer 2", 0.5, 25);
        
        List<Long> result = quizSessionService.getAnsweredQuestions(participantId, testId);

        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    void getQuestionScore_ExistingScore_ShouldReturnScore() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 1L;
        Double expectedScore = 0.75;
        
        quizSessionService.startQuizSession(participantId, testId);
        quizSessionService.submitAnswer(participantId, testId, questionNumber, "Answer", expectedScore, 30);

        Optional<Double> result = quizSessionService.getQuestionScore(participantId, testId, questionNumber);

        assertTrue(result.isPresent());
        assertEquals(expectedScore, result.get());
    }

    @Test
    void getQuestionScore_NoScore_ShouldReturnEmpty() {
        Optional<Double> result = quizSessionService.getQuestionScore(100L, 1L, 999L);

        assertFalse(result.isPresent());
    }

    @Test
    void endQuizSession_ShouldReturnRepositoryResult() {
        Long participantId = 100L;
        quizSessionService.startQuizSession(participantId, 1L);

        boolean result = quizSessionService.endQuizSession(participantId);

        assertTrue(result);
        
        Optional<QuizSession> session = quizSessionService.getActiveSession(participantId);
        assertFalse(session.isPresent());
    }

    @Test
    void endQuizSession_RepositoryReturnsFalse_ShouldReturnFalse() {
        boolean result = quizSessionService.endQuizSession(999L);

        assertFalse(result);
    }

    @Test
    void hasActiveSession_ExistingSession_ShouldReturnTrue() {
        Long participantId = 100L;
        quizSessionService.startQuizSession(participantId, 1L);

        boolean result = quizSessionService.hasActiveSession(participantId);

        assertTrue(result);
    }

    @Test
    void hasActiveSession_NoSession_ShouldReturnFalse() {
        boolean result = quizSessionService.hasActiveSession(999L);

        assertFalse(result);
    }

    @Test
    void startQuizSession_SetsCorrectSessionProperties() {
        Long participantId = 100L;
        Long quizId = 1L;
        LocalDateTime beforeStart = LocalDateTime.now();
        
        quizSessionService.startQuizSession(participantId, quizId);

        Optional<QuizSession> session = quizSessionService.getActiveSession(participantId);
        assertTrue(session.isPresent());
        assertEquals(participantId, session.get().getParticipantUserId());
        assertEquals(quizId, session.get().getTestId());
        assertEquals(30L, session.get().getTimeAllocated());
        assertEquals(0L, session.get().getCurrentQuestionNum());
        assertTrue(session.get().getSessionStart().isAfter(beforeStart.minusSeconds(1)));
    }

    @Test
    void quizResults_Constructor_ShouldSetAllProperties() {
        Long participantId = 200L;
        Long testId = 5L;
        double totalScore = 7.5;
        int totalQuestions = 10;
        List<ParticipantAnswer> answers = Arrays.asList(
            createAnswer(1L, 1.0), 
            createAnswer(2L, 0.5)
        );

        QuizResults results = new QuizResults(participantId, testId, totalScore, totalQuestions, answers);

        assertEquals(participantId, results.participantId());
        assertEquals(testId, results.testId());
        assertEquals(totalScore, results.totalScore(), 0.001);
        assertEquals(totalQuestions, results.totalQuestions());
        assertEquals(answers, results.answers());
        assertEquals(0.75, results.getAverageScore(), 0.001);
    }

    @Test
    void quizResults_GetAverageScore_ZeroQuestions_ShouldReturnZero() {
        QuizResults results = new QuizResults(1L, 1L, 5.0, 0, new ArrayList<>());

        double averageScore = results.getAverageScore();

        assertEquals(0.0, averageScore, 0.001);
    }

    private ParticipantAnswer createAnswer(Long questionNumber, Double points) {
        ParticipantAnswer answer = new ParticipantAnswer();
        answer.setQuestionNumber(questionNumber);
        answer.setPointsEarned(points);
        answer.setParticipantUserId(100L);
        answer.setTestId(1L);
        return answer;
    }
} 