package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.servlets.handlers.QuizEditorHandler;
import com.freeuni.quiz.servlets.handlers.QuizQuestionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/quiz-editor")
public class QuizEditorServlet extends BaseServlet {

    private QuizEditorHandler editorHandler;
    private QuizQuestionHandler questionHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        this.editorHandler = new QuizEditorHandler(quizService, categoryService);
        this.questionHandler = new QuizQuestionHandler(quizService, questionService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        UserDTO currentUser = getCurrentUser(request);
        Long quizId = getLongParam(request, "quizId");
        
        if (quizId == null) {
            response.sendRedirect("quiz-manager?action=dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        if ("preview".equals(action)) {
            editorHandler.handlePreviewQuiz(request, response, currentUser, quizId);
        } else {
            editorHandler.handleEditForm(request, response, currentUser, quizId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (requireAuth(request, response)) {
            return;
        }
        
        UserDTO currentUser = getCurrentUser(request);
        Long quizId = getLongParam(request, "quizId");
        
        if (quizId == null) {
            response.sendRedirect("quiz-manager?action=dashboard");
            return;
        }
        
        String action = request.getParameter("action");

        switch (action) {
            case "addQuestion":
                questionHandler.handleAddQuestion(request, response, currentUser, quizId);
                break;
            case "deleteQuestion":
                Long questionId = getLongParam(request, "questionId");
                if (questionId != null) {
                    questionHandler.handleDeleteQuestion(request, response, currentUser, quizId, questionId);
                }
                break;
            default:
                editorHandler.handleUpdateQuiz(request, response, currentUser, quizId);
                break;
        }
    }
} 