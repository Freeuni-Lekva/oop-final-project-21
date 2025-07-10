package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceTest {

    private static DataSource dataSource;
    private QuizService quizService;
    private Quiz testQuiz;
    private Question testQuestion;

    @BeforeAll
    static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:quiztest;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=FALSE;DEFAULT_NULL_ORDERING=HIGH");
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
            
            stmt.execute("CREATE TABLE test_questions (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "author_user_id INT NOT NULL," +
                    "category_id BIGINT DEFAULT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "question_data MEDIUMBLOB NOT NULL," +
                    "question_title VARCHAR(128)," +
                    "question_type ENUM('TEXT', 'MULTIPLE_CHOICE', 'IMAGE') DEFAULT 'TEXT'," +
                    "FOREIGN KEY (author_user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (category_id) REFERENCES quiz_categories(id) ON DELETE CASCADE" +
                    ")");
                    
            stmt.execute("CREATE TABLE quiz_question_mapping (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "quiz_id BIGINT NOT NULL," +
                    "question_id BIGINT NOT NULL," +
                    "sequence_order BIGINT NOT NULL," +
                    "FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE," +
                    "UNIQUE (quiz_id, question_id)," +
                    "UNIQUE (quiz_id, sequence_order)" +
                    ")");
                    
            stmt.execute("CREATE TABLE quiz_completions (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "participant_user_id INT NOT NULL," +
                    "test_id BIGINT NOT NULL," +
                    "final_score DOUBLE DEFAULT 0," +
                    "total_possible DOUBLE DEFAULT 0," +
                    "completion_percentage DECIMAL(5,2)," +
                    "started_at DATETIME," +
                    "finished_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "total_time_minutes INT," +
                    "FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
                    ")");
            
            stmt.execute("INSERT INTO users (id, hashPassword, salt, firstName, lastName, userName, email) VALUES " +
                    "(100, 'hash', 'salt', 'Test', 'User', 'testuser', 'test@example.com')");
            stmt.execute("INSERT INTO quiz_categories (id, category_name, description) VALUES " +
                    "(10, 'Mathematics', 'Math questions')");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        quizService = new QuizService(dataSource);
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM quiz_question_mapping");
            stmt.execute("DELETE FROM quiz_completions");
            stmt.execute("DELETE FROM quizzes");
            stmt.execute("DELETE FROM test_questions");
        }
        
        testQuiz = new Quiz();
        testQuiz.setCreatorUserId(100);
        testQuiz.setCategoryId(10L);
        testQuiz.setTestTitle("Test Quiz");
        testQuiz.setTestDescription("Test Description");
        testQuiz.setTimeLimitMinutes(30L);
        
        testQuestion = new Question();
        testQuestion.setAuthorUserId(100);
        testQuestion.setCategoryId(10L);
        testQuestion.setQuestionTitle("Test Question");
        testQuestion.setQuestionType(QuestionType.TEXT);
    }

    @Test
    void createQuiz_ValidQuiz_ShouldReturnQuizId() {
        Long result = quizService.createQuiz(testQuiz);

        assertNotNull(result);
        assertTrue(result > 0);
        assertNotNull(testQuiz.getCreatedAt());
        assertEquals(0L, testQuiz.getLastQuestionNumber());
    }

    @Test
    void createQuiz_NullTitle_ShouldThrowException() {
        testQuiz.setTestTitle(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Quiz title is required", exception.getMessage());
    }

    @Test
    void createQuiz_EmptyTitle_ShouldThrowException() {
        testQuiz.setTestTitle("   ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Quiz title is required", exception.getMessage());
    }

    @Test
    void createQuiz_NullDescription_ShouldThrowException() {
        testQuiz.setTestDescription(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Quiz description is required", exception.getMessage());
    }

    @Test
    void createQuiz_EmptyDescription_ShouldThrowException() {
        testQuiz.setTestDescription("");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Quiz description is required", exception.getMessage());
    }

    @Test
    void createQuiz_InvalidTimeLimit_ShouldThrowException() {
        testQuiz.setTimeLimitMinutes(0L);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Time limit must be positive", exception.getMessage());
    }

    @Test
    void createQuiz_NullCategoryId_ShouldThrowException() {
        testQuiz.setCategoryId(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Category ID is required", exception.getMessage());
    }

    @Test
    void getQuizById_ExistingQuiz_ShouldReturnQuiz() {
        Long quizId = quizService.createQuiz(testQuiz);

        Optional<Quiz> result = quizService.getQuizById(quizId);

        assertTrue(result.isPresent());
        assertEquals("Test Quiz", result.get().getTestTitle());
    }

    @Test
    void getQuizById_NonExistingQuiz_ShouldReturnEmpty() {
        Optional<Quiz> result = quizService.getQuizById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getQuizzesByCreator_ValidParameters_ShouldReturnQuizzes() {
        quizService.createQuiz(testQuiz);

        List<Quiz> result = quizService.getQuizzesByCreator(100L, 0, 10);

        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTestTitle());
    }

    @Test
    void getQuizzesByCategory_ValidParameters_ShouldReturnQuizzes() {
        quizService.createQuiz(testQuiz);

        List<Quiz> result = quizService.getQuizzesByCategory(10L, 0, 5);

        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTestTitle());
    }

    @Test
    void getAllQuizzes_ValidParameters_ShouldReturnQuizzes() {
        quizService.createQuiz(testQuiz);

        List<Quiz> result = quizService.getAllQuizzes(0, 15);

        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTestTitle());
    }

    @Test
    void updateQuiz_ValidQuiz_ShouldReturnTrue() {
        Long quizId = quizService.createQuiz(testQuiz);
        testQuiz.setId(quizId);
        testQuiz.setTestTitle("Updated Quiz Title");

        boolean result = quizService.updateQuiz(testQuiz);

        assertTrue(result);
    }

    @Test
    void updateQuiz_InvalidQuiz_ShouldThrowException() {
        testQuiz.setTestTitle(null);

        assertThrows(IllegalArgumentException.class, () -> quizService.updateQuiz(testQuiz));
    }

    @Test
    void deleteQuiz_ValidId_ShouldReturnRepositoryResult() {
        Long quizId = quizService.createQuiz(testQuiz);

        boolean result = quizService.deleteQuiz(quizId);

        assertTrue(result);
        
        Optional<Quiz> deletedQuiz = quizService.getQuizById(quizId);
        assertFalse(deletedQuiz.isPresent());
    }

    @Test
    void getQuizQuestionCount_ShouldReturnRepositoryResult() {
        Long quizId = quizService.createQuiz(testQuiz);

        int result = quizService.getQuizQuestionCount(quizId);

        assertEquals(0, result);
    }

    @Test
    void isQuizOwner_UserIsOwner_ShouldReturnTrue() {
        Long quizId = quizService.createQuiz(testQuiz);

        boolean result = quizService.isQuizOwner(quizId, 100L);

        assertTrue(result);
    }

    @Test
    void isQuizOwner_UserIsNotOwner_ShouldReturnFalse() {
        Long quizId = quizService.createQuiz(testQuiz);

        boolean result = quizService.isQuizOwner(quizId, 999L);

        assertFalse(result);
    }

    @Test
    void isQuizOwner_QuizNotFound_ShouldReturnFalse() {
        boolean result = quizService.isQuizOwner(999L, 100L);

        assertFalse(result);
    }

    @Test
    void getCompletionCountForQuiz_ShouldReturnZero() {
        Long quizId = quizService.createQuiz(testQuiz);

        int result = quizService.getCompletionCountForQuiz(quizId);

        assertEquals(0, result);
    }

    @Test
    void getAverageScoreForQuiz_ShouldReturnNull() {
        Long quizId = quizService.createQuiz(testQuiz);

        Double result = quizService.getAverageScoreForQuiz(quizId);

        assertNull(result);
    }
} 