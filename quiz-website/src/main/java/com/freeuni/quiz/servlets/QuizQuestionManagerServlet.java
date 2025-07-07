package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.util.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-questions")
public class QuizQuestionManagerServlet extends HttpServlet {

    private QuizService quizService;
    private QuestionService questionService;
    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        super.init();
        this.quizService = (QuizService) getServletContext().getAttribute("quizService");
        this.questionService = (QuestionService) getServletContext().getAttribute("questionService");
        this.sessionManager = new SessionManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleManageQuestions(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
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
            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
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
            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            QuestionOperationData operationData = extractOperationData(request);
            
            if (isValidOperationData(operationData)) {
                response.sendRedirect("quiz-questions?quizId=" + operationData.getQuizIdStr() + "&error=Invalid parameters");
                return;
            }
            
            if (!quizService.isQuizOwner(operationData.getQuizId(), (long) currentUser.getId())) {
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
            String param = added ? "message" : "error";
            
            response.sendRedirect("quiz-questions?quizId=" + operationData.getQuizId() + "&" + param + "=" + message);
            
        } catch (Exception e) {
            handleError(request, response, "Error adding question: " + e.getMessage());
        }
    }

    private void handleRemoveQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            User currentUser = sessionManager.getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            QuestionOperationData operationData = extractOperationData(request);
            
            if (isValidOperationData(operationData)) {
                response.sendRedirect("quiz-questions?quizId=" + operationData.getQuizIdStr() + "&error=Invalid parameters");
                return;
            }
            
            if (!quizService.isQuizOwner(operationData.getQuizId(), (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to modify this quiz");
                return;
            }
            
            boolean removed = quizService.removeQuestionFromQuiz(operationData.getQuizId(), operationData.getQuestionId());
            
            String message = removed ? "Question removed successfully" : "Failed to remove question";
            String param = removed ? "message" : "error";
            
            response.sendRedirect("quiz-questions?quizId=" + operationData.getQuizId() + "&" + param + "=" + message);
            
        } catch (Exception e) {
            handleError(request, response, "Error removing question: " + e.getMessage());
        }
    }

    private QuestionOperationData extractOperationData(HttpServletRequest request) {
        String quizIdStr = request.getParameter("quizId");
        String questionIdStr = request.getParameter("questionId");
        
        QuestionOperationData data = new QuestionOperationData();
        data.setQuizIdStr(quizIdStr);
        data.setQuestionIdStr(questionIdStr);
        
        return data;
    }

    private boolean isValidOperationData(QuestionOperationData data) {
        if (data.getQuizIdStr() == null || data.getQuizIdStr().isEmpty()) {
            return true;
        }
        
        if (data.getQuestionIdStr() == null || data.getQuestionIdStr().isEmpty()) {
            return true;
        }
        
        try {
            data.setQuizId(Long.parseLong(data.getQuizIdStr()));
            data.setQuestionId(Long.parseLong(data.getQuestionIdStr()));
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private List<Question> filterAvailableQuestions(List<Question> userQuestions, List<Question> quizQuestions) {
        return userQuestions.stream()
            .filter(userQuestion -> 
                quizQuestions.stream()
                    .noneMatch(quizQuestion -> quizQuestion.getId().equals(userQuestion.getId()))
            )
            .collect(java.util.stream.Collectors.toList());
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }

    private static class QuestionOperationData {
        private String quizIdStr;
        private String questionIdStr;
        private Long quizId;
        private Long questionId;

        public String getQuizIdStr() { return quizIdStr; }
        public void setQuizIdStr(String quizIdStr) { this.quizIdStr = quizIdStr; }
        
        public String getQuestionIdStr() { return questionIdStr; }
        public void setQuestionIdStr(String questionIdStr) { this.questionIdStr = questionIdStr; }
        
        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }
        
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
    }
} 