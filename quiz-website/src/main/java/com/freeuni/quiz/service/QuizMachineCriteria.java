package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizCompletionDAO;
import com.freeuni.quiz.DAO.impl.QuizCompletionDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QuizMachineCriteria implements AchievementCriteria {
    private final QuizCompletionDAO quizCompletionRepository;

    public QuizMachineCriteria(DataSource dataSource) {
        this.quizCompletionRepository = new QuizCompletionDAOImpl(dataSource);
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