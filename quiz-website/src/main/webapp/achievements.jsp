<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, com.freeuni.quiz.bean.UserAchievement, com.freeuni.quiz.bean.Achievement" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Your Achievements</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
            background: #f5f5f5;
        }
        h2 {
            color: #333;
        }
        ul.achievements-list {
            list-style-type: none;
            padding: 0;
        }
        ul.achievements-list li {
            background: white;
            border-radius: 6px;
            padding: 15px;
            margin-bottom: 12px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.1);
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
        }
        .achievement-description {
            margin-bottom: 6px;
            color: #555;
        }
        .awarded-at {
            font-size: 0.85em;
            color: #999;
        }
        .no-achievements {
            font-style: italic;
            color: #777;
            margin-top: 20px;
        }
    </style>
</head>
<body>

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

</body>
</html>

