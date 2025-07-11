package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizCompletionDAO;
import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.impl.QuizCompletionDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QuizMasterCriteria implements AchievementCriteria {
    private final QuizDAO quizRepository;
    private final QuizCompletionDAO quizCompletionRepository;

    public QuizMasterCriteria(DataSource dataSource) {
        this.quizRepository = new QuizDAOImpl(dataSource);
        this.quizCompletionRepository = new QuizCompletionDAOImpl(dataSource);
    }

    @Override
    public boolean isSatisfied(int userId) throws SQLException {
        int createdCount = quizRepository.findByCreator((long) userId, 0, 20).size();
        int takenCount = quizCompletionRepository.getCompletionCountByUser(userId);
        return createdCount >= 20 && takenCount >= 50;
    }

    @Override
    public String getAchievementName() {
        return "QUIZ_MASTER";
    }
}

