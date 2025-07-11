package com.freeuni.quiz.quiz_util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class AbstractQuestionHandler implements Serializable {

    protected String questionStatement;
    protected Map<String, Object> questionData;

    public AbstractQuestionHandler(String questionStatement, Map<String, Object> questionData) {
        this.questionStatement = questionStatement;
        this.questionData = questionData;
    }

    public abstract double evaluateUserResponse(Map<String, String> userInputs);
    public abstract List<String> getExpectedParameters();
    public abstract String getViewTemplate();
    public abstract Map<String, Object> getViewData();

    public String getQuestionStatement() {
        return questionStatement;
    }
    public Map<String, Object> getQuestionData() {
        return questionData;
    }
}
