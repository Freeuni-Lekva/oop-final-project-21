package com.freeuni.quiz.quiz_util;

import java.util.*;

public class TextQuestionHandler extends AbstractQuestionHandler {

    public TextQuestionHandler(String questionStatement, List<String> correctAnswers) {
        super(questionStatement, Map.of("correctAnswers", correctAnswers));
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
        return "text-question";
    }

    @Override
    public Map<String, Object> getViewData() {
        Map<String, Object> viewData = new HashMap<>();
        viewData.put("questionStatement", questionStatement);
        return viewData;
    }
}
