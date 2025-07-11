package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.MessageDAO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Message;
import com.freeuni.quiz.service.MessageService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

@WebServlet("/inbox")
public class MessageInboxServlet extends HttpServlet {

    private MessageService messageService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        messageService = new MessageService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        UserDTO currentUser = (UserDTO) req.getSession().getAttribute("user");

        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            LinkedHashMap<Message, UserDTO> conversations=messageService.getConversationsWithProfileDetails(currentUser.getId());
            req.setAttribute("conversations", conversations);
            req.setAttribute("currentUserId", currentUser.getId());
            req.getRequestDispatcher("/messagesInbox.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error retrieving inbox conversations", e);
        }
    }
}
