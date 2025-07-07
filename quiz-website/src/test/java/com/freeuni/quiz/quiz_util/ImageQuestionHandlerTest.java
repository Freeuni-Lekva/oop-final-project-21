package com.freeuni.quiz.quiz_util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ImageQuestionHandlerTest {

    private ImageQuestionHandler questionHandler;
    private String questionStatement;
    private String imageUrl;
    private List<String> correctAnswers;

    @BeforeEach
    void setUp() {
        questionStatement = "What landmark is shown in this image?";
        imageUrl = "https://example.com/images/eiffel-tower.jpg";
        correctAnswers = Arrays.asList("Eiffel Tower", "eiffel tower", "EIFFEL TOWER");
        questionHandler = new ImageQuestionHandler(questionStatement, imageUrl, correctAnswers);
    }

    @Test
    void constructor_ValidParameters_ShouldCreateHandler() {
        assertNotNull(questionHandler);
        assertEquals(questionStatement, questionHandler.getQuestionStatement());
        
        Map<String, Object> questionData = questionHandler.getQuestionData();
        assertNotNull(questionData);
        assertTrue(questionData.containsKey("imageUrl"));
        assertTrue(questionData.containsKey("correctAnswers"));
        assertEquals(imageUrl, questionData.get("imageUrl"));
        assertEquals(correctAnswers, questionData.get("correctAnswers"));
    }

    @Test
    void evaluateUserResponse_CorrectAnswer_ShouldReturn1() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Eiffel Tower");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerDifferentCase_ShouldReturn1() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "eiffel tower");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerAllCaps_ShouldReturn1() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "EIFFEL TOWER");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_CorrectAnswerWithWhitespace_ShouldReturn1() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "  Eiffel Tower  ");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_IncorrectAnswer_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Big Ben");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_EmptyAnswer_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_WhitespaceOnlyAnswer_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "   ");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_NullAnswer_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", null);

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_MissingTextInputKey_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("wrongKey", "Eiffel Tower");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_EmptyUserInputs_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getExpectedParameters_ShouldReturnTextInput() {
        List<String> expectedParameters = questionHandler.getExpectedParameters();

        assertNotNull(expectedParameters);
        assertEquals(1, expectedParameters.size());
        assertEquals("textInput", expectedParameters.getFirst());
    }

    @Test
    void getViewTemplate_ShouldReturnImageQuestion() {
        String viewTemplate = questionHandler.getViewTemplate();

        assertEquals("image-question", viewTemplate);
    }

    @Test
    void getViewData_ShouldReturnCorrectData() {
        Map<String, Object> viewData = questionHandler.getViewData();

        assertNotNull(viewData);
        assertTrue(viewData.containsKey("questionStatement"));
        assertTrue(viewData.containsKey("imageUrl"));
        assertEquals(questionStatement, viewData.get("questionStatement"));
        assertEquals(imageUrl, viewData.get("imageUrl"));
    }

    @Test
    void evaluateUserResponse_SingleCorrectAnswer_ShouldWork() {
        List<String> singleAnswer = List.of("Statue of Liberty");
        ImageQuestionHandler singleAnswerHandler = new ImageQuestionHandler(
            "What statue is this?", "https://example.com/statue.jpg", singleAnswer);
        
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Statue of Liberty");

        double result = singleAnswerHandler.evaluateUserResponse(userInputs);

        assertEquals(1.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_MultipleCorrectAnswers_ShouldAcceptAny() {
        List<String> multipleAnswers = Arrays.asList("Mona Lisa", "La Gioconda", "La Joconde");
        ImageQuestionHandler multiAnswerHandler = new ImageQuestionHandler(
            "What painting is this?", "https://example.com/mona-lisa.jpg", multipleAnswers);
        
        for (String correctAnswer : multipleAnswers) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("textInput", correctAnswer);

            double result = multiAnswerHandler.evaluateUserResponse(userInputs);

            assertEquals(1.0, result, 0.001, "Failed for answer: " + correctAnswer);
        }
    }

    @Test
    void evaluateUserResponse_CaseInsensitiveMatching_ShouldWork() {
        List<String> answers = Arrays.asList("Taj Mahal", "Golden Gate Bridge", "Colosseum");
        ImageQuestionHandler landmarkHandler = new ImageQuestionHandler(
            "Name this landmark", "https://example.com/landmark.jpg", answers);
        
        String[] testCases = {"taj mahal", "GOLDEN GATE BRIDGE", "coLOsseum", "Taj Mahal", "Golden Gate Bridge", "Colosseum"};
        
        for (String testCase : testCases) {
            Map<String, String> userInputs = new HashMap<>();
            userInputs.put("textInput", testCase);

            double result = landmarkHandler.evaluateUserResponse(userInputs);

            assertEquals(1.0, result, 0.001, "Failed for case: " + testCase);
        }
    }

    @Test
    void constructor_NullQuestionStatement_ShouldCreateHandler() {
        ImageQuestionHandler handler = new ImageQuestionHandler(null, imageUrl, correctAnswers);

        assertNotNull(handler);
        assertNull(handler.getQuestionStatement());
    }

    @Test
    void constructor_NullImageUrl_ShouldCreateHandler() {
        ImageQuestionHandler handler = new ImageQuestionHandler(questionStatement, null, correctAnswers);

        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
        
        Map<String, Object> questionData = handler.getQuestionData();
        assertNull(questionData.get("imageUrl"));
    }

    @Test
    void constructor_EmptyCorrectAnswers_ShouldCreateHandler() {
        ImageQuestionHandler handler = new ImageQuestionHandler(questionStatement, imageUrl, new ArrayList<>());

        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
    }

    @Test
    void evaluateUserResponse_EmptyCorrectAnswersList_ShouldReturn0() {
        ImageQuestionHandler emptyAnswersHandler = new ImageQuestionHandler(
            questionStatement, imageUrl, new ArrayList<>());
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "any answer");

        double result = emptyAnswersHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getViewData_WithNullImageUrl_ShouldHandleGracefully() {
        ImageQuestionHandler handlerWithNullUrl = new ImageQuestionHandler(
            questionStatement, null, correctAnswers);

        Map<String, Object> viewData = handlerWithNullUrl.getViewData();

        assertNotNull(viewData);
        assertTrue(viewData.containsKey("questionStatement"));
        assertTrue(viewData.containsKey("imageUrl"));
        assertEquals(questionStatement, viewData.get("questionStatement"));
        assertNull(viewData.get("imageUrl"));
    }

    @Test
    void constructor_EmptyImageUrl_ShouldCreateHandler() {
        ImageQuestionHandler handler = new ImageQuestionHandler(questionStatement, "", correctAnswers);

        assertNotNull(handler);
        assertEquals(questionStatement, handler.getQuestionStatement());
        
        Map<String, Object> questionData = handler.getQuestionData();
        assertEquals("", questionData.get("imageUrl"));
    }

    @Test
    void evaluateUserResponse_PartialMatch_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Eiffel");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void evaluateUserResponse_ExtraText_ShouldReturn0() {
        Map<String, String> userInputs = new HashMap<>();
        userInputs.put("textInput", "Eiffel Tower in Paris");

        double result = questionHandler.evaluateUserResponse(userInputs);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void getQuestionData_ShouldContainAllExpectedFields() {
        Map<String, Object> questionData = questionHandler.getQuestionData();

        assertNotNull(questionData);
        assertEquals(2, questionData.size());
        assertTrue(questionData.containsKey("imageUrl"));
        assertTrue(questionData.containsKey("correctAnswers"));
        assertEquals(imageUrl, questionData.get("imageUrl"));
        assertEquals(correctAnswers, questionData.get("correctAnswers"));
    }

    @Test
    void constructor_ComplexImageUrl_ShouldWork() {
        String complexUrl = "https://cdn.example.com/images/landmarks/europe/france/paris/eiffel-tower-night.jpg?size=large&quality=high";
        
        ImageQuestionHandler handler = new ImageQuestionHandler(questionStatement, complexUrl, correctAnswers);

        assertNotNull(handler);
        Map<String, Object> questionData = handler.getQuestionData();
        assertEquals(complexUrl, questionData.get("imageUrl"));
    }
} 