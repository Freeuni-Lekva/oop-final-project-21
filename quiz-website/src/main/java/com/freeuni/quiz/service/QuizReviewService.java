package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.QuizReviewDAO;
import com.freeuni.quiz.DTO.QuizReviewDTO;
import com.freeuni.quiz.bean.QuizReview;
import com.freeuni.quiz.converter.QuizReviewConverter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizReviewService {
    private final QuizReviewDAO reviewDAO;

    public QuizReviewService(DataSource dataSource) {
        this.reviewDAO = new QuizReviewDAO(dataSource);
    }

    public boolean addOrUpdateReview(int userId, long quizId, String reviewText) throws SQLException {
        if (reviewText == null || reviewText.trim().isEmpty()) {
            return false;
        }

        QuizReview reviewEntity = new QuizReview(userId, quizId, reviewText);
        return reviewDAO.addReview(reviewEntity);
    }

    public QuizReviewDTO getUserReview(int userId, long quizId) throws SQLException {
        QuizReview reviewEntity = reviewDAO.findByUserAndQuiz(userId, quizId);
        return QuizReviewConverter.toDTO(reviewEntity);
    }

    public List<QuizReviewDTO> getQuizReviews(long quizId) throws SQLException {
        List<QuizReview> reviewEntities = reviewDAO.findByQuiz(quizId);
        List<QuizReviewDTO> dtos = new ArrayList<>();

        for (QuizReview review : reviewEntities) {
            dtos.add(QuizReviewConverter.toDTO(review));
        }

        return dtos;
    }
    public boolean deleteReview(int userId, long quizId) throws SQLException {
        return reviewDAO.deleteReview(userId, quizId);
    }
}