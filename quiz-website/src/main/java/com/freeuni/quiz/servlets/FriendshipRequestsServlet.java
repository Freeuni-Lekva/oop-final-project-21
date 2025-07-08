package com.freeuni.quiz.servlets;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.FriendshipRequest;
import com.freeuni.quiz.service.FriendshipRequestService;
import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@WebServlet("/friendshipRequests")
public class FriendshipRequestsServlet extends HttpServlet {
    private FriendshipRequestService friendshipRequestService;
    private UserService userService;
    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        friendshipRequestService = new FriendshipRequestService(dataSource);
        userService = new UserService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
        Integer userId = (currentUser != null) ? currentUser.getId() : null;

        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            LinkedHashMap<FriendshipRequest, UserDTO> requestsWithSenders = new LinkedHashMap<>();

            List<FriendshipRequest> incomingRequests = friendshipRequestService.getRequestsReceivedByUser(userId);

            for (FriendshipRequest request : incomingRequests) {
                UserDTO sender = userService.findById(request.getRequestSenderId());
                requestsWithSenders.put(request, sender);
            }

            req.setAttribute("requestsWithSenders", requestsWithSenders);
            req.getRequestDispatcher("friendRequests.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Failed to load friend requests", e);
        }
    }
}

