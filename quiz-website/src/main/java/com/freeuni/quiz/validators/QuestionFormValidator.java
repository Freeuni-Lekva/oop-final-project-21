package com.freeuni.quiz.validators;

import javax.servlet.http.HttpServletRequest;


public class QuestionFormValidator {

    public static String validateBasicQuestionForm(HttpServletRequest request) {
        String questionText = request.getParameter("questionText");
        String questionType = request.getParameter("questionType");
        String points = request.getParameter("points");

        if (questionText == null || questionText.trim().isEmpty()) {
            return "Question text is required";
        }

        if (questionType == null || questionType.trim().isEmpty()) {
            return "Question type is required";
        }

        if (points != null && !points.trim().isEmpty()) {
            try {
                double pointsValue = Double.parseDouble(points);
                if (pointsValue < 0) {
                    return "Points must be non-negative";
                }
            } catch (NumberFormatException e) {
                return "Invalid points format";
            }
        }

        return null;
    }

}