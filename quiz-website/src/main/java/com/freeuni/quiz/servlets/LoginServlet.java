package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.util.PasswordUtil;
import com.freeuni.quiz.bean.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        BasicDataSource dataSource = (BasicDataSource) getServletContext().getAttribute("dataSource");
        UserDAO userDao = new UserDAO(dataSource);

        try {
            User user = userDao.findByUsername(username);

            if (user != null) {
                // Get stored salt and hash
                String storedSalt = user.getSalt();
                String storedHash = user.getHashPassword();

                // Decode salt and hash input password
                byte[] saltBytes = PasswordUtil.decodeSalt(storedSalt);
                String hashedInput = PasswordUtil.hashPassword(password, saltBytes);

                if (hashedInput.equals(storedHash)) {
                    // Successful login
                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);
                    resp.sendRedirect("home.jsp");
                    return;
                }
            }

            // Failed login
            req.setAttribute("error", "Invalid username or password.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);

        } catch (Exception e) { // SQLException or hashing error
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
}
