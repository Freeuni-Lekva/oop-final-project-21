package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/edit-profile")
public class EditProfileServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // Pass user to JSP to pre-fill form
        req.setAttribute("user", user);
        req.getRequestDispatcher("editProfile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String imageURL = req.getParameter("imageURL");
        String bio = req.getParameter("bio");

        boolean updated = false;
        try {
            updated = userService.updateUserInfo(user.getId(), firstName, lastName,
                    email, imageURL, bio);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(updated){
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setImageURL(imageURL);
            user.setBio(bio);
            session.setAttribute("user", user);
            resp.sendRedirect("profile");
        } else {
            req.setAttribute("error", "Failed to update profile.");
            req.getRequestDispatcher("editProfile.jsp").forward(req, resp);
        }

    }
}
