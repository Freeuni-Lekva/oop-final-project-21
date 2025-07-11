package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.PopularQuizDTO;
import com.freeuni.quiz.repository.QuizCompletionRepository;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuizServiceTest {

    private static DataSource dataSource;
    private QuizService quizService;
    private Quiz testQuiz;
    private Question testQuestion;
    private static QuizCompletionRepository quizCompletionRepository;

    @BeforeAll
    static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:quiztest;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=FALSE;DEFAULT_NULL_ORDERING=HIGH");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;
        quizCompletionRepository=new QuizCompletionRepositoryImpl(ds);

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
                    "points DOUBLE DEFAULT 10," +
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
        testQuestion.setPoints(10.0);
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


    @Test
    void addQuestionToQuiz_Valid_ShouldReturnTrueAndUpdateLastQuestionNumber() {
        Long quizId = quizService.createQuiz(testQuiz);

        Question question = new Question();
        question.setAuthorUserId(100);
        question.setCategoryId(10L);
        question.setQuestionTitle("Q1");
        question.setQuestionType(QuestionType.TEXT);
        Long questionId = insertTestQuestion(question);

        boolean added = quizService.addQuestionToQuiz(quizId, questionId, 1L);
        assertTrue(added);

        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
        assertTrue(quizOpt.isPresent());
        assertEquals(1L, quizOpt.get().getLastQuestionNumber());
    }

    @Test
    void addQuestionToQuiz_QuizNotFound_ShouldReturnFalse() {
        boolean result = quizService.addQuestionToQuiz(999L, 1L, 1L);
        assertFalse(result);
    }

    @Test
    void addQuestionToQuiz_QuestionNotFound_ShouldReturnFalse() {
        Long quizId = quizService.createQuiz(testQuiz);
        boolean result = quizService.addQuestionToQuiz(quizId, 999L, 1L);
        assertFalse(result);
    }

    @Test
    void removeQuestionFromQuiz_ShouldReturnTrue() {
        Long quizId = quizService.createQuiz(testQuiz);
        Question question = new Question();
        question.setAuthorUserId(100);
        question.setCategoryId(10L);
        question.setQuestionTitle("Q2");
        question.setQuestionType(QuestionType.TEXT);
        Long questionId = insertTestQuestion(question);

        quizService.addQuestionToQuiz(quizId, questionId, 1L);

        boolean removed = quizService.removeQuestionFromQuiz(quizId, questionId);
        assertTrue(removed);
    }

    @Test
    void getQuizQuestions_ShouldReturnList() {
        Long quizId = quizService.createQuiz(testQuiz);

        Question question1 = new Question();
        question1.setAuthorUserId(100);
        question1.setCategoryId(10L);
        question1.setQuestionTitle("Q1");
        question1.setQuestionType(QuestionType.TEXT);
        Long q1Id = insertTestQuestion(question1);

        Question question2 = new Question();
        question2.setAuthorUserId(100);
        question2.setCategoryId(10L);
        question2.setQuestionTitle("Q2");
        question2.setQuestionType(QuestionType.TEXT);
        Long q2Id = insertTestQuestion(question2);

        quizService.addQuestionToQuiz(quizId, q1Id, 1L);
        quizService.addQuestionToQuiz(quizId, q2Id, 2L);

        List<Question> questions = quizService.getQuizQuestions(quizId);
        assertEquals(2, questions.size());
        assertTrue(questions.stream().anyMatch(q -> q.getId().equals(q1Id)));
        assertTrue(questions.stream().anyMatch(q -> q.getId().equals(q2Id)));
    }

    @Test
    void getCompletionCountsForQuizzes_ShouldReturnEmptyForEmptyList() {
        Map<Integer, Integer> result = quizService.getCompletionCountsForQuizzes(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getAverageScoresForQuizzes_ShouldReturnEmptyForEmptyList() {
        Map<Integer, Double> result = quizService.getAverageScoresForQuizzes(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getPopularQuizzesWithCompletionCount_ShouldReturnList() {
        List<PopularQuizDTO> popularQuizzes = quizService.getPopularQuizzesWithCompletionCount(5);
        assertNotNull(popularQuizzes);
    }

    @Test
    void getRecentlyCreatedQuizzes_ShouldReturnList() {
        quizService.createQuiz(testQuiz);
        List<Quiz> recent = quizService.getRecentlyCreatedQuizzes(5);
        assertFalse(recent.isEmpty());
    }

    @Test
    void getRecentlyCreatedByUser_ShouldReturnList() {
        quizService.createQuiz(testQuiz);
        List<Quiz> recentByUser = quizService.getRecentlyCreatedByUser(100L, 5);
        assertFalse(recentByUser.isEmpty());
    }

    @Test
    void getRecentCompletionsByUser_ShouldReturnList() {
        List<QuizCompletion> completions = quizService.getRecentCompletionsByUser(100L, 5);
        assertNotNull(completions);
    }
    @Test
    void getCompletionCountsForQuizzes_ShouldReturnCorrectMap() throws Exception {
        QuizService quizService = new QuizService(null); // no DataSource needed here

        // Mock the QuizCompletionRepository
        QuizCompletionRepository mockQuizCompletionRepository = Mockito.mock(QuizCompletionRepository.class);

        // Inject the mock into the private final field using reflection
        java.lang.reflect.Field field = QuizService.class.getDeclaredField("quizCompletionRepository");
        field.setAccessible(true);
        field.set(quizService, mockQuizCompletionRepository);

        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        Map<Long, Integer> completionCounts = new HashMap<>();
        completionCounts.put(1L, 5);
        completionCounts.put(2L, 10);

        when(mockQuizCompletionRepository.getCompletionCountsByQuizzes(anyList())).thenReturn(completionCounts);

        Map<Integer, Integer> result = quizService.getCompletionCountsForQuizzes(quizzes);

        assertEquals(2, result.size());
        assertEquals(5, result.get(1));
        assertEquals(10, result.get(2));

        verify(mockQuizCompletionRepository).getCompletionCountsByQuizzes(anyList());
    }
    @Test
    void getAverageScoresForQuizzes_ShouldReturnCorrectMap() {
        QuizService quizService = new QuizService(null); // pass null DataSource

        // Create a mock QuizCompletionRepository
        QuizCompletionRepository mockQuizCompletionRepository = Mockito.mock(QuizCompletionRepository.class);

        // Use reflection to inject mock into private final field
        try {
            java.lang.reflect.Field field = QuizService.class.getDeclaredField("quizCompletionRepository");
            field.setAccessible(true);
            field.set(quizService, mockQuizCompletionRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        Map<Long, Double> averageScores = new HashMap<>();
        averageScores.put(1L, 85.5);
        averageScores.put(2L, 90.0);

        when(mockQuizCompletionRepository.getAverageScoresByQuizzes(anyList())).thenReturn(averageScores);

        Map<Integer, Double> result = quizService.getAverageScoresForQuizzes(quizzes);

        assertEquals(2, result.size());
        assertEquals(85.5, result.get(1));
        assertEquals(90.0, result.get(2));

        verify(mockQuizCompletionRepository).getAverageScoresByQuizzes(anyList());
    }

    @Test
    void getAverageScoresForQuizzes_NullOrEmptyList_ShouldReturnEmptyMap() {
        QuizService quizService = new QuizService(null);

        Map<Integer, Double> resultNull = quizService.getAverageScoresForQuizzes(null);
        Map<Integer, Double> resultEmpty = quizService.getAverageScoresForQuizzes(Collections.emptyList());

        assertTrue(resultNull.isEmpty());
        assertTrue(resultEmpty.isEmpty());
    }
    private Long insertTestQuestion(Question question) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String insert = String.format(
                    "INSERT INTO test_questions (author_user_id, category_id, question_title, question_type, question_data, points) VALUES (%d, %d, '%s', '%s', ?, %s)",
                    question.getAuthorUserId(),
                    question.getCategoryId(),
                    question.getQuestionTitle(),
                    question.getQuestionType().name(),
                    question.getPoints() != null ? question.getPoints() : 10.0
            );
            var ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setBytes(1, new byte[0]);

            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            rs.next();
            Long id = rs.getLong(1);
            question.setId(id);
            ps.close();
            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}