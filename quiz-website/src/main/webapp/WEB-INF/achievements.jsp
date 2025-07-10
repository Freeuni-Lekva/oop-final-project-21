<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.AchievementDTO" %>
<%@ page import="java.util.List" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<AchievementDTO> achievements = (List<AchievementDTO>) request.getAttribute("achievements");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Achievements</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f0f0f0;
            margin: 0;
        }
        .container {
            margin-left: 240px;
            padding: 2rem;
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
        h2 {
            text-align: center;
            margin-bottom: 2rem;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 14px 18px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #240955;
            color: white;
        }
        .no-achievements {
            text-align: center;
            font-size: 16px;
            color: #666;
            margin-top: 2rem;
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

    <a href="${pageContext.request.contextPath}/profile">Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests">Friend Requests</a>
    <a href="#">Messages</a>
    <a href="#">Challenges</a>
    <a href="#">History</a>
    <a href="${pageContext.request.contextPath}/achievements">Achievements</a>
    <a href="#">Create Quiz</a>
</div>

<div class="container">
    <h2>Your Achievements</h2>
    <% if (achievements == null || achievements.isEmpty()) { %>
    <div class="no-achievements">You have not earned any achievements yet.</div>
    <% } else { %>
    <table>
        <thead>
        <tr>
            <th>Type</th>
            <th>Date Achieved</th>
        </tr>
        </thead>
        <tbody>
        <% for (AchievementDTO a : achievements) { %>
        <tr>
            <td><%= a.getType() %></td>
            <td><%= a.getAchievedAt().toLocalDate() %> <%= a.getAchievedAt().toLocalTime().withNano(0) %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>

</body>
</html>
