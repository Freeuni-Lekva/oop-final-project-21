package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.repository.QuestionRepository;
import com.freeuni.quiz.quiz_util.AbstractQuestionHandler;
import com.freeuni.quiz.quiz_util.TextQuestionHandler;
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
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    private QuestionService questionService;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(questionRepository);

        AbstractQuestionHandler testHandler = new TextQuestionHandler("What is 2+2?", Arrays.asList("4", "four"));
        
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setAuthorUserId(100);
        testQuestion.setCategoryId(10L);
        testQuestion.setQuestionTitle("Math Question");
        testQuestion.setQuestionType(QuestionType.TEXT);
        testQuestion.setQuestionHandler(testHandler);
    }

    @Test
    void createQuestion_ValidQuestion_ShouldReturnQuestionId() {
        Long expectedId = 1L;
        when(questionRepository.saveQuestion(any(Question.class))).thenReturn(expectedId);

        Long result = questionService.createQuestion(testQuestion);

        assertEquals(expectedId, result);
        assertNotNull(testQuestion.getCreatedAt());
        verify(questionRepository).saveQuestion(testQuestion);
    }

    @Test
    void createQuestion_NullTitle_ShouldThrowException() {
        testQuestion.setQuestionTitle(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> questionService.createQuestion(testQuestion)
        );
        assertEquals("Question title is required", exception.getMessage());
        verify(questionRepository, never()).saveQuestion(any());
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
        Long questionId = 1L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));

        Optional<Question> result = questionService.getQuestionById(questionId);

        assertTrue(result.isPresent());
        assertEquals(testQuestion, result.get());
        verify(questionRepository).findById(questionId);
    }

    @Test
    void getQuestionById_NonExistingQuestion_ShouldReturnEmpty() {
        Long questionId = 999L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.getQuestionById(questionId);

        assertFalse(result.isPresent());
        verify(questionRepository).findById(questionId);
    }

    @Test
    void getQuestionsByAuthor_ValidParameters_ShouldReturnQuestions() {
        Long authorId = 100L;
        int page = 0;
        int size = 10;
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        when(questionRepository.findByAuthor(authorId, 0, size)).thenReturn(expectedQuestions);

        List<Question> result = questionService.getQuestionsByAuthor(authorId, page, size);

        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByAuthor(authorId, 0, size);
    }

    @Test
    void getQuestionsByAuthor_DifferentPageAndSize_ShouldCalculateOffsetCorrectly() {
        Long authorId = 100L;
        int page = 2;
        int size = 5;
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        when(questionRepository.findByAuthor(authorId, 10, size)).thenReturn(expectedQuestions);

        List<Question> result = questionService.getQuestionsByAuthor(authorId, page, size);

        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByAuthor(authorId, 10, size);
    }

    @Test
    void getQuestionsByCategory_ValidParameters_ShouldReturnQuestions() {
        Long categoryId = 10L;
        int page = 1;
        int size = 8;
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        when(questionRepository.findByCategory(categoryId, 8, size)).thenReturn(expectedQuestions);

        List<Question> result = questionService.getQuestionsByCategory(categoryId, page, size);

        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByCategory(categoryId, 8, size);
    }

    @Test
    void getQuestionsByType_ValidParameters_ShouldReturnQuestions() {
        QuestionType type = QuestionType.TEXT;
        int page = 0;
        int size = 15;
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        when(questionRepository.findByType(type, 0, size)).thenReturn(expectedQuestions);

        List<Question> result = questionService.getQuestionsByType(type, page, size);

        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByType(type, 0, size);
    }

    @Test
    void searchQuestionsByTitle_ValidParameters_ShouldReturnQuestions() {
        String searchTerm = "math";
        int page = 0;
        int size = 10;
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        when(questionRepository.searchByTitle(searchTerm, 0, size)).thenReturn(expectedQuestions);

        List<Question> result = questionService.searchQuestionsByTitle(searchTerm, page, size);

        assertEquals(expectedQuestions, result);
        verify(questionRepository).searchByTitle(searchTerm, 0, size);
    }

    @Test
    void updateQuestion_ValidQuestion_ShouldReturnTrue() {
        when(questionRepository.updateQuestion(testQuestion)).thenReturn(true);

        boolean result = questionService.updateQuestion(testQuestion);

        assertTrue(result);
        verify(questionRepository).updateQuestion(testQuestion);
    }

    @Test
    void updateQuestion_InvalidQuestion_ShouldThrowException() {
        testQuestion.setQuestionTitle(null);

        assertThrows(IllegalArgumentException.class, () -> questionService.updateQuestion(testQuestion));
        verify(questionRepository, never()).updateQuestion(any());
    }

    @Test
    void updateQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        when(questionRepository.updateQuestion(testQuestion)).thenReturn(false);

        boolean result = questionService.updateQuestion(testQuestion);

        assertFalse(result);
        verify(questionRepository).updateQuestion(testQuestion);
    }

    @Test
    void deleteQuestion_ValidId_ShouldReturnRepositoryResult() {
        Long questionId = 1L;
        when(questionRepository.deleteQuestion(questionId)).thenReturn(true);

        boolean result = questionService.deleteQuestion(questionId);

        assertTrue(result);
        verify(questionRepository).deleteQuestion(questionId);
    }

    @Test
    void deleteQuestion_RepositoryReturnsFalse_ShouldReturnFalse() {
        Long questionId = 1L;
        when(questionRepository.deleteQuestion(questionId)).thenReturn(false);

        boolean result = questionService.deleteQuestion(questionId);

        assertFalse(result);
        verify(questionRepository).deleteQuestion(questionId);
    }

    @Test
    void isQuestionOwner_UserIsOwner_ShouldReturnTrue() {
        Long questionId = 1L;
        Long userId = 100L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));

        boolean result = questionService.isQuestionOwner(questionId, userId);

        assertTrue(result);
        verify(questionRepository).findById(questionId);
    }

    @Test
    void isQuestionOwner_UserIsNotOwner_ShouldReturnFalse() {
        Long questionId = 1L;
        Long userId = 999L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));

        boolean result = questionService.isQuestionOwner(questionId, userId);

        assertFalse(result);
        verify(questionRepository).findById(questionId);
    }

    @Test
    void isQuestionOwner_QuestionNotFound_ShouldReturnFalse() {
        Long questionId = 999L;
        Long userId = 100L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        boolean result = questionService.isQuestionOwner(questionId, userId);

        assertFalse(result);
        verify(questionRepository).findById(questionId);
    }

    @Test
    void createQuestion_SetsCreatedAtTimestamp() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        when(questionRepository.saveQuestion(any(Question.class))).thenReturn(1L);

        questionService.createQuestion(testQuestion);

        assertNotNull(testQuestion.getCreatedAt());
        assertTrue(testQuestion.getCreatedAt().isAfter(beforeCreation) || 
                  testQuestion.getCreatedAt().isEqual(beforeCreation));
    }

    @Test
    void getQuestionsByType_AllQuestionTypes_ShouldWork() {
        List<Question> expectedQuestions = Collections.singletonList(testQuestion);
        
        when(questionRepository.findByType(QuestionType.TEXT, 0, 10)).thenReturn(expectedQuestions);
        List<Question> textResult = questionService.getQuestionsByType(QuestionType.TEXT, 0, 10);
        assertEquals(expectedQuestions, textResult);
        
        when(questionRepository.findByType(QuestionType.MULTIPLE_CHOICE, 0, 10)).thenReturn(expectedQuestions);
        List<Question> mcResult = questionService.getQuestionsByType(QuestionType.MULTIPLE_CHOICE, 0, 10);
        assertEquals(expectedQuestions, mcResult);
        
        when(questionRepository.findByType(QuestionType.IMAGE, 0, 10)).thenReturn(expectedQuestions);
        List<Question> imageResult = questionService.getQuestionsByType(QuestionType.IMAGE, 0, 10);
        assertEquals(expectedQuestions, imageResult);
    }
} 