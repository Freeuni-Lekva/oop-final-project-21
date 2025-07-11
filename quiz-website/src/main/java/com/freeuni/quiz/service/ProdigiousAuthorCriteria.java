package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ProdigiousAuthorCriteria implements AchievementCriteria {
    private final QuizDAO quizRepository;

    public ProdigiousAuthorCriteria(DataSource dataSource) {
        this.quizRepository = new QuizDAOImpl(dataSource);
    }

    @Override
    public boolean isSatisfied(int userId) throws SQLException {
        return quizRepository.findByCreator((long) userId, 0, 10).size() >= 10;
    }

    @Override
    public String getAchievementName() {
        return "PRODIGIOUS_AUTHOR";
    }
}

