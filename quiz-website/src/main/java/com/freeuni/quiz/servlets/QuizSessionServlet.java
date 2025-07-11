package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.servlets.handlers.QuizSessionHandler;
import com.freeuni.quiz.servlets.handlers.QuizAnswerHandler;
import com.freeuni.quiz.DAO.impl.ParticipantAnswerDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizCompletionDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/quiz-session")
public class QuizSessionServlet extends BaseServlet {

    private QuizSessionHandler sessionHandler;
    private QuizAnswerHandler answerHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        ParticipantAnswerDAOImpl participantAnswerRepository = new ParticipantAnswerDAOImpl(dataSource);
        QuizCompletionDAOImpl quizCompletionRepository = new QuizCompletionDAOImpl(dataSource);
        
        this.sessionHandler = new QuizSessionHandler(quizService, categoryService);
        this.answerHandler = new QuizAnswerHandler(quizService, participantAnswerRepository, quizCompletionRepository);
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
            response.sendRedirect("quiz-browser");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("review".equals(action)) {
            sessionHandler.handleReviewMode(request, response, currentUser, quizId);
        } else if ("next".equals(action) || "previous".equals(action)) {
            sessionHandler.handleNavigation(request, response, currentUser, quizId, action);
        } else {
            sessionHandler.handleQuizSession(request, response, currentUser, quizId);
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
            handleError(request, response, "Quiz ID is required");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "answer";
        
        switch (action) {
            case "answer":
                answerHandler.handleAnswerSubmission(request, response, currentUser, quizId);
                break;
            case "next":
            case "previous":
                sessionHandler.handleNavigation(request, response, currentUser, quizId, action);
                break;
            case "review":
                sessionHandler.handleReviewMode(request, response, currentUser, quizId);
                break;
            case "submit":
            case "finish":
                answerHandler.handleQuizCompletion(request, response, currentUser, quizId);
                break;
            default:
                response.sendRedirect("quiz-browser");
                break;
        }
    }
} 