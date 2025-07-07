package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.TextQuestionDto;
import com.freeuni.quiz.quiz_util.TextQuestionHandler;
import com.freeuni.quiz.util.DTOUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/text-question-creator")
public class TextQuestionCreatorServlet extends BaseQuestionCreatorServlet {

    @Override
    protected String getQuestionType() {
        return "TEXT";
    }

    @Override
    protected String getJspPage() {
        return "/WEB-INF/text-question-creator.jsp";
    }

    @Override
    protected String validateFormData(HttpServletRequest request) {
        TextQuestionDto dto = DTOUtils.extractTextQuestionData(request);
        return DTOUtils.validateTextQuestion(dto);
    }

    @Override
    protected Question createQuestionFromForm(HttpServletRequest request, UserDTO currentUser) {
        Question question = createBaseQuestion(request, currentUser);
        if (question == null) {
            return null;
        }
        
        TextQuestionDto dto = DTOUtils.extractTextQuestionData(request);
        
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add(dto.getCorrectAnswer().trim());
        

        if (dto.getAlternativeAnswers() != null && !dto.getAlternativeAnswers().trim().isEmpty()) {
            String[] alternatives = dto.getAlternativeAnswers().split(",");
            for (String alt : alternatives) {
                if (alt != null && !alt.trim().isEmpty()) {
                    correctAnswers.add(alt.trim());
                }
            }
        }
        
        TextQuestionHandler handler = new TextQuestionHandler(dto.getQuestionText(), correctAnswers);
        question.setQuestionHandler(handler);
        
        return question;
    }
} 