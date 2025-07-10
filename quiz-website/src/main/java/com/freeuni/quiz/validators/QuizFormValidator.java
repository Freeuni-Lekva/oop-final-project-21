package com.freeuni.quiz.validators;

import javax.servlet.http.HttpServletRequest;


public class QuizFormValidator {

    public static String validateQuizForm(HttpServletRequest request) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String categoryId = request.getParameter("categoryId");
        String timeLimit = request.getParameter("timeLimit");

        if (title == null || title.trim().isEmpty()) {
            return "Quiz title is required";
        }

        if (description == null || description.trim().isEmpty()) {
            return "Quiz description is required";
        }

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            try {
                Long.parseLong(categoryId);
            } catch (NumberFormatException e) {
                return "Invalid category selected";
            }
        }

        if (timeLimit != null && !timeLimit.trim().isEmpty()) {
            try {
                long timeLimitValue = Long.parseLong(timeLimit);
                if (timeLimitValue <= 0) {
                    return "Time limit must be positive";
                }
            } catch (NumberFormatException e) {
                return "Invalid time limit format";
            }
        }

        return null;
    }
} 