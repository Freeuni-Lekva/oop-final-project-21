package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
        } else {
            req.setAttribute("user", user);
            req.getRequestDispatcher("profile.jsp").forward(req, resp);
        }
    }
}
