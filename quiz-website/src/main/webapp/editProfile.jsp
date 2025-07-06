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
            background: #f0f0f0;
            padding: 2rem;
        }
        .edit-container {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            max-width: 500px;
            margin: auto;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
            margin-bottom: 1.5rem;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            font-weight: bold;
            margin-top: 1rem;
            margin-bottom: 0.5rem;
        }
        input[type="text"],
        input[type="email"],
        textarea {
            padding: 8px;
            font-size: 14px;
            border-radius: 4px;
            border: 1px solid #ccc;
            box-sizing: border-box;
            width: 100%;
        }
        /* Fix: disable resizing on textarea */
        textarea {
            resize: none;
            height: 80px;
        }
        button {
            margin-top: 2rem;
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #0056b3;
        }
        .error {
            color: red;
            margin-top: 1rem;
            text-align: center;
        }
    </style>
</head>
<body>
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
</body>
</html>
