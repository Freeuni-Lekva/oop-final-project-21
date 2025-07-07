package com.freeuni.quiz.quiz_util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleChoiceQuestionHandler extends AbstractQuestionHandler {

    public MultipleChoiceQuestionHandler(String questionStatement, List<String> choiceOptions, List<String> correctChoices) {
        super(questionStatement, Map.of(
                "choiceOptions", choiceOptions,
                "correctChoices", correctChoices
        ));
    }

    @Override
    public double evaluateUserResponse(Map<String, String> userInputs) {
        String selectedChoice = userInputs.get("choiceSelection");
        if (selectedChoice == null) {
            return 0.0;
        }

        @SuppressWarnings("unchecked")
        List<String> correctChoices = (List<String>) questionData.get("correctChoices");

        return correctChoices.contains(selectedChoice) ? 1.0 : 0.0;
    }

    @Override
    public List<String> getExpectedParameters() {
        return List.of("choiceSelection");
    }

    @Override
    public String getViewTemplate() {
        return "multiple-choice-question";
    }

    @Override
    public Map<String, Object> getViewData() {
        Map<String, Object> viewData = new HashMap<>();
        viewData.put("questionStatement", questionStatement);
        viewData.put("choiceOptions", questionData.get("choiceOptions"));
        return viewData;
    }
}
