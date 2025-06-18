package servlets;

import DAO.UserDAO;
import bean.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        BasicDataSource dataSource = (BasicDataSource) getServletContext().getAttribute("dataSource");
        UserDAO userDao = new UserDAO(dataSource); // create DAO from DataSource

        try {
            User user = userDao.findByUsername(username);

            if (user != null && user.getHashPassword().equals(password)) {
                // Successful login
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.sendRedirect("home.jsp");
            } else {
                // Login failed
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
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
