<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, com.freeuni.quiz.bean.UserAchievement, com.freeuni.quiz.bean.Achievement" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    boolean isAdmin = user.isAdmin();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Your Achievements</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background: #13081f;
            color: white;
        }
        .sidebar {
            width: 220px;
            background-color: #240955;
            color: white;
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            padding: 20px 10px;
        }
        .sidebar img {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            display: block;
            margin: 0 auto 10px;
            object-fit: cover;
            background-color: #ccc;
        }
        .sidebar .username {
            text-align: center;
            font-size: 14px;
            margin-bottom: 20px;
        }
        .sidebar a {
            display: block;
            padding: 10px 15px;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-bottom: 10px;
            background-color: rgba(255, 255, 255, 0.1);
        }
        .sidebar a:hover {
            background-color: rgba(255, 255, 255, 0.2);
        }
        .main-content {
            margin-left: 240px;
            padding: 20px;
        }
        h2 {
            color: #e0aaff;
            margin-bottom: 20px;
        }
        ul.achievements-list {
            list-style-type: none;
            padding: 0;
        }
        ul.achievements-list li {
            background: #1a0b2e;
            border-radius: 6px;
            padding: 15px;
            margin-bottom: 12px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.3);
            display: flex;
            align-items: center;
            gap: 15px;
        }
        ul.achievements-list img {
            border-radius: 50%;
            width: 50px;
            height: 50px;
            object-fit: contain;
        }
        .achievement-info {
            flex-grow: 1;
        }
        .achievement-name {
            font-weight: bold;
            font-size: 1.1em;
            margin-bottom: 4px;
            color: #ffffff;
        }
        .achievement-description {
            margin-bottom: 6px;
            color: #b19cd9;
        }
        .awarded-at {
            font-size: 0.85em;
            color: #999;
        }
        .no-achievements {
            font-style: italic;
            color: #b19cd9;
            margin-top: 20px;
            text-align: center;
            padding: 40px;
        }
    </style>
</head>
<body>

<div class="sidebar">
    <% if (user.getImageURL() != null && !user.getImageURL().isEmpty()) { %>
    <img src="<%= user.getImageURL() %>" alt="Profile Image">
    <% } else { %>
    <img src="https://via.placeholder.com/100" alt="No Image">
    <% } %>
    <div class="username"><%= user.getUserName() %></div>
    
    <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
    <a href="#" onclick="showAnnouncements()">📢 Announcements</a>
    <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
    <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
    <a href="${pageContext.request.contextPath}/achievements" style="background-color: rgba(255, 255, 255, 0.2);">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>
    <% if (isAdmin) { %>
    <a href="${pageContext.request.contextPath}/admin">🛠️ Admin Panel</a>
    <% } %>
</div>

<div class="main-content">

<h2>Your Achievements</h2>

<%
    List<UserAchievement> achievements = (List<UserAchievement>) request.getAttribute("achievements");
    if (achievements == null || achievements.isEmpty()) {
%>
<p class="no-achievements">You have no achievements yet.</p>
<%
} else {
%>
<ul class="achievements-list">
    <% for (UserAchievement ua : achievements) {
        Achievement a = ua.getAchievement();
    %>
    <li>
        <img src="<%= a.getIconUrl() %>" alt="<%= a.getName() %>" />
        <div class="achievement-info">
            <div class="achievement-name"><%= a.getName() %></div>
            <div class="achievement-description"><%= a.getDescription() %></div>
            <div class="awarded-at">Awarded at: <%= ua.getAwardedAt() %></div>
        </div>
    </li>
    <% } %>
</ul>
<%
    }
%>

</div>
</body>
</html>

