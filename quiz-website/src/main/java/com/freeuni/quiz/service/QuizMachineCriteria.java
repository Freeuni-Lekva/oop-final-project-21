package com.freeuni.quiz.service;

import com.freeuni.quiz.repository.QuizCompletionRepository;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QuizMachineCriteria implements AchievementCriteria {
    private final QuizCompletionRepository quizCompletionRepository;

    public QuizMachineCriteria(DataSource dataSource) {
        this.quizCompletionRepository = new QuizCompletionRepositoryImpl(dataSource);
    }

    @Override
    public boolean isSatisfied(int userId) throws SQLException {
        int takenCount = quizCompletionRepository.getCompletionCountByUser(userId);
        return takenCount >= 10;
    }

    @Override
    public String getAchievementName() {
        return "QUIZ_MACHINE";
    }
}