package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.repository.*;
import com.freeuni.quiz.service.QuizSessionService.QuizResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizSessionServiceTest {

    @Mock
    private QuizSessionRepository quizSessionRepository;
    
    @Mock
    private QuizRepository quizRepository;
    
    @Mock
    private ParticipantAnswerRepository participantAnswerRepository;

    private QuizSessionService quizSessionService;
    private Quiz testQuiz;
    private QuizSession testSession;

    @BeforeEach
    void setUp() {
        quizSessionService = new QuizSessionService(
            quizSessionRepository, quizRepository, participantAnswerRepository);
        
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
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(false);
        when(quizSessionRepository.createSession(any(QuizSession.class))).thenReturn(true);

        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertTrue(result);
        verify(quizRepository).findById(quizId);
        verify(quizSessionRepository).hasActiveSession(participantId);
        verify(quizSessionRepository).createSession(any(QuizSession.class));
    }

    @Test
    void startQuizSession_NonExistentQuiz_ShouldReturnFalse() {
        Long participantId = 100L;
        Long quizId = 999L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
        verify(quizSessionRepository, never()).hasActiveSession(any());
        verify(quizSessionRepository, never()).createSession(any());
    }

    @Test
    void startQuizSession_ParticipantHasActiveSession_ShouldReturnFalse() {
        Long participantId = 100L;
        Long quizId = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(true);

        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
        verify(quizSessionRepository).hasActiveSession(participantId);
        verify(quizSessionRepository, never()).createSession(any());
    }

    @Test
    void startQuizSession_CreateSessionFails_ShouldReturnFalse() {
        Long participantId = 100L;
        Long quizId = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(false);
        when(quizSessionRepository.createSession(any(QuizSession.class))).thenReturn(false);

        boolean result = quizSessionService.startQuizSession(participantId, quizId);

        assertFalse(result);
        verify(quizSessionRepository).createSession(any(QuizSession.class));
    }

    @Test
    void getActiveSession_ExistingSession_ShouldReturnSession() {
        Long participantId = 100L;
        when(quizSessionRepository.findByParticipant(participantId)).thenReturn(Optional.of(testSession));

        Optional<QuizSession> result = quizSessionService.getActiveSession(participantId);

        assertTrue(result.isPresent());
        assertEquals(testSession, result.get());
        verify(quizSessionRepository).findByParticipant(participantId);
    }

    @Test
    void getActiveSession_NoSession_ShouldReturnEmpty() {
        Long participantId = 100L;
        when(quizSessionRepository.findByParticipant(participantId)).thenReturn(Optional.empty());

        Optional<QuizSession> result = quizSessionService.getActiveSession(participantId);

        assertFalse(result.isPresent());
        verify(quizSessionRepository).findByParticipant(participantId);
    }

    @Test
    void updateCurrentQuestion_ValidParameters_ShouldReturnTrue() {
        Long participantId = 100L;
        Long questionNumber = 5L;
        when(quizSessionRepository.updateCurrentQuestion(participantId, questionNumber)).thenReturn(true);

        boolean result = quizSessionService.updateCurrentQuestion(participantId, questionNumber);

        assertTrue(result);
        verify(quizSessionRepository).updateCurrentQuestion(participantId, questionNumber);
    }

    @Test
    void updateCurrentQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        Long participantId = 100L;
        Long questionNumber = 5L;
        when(quizSessionRepository.updateCurrentQuestion(participantId, questionNumber)).thenReturn(false);

        boolean result = quizSessionService.updateCurrentQuestion(participantId, questionNumber);

        assertFalse(result);
        verify(quizSessionRepository).updateCurrentQuestion(participantId, questionNumber);
    }

    @Test
    void submitAnswer_ValidAnswer_ShouldReturnTrue() {
        Long participantId = 100L;
        Long testId = 1L;
        long questionNumber = 3L;
        String answerText = "Paris";
        Double pointsEarned = 1.0;
        Integer timeSpent = 30;
        
        when(participantAnswerRepository.saveAnswer(any(ParticipantAnswer.class))).thenReturn(true);
        when(quizSessionRepository.updateCurrentQuestion(participantId, questionNumber + 1)).thenReturn(true);

        boolean result = quizSessionService.submitAnswer(
            participantId, testId, questionNumber, answerText, pointsEarned, timeSpent);

        assertTrue(result);
        verify(participantAnswerRepository).saveAnswer(any(ParticipantAnswer.class));
        verify(quizSessionRepository).updateCurrentQuestion(participantId, 4L);
    }

    @Test
    void submitAnswer_SaveAnswerFails_ShouldReturnFalse() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 3L;
        String answerText = "Paris";
        Double pointsEarned = 1.0;
        Integer timeSpent = 30;
        
        when(participantAnswerRepository.saveAnswer(any(ParticipantAnswer.class))).thenReturn(false);

        boolean result = quizSessionService.submitAnswer(
            participantId, testId, questionNumber, answerText, pointsEarned, timeSpent);

        assertFalse(result);
        verify(participantAnswerRepository).saveAnswer(any(ParticipantAnswer.class));
        verify(quizSessionRepository, never()).updateCurrentQuestion(any(), any());
    }

    @Test
    void submitAnswer_SaveSucceedsButUpdateFails_ShouldReturnFalse() {
        Long participantId = 100L;
        Long testId = 1L;
        long questionNumber = 3L;
        String answerText = "Paris";
        Double pointsEarned = 1.0;
        Integer timeSpent = 30;
        
        when(participantAnswerRepository.saveAnswer(any(ParticipantAnswer.class))).thenReturn(true);
        when(quizSessionRepository.updateCurrentQuestion(participantId, questionNumber + 1)).thenReturn(false);

        boolean result = quizSessionService.submitAnswer(
            participantId, testId, questionNumber, answerText, pointsEarned, timeSpent);

        assertFalse(result);
        verify(participantAnswerRepository).saveAnswer(any(ParticipantAnswer.class));
        verify(quizSessionRepository).updateCurrentQuestion(participantId, 4L);
    }

    @Test
    void getQuizResults_WithAnswers_ShouldReturnCorrectResults() {
        Long participantId = 100L;
        Long testId = 1L;
        
        ParticipantAnswer answer1 = createAnswer(1L, 1.0);
        ParticipantAnswer answer2 = createAnswer(2L, 0.5);
        ParticipantAnswer answer3 = createAnswer(3L, 1.0);
        
        List<ParticipantAnswer> answers = Arrays.asList(answer1, answer2, answer3);
        when(participantAnswerRepository.getAllAnswers(participantId, testId)).thenReturn(answers);

        QuizResults result = quizSessionService.getQuizResults(participantId, testId);

        assertNotNull(result);
        assertEquals(participantId, result.participantId());
        assertEquals(testId, result.testId());
        assertEquals(2.5, result.totalScore(), 0.001);
        assertEquals(3, result.totalQuestions());
        assertEquals(answers, result.answers());
        assertEquals(2.5 / 3, result.getAverageScore(), 0.001);
        verify(participantAnswerRepository).getAllAnswers(participantId, testId);
    }

    @Test
    void getQuizResults_NoAnswers_ShouldReturnEmptyResults() {
        Long participantId = 100L;
        Long testId = 1L;
        
        when(participantAnswerRepository.getAllAnswers(participantId, testId)).thenReturn(new ArrayList<>());

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
        List<Long> expectedQuestions = Arrays.asList(1L, 2L, 3L);
        
        when(participantAnswerRepository.getAnsweredQuestionNumbers(participantId, testId))
            .thenReturn(expectedQuestions);

        List<Long> result = quizSessionService.getAnsweredQuestions(participantId, testId);

        assertEquals(expectedQuestions, result);
        verify(participantAnswerRepository).getAnsweredQuestionNumbers(participantId, testId);
    }

    @Test
    void getQuestionScore_ExistingScore_ShouldReturnScore() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 1L;
        Double expectedScore = 0.75;
        
        when(participantAnswerRepository.getAnswerScore(participantId, testId, questionNumber))
            .thenReturn(Optional.of(expectedScore));

        Optional<Double> result = quizSessionService.getQuestionScore(participantId, testId, questionNumber);

        assertTrue(result.isPresent());
        assertEquals(expectedScore, result.get());
        verify(participantAnswerRepository).getAnswerScore(participantId, testId, questionNumber);
    }

    @Test
    void getQuestionScore_NoScore_ShouldReturnEmpty() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 1L;
        
        when(participantAnswerRepository.getAnswerScore(participantId, testId, questionNumber))
            .thenReturn(Optional.empty());

        Optional<Double> result = quizSessionService.getQuestionScore(participantId, testId, questionNumber);

        assertFalse(result.isPresent());
        verify(participantAnswerRepository).getAnswerScore(participantId, testId, questionNumber);
    }

    @Test
    void endQuizSession_ShouldReturnRepositoryResult() {
        Long participantId = 100L;
        when(quizSessionRepository.deleteSession(participantId)).thenReturn(true);

        boolean result = quizSessionService.endQuizSession(participantId);

        assertTrue(result);
        verify(quizSessionRepository).deleteSession(participantId);
    }

    @Test
    void endQuizSession_RepositoryReturnsFalse_ShouldReturnFalse() {
        Long participantId = 100L;
        when(quizSessionRepository.deleteSession(participantId)).thenReturn(false);

        boolean result = quizSessionService.endQuizSession(participantId);

        assertFalse(result);
        verify(quizSessionRepository).deleteSession(participantId);
    }

    @Test
    void hasActiveSession_ExistingSession_ShouldReturnTrue() {
        Long participantId = 100L;
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(true);

        boolean result = quizSessionService.hasActiveSession(participantId);

        assertTrue(result);
        verify(quizSessionRepository).hasActiveSession(participantId);
    }

    @Test
    void hasActiveSession_NoSession_ShouldReturnFalse() {
        Long participantId = 100L;
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(false);

        boolean result = quizSessionService.hasActiveSession(participantId);

        assertFalse(result);
        verify(quizSessionRepository).hasActiveSession(participantId);
    }

    @Test
    void startQuizSession_SetsCorrectSessionProperties() {
        Long participantId = 100L;
        Long quizId = 1L;
        LocalDateTime beforeStart = LocalDateTime.now();
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(quizSessionRepository.hasActiveSession(participantId)).thenReturn(false);
        when(quizSessionRepository.createSession(any(QuizSession.class))).thenReturn(true);

        quizSessionService.startQuizSession(participantId, quizId);

        verify(quizSessionRepository).createSession(argThat(session -> session.getParticipantUserId().equals(participantId) &&
               session.getTestId().equals(quizId) &&
               session.getTimeAllocated().equals(testQuiz.getTimeLimitMinutes()) &&
               session.getCurrentQuestionNum().equals(0L) &&
               session.getSessionStart().isAfter(beforeStart.minusSeconds(1))));
    }

    @Test
    void submitAnswer_SetsCorrectAnswerProperties() {
        Long participantId = 100L;
        Long testId = 1L;
        Long questionNumber = 3L;
        String answerText = "Test Answer";
        Double pointsEarned = 0.8;
        Integer timeSpent = 45;
        
        when(participantAnswerRepository.saveAnswer(any(ParticipantAnswer.class))).thenReturn(true);
        when(quizSessionRepository.updateCurrentQuestion(participantId, questionNumber + 1)).thenReturn(true);

        quizSessionService.submitAnswer(participantId, testId, questionNumber, answerText, pointsEarned, timeSpent);

        verify(participantAnswerRepository).saveAnswer(argThat(answer -> answer.getParticipantUserId().equals(participantId) &&
               answer.getTestId().equals(testId) &&
               answer.getQuestionNumber().equals(questionNumber) &&
               answer.getAnswerText().equals(answerText) &&
               answer.getPointsEarned().equals(pointsEarned) &&
               answer.getTimeSpentSeconds().equals(timeSpent)));
    }

    @Test
    void quizResults_Constructor_ShouldSetAllProperties() {
        Long participantId = 200L;
        Long testId = 5L;
        double totalScore = 7.5;
        int totalQuestions = 10;
        List<ParticipantAnswer> answers = Arrays.asList(createAnswer(1L, 1.0), createAnswer(2L, 0.5));

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