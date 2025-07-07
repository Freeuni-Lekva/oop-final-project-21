package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.UserService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    private UserService userService;
    private Gson gson = new Gson();

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");  // "users" or "quizzes"
        String query = req.getParameter("query");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (query == null || query.trim().isEmpty()) {
            // Return empty JSON array
            resp.getWriter().write("[]");
            return;
        }

        try {
            if ("users".equalsIgnoreCase(type)) {
                List<UserDTO> users = userService.searchUsers(query.trim());
                String json = gson.toJson(users);
                resp.getWriter().write(json);
            } else if ("quizzes".equalsIgnoreCase(type)) {
                // TODO: implement quiz search service
                // For now, return empty list or dummy data
                resp.getWriter().write("[]");
            } else {
                // Invalid type - return empty JSON array
                resp.getWriter().write("[]");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}

