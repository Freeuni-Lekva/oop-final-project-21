package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizRating;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface QuizRatingDAO {
    boolean addRating(QuizRating rating) throws SQLException;

    QuizRating findByUserAndQuiz(int userId, long quizId) throws SQLException;

    List<QuizRating> findByQuiz(long quizId) throws SQLException;

    double getAverageRating(long quizId) throws SQLException;

    int getRatingCount(long quizId) throws SQLException;

    Map<Long, Double> getPopularQuizzes(int limit) throws SQLException;

    boolean deleteRating(int userId, long quizId) throws SQLException;
}
