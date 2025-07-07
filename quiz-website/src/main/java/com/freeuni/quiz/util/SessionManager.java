package com.freeuni.quiz.util;

import com.freeuni.quiz.bean.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {
    
    private static final String USER_SESSION_KEY = "currentUser";
    
    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(USER_SESSION_KEY);
        }
        return null;
    }
    
    public void setCurrentUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);
    }
    
    public void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    public boolean isUserLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
} 