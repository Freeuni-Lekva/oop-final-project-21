package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.ImageQuestionDto;
import com.freeuni.quiz.quiz_util.ImageQuestionHandler;
import com.freeuni.quiz.util.DTOUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@WebServlet("/image-question-creator")
public class ImageQuestionCreatorServlet extends BaseQuestionCreatorServlet {

    @Override
    protected String getQuestionType() {
        return "IMAGE";
    }

    @Override
    protected String getJspPage() {
        return "/WEB-INF/image-question-creator.jsp";
    }

    @Override
    protected String validateFormData(HttpServletRequest request) {
        ImageQuestionDto dto = DTOUtils.extractImageQuestionData(request);
        String baseValidation = DTOUtils.validateImageQuestion(dto);
        if (baseValidation != null) {
            return baseValidation;
        }
        
        if (!isValidImageUrl(dto.getImageUrl())) {
            return "Please provide a valid image URL (http/https)";
        }
        
        return null;
    }

    @Override
    protected Question createQuestionFromForm(HttpServletRequest request, UserDTO currentUser) {
        Question question = createBaseQuestion(request, currentUser);
        if (question == null) {
            return null;
        }
        
        ImageQuestionDto dto = DTOUtils.extractImageQuestionData(request);
        
        List<String> correctAnswers = Collections.singletonList(dto.getCorrectAnswer().trim());
        
        ImageQuestionHandler handler = new ImageQuestionHandler(dto.getQuestionText(), dto.getImageUrl().trim(), correctAnswers);
        question.setQuestionHandler(handler);
        
        return question;
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        String trimmedUrl = url.trim().toLowerCase();
        
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            return false;
        }
        
        return trimmedUrl.endsWith(".jpg") || 
               trimmedUrl.endsWith(".jpeg") || 
               trimmedUrl.endsWith(".png") || 
               trimmedUrl.endsWith(".gif") || 
               trimmedUrl.endsWith(".webp") ||
               trimmedUrl.contains("image") ||
               trimmedUrl.contains("img");
    }
} 