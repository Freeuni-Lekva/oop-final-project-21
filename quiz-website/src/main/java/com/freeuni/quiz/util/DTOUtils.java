package com.freeuni.quiz.util;

import com.freeuni.quiz.DTO.TextQuestionDto;
import com.freeuni.quiz.DTO.MultipleChoiceQuestionDto;
import com.freeuni.quiz.DTO.ImageQuestionDto;

import javax.servlet.http.HttpServletRequest;

public class DTOUtils {
    
    public static TextQuestionDto extractTextQuestionData(HttpServletRequest request) {
        return new TextQuestionDto(
            ServletUtils.getStringParameter(request, "title"),
            ServletUtils.getStringParameter(request, "categoryId"),
            ServletUtils.getStringParameter(request, "questionText"),
            ServletUtils.getStringParameter(request, "correctAnswer"),
            ServletUtils.getStringParameter(request, "alternativeAnswers")
        );
    }
    
    public static MultipleChoiceQuestionDto extractMultipleChoiceQuestionData(HttpServletRequest request) {
        return new MultipleChoiceQuestionDto(
            ServletUtils.getStringParameter(request, "title"),
            ServletUtils.getStringParameter(request, "categoryId"),
            ServletUtils.getStringParameter(request, "questionText"),
            ServletUtils.getStringArrayParameter(request, "options"),
            ServletUtils.getStringParameter(request, "correctOption")
        );
    }
    
    public static ImageQuestionDto extractImageQuestionData(HttpServletRequest request) {
        return new ImageQuestionDto(
            ServletUtils.getStringParameter(request, "title"),
            ServletUtils.getStringParameter(request, "categoryId"),
            ServletUtils.getStringParameter(request, "questionText"),
            ServletUtils.getStringParameter(request, "imageUrl"),
            ServletUtils.getStringParameter(request, "correctAnswer")
        );
    }
    
    public static String validateTextQuestion(TextQuestionDto dto) {
        String baseValidation = ServletUtils.validateQuestionForm(dto);
        if (baseValidation != null) {
            return baseValidation;
        }
        
        if (dto.getCorrectAnswer() == null || dto.getCorrectAnswer().trim().isEmpty()) {
            return "Correct answer is required";
        }
        
        return null;
    }
    
    public static String validateMultipleChoiceQuestion(MultipleChoiceQuestionDto dto) {
        String baseValidation = ServletUtils.validateQuestionForm(dto);
        if (baseValidation != null) {
            return baseValidation;
        }
        
        if (dto.getOptions() == null || dto.getOptions().length < 2) {
            return "At least 2 options are required";
        }
        
        for (String option : dto.getOptions()) {
            if (option == null || option.trim().isEmpty()) {
                return "All options must be filled";
            }
        }
        
        if (dto.getCorrectOptionStr() == null || dto.getCorrectOptionStr().trim().isEmpty()) {
            return "Correct option must be selected";
        }
        
        try {
            int correctOption = Integer.parseInt(dto.getCorrectOptionStr());
            if (correctOption < 0 || correctOption >= dto.getOptions().length) {
                return "Invalid correct option selected";
            }
        } catch (NumberFormatException e) {
            return "Invalid correct option format";
        }
        
        return null;
    }
    
    public static String validateImageQuestion(ImageQuestionDto dto) {
        String baseValidation = ServletUtils.validateQuestionForm(dto);
        if (baseValidation != null) {
            return baseValidation;
        }
        
        if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        }
        
        if (dto.getCorrectAnswer() == null || dto.getCorrectAnswer().trim().isEmpty()) {
            return "Correct answer is required";
        }
        
        return null;
    }
} 