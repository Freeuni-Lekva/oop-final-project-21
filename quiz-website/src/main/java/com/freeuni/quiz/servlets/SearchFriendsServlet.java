package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.FriendshipService;
import com.freeuni.quiz.service.UserService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search-friends")
public class SearchFriendsServlet extends HttpServlet {
    private FriendshipService friendshipService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        friendshipService = new FriendshipService(dataSource);
        userService = new UserService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String query = req.getParameter("q");
        if (query == null || query.trim().length() < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            List<Integer> friendIds = friendshipService.getFriendsOfUser(currentUser.getId());

            List<UserDTO> friends = new ArrayList<>();
            for (Integer friendId : friendIds) {
                UserDTO friend = userService.findById(friendId);
                if (friend != null) {
                    friends.add(friend);
                }
            }

            List<UserDTO> matchingFriends = new ArrayList<>();
            String searchQuery = query.toLowerCase().trim();

            for (UserDTO friend : friends) {
                if (friend.getUserName().toLowerCase().contains(searchQuery) ||
                        friend.getFirstName().toLowerCase().contains(searchQuery) ||
                        friend.getLastName().toLowerCase().contains(searchQuery)) {
                    matchingFriends.add(friend);
                }
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            Gson gson = new Gson();
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(matchingFriends));
            out.flush();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}