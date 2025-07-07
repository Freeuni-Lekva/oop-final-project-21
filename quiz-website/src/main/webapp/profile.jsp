<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
  UserDTO user = (UserDTO) request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
  <title><%= user.getUserName() %>'s Profile</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background: #f0f0f0;
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
      line-height: 150px; /* vertical center text */
    }
    .no-image-placeholder {
      cursor: default;
    }
    h2 {
      margin-top: 1rem;
      text-align: center;
    }
    .profile-info {
      margin-top: 1rem;
      max-width: 350px;
      margin-left: auto;
      margin-right: auto;
    }
    .profile-info p {
      margin: 6px 0;
      font-size: 16px;
    }
    .profile-info p strong {
      display: inline-block;
      width: 90px; /* align all labels on same x */
    }
    .profile-info p.about {
      color: #555;
    }
    .button-container {
      margin-top: 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .btn {
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 14px;
      text-decoration: none;
      text-align: center;
      display: inline-block;
      transition: background-color 0.3s;
    }
    .btn-edit {
      background-color: #007bff;
      color: white;
    }
    .btn-edit:hover {
      background-color: #0056b3;
    }
    .btn-logout {
      background-color: #dc3545;
      color: white;
    }
    .btn-logout:hover {
      background-color: #c82333;
    }
  </style>
</head>
<body>
<div class="profile-container">
  <% if (user.getImageURL() != null && !user.getImageURL().isEmpty()) { %>
  <img class="profile-image" src="<%= user.getImageURL() %>" alt="Profile Image" />
  <% } else { %>
  <div class="profile-image no-image-placeholder">No Image</div>
  <% } %>

  <!-- Show username on top -->
  <h2><%= user.getUserName() %></h2>

  <div class="profile-info">
    <p><strong>Username:</strong> <%= user.getUserName() %></p>
    <p><strong>First Name:</strong> <%= user.getFirstName() %></p>
    <p><strong>Last Name:</strong> <%= user.getLastName() %></p>
    <p><strong>Email:</strong> <%= user.getEmail() %></p>

    <% if (user.getBio() != null && !user.getBio().isEmpty()) { %>
    <p class="about"><strong>About:</strong> <%= user.getBio() %></p>
    <% } %>
  </div>

  <div class="button-container">
    <a href="home.jsp" class="btn btn-home">← Back to Homepage</a>
    <a href="edit-profile" class="btn btn-edit">Edit Profile</a>
    <a href="logout" class="btn btn-logout">Logout</a>
  </div>
</div>
</body>
</html>
