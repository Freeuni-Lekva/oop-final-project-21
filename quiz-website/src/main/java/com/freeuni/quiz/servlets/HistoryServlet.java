package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.UserHistoryDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.service.HistoryService;
import com.freeuni.quiz.service.QuizService;
import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/history"})
public class HistoryServlet extends BaseServlet {

    private HistoryService historyService;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        historyService = new HistoryService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (requireAuth(req, resp)) {
            return;
        }

        UserDTO currentUser = getCurrentUser(req);
        int userId = currentUser.getId();

        String view = req.getParameter("view");

        try {
            if ("summary".equals(view)) {
                UserHistoryDTO historyDTO = historyService.getUserHistory(userId, currentUser);
                req.setAttribute("userHistory", historyDTO);

                Map<String, Integer> categoryDistribution = historyService.getCategoryDistribution(userId);
                req.setAttribute("categoryDistribution", categoryDistribution);

                req.getRequestDispatcher("history-summary.jsp").forward(req, resp);

            } else if ("detail".equals(view)) {
                Map<Quiz, List<QuizCompletion>> quizCompletionsMap = historyService.getQuizCompletionsMap(userId);
                req.setAttribute("quizCompletionsMap", quizCompletionsMap);

                req.getRequestDispatcher("history-detail.jsp").forward(req, resp);

            } else {
                UserHistoryDTO historyDTO = historyService.getUserHistory(userId, currentUser);
                req.setAttribute("userHistory", historyDTO);

                req.getRequestDispatcher("history.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            handleError(req, resp, "Error loading history: " + e.getMessage());
        }
    }
}