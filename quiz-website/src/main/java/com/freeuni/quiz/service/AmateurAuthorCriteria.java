package com.freeuni.quiz.service;

import com.freeuni.quiz.repository.QuizRepository;
import com.freeuni.quiz.repository.impl.QuizRepositoryImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class AmateurAuthorCriteria implements AchievementCriteria {
    private final QuizRepository quizRepository;

    public AmateurAuthorCriteria(DataSource dataSource) {
        this.quizRepository = new QuizRepositoryImpl(dataSource);
    }

    @Override
    public boolean isSatisfied(int userId) throws SQLException {
        return !quizRepository.findByCreator((long) userId, 0, 1).isEmpty();
    }

    @Override
    public String getAchievementName() {
        return "AMATEUR_AUTHOR";
    }
}
