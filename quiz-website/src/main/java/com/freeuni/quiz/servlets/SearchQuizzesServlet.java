package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.service.QuizService;
import com.freeuni.quiz.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search-quizzes")
public class SearchQuizzesServlet extends HttpServlet {
    private QuizService quizService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);


        quizService = new QuizService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String query = req.getParameter("q");
        if (query == null || query.trim().length() < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            List<Quiz> allQuizzes = quizService.getAllQuizzes(0, 100);
            List<JsonObject> matchingQuizzes = new ArrayList<>();
            String searchQuery = query.toLowerCase().trim();

            for (Quiz quiz : allQuizzes) {
                if (quiz.getTestTitle().toLowerCase().contains(searchQuery) ||
                        quiz.getTestDescription().toLowerCase().contains(searchQuery)) {

                    UserDTO creator = userService.findById(quiz.getCreatorUserId());
                    String creatorName = creator != null ? creator.getUserName() : "Unknown";

                    JsonObject quizJson = new JsonObject();
                    quizJson.addProperty("id", quiz.getId());
                    quizJson.addProperty("testTitle", quiz.getTestTitle());
                    quizJson.addProperty("description", quiz.getTestDescription());
                    quizJson.addProperty("creatorName", creatorName);
                    quizJson.addProperty("createdAt", quiz.getCreatedAt().toString());

                    matchingQuizzes.add(quizJson);
                }
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            Gson gson = new Gson();
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(matchingQuizzes));
            out.flush();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}