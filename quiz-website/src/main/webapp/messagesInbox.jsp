<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.bean.Message" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>

<%
  LinkedHashMap<Message, UserDTO> conversations =
          (LinkedHashMap<Message, UserDTO>) request.getAttribute("conversations");
  int currentUserId = (Integer) request.getAttribute("currentUserId");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Inbox</title>
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
    .inbox-container {
      background-color: #1a0b2e;
      padding: 20px;
      border-radius: 10px;
      max-width: 800px;
      margin: 0 auto;
    }
    .conversation {
      padding: 15px;
      border-bottom: 1px solid #444;
      display: flex;
      align-items: center;
      background-color: #240955;
      margin-bottom: 10px;
      border-radius: 5px;
    }
    .conversation a {
      text-decoration: none;
      color: white;
      display: block;
      flex-grow: 1;
    }
    .conversation:hover {
      background-color: #2d1065;
    }
    .preview {
      color: #b19cd9;
      font-size: 0.9em;
    }
    .profile-image {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 10px;
    }
    h2 {
      color: #e0aaff;
      margin-bottom: 20px;
    }
    .no-messages {
      text-align: center;
      color: #b19cd9;
      padding: 40px;
    }
  </style>
</head>
<body>

<%
  UserDTO currentUser = (UserDTO) session.getAttribute("user");
  if (currentUser == null) {
    response.sendRedirect("login.jsp");
    return;
  }
%>

<div class="sidebar">
  <% if (currentUser.getImageURL() != null && !currentUser.getImageURL().isEmpty()) { %>
  <img src="<%= currentUser.getImageURL() %>" alt="Profile Image">
  <% } else { %>
  <img src="https://via.placeholder.com/100" alt="No Image">
  <% } %>
  <div class="username"><%= currentUser.getUserName() %></div>
  
  <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
  <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
  <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
  <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
  <a href="#">🏆 Achievements</a>
  <a href="${pageContext.request.contextPath}/inbox" style="background-color: rgba(255, 255, 255, 0.2);">💬 Messages</a>
  <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>

<div class="main-content">
  <div class="inbox-container">
    <h2>Your Messages</h2>

<% if (conversations == null || conversations.isEmpty()) { %>
<div class="no-messages">You have no conversations yet.</div>
<% } else { %>
<% for (Map.Entry<Message, UserDTO> entry : conversations.entrySet()) {
  Message msg = entry.getKey();
  UserDTO otherUser = entry.getValue();
%>
<div class="conversation">
  <% if (otherUser.getImageURL() != null && !otherUser.getImageURL().isEmpty()) { %>
  <img class="profile-image" src="<%= otherUser.getImageURL() %>" alt="Profile" />
  <% } else { %>
  <div class="profile-image" style="background-color: #ccc; text-align: center; line-height: 40px;">?</div>
  <% } %>

  <a href="<%= request.getContextPath() %>/chat?with=<%= otherUser.getId() %>">
    <strong><%= otherUser.getUserName() %></strong><br/>
    <span class="preview"><%= msg.getContent() %></span>
  </a>
</div>
<% } %>
<% } %>

  </div>
</div>

</body>
</html>

