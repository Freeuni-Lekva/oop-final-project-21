package com.freeuni.quiz.servlets;

import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String imageURL = request.getParameter("imageURL");
        String bio = request.getParameter("bio");


        if (username == null || password == null || firstName == null || lastName == null || email == null) {
            request.setAttribute("error", "Please fill out all required fields.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = userService.registerUser(username, password, firstName, lastName, email, imageURL, bio);

            if (success) {
                request.setAttribute("message", "New user registered successfully! \uD83E\uDD73");
                request.getRequestDispatcher("/register-success.jsp").forward(request, response);
                //response.sendRedirect("login.jsp");
            } else {
                request.setAttribute("error", "Username already taken.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
