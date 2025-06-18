package servlets;

import DAO.UserDAO;
import bean.User;

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
    private UserDAO usersDAO;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        usersDAO = new UserDAO(dataSource);
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
            // Check if user already exists by username or email
            if (usersDAO.findByUsername(username) != null) {
                request.setAttribute("error", "Username already taken.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            User user = new User();
            user.setUserName(username);
            user.setHashPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setImageURL(imageURL != null && !imageURL.trim().isEmpty() ? imageURL : null);
            user.setBio(bio);

            boolean success = usersDAO.addUser(user);

            if (success) {
                response.sendRedirect("login.jsp");
            } else {
                request.setAttribute("error", "Failed to register user. Try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
