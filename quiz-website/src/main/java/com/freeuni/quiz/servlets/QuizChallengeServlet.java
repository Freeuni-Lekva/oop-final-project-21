package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.QuizChallengeDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.repository.QuestionRepository;
import com.freeuni.quiz.repository.QuizQuestionMappingRepository;
import com.freeuni.quiz.repository.QuizRepository;
import com.freeuni.quiz.repository.impl.QuestionRepositoryImpl;
import com.freeuni.quiz.repository.impl.QuizQuestionMappingRepositoryImpl;
import com.freeuni.quiz.repository.impl.QuizRepositoryImpl;
import com.freeuni.quiz.service.QuizChallengeService;
import com.freeuni.quiz.service.UserService;
import com.freeuni.quiz.service.QuizService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-challenges")
public class QuizChallengeServlet extends HttpServlet {
    private QuizChallengeService challengeService;
    private UserService userService;
    private QuizService quizService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
        QuizRepository quizRepository = new QuizRepositoryImpl(dataSource);
        QuestionRepository questionRepository = new QuestionRepositoryImpl(dataSource);
        QuizQuestionMappingRepository quizQuestionMappingRepository = new QuizQuestionMappingRepositoryImpl(dataSource);

        quizService = new QuizService(quizRepository, questionRepository, quizQuestionMappingRepository);
        challengeService = new QuizChallengeService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("received".equals(action)) {
                List<QuizChallengeDTO> receivedChallenges = challengeService.getReceivedChallenges(currentUser.getId(), userService, quizService);
                req.setAttribute("receivedChallenges", receivedChallenges);
                req.getRequestDispatcher("receivedChallenges.jsp").forward(req, resp);

            } else if ("sent".equals(action)) {
                List<QuizChallengeDTO> sentChallenges = challengeService.getSentChallenges(currentUser.getId(), userService, quizService);
                req.setAttribute("sentChallenges", sentChallenges);
                req.getRequestDispatcher("sentChallenges.jsp").forward(req, resp);

            } else {
                List<QuizChallengeDTO> receivedChallenges = challengeService.getReceivedChallenges(currentUser.getId(), userService, quizService);
                List<QuizChallengeDTO> sentChallenges = challengeService.getSentChallenges(currentUser.getId(), userService, quizService);

                req.setAttribute("receivedChallenges", receivedChallenges);
                req.setAttribute("sentChallenges", sentChallenges);
                req.getRequestDispatcher("challenges.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            throw new ServletException("Error loading challenges", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("send".equals(action)) {
                int challengedUserId = Integer.parseInt(req.getParameter("challengedUserId"));
                Long quizId = Long.parseLong(req.getParameter("quizId"));
                String message = req.getParameter("message");

                boolean success = challengeService.sendChallenge(currentUser.getId(), challengedUserId, quizId, message);

                if (success) {
                    resp.sendRedirect("quiz-challenges?action=sent&success=true");
                } else {
                    resp.sendRedirect("quiz-challenges?action=sent&error=already_exists");
                }

            } else if ("accept".equals(action)) {
                Long challengeId = Long.parseLong(req.getParameter("challengeId"));
                challengeService.acceptChallenge(challengeId);
                resp.sendRedirect("quiz-challenges?action=received&accepted=true");

            } else if ("decline".equals(action)) {
                Long challengeId = Long.parseLong(req.getParameter("challengeId"));
                challengeService.declineChallenge(challengeId);
                resp.sendRedirect("quiz-challenges?action=received&declined=true");

            } else if ("delete".equals(action)) {
                Long challengeId = Long.parseLong(req.getParameter("challengeId"));
                challengeService.deleteChallenge(challengeId);
                resp.sendRedirect("quiz-challenges?deleted=true");
            }

        } catch (Exception e) {
            throw new ServletException("Error processing challenge action", e);
        }
    }
}