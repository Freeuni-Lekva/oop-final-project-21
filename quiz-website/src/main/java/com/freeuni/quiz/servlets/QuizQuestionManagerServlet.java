package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-questions")
public class QuizQuestionManagerServlet extends BaseServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        handleManageQuestions(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("quiz-manager?action=dashboard");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddQuestion(request, response);
                break;
            case "remove":
                handleRemoveQuestion(request, response);
                break;
            default:
                response.sendRedirect("quiz-manager?action=dashboard");
                break;
        }
    }

    private void handleManageQuestions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = getCurrentUser(request);
            Long quizId = getLongParam(request, "quizId");
            
            if (quizId == null) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }
            
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            if (isQuizOwner(request, quizId)) {
                handleError(request, response, "You don't have permission to manage questions for this quiz");
                return;
            }
            
            List<Question> quizQuestions = quizService.getQuizQuestions(quizId);
            
            List<Question> userQuestions = questionService.getQuestionsByAuthor((long) currentUser.getId(), 0, 50);
            
            List<Question> availableQuestions = filterAvailableQuestions(userQuestions, quizQuestions);
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("quizQuestions", quizQuestions);
            request.setAttribute("availableQuestions", availableQuestions);
            
            request.getRequestDispatcher("/WEB-INF/quiz-question-manager.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading question management: " + e.getMessage());
        }
    }

    private void handleAddQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDTO currentUser = getCurrentUser(request);
            
            QuestionOperationData operationData = extractOperationData(request);
            
            if (isValidOperationData(operationData)) {
                redirectWithError(response, "quiz-questions?quizId=" + operationData.getQuizIdStr(), "Invalid parameters");
                return;
            }
            
            if (isQuizOwner(request, operationData.getQuizId())) {
                handleError(request, response, "You don't have permission to modify this quiz");
                return;
            }
            
            if (!questionService.isQuestionOwner(operationData.getQuestionId(), (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to use this question");
                return;
            }
            
            int currentCount = quizService.getQuizQuestionCount(operationData.getQuizId());
            Long nextPosition = (long) (currentCount + 1);
            
            boolean added = quizService.addQuestionToQuiz(operationData.getQuizId(), operationData.getQuestionId(), nextPosition);
            
            String message = added ? "Question added successfully" : "Failed to add question";
            
            if (added) {
                redirectWithSuccess(response, "quiz-questions?quizId=" + operationData.getQuizId(), message);
            } else {
                redirectWithError(response, "quiz-questions?quizId=" + operationData.getQuizId(), message);
            }
            
        } catch (Exception e) {
            handleError(request, response, "Error adding question: " + e.getMessage());
        }
    }

    private void handleRemoveQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            QuestionOperationData operationData = extractOperationData(request);
            
            if (isValidOperationData(operationData)) {
                redirectWithError(response, "quiz-questions?quizId=" + operationData.getQuizIdStr(), "Invalid parameters");
                return;
            }
            
            if (isQuizOwner(request, operationData.getQuizId())) {
                handleError(request, response, "You don't have permission to modify this quiz");
                return;
            }
            
            boolean removed = quizService.removeQuestionFromQuiz(operationData.getQuizId(), operationData.getQuestionId());
            
            String message = removed ? "Question removed successfully" : "Failed to remove question";
            
            if (removed) {
                redirectWithSuccess(response, "quiz-questions?quizId=" + operationData.getQuizId(), message);
            } else {
                redirectWithError(response, "quiz-questions?quizId=" + operationData.getQuizId(), message);
            }
            
        } catch (Exception e) {
            handleError(request, response, "Error removing question: " + e.getMessage());
        }
    }

    private QuestionOperationData extractOperationData(HttpServletRequest request) {
        Long quizId = getLongParam(request, "quizId");
        Long questionId = getLongParam(request, "questionId");
        
        QuestionOperationData data = new QuestionOperationData();
        data.setQuizId(quizId);
        data.setQuestionId(questionId);
        data.setQuizIdStr(request.getParameter("quizId"));

        return data;
    }

    private boolean isValidOperationData(QuestionOperationData data) {
        return data.getQuizId() == null || data.getQuestionId() == null;
    }

    private List<Question> filterAvailableQuestions(List<Question> userQuestions, List<Question> quizQuestions) {
        return userQuestions.stream()
            .filter(userQuestion -> 
                quizQuestions.stream()
                    .noneMatch(quizQuestion -> quizQuestion.getId().equals(userQuestion.getId()))
            )
            .collect(java.util.stream.Collectors.toList());
    }



    private static class QuestionOperationData {
        private String quizIdStr;
        private Long quizId;
        private Long questionId;

        public String getQuizIdStr() { return quizIdStr; }
        public void setQuizIdStr(String quizIdStr) { this.quizIdStr = quizIdStr; }
        
        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }
        
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
    }
} 