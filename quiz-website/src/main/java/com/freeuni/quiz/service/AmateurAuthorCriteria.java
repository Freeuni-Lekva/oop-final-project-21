package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class AmateurAuthorCriteria implements AchievementCriteria {
    private final QuizDAO quizRepository;

    public AmateurAuthorCriteria(DataSource dataSource) {
        this.quizRepository = new QuizDAOImpl(dataSource);
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
