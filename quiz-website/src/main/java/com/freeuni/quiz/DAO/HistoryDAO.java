package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizCompletion;
import java.util.List;
import java.util.Map;

public interface HistoryDAO {

    List<QuizCompletion> getUserCompletions(int userId);

    List<QuizCompletion> getUserRecentCompletions(int userId, int limit);

    List<QuizCompletion> getUserCompletionsByCategory(int userId, Long categoryId);

    int getTotalQuizzesTaken(int userId);

    double getAverageScore(int userId);

    int getBestScore(int userId);

    String getMostPlayedCategory(int userId);

    int getTotalTimeTaken(int userId);

    Map<String, Integer> getCategoryDistribution(int userId);
}