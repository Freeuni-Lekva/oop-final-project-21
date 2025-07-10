package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.quiz_util.AbstractQuestionHandler;
import com.freeuni.quiz.quiz_util.TextQuestionHandler;
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

class QuestionServiceTest {

    private static DataSource dataSource;
    private QuestionService questionService;
    private Question testQuestion;

    @BeforeAll
    static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:questiontest;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=FALSE;DEFAULT_NULL_ORDERING=HIGH");
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
            
            stmt.execute("INSERT INTO users (id, hashPassword, salt, firstName, lastName, userName, email) VALUES " +
                    "(100, 'hash', 'salt', 'Test', 'User', 'testuser', 'test@example.com')");
            stmt.execute("INSERT INTO quiz_categories (id, category_name, description) VALUES " +
                    "(10, 'Mathematics', 'Math questions')");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        questionService = new QuestionService(dataSource);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM test_questions");
        }

        AbstractQuestionHandler testHandler = new TextQuestionHandler("What is 2+2?", Arrays.asList("4", "four"));
        
        testQuestion = new Question();
        testQuestion.setAuthorUserId(100);
        testQuestion.setCategoryId(10L);
        testQuestion.setQuestionTitle("Math Question");
        testQuestion.setQuestionType(QuestionType.TEXT);
        testQuestion.setQuestionHandler(testHandler);
    }

    @Test
    void createQuestion_ValidQuestion_ShouldReturnQuestionId() {
        Long result = questionService.createQuestion(testQuestion);

        assertNotNull(result);
        assertTrue(result > 0);
        assertNotNull(testQuestion.getCreatedAt());
    }

    @Test
    void createQuestion_NullTitle_ShouldThrowException() {
        testQuestion.setQuestionTitle(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Question title is required", exception.getMessage());
    }

    @Test
    void createQuestion_EmptyTitle_ShouldThrowException() {
        testQuestion.setQuestionTitle("   ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Question title is required", exception.getMessage());
    }

    @Test
    void createQuestion_NullQuestionType_ShouldThrowException() {
        testQuestion.setQuestionType(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Question type is required", exception.getMessage());
    }

    @Test
    void createQuestion_NullQuestionHandler_ShouldThrowException() {
        testQuestion.setQuestionHandler(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Question handler is required", exception.getMessage());
    }

    @Test
    void createQuestion_NullCategoryId_ShouldThrowException() {
        testQuestion.setCategoryId(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Category ID is required", exception.getMessage());
    }

    @Test
    void getQuestionById_ExistingQuestion_ShouldReturnQuestion() {
        Long questionId = questionService.createQuestion(testQuestion);

        Optional<Question> result = questionService.getQuestionById(questionId);

        assertTrue(result.isPresent());
        assertEquals("Math Question", result.get().getQuestionTitle());
    }

    @Test
    void getQuestionById_NonExistingQuestion_ShouldReturnEmpty() {
        Optional<Question> result = questionService.getQuestionById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getQuestionsByAuthor_ValidParameters_ShouldReturnQuestions() {
        questionService.createQuestion(testQuestion);

        List<Question> result = questionService.getQuestionsByAuthor(100L, 0, 10);

        assertEquals(1, result.size());
        assertEquals("Math Question", result.get(0).getQuestionTitle());
    }

    @Test
    void getQuestionsByAuthor_DifferentPageAndSize_ShouldCalculateOffsetCorrectly() {
        questionService.createQuestion(testQuestion);

        List<Question> result = questionService.getQuestionsByAuthor(100L, 1, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void getQuestionsByCategory_ValidParameters_ShouldReturnQuestions() {
        questionService.createQuestion(testQuestion);

        List<Question> result = questionService.getQuestionsByCategory(10L, 0, 8);

        assertEquals(1, result.size());
        assertEquals("Math Question", result.get(0).getQuestionTitle());
    }

    @Test
    void getQuestionsByType_ValidParameters_ShouldReturnQuestions() {
        questionService.createQuestion(testQuestion);

        List<Question> result = questionService.getQuestionsByType(QuestionType.TEXT, 0, 15);

        assertEquals(1, result.size());
        assertEquals("Math Question", result.get(0).getQuestionTitle());
    }

    @Test
    void searchQuestionsByTitle_ValidParameters_ShouldReturnQuestions() {
        questionService.createQuestion(testQuestion);

        List<Question> result = questionService.searchQuestionsByTitle("Math", 0, 10);

        assertEquals(1, result.size());
        assertEquals("Math Question", result.get(0).getQuestionTitle());
    }

    @Test
    void updateQuestion_ValidQuestion_ShouldReturnTrue() {
        Long questionId = questionService.createQuestion(testQuestion);
        testQuestion.setId(questionId);
        testQuestion.setQuestionTitle("Updated Math Question");

        boolean result = questionService.updateQuestion(testQuestion);

        assertTrue(result);
    }

    @Test
    void updateQuestion_InvalidQuestion_ShouldThrowException() {
        testQuestion.setQuestionTitle(null);

        assertThrows(IllegalArgumentException.class, () -> questionService.updateQuestion(testQuestion));
    }

    @Test
    void updateQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        testQuestion.setId(999L);

        boolean result = questionService.updateQuestion(testQuestion);

        assertFalse(result);
    }

    @Test
    void deleteQuestion_ValidId_ShouldReturnRepositoryResult() {
        Long questionId = questionService.createQuestion(testQuestion);

        boolean result = questionService.deleteQuestion(questionId);

        assertTrue(result);
        
        Optional<Question> deletedQuestion = questionService.getQuestionById(questionId);
        assertFalse(deletedQuestion.isPresent());
    }

    @Test
    void deleteQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        boolean result = questionService.deleteQuestion(999L);

        assertFalse(result);
    }

    @Test
    void isQuestionOwner_UserIsOwner_ShouldReturnTrue() {
        Long questionId = questionService.createQuestion(testQuestion);

        boolean result = questionService.isQuestionOwner(questionId, 100L);

        assertTrue(result);
    }

    @Test
    void isQuestionOwner_UserIsNotOwner_ShouldReturnFalse() {
        Long questionId = questionService.createQuestion(testQuestion);

        boolean result = questionService.isQuestionOwner(questionId, 999L);

        assertFalse(result);
    }

    @Test
    void isQuestionOwner_QuestionNotFound_ShouldReturnFalse() {
        boolean result = questionService.isQuestionOwner(999L, 100L);

        assertFalse(result);
    }

    @Test
    void createQuestion_SetsCreatedAtTimestamp() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        questionService.createQuestion(testQuestion);

        assertNotNull(testQuestion.getCreatedAt());
        assertTrue(testQuestion.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
    }

    @Test
    void getQuestionsByType_AllQuestionTypes_ShouldWork() {
        questionService.createQuestion(testQuestion);
        
        List<Question> textResult = questionService.getQuestionsByType(QuestionType.TEXT, 0, 10);
        assertEquals(1, textResult.size());
        
        List<Question> mcResult = questionService.getQuestionsByType(QuestionType.MULTIPLE_CHOICE, 0, 10);
        assertEquals(0, mcResult.size());
        
        List<Question> imageResult = questionService.getQuestionsByType(QuestionType.IMAGE, 0, 10);
        assertEquals(0, imageResult.size());
    }
} 