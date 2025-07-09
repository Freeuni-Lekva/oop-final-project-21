package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.impl.QuizRatingDAOImpl;
import com.freeuni.quiz.DTO.QuizRatingDTO;
import com.freeuni.quiz.bean.QuizRating;
import com.freeuni.quiz.converter.QuizRatingConverter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizRatingService {
    private final QuizRatingDAOImpl ratingDAO;

    public QuizRatingService(DataSource dataSource) {
        this.ratingDAO = new QuizRatingDAOImpl(dataSource);
    }

    public boolean rateQuiz(int userId, long quizId, int rating) throws SQLException {
        if (rating < 1 || rating > 5) {
            return false;
        }

        QuizRating ratingEntity = new QuizRating(userId, quizId, rating);
        return ratingDAO.addRating(ratingEntity);
    }

    public QuizRatingDTO getUserRating(int userId, long quizId) throws SQLException {
        QuizRating ratingEntity = ratingDAO.findByUserAndQuiz(userId, quizId);
        return QuizRatingConverter.toDTO(ratingEntity);
    }

    public List<QuizRatingDTO> getQuizRatings(long quizId) throws SQLException {
        List<QuizRating> ratingEntities = ratingDAO.findByQuiz(quizId);
        List<QuizRatingDTO> dtos = new ArrayList<>();

        for (QuizRating rating : ratingEntities) {
            dtos.add(QuizRatingConverter.toDTO(rating));
        }

        return dtos;
    }

    public double getAverageRating(long quizId) throws SQLException {
        return ratingDAO.getAverageRating(quizId);
    }

    public int getRatingCount(long quizId) throws SQLException {
        return ratingDAO.getRatingCount(quizId);
    }

    public Map<Long, Double> getPopularQuizzes(int limit) throws SQLException {
        return ratingDAO.getPopularQuizzes(limit);
    }

    public boolean deleteRating(int userId, long quizId) throws SQLException {
        return ratingDAO.deleteRating(userId, quizId);
    }
}