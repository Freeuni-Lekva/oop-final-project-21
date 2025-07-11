package com.freeuni.quiz.service;

import com.freeuni.quiz.repository.QuizCompletionRepository;
import com.freeuni.quiz.repository.QuizRepository;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;
import com.freeuni.quiz.repository.impl.QuizRepositoryImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QuizMasterCriteria implements AchievementCriteria {
    private final QuizRepository quizRepository;
    private final QuizCompletionRepository quizCompletionRepository;

    public QuizMasterCriteria(DataSource dataSource) {
        this.quizRepository = new QuizRepositoryImpl(dataSource);
        this.quizCompletionRepository = new QuizCompletionRepositoryImpl(dataSource);
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

