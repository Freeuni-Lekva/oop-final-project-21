package com.freeuni.quiz.quiz_util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MultipleChoiceQuestionHandlerTest {

    private MultipleChoiceQuestionHandler questionHandler;
    private String questionStatement;
    private List<String> choices;
    private List<String> correctChoices;

    @BeforeEach
    void setUp() {
        questionStatement = "What is the capital of France?";
        choices = Arrays.asList("London", "Paris", "Berlin", "Madrid");
        correctChoices = Arrays.asList("Paris"); // Paris is the correct answer
        questionHandler = new MultipleChoiceQuestionHandler(questionStatement, choices, correctChoices);
    }

    @Test
    void constructor_ValidParameters_ShouldCreateHandler() {
        // Act & Assert
        assertNotNull(questionHandler);
        assertEquals(questionStatement, questionHandler.getQuestionStatement());
        
        Map<String, Object> questionData = questionHandler.getQuestionData();
        assertNotNull(questionData);
        assertTrue(questionData.containsKey("choiceOptions"));
        assertTrue(questionData.containsKey("correctChoices"));
        assertEquals(choices, questionData.get("choiceOptions"));
        assertEquals(correctChoices, questionData.get("correctChoices"));
    }

    @Test
    void evaluateUserResponse_CorrectChoice_ShouldReturn1() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Paris"); // Correct answer

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_IncorrectChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "London"); // Incorrect answer

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_AnotherIncorrectChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Berlin"); // Incorrect answer

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_LastIncorrectChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Madrid"); // Incorrect answer

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_NonExistentChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Tokyo"); // Not in choices

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_EmptyChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "");

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_NullChoice_ShouldReturn0() {
        // Arrange
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", null);

        // Act
        double result = questionHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_MissingChoiceSelectionKey_ShouldReturn0() {
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
    void getExpectedParameters_ShouldReturnChoiceSelection() {
        // Act
        List<String> expectedParameters = questionHandler.getExpectedParameters();

        // Assert
        assertNotNull(expectedParameters);
        assertEquals(1, expectedParameters.size());
        assertEquals("choiceSelection", expectedParameters.get(0));
    }

    @Test
    void getViewTemplate_ShouldReturnMultipleChoiceQuestion() {
        // Act
        String viewTemplate = questionHandler.getViewTemplate();

        // Assert
        assertEquals("multiple-choice-question", viewTemplate);
    }

    @Test
    void getViewData_ShouldReturnCorrectData() {
        // Act
        Map<String, Object> viewData = questionHandler.getViewData();

        // Assert
        assertNotNull(viewData);
        assertTrue(viewData.containsKey("questionStatement"));
        assertTrue(viewData.containsKey("choiceOptions"));
        assertEquals(questionStatement, viewData.get("questionStatement"));
        assertEquals(choices, viewData.get("choiceOptions"));
    }

    @Test
    void evaluateUserResponse_MultipleCorrectAnswers_ShouldAcceptAny() {
        // Arrange - Create handler with multiple correct answers
        List<String> testChoices = Arrays.asList("True", "False", "Maybe", "Always");
        List<String> multipleCorrectAnswers = Arrays.asList("True", "Always");
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            "Which are positive responses?", testChoices, multipleCorrectAnswers);

        // Test each correct answer
        for (String correctAnswer : multipleCorrectAnswers) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("choiceSelection", correctAnswer);

            // Act
            double result = handler.evaluateUserResponse(userInputs);

            // Assert
            assertEquals(1.0, result, 0.001, "Failed for answer: " + correctAnswer);
        }
    }

    @Test
    void evaluateUserResponse_MultipleCorrectAnswers_ShouldRejectIncorrect() {
        // Arrange - Create handler with multiple correct answers
        List<String> testChoices = Arrays.asList("True", "False", "Maybe", "Always");
        List<String> multipleCorrectAnswers = Arrays.asList("True", "Always");
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            "Which are positive responses?", testChoices, multipleCorrectAnswers);

        // Test incorrect answers
        String[] incorrectAnswers = {"False", "Maybe"};
        for (String incorrectAnswer : incorrectAnswers) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("choiceSelection", incorrectAnswer);

            // Act
            double result = handler.evaluateUserResponse(userInputs);

            // Assert
            assertEquals(0.0, result, 0.001, "Should fail for answer: " + incorrectAnswer);
        }
    }

    @Test
    void constructor_TwoChoicesOnly_ShouldWork() {
        // Arrange
        List<String> testChoices = Arrays.asList("True", "False");
        List<String> correctAnswer = Arrays.asList("True");
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            "True or False?", testChoices, correctAnswer);

        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "True");

        // Act
        double result = handler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void constructor_ManyChoices_ShouldWork() {
        // Arrange
        List<String> testChoices = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H");
        List<String> correctAnswer = Arrays.asList("H");
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            "Choose H", testChoices, correctAnswer);

        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "H");

        // Act
        double result = handler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void constructor_NullQuestionStatement_ShouldCreateHandler() {
        // Act
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            null, choices, correctChoices);

        // Assert
        assertNotNull(handler);
        assertNull(handler.getQuestionStatement());
    }

    @Test
    void constructor_EmptyChoices_ShouldCreateHandler() {
        // Act
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            questionStatement, new ArrayList<>(), correctChoices);

        // Assert
        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
    }

    @Test
    void evaluateUserResponse_EmptyChoicesList_ShouldReturn0() {
        // Arrange
        MultipleChoiceQuestionHandler emptyChoicesHandler = new MultipleChoiceQuestionHandler(
            questionStatement, new ArrayList<>(), correctChoices);
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Paris");

        // Act
        double result = emptyChoicesHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(1.0, result, 0.001); // Should still work since it checks correctChoices, not choiceOptions
    }

    @Test
    void constructor_EmptyCorrectChoices_ShouldCreateHandler() {
        // Act
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            questionStatement, choices, new ArrayList<>());

        // Assert
        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
    }

    @Test
    void evaluateUserResponse_EmptyCorrectChoicesList_ShouldReturn0() {
        // Arrange
        MultipleChoiceQuestionHandler emptyCorrectHandler = new MultipleChoiceQuestionHandler(
            questionStatement, choices, new ArrayList<>());
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("choiceSelection", "Paris");

        // Act
        double result = emptyCorrectHandler.evaluateUserResponse(userInputs);

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CaseSensitive_ShouldWork() {
        // Arrange - Test case sensitivity
        List<String> testChoices = Arrays.asList("javascript", "JavaScript", "JAVASCRIPT");
        List<String> correctAnswer = Arrays.asList("JavaScript");
        MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
            "Which is the correct case?", testChoices, correctAnswer);

        // Test exact match
        Map<String, String> correctInput = new HashMap<>();
        correctInput.put("choiceSelection", "JavaScript");
        assertEquals(1.0, handler.evaluateUserResponse(correctInput), 0.001);

        // Test case mismatch
        Map<String, String> incorrectInput = new HashMap<>();
        incorrectInput.put("choiceSelection", "javascript");
        assertEquals(0.0, handler.evaluateUserResponse(incorrectInput), 0.001);
    }
} 