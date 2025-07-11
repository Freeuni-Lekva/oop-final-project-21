package com.freeuni.quiz.quiz_util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TextQuestionHandlerTest {

    private TextQuestionHandler questionHandler;
    private String questionStatement;
    private List<String> correctAnswers;

    @BeforeEach
    void setUp() {
        questionStatement = "What is the capital of France?";
        correctAnswers = Arrays.asList("Paris", "paris", "PARIS");
        questionHandler = new TextQuestionHandler(questionStatement, correctAnswers);
    }

    @Test
    void constructor_ValidParameters_ShouldCreateHandler() {
        // Act & Assert
        assertNotNull(questionHandler);
        assertEquals(questionStatement, questionHandler.getQuestionStatement());
        
        Map<String, Object> questionData = questionHandler.getQuestionData();
        assertNotNull(questionData);
        assertTrue(questionData.containsKey("correctAnswers"));
        assertEquals(correctAnswers, questionData.get("correctAnswers"));
    }

    @Test
    void evaluateUserResponse_CorrectAnswer_ShouldReturn1() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Paris");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerDifferentCase_ShouldReturn1() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "paris");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerAllCaps_ShouldReturn1() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "PARIS");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerWithWhitespace_ShouldReturn1() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "  Paris  ");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_IncorrectAnswer_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "London");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_EmptyAnswer_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_WhitespaceOnlyAnswer_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "   ");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_NullAnswer_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", null);

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_MissingTextInputKey_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("wrongKey", "Paris");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_EmptyUserInputs_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getExpectedParameters_ShouldReturnTextInput() {
        // Act
        List<String> expectedParameters = questionHandler.getExpectedParameters();

        // Assert
        assertNotNull(expectedParameters);
        assertEquals(1, expectedParameters.size());
        assertEquals("textInput", expectedParameters.get(0));
    }

    @Test
    void getViewTemplate_ShouldReturnTextQuestion() {
        // Act
        String viewTemplate = questionHandler.getViewTemplate();

        // Assert
        assertEquals("text-question", viewTemplate);
    }

    @Test
    void getViewData_ShouldReturnCorrectData() {
        // Act
        Map<String, Object> viewData = questionHandler.getViewData();

        // Assert
        assertNotNull(viewData);
        assertTrue(viewData.containsKey("questionStatement"));
        assertEquals(questionStatement, viewData.get("questionStatement"));
    }

    @Test
    void evaluateUserResponse_SingleCorrectAnswer_ShouldWork() {
        // Arrange - Create handler with single correct answer
        List<String> singleAnswer = Arrays.asList("42");
        TextQuestionHandler singleAnswerHandler = new TextQuestionHandler("What is 6*7?", singleAnswer);
        
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "42");

        // Act
        double result = singleAnswerHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_MultipleCorrectAnswers_ShouldAcceptAny() {
        // Arrange - Create handler with multiple correct answers
        List<String> multipleAnswers = Arrays.asList("yes", "y", "true", "1");
        TextQuestionHandler multiAnswerHandler = new TextQuestionHandler("Is this true?", multipleAnswers);
        
        // Test each correct answer
        for (String correctAnswer : multipleAnswers) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("textInput", correctAnswer);

            // Act
            double result = multiAnswerHandler.evaluateUserResponse(userInputs);

            // Assert
            assertEquals(1.0, result, 0.001, "Failed for answer: " + correctAnswer);
        }
    }

    @Test
    void evaluateUserResponse_CaseInsensitiveMatching_ShouldWork() {
        // Arrange
        List<String> answers = Arrays.asList("JavaScript", "Python", "Java");
        TextQuestionHandler langHandler = new TextQuestionHandler("Name a programming language", answers);
        
        // Test various case combinations
        String[] testCases = {"javascript", "PYTHON", "jAvA", "JavaScript", "Python", "Java"};
        
        for (String testCase : testCases) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("textInput", testCase);

            // Act
            double result = langHandler.evaluateUserResponse(userInputs);

            // Assert
            assertEquals(1.0, result, 0.001, "Failed for case: " + testCase);
        }
    }

    @Test
    void constructor_NullQuestionStatement_ShouldCreateHandler() {
        // Act
        TextQuestionHandler handler = new TextQuestionHandler(null, correctAnswers);

        // Assert
        assertNotNull(handler);
        assertNull(handler.getQuestionStatement());
    }

    @Test
    void constructor_EmptyCorrectAnswers_ShouldCreateHandler() {
        // Act
        TextQuestionHandler handler = new TextQuestionHandler(questionStatement, new ArrayList<>());

        // Assert
        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
    }

    @Test
    void evaluateUserResponse_EmptyCorrectAnswersList_ShouldReturn0() {
        // Arrange
        TextQuestionHandler emptyAnswersHandler = new TextQuestionHandler(questionStatement, new ArrayList<>());
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "any answer");

        // Act
        double result = emptyAnswersHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }
} 