package com.freeuni.quiz.servlets.handlers;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.validators.QuestionFormValidator;
import com.freeuni.quiz.quiz_util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizQuestionHandler {

    private final QuizService quizService;
    private final QuestionService questionService;

    public QuizQuestionHandler(QuizService quizService, QuestionService questionService) {
        this.quizService = quizService;
        this.questionService = questionService;
    }

    public void handleAddQuestion(HttpServletRequest request, HttpServletResponse response, 
                                 UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
            request.setAttribute("errorMessage", "You don't have permission to add questions to this quiz");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        String validationError = QuestionFormValidator.validateBasicQuestionForm(request);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            redirectToEditor(response, quizId);
            return;
        }
        
        Question question = createQuestionFromForm(request, currentUser);
        if (question == null) {
            request.setAttribute("errorMessage", "Failed to create question - invalid data");
            redirectToEditor(response, quizId);
            return;
        }
        
        Long questionId = questionService.createQuestion(question);
        
        if (questionId != null) {
            int questionCount = quizService.getQuizQuestionCount(quizId);
            Long nextQuestionNumber = (long) (questionCount + 1);
            
            boolean linked = quizService.addQuestionToQuiz(quizId, questionId, nextQuestionNumber);
            
            if (linked) {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Question added successfully");
            } else {
                questionService.deleteQuestion(questionId);
                request.setAttribute("errorMessage", "Failed to link question to quiz");
                redirectToEditor(response, quizId);
            }
        } else {
            request.setAttribute("errorMessage", "Failed to create question");
            redirectToEditor(response, quizId);
        }
    }

    public void handleDeleteQuestion(HttpServletRequest request, HttpServletResponse response, 
                                    UserDTO currentUser, Long quizId, Long questionId) 
            throws ServletException, IOException {
        
        if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
            request.setAttribute("errorMessage", "You don't have permission to delete questions from this quiz");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
            return;
        }
        
        boolean removedFromQuiz = quizService.removeQuestionFromQuiz(quizId, questionId);
        
        if (removedFromQuiz) {
            boolean deletedQuestion = questionService.deleteQuestion(questionId);
            
            if (deletedQuestion) {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Question deleted successfully");
            } else {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Question removed from quiz");
            }
        } else {
            request.setAttribute("errorMessage", "Failed to remove question from quiz");
            redirectToEditor(response, quizId);
        }
    }

    private Question createQuestionFromForm(HttpServletRequest request, UserDTO currentUser) {
        try {
            String questionText = request.getParameter("questionText");
            String questionTypeStr = request.getParameter("questionType");
            String pointsStr = request.getParameter("points");
            
            double points = pointsStr != null ? Double.parseDouble(pointsStr) : 10.0;
            QuestionType questionType = QuestionType.valueOf(questionTypeStr);
            
            AbstractQuestionHandler questionHandler = createQuestionHandler(questionType, request);
            if (questionHandler == null) {
                return null;
            }
            
            Question question = new Question();
            question.setAuthorUserId(currentUser.getId());
            question.setCategoryId(1L);
            question.setQuestionTitle(questionText);
            question.setQuestionType(questionType);
            question.setQuestionHandler(questionHandler);
            question.setPoints(points);
            
            return question;
            
        } catch (Exception e) {
            return null;
        }
    }

    private AbstractQuestionHandler createQuestionHandler(QuestionType questionType, HttpServletRequest request) {
        return switch (questionType) {
            case TEXT -> createTextQuestionHandler(request);
            case MULTIPLE_CHOICE -> createMultipleChoiceQuestionHandler(request);
            case IMAGE -> createImageQuestionHandler(request);
        };
    }

    private TextQuestionHandler createTextQuestionHandler(HttpServletRequest request) {
        String questionText = request.getParameter("questionText");
        String expectedAnswer = request.getParameter("expectedAnswer");
        
        List<String> correctAnswers = new ArrayList<>();
        if (expectedAnswer != null && !expectedAnswer.trim().isEmpty()) {
            correctAnswers.add(expectedAnswer.trim());
        }
        
        return new TextQuestionHandler(questionText, correctAnswers);
    }

    private MultipleChoiceQuestionHandler createMultipleChoiceQuestionHandler(HttpServletRequest request) {
        String questionText = request.getParameter("questionText");
        String[] options = request.getParameterValues("options[]");
        String correctAnswerStr = request.getParameter("correctAnswer");
        
        if (options == null || options.length < 2) {
            return null;
        }
        
        try {
            int correctIndex = Integer.parseInt(correctAnswerStr);
            if (correctIndex < 0 || correctIndex >= options.length) {
                return null;
            }
            
            List<String> choiceOptions = Arrays.asList(options);
            List<String> correctChoices = Collections.singletonList(options[correctIndex]);
            
            return new MultipleChoiceQuestionHandler(questionText, choiceOptions, correctChoices);
            
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ImageQuestionHandler createImageQuestionHandler(HttpServletRequest request) {
        String questionText = request.getParameter("questionText");
        String imageUrl = request.getParameter("imageUrl");
        String expectedAnswer = request.getParameter("expectedAnswer");
        
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }
        
        List<String> correctAnswers = new ArrayList<>();
        if (expectedAnswer != null && !expectedAnswer.trim().isEmpty()) {
            correctAnswers.add(expectedAnswer.trim());
        }
        
        return new ImageQuestionHandler(questionText, imageUrl, correctAnswers);
    }

    private void redirectToEditor(HttpServletResponse response, Long quizId) throws IOException {
        response.sendRedirect("quiz-editor?quizId=" + quizId);
    }
} 