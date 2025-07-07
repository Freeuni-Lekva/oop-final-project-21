package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.MultipleChoiceQuestionDto;
import com.freeuni.quiz.quiz_util.MultipleChoiceQuestionHandler;
import com.freeuni.quiz.util.DTOUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet("/multiple-choice-question-creator")
public class MultipleChoiceQuestionCreatorServlet extends BaseQuestionCreatorServlet {

    @Override
    protected String getQuestionType() {
        return "MULTIPLE_CHOICE";
    }

    @Override
    protected String getJspPage() {
        return "/WEB-INF/multiple-choice-question-creator.jsp";
    }

    @Override
    protected String validateFormData(HttpServletRequest request) {
        MultipleChoiceQuestionDto dto = DTOUtils.extractMultipleChoiceQuestionData(request);
        return DTOUtils.validateMultipleChoiceQuestion(dto);
    }

    @Override
    protected Question createQuestionFromForm(HttpServletRequest request, UserDTO currentUser) {
        Question question = createBaseQuestion(request, currentUser);
        if (question == null) {
            return null;
        }
        
        MultipleChoiceQuestionDto dto = DTOUtils.extractMultipleChoiceQuestionData(request);
        
        try {
            List<String> choiceOptions = Arrays.asList(dto.getOptions());
            int correctIndex = Integer.parseInt(dto.getCorrectOptionStr());
            List<String> correctChoices = Collections.singletonList(dto.getOptions()[correctIndex]);
            
            MultipleChoiceQuestionHandler handler = new MultipleChoiceQuestionHandler(
                dto.getQuestionText(), choiceOptions, correctChoices);
            question.setQuestionHandler(handler);
            
            return question;
        } catch (Exception e) {
            return null;
        }
    }
} 