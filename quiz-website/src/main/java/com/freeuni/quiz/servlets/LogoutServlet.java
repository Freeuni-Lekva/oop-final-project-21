package com.freeuni.quiz.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false); // get session if exists
        if (session != null) {
            session.invalidate(); // log the user out
        }
        resp.sendRedirect("login.jsp"); // redirect to login page
    }
}
