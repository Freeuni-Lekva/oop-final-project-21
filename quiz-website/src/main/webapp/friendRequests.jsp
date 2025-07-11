<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.bean.FriendshipRequest" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%
    LinkedHashMap<FriendshipRequest, UserDTO> requestsWithSenders =
            (LinkedHashMap<FriendshipRequest, UserDTO>) request.getAttribute("requestsWithSenders");
    
    UserDTO user = (UserDTO) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Friend Requests</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
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
        .container {
            max-width: 600px;
            margin: auto;
            background: #1a0b2e;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.3);
        }
        .request-card {
            display: flex;
            align-items: center;
            padding: 15px;
            border-bottom: 1px solid #444;
            background-color: #240955;
            margin-bottom: 10px;
            border-radius: 5px;
        }
        .request-card:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }
        .profile-img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 15px;
        }
        .sender-info {
            flex: 1;
        }
        .sender-info h3 {
            margin: 0;
            font-size: 18px;
            color: white;
        }
        .sender-info p {
            margin: 2px 0;
            color: #b19cd9;
        }
        .no-requests {
            text-align: center;
            color: #b19cd9;
            font-size: 16px;
            padding: 2rem 0;
        }
        h2 {
            color: #e0aaff;
            margin-bottom: 20px;
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
    <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests" style="background-color: rgba(255, 255, 255, 0.2);">👋 Friend Requests</a>
    <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
    <a href="#">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>
</div>

<div class="main-content">
    <div class="container">
    <h2>Friend Requests</h2>
    <% if (requestsWithSenders == null || requestsWithSenders.isEmpty()) { %>
    <div class="no-requests">You have no incoming friend requests.</div>
    <% } else {
        for (Map.Entry<FriendshipRequest, UserDTO> entry : requestsWithSenders.entrySet()) {
            UserDTO sender = entry.getValue();
    %>
    <div class="request-card">
        <img class="profile-img" src="<%= sender.getImageURL() != null && !sender.getImageURL().isEmpty()
        ? sender.getImageURL()
        : "https://via.placeholder.com/60" %>" alt="Sender Image">
        <div class="sender-info">
            <h3><%= sender.getUserName() %></h3>
            <p><%= sender.getFirstName() %> <%= sender.getLastName() %></p>
            <form class="friend-response-form"
                  data-sender-id="<%= sender.getId() %>"
                  data-request-id="<%= entry.getKey().getId() %>">
                <button type="submit" name="action" value="accept" class="btn btn-accept">Accept</button>
                <button type="submit" name="action" value="decline" class="btn btn-decline">Decline</button>
            </form>
        </div>
    </div>
    <% }} %>
    </div>
</div>

<script>
    window.contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/respondToFriendRequest.js"></script>
</body>
</html>


