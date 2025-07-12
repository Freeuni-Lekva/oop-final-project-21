package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.impl.MessageDAOImpl;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Message;
import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private MessageDAOImpl messageDAO;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        messageDAO = new MessageDAOImpl(dataSource);
        userService= new UserService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        UserDTO currentUser = (UserDTO) req.getSession().getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String withUserIdStr = req.getParameter("with");
        if (withUserIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'with' parameter");
            return;
        }

        int withUserId;
        try {
            withUserId = Integer.parseInt(withUserIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid 'with' parameter");
            return;
        }

        try {
            List<Message> recentMessages = messageDAO.getRecentMessages(currentUser.getId(), withUserId);
            UserDTO withUser = userService.findById(withUserId);
            req.setAttribute("withUser", withUser);
            req.setAttribute("messages", recentMessages);
            req.setAttribute("currentUserId", currentUser.getId());

            req.getRequestDispatcher("/chat.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Error loading chat messages", e);
        }
    }
}
