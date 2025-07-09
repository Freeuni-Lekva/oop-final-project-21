package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizReview;

import java.sql.SQLException;
import java.util.List;

public interface QuizReviewDAO {

    boolean addReview(QuizReview review) throws SQLException;

    QuizReview findByUserAndQuiz(int userId, long quizId) throws SQLException;

    List<QuizReview> findByQuiz(long quizId) throws SQLException;

    boolean updateReview(QuizReview review) throws SQLException;

    boolean deleteReview(int userId, long quizId) throws SQLException;
}
