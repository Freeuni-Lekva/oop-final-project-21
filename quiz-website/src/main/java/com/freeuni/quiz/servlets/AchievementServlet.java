package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.AchievementDTO;
import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.service.AchievementService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/achievements")
public class AchievementServlet extends HttpServlet {

    private AchievementService achievementService;

    @Override
    public void init() {
        achievementService = (AchievementService) getServletContext().getAttribute("achievementService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = ((com.freeuni.quiz.DTO.UserDTO) session.getAttribute("user")).getId();

        List<Achievement> achievements = achievementService.getAchievementsByUser(userId);

        List<AchievementDTO> achievementDTOs = achievements.stream()
                .map(a -> new AchievementDTO(
                        a.getId(),
                        a.getUserId(),
                        a.getType(),
                        a.getAchievedAt()))
                .collect(Collectors.toList());

        req.setAttribute("achievements", achievementDTOs);
        req.getRequestDispatcher("/achievements.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = ((com.freeuni.quiz.DTO.UserDTO) session.getAttribute("user")).getId();
        String type = req.getParameter("type");

        if (type == null || type.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Achievement type required");
            return;
        }

        achievementService.award(userId, type.trim());
        resp.sendRedirect("achievements");
    }
}
