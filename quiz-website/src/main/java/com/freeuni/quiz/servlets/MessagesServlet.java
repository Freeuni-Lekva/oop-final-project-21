package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DAO.impl.MessageDAOImpl;
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
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/messages")
public class MessagesServlet extends HttpServlet {

    private MessageDAOImpl messageDAO;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        messageDAO = new MessageDAOImpl(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO user = (UserDTO) req.getSession().getAttribute("user");
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Not logged in");
            return;
        }

        String otherUserIdStr = req.getParameter("userId");
        if (otherUserIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing userId parameter");
            return;
        }

        int currentUserId = user.getId();
        int otherUserId = Integer.parseInt(otherUserIdStr);

        List<Message> messages;
        String beforeStr = req.getParameter("before");
        String beforeIdStr = req.getParameter("beforeId");

        try {
            if (beforeStr != null && beforeIdStr != null) {
                LocalDateTime beforeTime = LocalDateTime.parse(beforeStr);
                Long beforeId = Long.parseLong(beforeIdStr);
                messages = messageDAO.getMessagesBefore(currentUserId, otherUserId, beforeTime, beforeId);
            } else {
                messages = messageDAO.getRecentMessages(currentUserId, otherUserId);
            }

            resp.setContentType("application/json");
            String json = gson.toJson(messages);
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error fetching messages");
            e.printStackTrace();
        }
    }

}
