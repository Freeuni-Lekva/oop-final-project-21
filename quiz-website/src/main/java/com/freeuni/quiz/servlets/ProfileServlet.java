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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserService userService;
    private FriendshipRequestService friendshipRequestService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        userService = new UserService(dataSource);
        friendshipRequestService = new FriendshipRequestService(dataSource);
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
            Integer friendshipRequestId = null;

            if (currentUser != null && !currentUser.getUserName().equals(profileUser.getUserName())) {
                FriendshipRequest pendingRequest = friendshipRequestService.getRequest(profileUser.getId(), currentUser.getId());
                if (pendingRequest != null) {
                    friendshipRequestId = pendingRequest.getId();
                }
            }

            req.setAttribute("requestId", friendshipRequestId);

            req.setAttribute("user", profileUser);
            req.setAttribute("isOwner", currentUser != null && currentUser.getUserName().equals(profileUser.getUserName()));
            req.getRequestDispatcher("profile.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Failed to load profile", e);
        }
    }

}
