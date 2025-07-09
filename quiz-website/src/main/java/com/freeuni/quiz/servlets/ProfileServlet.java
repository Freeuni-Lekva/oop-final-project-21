package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.FriendshipRequest;
import com.freeuni.quiz.service.FriendshipRequestService;
import com.freeuni.quiz.service.FriendshipService;
import com.freeuni.quiz.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserService userService;
    private FriendshipRequestService friendshipRequestService;
    private FriendshipService friendshipService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
        friendshipRequestService = new FriendshipRequestService(dataSource);
        friendshipService = new FriendshipService(dataSource);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        String usernameParam = req.getParameter("user");

        try {
            UserDTO profileUser;

            if (usernameParam == null || usernameParam.trim().isEmpty()) {
                if (currentUser == null) {
                    resp.sendRedirect("login.jsp");
                    return;
                }
                profileUser = currentUser;
            } else {
                profileUser = userService.findByUsername(usernameParam);
                if (profileUser == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
            }

            boolean isOwner = currentUser != null && currentUser.getUserName().equals(profileUser.getUserName());
            req.setAttribute("isOwner", isOwner);
            if (!isOwner && currentUser != null) {
                FriendshipRequest requestSentByMe = friendshipRequestService.getRequest(currentUser.getId(), profileUser.getId());
                FriendshipRequest requestSentToMe = friendshipRequestService.getRequest(profileUser.getId(), currentUser.getId());

                if (requestSentToMe != null) {
                    req.setAttribute("requestId", requestSentToMe.getId());
                    req.setAttribute("incomingRequest", true);
                } else if (requestSentByMe != null) {
                    req.setAttribute("requestSent", true);
                }
                boolean areFriends=friendshipService.areFriends(currentUser.getId(), profileUser.getId());
                req.setAttribute("areFriends", areFriends);
            }

            req.setAttribute("user", profileUser);
            req.getRequestDispatcher("profile.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Failed to load profile", e);
        }
    }


}
