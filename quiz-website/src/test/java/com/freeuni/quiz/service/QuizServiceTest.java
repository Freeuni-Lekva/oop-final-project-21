package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizQuestionMappingRepository quizQuestionMappingRepository;

    private QuizService quizService;

    private Quiz testQuiz;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        quizService = new QuizService(quizRepository, questionRepository, quizQuestionMappingRepository);
        
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setCreatorUserId(100);
        testQuiz.setCategoryId(10L);
        testQuiz.setTestTitle("Test Quiz");
        testQuiz.setTestDescription("Test Description");
        testQuiz.setTimeLimitMinutes(30L);
        testQuiz.setLastQuestionNumber(0L);
        
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setAuthorUserId(100);
        testQuestion.setCategoryId(10L);
        testQuestion.setQuestionTitle("Test Question");
        testQuestion.setQuestionType(QuestionType.TEXT);
    }

    @Test
    void createQuiz_ValidQuiz_ShouldReturnQuizId() {
        // Arrange
        Long expectedId = 1L;
        when(quizRepository.saveQuiz(any(Quiz.class))).thenReturn(expectedId);

        Long result = quizService.createQuiz(testQuiz);

        assertEquals(expectedId, result);
        assertNotNull(testQuiz.getCreatedAt());
        assertEquals(0L, testQuiz.getLastQuestionNumber());
        verify(quizRepository).saveQuiz(testQuiz);
    }

    @Test
    void createQuiz_NullTitle_ShouldThrowException() {
        testQuiz.setTestTitle(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> quizService.createQuiz(testQuiz)
        );
        assertEquals("Quiz title is required", exception.getMessage());
        verify(quizRepository, never()).saveQuiz(any());
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
        Long quizId = 1L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));

        Optional<Quiz> result = quizService.getQuizById(quizId);

        assertTrue(result.isPresent());
        assertEquals(testQuiz, result.get());
        verify(quizRepository).findById(quizId);
    }

    @Test
    void getQuizById_NonExistingQuiz_ShouldReturnEmpty() {
        Long quizId = 999L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        Optional<Quiz> result = quizService.getQuizById(quizId);

        assertFalse(result.isPresent());
        verify(quizRepository).findById(quizId);
    }

    @Test
    void getQuizzesByCreator_ValidParameters_ShouldReturnQuizzes() {
        Long creatorId = 100L;
        int page = 0;
        int size = 10;
        List<Quiz> expectedQuizzes = Collections.singletonList(testQuiz);
        when(quizRepository.findByCreator(creatorId, 0, size)).thenReturn(expectedQuizzes);

        List<Quiz> result = quizService.getQuizzesByCreator(creatorId, page, size);

        assertEquals(expectedQuizzes, result);
        verify(quizRepository).findByCreator(creatorId, 0, size);
    }

    @Test
    void getQuizzesByCategory_ValidParameters_ShouldReturnQuizzes() {
        Long categoryId = 10L;
        int page = 1;
        int size = 5;
        List<Quiz> expectedQuizzes = Collections.singletonList(testQuiz);
        when(quizRepository.findByCategory(categoryId, 5, size)).thenReturn(expectedQuizzes);

        List<Quiz> result = quizService.getQuizzesByCategory(categoryId, page, size);

        assertEquals(expectedQuizzes, result);
        verify(quizRepository).findByCategory(categoryId, 5, size);
    }

    @Test
    void getAllQuizzes_ValidParameters_ShouldReturnQuizzes() {
        int page = 2;
        int size = 15;
        List<Quiz> expectedQuizzes = Collections.singletonList(testQuiz);
        when(quizRepository.findAll(30, size)).thenReturn(expectedQuizzes);

        List<Quiz> result = quizService.getAllQuizzes(page, size);

        assertEquals(expectedQuizzes, result);
        verify(quizRepository).findAll(30, size);
    }

    @Test
    void updateQuiz_ValidQuiz_ShouldReturnTrue() {
        when(quizRepository.updateQuiz(testQuiz)).thenReturn(true);

        boolean result = quizService.updateQuiz(testQuiz);

        assertTrue(result);
        verify(quizRepository).updateQuiz(testQuiz);
    }

    @Test
    void updateQuiz_InvalidQuiz_ShouldThrowException() {
        testQuiz.setTestTitle(null);

        assertThrows(IllegalArgumentException.class, () -> quizService.updateQuiz(testQuiz));
        verify(quizRepository, never()).updateQuiz(any());
    }

    @Test
    void deleteQuiz_ValidId_ShouldReturnRepositoryResult() {
        Long quizId = 1L;
        when(quizRepository.deleteQuiz(quizId)).thenReturn(true);

        boolean result = quizService.deleteQuiz(quizId);

        assertTrue(result);
        verify(quizRepository).deleteQuiz(quizId);
    }

    @Test
    void addQuestionToQuiz_ValidParameters_ShouldReturnTrue() {
        Long quizId = 1L;
        Long questionId = 1L;
        Long questionNumber = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));
        when(quizQuestionMappingRepository.addQuestionToQuiz(quizId, questionId, questionNumber)).thenReturn(true);

        boolean result = quizService.addQuestionToQuiz(quizId, questionId, questionNumber);

        assertTrue(result);
        verify(quizRepository).findById(quizId);
        verify(questionRepository).findById(questionId);
        verify(quizQuestionMappingRepository).addQuestionToQuiz(quizId, questionId, questionNumber);
        verify(quizRepository).updateLastQuestionNumber(quizId, questionNumber);
    }

    @Test
    void addQuestionToQuiz_NonExistingQuiz_ShouldReturnFalse() {
        Long quizId = 999L;
        Long questionId = 1L;
        Long questionNumber = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        boolean result = quizService.addQuestionToQuiz(quizId, questionId, questionNumber);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
        verify(questionRepository, never()).findById(any());
        verify(quizQuestionMappingRepository, never()).addQuestionToQuiz(any(), any(), any());
    }

    @Test
    void addQuestionToQuiz_NonExistingQuestion_ShouldReturnFalse() {
        Long quizId = 1L;
        Long questionId = 999L;
        Long questionNumber = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        boolean result = quizService.addQuestionToQuiz(quizId, questionId, questionNumber);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
        verify(questionRepository).findById(questionId);
        verify(quizQuestionMappingRepository, never()).addQuestionToQuiz(any(), any(), any());
    }

    @Test
    void addQuestionToQuiz_RepositoryFails_ShouldReturnFalse() {
        Long quizId = 1L;
        Long questionId = 1L;
        Long questionNumber = 1L;
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));
        when(quizQuestionMappingRepository.addQuestionToQuiz(quizId, questionId, questionNumber)).thenReturn(false);

        boolean result = quizService.addQuestionToQuiz(quizId, questionId, questionNumber);

        assertFalse(result);
        verify(quizQuestionMappingRepository).addQuestionToQuiz(quizId, questionId, questionNumber);
        verify(quizRepository, never()).updateLastQuestionNumber(any(), any());
    }

    @Test
    void addQuestionToQuiz_QuestionNumberNotGreater_ShouldNotUpdateLastQuestionNumber() {
        Long quizId = 1L;
        Long questionId = 1L;
        Long questionNumber = 0L;
        testQuiz.setLastQuestionNumber(5L);
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));
        when(quizQuestionMappingRepository.addQuestionToQuiz(quizId, questionId, questionNumber)).thenReturn(true);

        boolean result = quizService.addQuestionToQuiz(quizId, questionId, questionNumber);

        assertTrue(result);
        verify(quizRepository, never()).updateLastQuestionNumber(any(), any());
    }

    @Test
    void removeQuestionFromQuiz_ShouldReturnRepositoryResult() {
        Long quizId = 1L;
        Long questionId = 1L;
        when(quizQuestionMappingRepository.removeQuestionFromQuiz(quizId, questionId)).thenReturn(true);

        boolean result = quizService.removeQuestionFromQuiz(quizId, questionId);

        assertTrue(result);
        verify(quizQuestionMappingRepository).removeQuestionFromQuiz(quizId, questionId);
    }

    @Test
    void getQuizQuestions_ValidQuizId_ShouldReturnQuestions() {
        Long quizId = 1L;
        List<Long> questionIds = Arrays.asList(1L, 2L, 3L);
        Question question2 = new Question();
        question2.setId(2L);
        Question question3 = new Question();
        question3.setId(3L);
        
        when(quizQuestionMappingRepository.getQuestionIdsByQuizOrdered(quizId)).thenReturn(questionIds);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.findById(2L)).thenReturn(Optional.of(question2));
        when(questionRepository.findById(3L)).thenReturn(Optional.of(question3));

        List<Question> result = quizService.getQuizQuestions(quizId);

        assertEquals(3, result.size());
        assertEquals(testQuestion, result.get(0));
        assertEquals(question2, result.get(1));
        assertEquals(question3, result.get(2));
        verify(quizQuestionMappingRepository).getQuestionIdsByQuizOrdered(quizId);
    }

    @Test
    void getQuizQuestions_SomeQuestionsNotFound_ShouldReturnOnlyFoundQuestions() {
        Long quizId = 1L;
        List<Long> questionIds = Arrays.asList(1L, 999L, 3L);
        Question question3 = new Question();
        question3.setId(3L);
        
        when(quizQuestionMappingRepository.getQuestionIdsByQuizOrdered(quizId)).thenReturn(questionIds);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());
        when(questionRepository.findById(3L)).thenReturn(Optional.of(question3));

        List<Question> result = quizService.getQuizQuestions(quizId);

        assertEquals(2, result.size());
        assertEquals(testQuestion, result.get(0));
        assertEquals(question3, result.get(1));
    }

    @Test
    void getQuizQuestion_ValidParameters_ShouldReturnQuestion() {
        Long quizId = 1L;
        Long questionNumber = 1L;
        when(quizQuestionMappingRepository.getQuestionIdBySequence(quizId, questionNumber))
            .thenReturn(Optional.of(1L));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        Optional<Question> result = quizService.getQuizQuestion(quizId, questionNumber);

        assertTrue(result.isPresent());
        assertEquals(testQuestion, result.get());
    }

    @Test
    void getQuizQuestion_NoMappingFound_ShouldReturnEmpty() {
        Long quizId = 1L;
        Long questionNumber = 999L;
        when(quizQuestionMappingRepository.getQuestionIdBySequence(quizId, questionNumber))
            .thenReturn(Optional.empty());

        Optional<Question> result = quizService.getQuizQuestion(quizId, questionNumber);

        assertFalse(result.isPresent());
        verify(questionRepository, never()).findById(any());
    }

    @Test
    void getQuizQuestion_QuestionNotFound_ShouldReturnEmpty() {
        Long quizId = 1L;
        Long questionNumber = 1L;
        when(quizQuestionMappingRepository.getQuestionIdBySequence(quizId, questionNumber))
            .thenReturn(Optional.of(999L));
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Question> result = quizService.getQuizQuestion(quizId, questionNumber);

        assertFalse(result.isPresent());
    }

    @Test
    void getQuizQuestionCount_ShouldReturnRepositoryResult() {
        Long quizId = 1L;
        int expectedCount = 5;
        when(quizQuestionMappingRepository.getQuestionCount(quizId)).thenReturn(expectedCount);

        int result = quizService.getQuizQuestionCount(quizId);

        assertEquals(expectedCount, result);
        verify(quizQuestionMappingRepository).getQuestionCount(quizId);
    }

    @Test
    void isQuizOwner_UserIsOwner_ShouldReturnTrue() {
        Long quizId = 1L;
        Long userId = 100L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));

        boolean result = quizService.isQuizOwner(quizId, userId);

        assertTrue(result);
        verify(quizRepository).findById(quizId);
    }

    @Test
    void isQuizOwner_UserIsNotOwner_ShouldReturnFalse() {
        Long quizId = 1L;
        Long userId = 999L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));

        boolean result = quizService.isQuizOwner(quizId, userId);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
    }

    @Test
    void isQuizOwner_QuizNotFound_ShouldReturnFalse() {
        Long quizId = 999L;
        Long userId = 100L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        boolean result = quizService.isQuizOwner(quizId, userId);

        assertFalse(result);
        verify(quizRepository).findById(quizId);
    }
} 