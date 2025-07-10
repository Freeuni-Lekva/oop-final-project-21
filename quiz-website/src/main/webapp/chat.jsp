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
    .profile-image {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #ddd;
      margin-bottom: 1rem;
    }
    #messages {
      border: 1px solid #ccc;
      padding: 10px;
      height: 400px;
      overflow-y: scroll;
      display: flex;
      flex-direction: column;
    }
    .message {
      margin: 5px 0;
      padding: 6px 10px;
      border-radius: 10px;
      max-width: 60%;
    }
    .outgoing {
      align-self: flex-end;
      background-color: #dcf8c6;
    }
    .incoming {
      align-self: flex-start;
      background-color: #f1f0f0;
    }
    #message-form {
      margin-top: 10px;
      display: flex;
    }
    #message-input {
      flex-grow: 1;
      padding: 8px;
    }
    button {
      padding: 8px 12px;
    }
  </style>
</head>
<body>
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

