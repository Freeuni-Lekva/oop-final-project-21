<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
    UserDTO user = (UserDTO) request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Profile - <%= user.getUserName() %></title>
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
        .edit-container {
            background: #1a0b2e;
            padding: 2rem;
            border-radius: 10px;
            max-width: 500px;
            margin: auto;
            box-shadow: 0 0 10px rgba(0,0,0,0.3);
        }
        h2 {
            text-align: center;
            margin-bottom: 1.5rem;
            color: #e0aaff;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            font-weight: bold;
            margin-top: 1rem;
            margin-bottom: 0.5rem;
            color: #d1d8ff;
        }
        input[type="text"],
        input[type="email"],
        textarea {
            padding: 8px;
            font-size: 14px;
            border-radius: 4px;
            border: 1px solid #444;
            box-sizing: border-box;
            width: 100%;
            background-color: #240955;
            color: white;
        }
        /* Fix: disable resizing on textarea */
        textarea {
            resize: none;
            height: 80px;
        }
        button {
            margin-top: 2rem;
            padding: 10px;
            background-color: #10b981;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #059669;
        }
        .error {
            color: red;
            margin-top: 1rem;
            text-align: center;
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
    <a href="${pageContext.request.contextPath}/profile" style="background-color: rgba(255, 255, 255, 0.2);">👤 Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
    <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
    <a href="#">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>
</div>

<div class="main-content">
    <div class="edit-container">
    <h2>Edit Profile - <%= user.getUserName() %></h2>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="edit-profile" method="post">
        <label for="firstName">First Name</label>
        <input type="text" id="firstName" name="firstName" value="<%= user.getFirstName() %>" required />

        <label for="lastName">Last Name</label>
        <input type="text" id="lastName" name="lastName" value="<%= user.getLastName() %>" required />

        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="<%= user.getEmail() %>" required />

        <label for="imageURL">Image URL</label>
        <input type="text" id="imageURL" name="imageURL" value="<%= user.getImageURL() != null ? user.getImageURL() : "" %>" />

        <label for="bio">Bio</label>
        <textarea id="bio" name="bio"><%= user.getBio() != null ? user.getBio() : "" %></textarea>

        <button type="submit">Update Profile</button>
    </form>
    </div>
</div>
</body>
</html>
