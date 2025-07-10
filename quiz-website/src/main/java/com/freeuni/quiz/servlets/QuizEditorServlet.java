package com.freeuni.quiz.servlets;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.quiz_util.*;

import java.util.Arrays;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/quiz-editor")
public class QuizEditorServlet extends HttpServlet {

    private QuizService quizService;
    private CategoryService categoryService;
    private QuestionService questionService;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        this.quizService = new QuizService(dataSource);
        this.categoryService = new CategoryService(dataSource);
        this.questionService = new QuestionService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("preview".equals(action)) {
            handlePreviewQuiz(request, response);
        } else {
            handleEditForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        switch (action) {
            case "addQuestion" -> handleAddQuestion(request, response);
            case "deleteQuestion" -> handleDeleteQuestion(request, response);
            case null, default -> handleUpdateQuiz(request, response);
        }
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
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
                handleError(request, response, "You don't have permission to edit this quiz");
                return;
            }
            
            List<Category> categories = categoryService.getAllActiveCategories();
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("categories", categories);
            request.setAttribute("questions", questions);
            request.setAttribute("mode", "edit");
            
            request.getRequestDispatcher("/WEB-INF/quiz-editor.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz for editing: " + e.getMessage());
        }
    }

    private void handleUpdateQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
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
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to update this quiz");
                return;
            }
            
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            QuizUpdateData updateData = extractUpdateData(request);
            
            if (!isValidUpdateData(updateData)) {
                request.setAttribute("errorMessage", updateData.getValidationError());
                handleEditForm(request, response);
                return;
            }
            
            updateQuizFromData(quiz, updateData);
            
            boolean updated = quizService.updateQuiz(quiz);
            
            if (updated) {
                response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Quiz updated successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to update quiz");
                handleEditForm(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating quiz: " + e.getMessage());
            handleEditForm(request, response);
        }
    }

    private void handlePreviewQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String quizIdStr = request.getParameter("quizId");
            if (quizIdStr == null || quizIdStr.isEmpty()) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }

            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            Long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            
            if (quiz == null) {
                handleError(request, response, "Quiz not found");
                return;
            }
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to preview this quiz");
                return;
            }
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            
            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);
            request.setAttribute("isPreview", true);
            
            request.getRequestDispatcher("/WEB-INF/quiz-preview.jsp").forward(request, response);
            
        } catch (Exception e) {
            handleError(request, response, "Error loading quiz preview: " + e.getMessage());
        }
    }

    private QuizUpdateData extractUpdateData(HttpServletRequest request) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String timeLimitStr = request.getParameter("timeLimit");
        
        QuizUpdateData updateData = new QuizUpdateData();
        updateData.setTitle(title);
        updateData.setDescription(description);
        updateData.setCategoryIdStr(categoryIdStr);
        updateData.setTimeLimitStr(timeLimitStr);
        
        return updateData;
    }

    private boolean isValidUpdateData(QuizUpdateData updateData) {
        if (updateData.getTitle() == null || updateData.getTitle().trim().isEmpty()) {
            updateData.setValidationError("Quiz title is required");
            return false;
        }
        
        if (updateData.getDescription() == null || updateData.getDescription().trim().isEmpty()) {
            updateData.setValidationError("Quiz description is required");
            return false;
        }
        
        return true;
    }

    private void updateQuizFromData(Quiz quiz, QuizUpdateData updateData) {
        quiz.setTestTitle(updateData.getTitle());
        quiz.setTestDescription(updateData.getDescription());
        
        if (updateData.getCategoryIdStr() != null && !updateData.getCategoryIdStr().isEmpty()) {
            quiz.setCategoryId(Long.parseLong(updateData.getCategoryIdStr()));
        }
        
        if (updateData.getTimeLimitStr() != null && !updateData.getTimeLimitStr().isEmpty()) {
            try {
                quiz.setTimeLimitMinutes(Long.parseLong(updateData.getTimeLimitStr()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void handleAddQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
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
            
            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to add questions to this quiz");
                return;
            }
            
            String questionText = request.getParameter("questionText");
            String pointsStr = request.getParameter("points");
            String questionTypeStr = request.getParameter("questionType");
            
            if (questionText == null || questionText.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Question text is required");
                handleEditForm(request, response);
                return;
            }
            
            if (questionTypeStr == null || questionTypeStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Question type is required");
                handleEditForm(request, response);
                return;
            }
            
            double points;
            try {
                points = pointsStr != null ? Double.parseDouble(pointsStr) : 10.0;
            } catch (NumberFormatException e) {
                points = 10.0;
            }
            
            QuestionType questionType = QuestionType.valueOf(questionTypeStr);
            
            AbstractQuestionHandler questionHandler;
            switch (questionType) {
                case TEXT:
                    String expectedAnswer = request.getParameter("expectedAnswer");
                    List<String> correctAnswers = new ArrayList<>();
                    if (expectedAnswer != null && !expectedAnswer.trim().isEmpty()) {
                        correctAnswers.add(expectedAnswer.trim());
                    }
                    questionHandler = new TextQuestionHandler(questionText, correctAnswers);
                    break;
                    
                case MULTIPLE_CHOICE:
                    String[] options = request.getParameterValues("options[]");
                    String correctAnswerStr = request.getParameter("correctAnswer");
                    
                    if (options == null || options.length < 2) {
                        request.setAttribute("errorMessage", "At least 2 options are required for multiple choice questions");
                        handleEditForm(request, response);
                        return;
                    }
                    
                    if (correctAnswerStr == null || correctAnswerStr.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Correct answer index is required for multiple choice questions");
                        handleEditForm(request, response);
                        return;
                    }
                    
                    int correctIndex;
                    try {
                        correctIndex = Integer.parseInt(correctAnswerStr);
                        if (correctIndex < 0 || correctIndex >= options.length) {
                            throw new NumberFormatException("Index out of range");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "Invalid correct answer index");
                        handleEditForm(request, response);
                        return;
                    }
                    
                    List<String> choiceOptions = Arrays.asList(options);
                    List<String> correctChoices = Collections.singletonList(options[correctIndex]);
                    questionHandler = new MultipleChoiceQuestionHandler(questionText, choiceOptions, correctChoices);
                    break;
                    
                case IMAGE:
                    String imageUrl = request.getParameter("imageUrl");
                    String imageExpectedAnswer = request.getParameter("expectedAnswer");
                    
                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Image URL is required for image questions");
                        handleEditForm(request, response);
                        return;
                    }
                    
                    List<String> imageCorrectAnswers = new ArrayList<>();
                    if (imageExpectedAnswer != null && !imageExpectedAnswer.trim().isEmpty()) {
                        imageCorrectAnswers.add(imageExpectedAnswer.trim());
                    }
                    questionHandler = new ImageQuestionHandler(questionText, imageUrl, imageCorrectAnswers);
                    break;
                    
                default:
                    request.setAttribute("errorMessage", "Unsupported question type");
                    handleEditForm(request, response);
                    return;
            }
            
            Question question = new Question();
            question.setAuthorUserId(currentUser.getId());
            question.setCategoryId(1L);
            question.setQuestionTitle(questionText);
            question.setQuestionType(questionType);
            question.setQuestionHandler(questionHandler);
            question.setPoints(points);
            
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
                    handleEditForm(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Failed to create question");
                handleEditForm(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid question type: " + e.getMessage());
            handleEditForm(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error adding question: " + e.getMessage());
            handleEditForm(request, response);
        }
    }

    private void handleDeleteQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            javax.servlet.http.HttpSession session = request.getSession(false);
            UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            String quizIdStr = request.getParameter("quizId");
            String questionIdStr = request.getParameter("questionId");
            
            if (quizIdStr == null || quizIdStr.isEmpty() || questionIdStr == null || questionIdStr.isEmpty()) {
                response.sendRedirect("quiz-manager?action=dashboard");
                return;
            }
            
            Long quizId = Long.parseLong(quizIdStr);

            if (!quizService.isQuizOwner(quizId, (long) currentUser.getId())) {
                handleError(request, response, "You don't have permission to delete questions from this quiz");
                return;
            }
            
            response.sendRedirect("quiz-editor?quizId=" + quizId + "&message=Question deletion feature coming soon");
            
        } catch (Exception e) {
            handleError(request, response, "Error deleting question: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }

    private static class QuizUpdateData {
        private String title;
        private String description;
        private String categoryIdStr;
        private String timeLimitStr;
        private String validationError;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategoryIdStr() { return categoryIdStr; }
        public void setCategoryIdStr(String categoryIdStr) { this.categoryIdStr = categoryIdStr; }
        
        public String getTimeLimitStr() { return timeLimitStr; }
        public void setTimeLimitStr(String timeLimitStr) { this.timeLimitStr = timeLimitStr; }
        
        public String getValidationError() { return validationError; }
        public void setValidationError(String validationError) { this.validationError = validationError; }
    }
} 