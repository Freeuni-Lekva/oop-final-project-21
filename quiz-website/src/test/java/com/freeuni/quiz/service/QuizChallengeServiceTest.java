package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.QuizChallengeDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizChallenge;
import com.freeuni.quiz.repository.QuizChallengeRepository;
import com.freeuni.quiz.repository.impl.QuizChallengeRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizChallengeServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private QuizChallengeRepository mockRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuizService quizService;

    private QuizChallengeService challengeService;

    private QuizChallenge testChallenge;
    private UserDTO challengerDTO;
    private UserDTO challengedDTO;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testChallenge = new QuizChallenge();
        testChallenge.setId(1L);
        testChallenge.setChallengerUserId(100);
        testChallenge.setChallengedUserId(200);
        testChallenge.setQuizId(300L);
        testChallenge.setMessage("Test challenge message");
        testChallenge.setCreatedAt(Timestamp.from(Instant.now()));
        testChallenge.setStatus("PENDING");

        challengerDTO = new UserDTO(100, "challenger", "John", "Doe", "john@test.com", null, null);
        challengedDTO = new UserDTO(200, "challenged", "Jane", "Smith", "jane@test.com", null, null);

        testQuiz = new Quiz();
        testQuiz.setId(300L);
        testQuiz.setTestTitle("Test Quiz");
        testQuiz.setCreatorUserId(100);
        testQuiz.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void constructor_ValidDataSource_ShouldCreateService() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class)) {

            QuizChallengeService service = new QuizChallengeService(dataSource);

            assertNotNull(service);
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    @Test
    void sendChallenge_ValidParameters_ShouldReturnTrue() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.challengeExists(100, 200, 300L)).thenReturn(false);
                         when(mock.createChallenge(any(QuizChallenge.class))).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.sendChallenge(100, 200, 300L, "Test message");

            assertTrue(result);
        }
    }

    @Test
    void sendChallenge_ChallengeAlreadyExists_ShouldReturnFalse() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.challengeExists(100, 200, 300L)).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.sendChallenge(100, 200, 300L, "Test message");

            assertFalse(result);
        }
    }

    @Test
    void getReceivedChallenges_ValidUserId_ShouldReturnChallenges() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Collections.singletonList(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(challengerDTO);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.of(testQuiz));

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(1, result.size());
        }
    }

    @Test
    void getReceivedChallenges_EmptyList_ShouldReturnEmptyList() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Collections.emptyList());
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(0, result.size());
        }
    }

    @Test
    void getSentChallenges_ValidUserId_ShouldReturnChallenges() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesSentByUser(100)).thenReturn(Collections.singletonList(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(challengerDTO);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.of(testQuiz));

            List<QuizChallengeDTO> result = challengeService.getSentChallenges(100, userService, quizService);

            assertEquals(1, result.size());
        }
    }

    @Test
    void getChallengeById_ExistingChallenge_ShouldReturnChallenge() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengeById(1L)).thenReturn(Optional.of(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(challengerDTO);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.of(testQuiz));

            Optional<QuizChallengeDTO> result = challengeService.getChallengeById(1L, userService, quizService);

            assertTrue(result.isPresent());
        }
    }

    @Test
    void getChallengeById_NonExistingChallenge_ShouldReturnEmpty() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengeById(999L)).thenReturn(Optional.empty());
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            Optional<QuizChallengeDTO> result = challengeService.getChallengeById(999L, userService, quizService);

            assertFalse(result.isPresent());
        }
    }

    @Test
    void acceptChallenge_ValidId_ShouldReturnTrue() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.updateChallengeStatus(1L, "ACCEPTED")).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.acceptChallenge(1L);

            assertTrue(result);
        }
    }

    @Test
    void acceptChallenge_InvalidId_ShouldReturnFalse() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.updateChallengeStatus(999L, "ACCEPTED")).thenReturn(false);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.acceptChallenge(999L);

            assertFalse(result);
        }
    }

    @Test
    void declineChallenge_ValidId_ShouldReturnTrue() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.updateChallengeStatus(1L, "DECLINED")).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.declineChallenge(1L);

            assertTrue(result);
        }
    }

    @Test
    void completeChallenge_ValidId_ShouldReturnTrue() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.updateChallengeStatus(1L, "COMPLETED")).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.completeChallenge(1L);

            assertTrue(result);
        }
    }

    @Test
    void deleteChallenge_ValidId_ShouldReturnTrue() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.deleteChallenge(1L)).thenReturn(true);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.deleteChallenge(1L);

            assertTrue(result);
        }
    }

    @Test
    void deleteChallenge_InvalidId_ShouldReturnFalse() {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.deleteChallenge(999L)).thenReturn(false);
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            boolean result = challengeService.deleteChallenge(999L);

            assertFalse(result);
        }
    }

    @Test
    void convertToDTO_UserNotFound_ShouldReturnEmptyList() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Collections.singletonList(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(null);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.of(testQuiz));

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(0, result.size());
        }
    }

    @Test
    void convertToDTO_QuizNotFound_ShouldReturnEmptyList() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Collections.singletonList(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(challengerDTO);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.empty());

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(0, result.size());
        }
    }

    @Test
    void convertToDTO_SQLException_ShouldReturnEmptyList() throws SQLException {
        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Collections.singletonList(testChallenge));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenThrow(new SQLException("Database error"));

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(0, result.size());
        }
    }

    @Test
    void getReceivedChallenges_MultipleChallenges_ShouldReturnAll() throws SQLException {
        QuizChallenge challenge2 = new QuizChallenge();
        challenge2.setId(2L);
        challenge2.setChallengerUserId(101);
        challenge2.setChallengedUserId(200);
        challenge2.setQuizId(301L);

        UserDTO challenger2 = new UserDTO(101, "challenger2", "Bob", "Wilson", "bob@test.com", null, null);
        Quiz quiz2 = new Quiz();
        quiz2.setId(301L);

        try (MockedConstruction<QuizChallengeRepositoryImpl> mockedConstruction =
                     mockConstruction(QuizChallengeRepositoryImpl.class, (mock, context) -> {
                         when(mock.getChallengesReceivedByUser(200)).thenReturn(Arrays.asList(testChallenge, challenge2));
                     })) {

            challengeService = new QuizChallengeService(dataSource);

            when(userService.findById(100)).thenReturn(challengerDTO);
            when(userService.findById(101)).thenReturn(challenger2);
            when(userService.findById(200)).thenReturn(challengedDTO);
            when(quizService.getQuizById(300L)).thenReturn(Optional.of(testQuiz));
            when(quizService.getQuizById(301L)).thenReturn(Optional.of(quiz2));

            List<QuizChallengeDTO> result = challengeService.getReceivedChallenges(200, userService, quizService);

            assertEquals(2, result.size());
        }
    }
}