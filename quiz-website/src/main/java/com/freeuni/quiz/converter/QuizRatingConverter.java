package com.freeuni.quiz.converter;

import com.freeuni.quiz.DTO.QuizRatingDTO;
import com.freeuni.quiz.bean.QuizRating;

public class QuizRatingConverter {

    public static QuizRatingDTO toDTO(QuizRating rating) {
        if (rating == null) {
            return null;
        }

        QuizRatingDTO dto = new QuizRatingDTO();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUserId());
        dto.setQuizId(rating.getQuizId());
        dto.setRating(rating.getRating());
        dto.setCreatedAt(rating.getCreatedAt());

        return dto;
    }

    public static QuizRating toEntity(QuizRatingDTO dto) {
        if (dto == null) {
            return null;
        }

        QuizRating rating = new QuizRating();
        rating.setId(dto.getId());
        rating.setUserId(dto.getUserId());
        rating.setQuizId(dto.getQuizId());
        rating.setRating(dto.getRating());
        rating.setCreatedAt(dto.getCreatedAt());

        return rating;
    }
}