package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Category;
import com.freeuni.quiz.bean.User;
import com.freeuni.quiz.service.CategoryService;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        try {
            DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
            userDAO = new UserDAO(dataSource);
            categoryService = new CategoryService(dataSource);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize AdminServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = userDAO.findById(currentUser.getId());
            if (user == null || !user.isAdmin()) {
                response.sendRedirect("home.jsp");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Error checking admin status", e);
        }

        try {
            List<User> allUsers = userDAO.findAll();
            List<Category> allCategories = categoryService.getAllActiveCategories();
            
            request.setAttribute("users", allUsers);
            request.setAttribute("categories", allCategories);
            
            request.getRequestDispatcher("WEB-INF/admin.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading admin data", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = userDAO.findById(currentUser.getId());
            if (user == null || !user.isAdmin()) {
                response.sendRedirect("home.jsp");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Error checking admin status", e);
        }

        String action = request.getParameter("action");
        String message = "";
        String messageType = "success";

        try {
            switch (action) {
                case "deleteUser":
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    if (userId == currentUser.getId()) {
                        message = "Cannot delete your own account";
                        messageType = "error";
                    } else {
                        boolean deleted = userDAO.deleteUser(userId);
                        if (deleted) {
                            message = "User deleted successfully";
                        } else {
                            message = "Failed to delete user";
                            messageType = "error";
                        }
                    }
                    break;

                case "createCategory":
                    String categoryName = request.getParameter("categoryName");
                    String categoryDescription = request.getParameter("categoryDescription");
                    
                    if (categoryName == null || categoryName.trim().isEmpty()) {
                        message = "Category name is required";
                        messageType = "error";
                    } else {
                        Category category = new Category();
                        category.setCategoryName(categoryName.trim());
                        category.setDescription(categoryDescription != null ? categoryDescription.trim() : "");
                        
                        try {
                            categoryService.createCategory(category);
                            message = "Category created successfully";
                        } catch (IllegalArgumentException e) {
                            message = e.getMessage();
                            messageType = "error";
                        }
                    }
                    break;

                case "deleteCategory":
                    long categoryId = Long.parseLong(request.getParameter("categoryId"));
                    boolean deleted = categoryService.deleteCategory(categoryId);
                    if (deleted) {
                        message = "Category deleted successfully";
                    } else {
                        message = "Failed to delete category";
                        messageType = "error";
                    }
                    break;

                default:
                    message = "Unknown action";
                    messageType = "error";
            }
        } catch (SQLException e) {
            message = "Database error: " + e.getMessage();
            messageType = "error";
        } catch (NumberFormatException e) {
            message = "Invalid ID format";
            messageType = "error";
        }

        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);
        
        response.sendRedirect("admin");
    }
} 