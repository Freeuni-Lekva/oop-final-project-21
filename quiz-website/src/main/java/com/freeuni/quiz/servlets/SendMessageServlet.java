package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.MessageDAO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Message;
import com.freeuni.quiz.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/sendMessage")
public class SendMessageServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        messageDAO = new MessageDAO(dataSource);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserDTO sender = (UserDTO) session.getAttribute("user");
        int senderId = sender.getId();

        int receiverId = Integer.parseInt(req.getParameter("receiverId"));
        String content = req.getParameter("content");

        if (content == null || content.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Message cannot be empty.");
            return;
        }

        try {

            int id = messageDAO.sendMessage(senderId, receiverId, content);
            Message message = messageDAO.getMessageById(id);
            resp.setContentType("application/json");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            resp.getWriter().write(gson.toJson(message));

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error sending message.");
        }
    }

}
