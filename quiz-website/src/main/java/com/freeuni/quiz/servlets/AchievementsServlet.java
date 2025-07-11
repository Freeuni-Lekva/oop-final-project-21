package com.freeuni.quiz.servlets;


import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.UserAchievement;
import com.freeuni.quiz.service.AchievementService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/achievements")
    public class AchievementsServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
            AchievementService achievementService = new AchievementService(dataSource);

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            UserDTO user = (UserDTO) session.getAttribute("user");
            int userId = user.getId();

            try {
                achievementService.checkAndAwardAchievements(userId);

                List<UserAchievement> achievements = achievementService.getUserAchievements(userId);
                request.setAttribute("achievements", achievements);

                request.getRequestDispatcher("/achievements.jsp").forward(request, response);

            } catch (SQLException e) {
                throw new ServletException("Error loading achievements", e);
            }
        }
    }