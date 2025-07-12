<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
  UserDTO user = (UserDTO) request.getAttribute("user");
  UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
  boolean isOwner = Boolean.TRUE.equals(request.getAttribute("isOwner"));
  boolean isAdmin = currentUser != null && currentUser.isAdmin();
%>
<!DOCTYPE html>
<html>
<head>
  <title><%= user.getUserName() %>'s Profile</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
  <style>
    body {
      font-family: Arial, sans-serif;
      background: #f0f0f0;
      margin: 0;
      padding: 0;
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
      padding: 2rem;
    }
    .profile-container {
      background: white;
      padding: 2rem;
      border-radius: 10px;
      max-width: 500px;
      margin: auto;
      box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }
    .profile-image {
      width: 150px;
      height: 150px;
      object-fit: cover;
      border-radius: 50%;
      display: block;
      margin: 0 auto;
      background-color: #ddd;
      color: #888;
      font-weight: bold;
      font-size: 16px;
      text-align: center;
      line-height: 150px;
    }
    .profile-info p strong {
      display: inline-block;
      width: 90px;
    }
    .button-container {
      margin-top: 2rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
    }
  </style>
</head>
<body>
<% if (currentUser != null) { %>
<div class="sidebar">
  <% if (currentUser.getImageURL() != null && !currentUser.getImageURL().isEmpty()) { %>
  <img src="<%= currentUser.getImageURL() %>" alt="Profile Image">
  <% } else { %>
  <img src="https://via.placeholder.com/100" alt="No Image">
  <% } %>
  <div class="username"><%= currentUser.getUserName() %></div>
  
  <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
  <a href="#" onclick="showAnnouncements()">📢 Announcements</a>
  <a href="${pageContext.request.contextPath}/profile" style="background-color: rgba(255, 255, 255, 0.2);">👤 Profile</a>
  <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
  <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
  <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
  <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
  <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
  <a href="${pageContext.request.contextPath}/history">📊 History</a>
  <% if (isAdmin) { %>
  <a href="${pageContext.request.contextPath}/admin">🛠️ Admin Panel</a>
  <% } %>
</div>
<% } %>

<div class="main-content">
<div class="profile-container">
  <% if (user.getImageURL() != null && !user.getImageURL().isEmpty()) { %>
  <img class="profile-image" src="<%= user.getImageURL() %>" alt="Profile Image" />
  <% } else { %>
  <div class="profile-image">No Image</div>
  <% } %>
  <h2><%= user.getUserName() %></h2>
  <div class="profile-info">
    <p><strong>Username:</strong> <%= user.getUserName() %></p>
    <p><strong>First Name:</strong> <%= user.getFirstName() %></p>
    <p><strong>Last Name:</strong> <%= user.getLastName() %></p>
    <p><strong>Email:</strong> <%= user.getEmail() %></p>
    <% if (user.getBio() != null && !user.getBio().isEmpty()) { %>
    <p><strong>About:</strong> <%= user.getBio() %></p>
    <% } %>
  </div>
  <div class="button-container">
    <a href="home" class="btn btn-send">← Back to Homepage</a>
    <% if (isOwner) { %>
    <a href="edit-profile" class="btn btn-edit">Edit Profile</a>
    <a href="logout" class="btn btn-logout">Logout</a>
    <% } else if (Boolean.TRUE.equals(request.getAttribute("areFriends"))) { %>
    <span class="status-message success friend-status-msg">You and <%= user.getUserName() %> are friends ✓</span>
    <form class="unfriend-form" data-user-id="<%= user.getId() %>">
      <button type="submit" class="btn btn-unfriend">Remove Friend</button>
    </form>
    <form action="<%= request.getContextPath() %>/chat" method="get" style="margin-top: 1rem;">
      <input type="hidden" name="with" value="<%= user.getId() %>">
      <button type="submit" class="btn btn-send">Message</button>
    </form>
    <% } else if (request.getAttribute("incomingRequest") != null) { %>
    <form class="friend-response-form"
          data-sender-id="<%= user.getId() %>"
          data-request-id="<%= request.getAttribute("requestId") %>">
      <button type="submit" name="action" value="accept" class="btn btn-accept">Accept</button>
      <button type="submit" name="action" value="decline" class="btn btn-decline">Decline</button>
    </form>
    <% } else if (request.getAttribute("requestSent") != null) { %>
    <button class="btn btn-disabled" disabled>Request Sent ✓</button>
    <% } else if (session.getAttribute("user") != null) { %>
    <form class="friend-request-form" data-receiver-id="<%= user.getId() %>">
      <button type="submit" class="btn btn-send">Send Friend Request</button>
    </form>
    <% } else { %>
    <span class="status-message error">You must be logged in to send friend requests.</span>
    <% } %>
  </div>
</div>
</div>
<script>
  window.contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/respondToFriendRequest.js"></script>
<script src="${pageContext.request.contextPath}/js/sendFriendRequest.js"></script>
<script src="${pageContext.request.contextPath}/js/removeFriend.js"></script>
</body>
</html>