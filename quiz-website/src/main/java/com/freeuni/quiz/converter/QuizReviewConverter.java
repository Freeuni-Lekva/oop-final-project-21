package com.freeuni.quiz.converter;

import com.freeuni.quiz.DTO.QuizReviewDTO;
import com.freeuni.quiz.bean.QuizReview;

public class QuizReviewConverter {

    public static QuizReviewDTO toDTO(QuizReview review) {
        if (review == null) {
            return null;
        }

        QuizReviewDTO dto = new QuizReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setQuizId(review.getQuizId());
        dto.setReviewText(review.getReviewText());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        return dto;
    }

    public static QuizReview toEntity(QuizReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        QuizReview review = new QuizReview();
        review.setId(dto.getId());
        review.setUserId(dto.getUserId());
        review.setQuizId(dto.getQuizId());
        review.setReviewText(dto.getReviewText());
        review.setCreatedAt(dto.getCreatedAt());
        review.setUpdatedAt(dto.getUpdatedAt());

        return review;
    }
}