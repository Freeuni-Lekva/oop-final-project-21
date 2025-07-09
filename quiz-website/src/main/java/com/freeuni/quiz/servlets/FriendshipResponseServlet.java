package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.FriendshipManager;
import com.freeuni.quiz.service.FriendshipRequestService;
import com.freeuni.quiz.service.FriendshipService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/respondToFriendRequest")
public class FriendshipResponseServlet extends HttpServlet {
    private FriendshipManager friendshipManager;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        FriendshipRequestService requestService = new FriendshipRequestService(dataSource);
        FriendshipService friendshipService = new FriendshipService(dataSource);
        friendshipManager = new FriendshipManager(requestService, friendshipService);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        String action = req.getParameter("action");
        String senderIdParam = req.getParameter("senderId");
        String requestIdParam = req.getParameter("requestId");

        if (action == null || senderIdParam == null || requestIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        int senderId;
        int requestId;
        int receiverId = currentUser.getId();

        try {
            senderId = Integer.parseInt(senderIdParam);
            requestId = Integer.parseInt(requestIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            return;
        }

        try {
            boolean success;
            if (action.equals("accept")) {
                success = friendshipManager.acceptFriendRequest(senderId, receiverId, requestId);
            } else if (action.equals("decline")) {
                success = friendshipManager.declineFriendRequest(senderId, receiverId, requestId);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
            }

            if (success) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\": \"Friend request " + action + "ed successfully\"}");
            } else {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Action could not be completed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

