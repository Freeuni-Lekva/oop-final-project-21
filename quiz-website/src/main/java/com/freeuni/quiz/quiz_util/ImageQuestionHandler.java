package com.freeuni.quiz.quiz_util;

import java.util.*;

public class ImageQuestionHandler extends AbstractQuestionHandler {

    public ImageQuestionHandler(String questionStatement, String imageUrl, List<String> correctAnswers) {
        super(questionStatement, createQuestionData(imageUrl, correctAnswers));
    }

    private static Map<String, Object> createQuestionData(String imageUrl, List<String> correctAnswers) {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("correctAnswers", correctAnswers);
        return data;
    }

    @Override
    public double evaluateUserResponse(Map<String, String> userInputs) {
        String userResponse = userInputs.get("textInput");
        if (userResponse == null || userResponse.trim().isEmpty()) {
            return 0.0;
        }

        @SuppressWarnings("unchecked")
        List<String> correctAnswers = (List<String>) questionData.get("correctAnswers");

        return correctAnswers.stream()
                .anyMatch(answer -> answer.equalsIgnoreCase(userResponse.trim())) ? 1.0 : 0.0;
    }

    @Override
    public List<String> getExpectedParameters() {
        return List.of("textInput");
    }

    @Override
    public String getViewTemplate() {
        return "image-question";
    }

    @Override
    public Map<String, Object> getViewData() {
        Map<String, Object> viewData = new HashMap<>();
        viewData.put("questionStatement", questionStatement);
        viewData.put("imageUrl", questionData.get("imageUrl"));
        return viewData;
    }
} 