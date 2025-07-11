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
    body { font-family: sans-serif; padding: 20px; }
    .conversation { padding: 10px; border-bottom: 1px solid #ccc; display: flex; align-items: center; }
    .conversation a { text-decoration: none; color: black; display: block; flex-grow: 1; }
    .conversation:hover { background-color: #f2f2f2; }
    .preview { color: gray; font-size: 0.9em; }
    .profile-image {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
      margin-right: 10px;
    }
  </style>
</head>
<body>

<h2>Your Messages</h2>

<% if (conversations == null || conversations.isEmpty()) { %>
<p>You have no conversations yet.</p>
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

</body>
</html>

