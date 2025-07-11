package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ProlificAuthorCriteria implements AchievementCriteria {
    private final QuizDAO quizRepository;

    public ProlificAuthorCriteria(DataSource dataSource) {
        this.quizRepository = new QuizDAOImpl(dataSource);
    }

    @Override
    public boolean isSatisfied(int userId) throws SQLException {
        return quizRepository.findByCreator((long) userId, 0, 5).size() >= 5;
    }

    @Override
    public String getAchievementName() {
        return "PROLIFIC_AUTHOR";
    }
}

