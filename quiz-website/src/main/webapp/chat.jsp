<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
  UserDTO user = (UserDTO) session.getAttribute("user");
  if (user == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  int userId = user.getId();
  UserDTO withUser = (UserDTO) request.getAttribute("withUser");
  if (withUser == null) {
    throw new IllegalArgumentException("Missing 'withUser' Attribute.");
  }

%>

<html>
<head>
  <title>Chat</title>
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
    .chat-container {
      background-color: #1a0b2e;
      padding: 20px;
      border-radius: 10px;
      max-width: 800px;
      margin: 0 auto;
    }
    .profile-image {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #ddd;
      margin-bottom: 1rem;
    }
    #messages {
      border: 1px solid #444;
      padding: 10px;
      height: 400px;
      overflow-y: scroll;
      display: flex;
      flex-direction: column;
      background-color: #240955;
      border-radius: 5px;
    }
    .message {
      margin: 5px 0;
      padding: 6px 10px;
      border-radius: 10px;
      max-width: 60%;
    }
    .outgoing {
      align-self: flex-end;
      background-color: #10b981;
      color: white;
    }
    .incoming {
      align-self: flex-start;
      background-color: #4b3d6e;
      color: white;
    }
    #message-form {
      margin-top: 10px;
      display: flex;
    }
    #message-input {
      flex-grow: 1;
      padding: 8px;
      background-color: #240955;
      border: 1px solid #444;
      border-radius: 5px;
      color: white;
    }
    button {
      padding: 8px 12px;
      background-color: #10b981;
      color: white;
      border: none;
      border-radius: 5px;
      margin-left: 10px;
      cursor: pointer;
    }
    button:hover {
      background-color: #059669;
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
  <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
  <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
  <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
  <a href="#">🏆 Achievements</a>
  <a href="${pageContext.request.contextPath}/inbox" style="background-color: rgba(255, 255, 255, 0.2);">💬 Messages</a>
  <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
  <a href="${pageContext.request.contextPath}/history">📊 History</a>
</div>

<div class="main-content">
  <div class="chat-container">
    <h2>Chat with <%= withUser.getUserName() %></h2>
<img class="profile-image"
     src="<%= (withUser.getImageURL() != null && !withUser.getImageURL().isEmpty())
              ? withUser.getImageURL()
              : "https://via.placeholder.com/40" %>"
     alt="Profile Image" />


<div id="messages" data-other-user-id="<%= withUser.getId() %>">
</div>

<form id="message-form">
  <input type="text" id="message-input" name="content" placeholder="Type a message..." required />
  <button type="submit">Send</button>
</form>

  </div>
</div>

<script>
  window.contextPath = '${pageContext.request.contextPath}';
  window.chatConfig = {
    userId: <%= userId %>,
    otherUserId: <%= withUser.getId() %>
  };
</script>

<script src="js/chat.js"></script>
</body>
</html>

