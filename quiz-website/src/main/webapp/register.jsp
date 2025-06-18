<%--
  Created by IntelliJ IDEA.
  User: tmama
  Date: 6/18/2025
  Time: 3:56 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .register-container {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            width: 350px;
        }
        input[type="text"], input[type="password"], input[type="email"] {
            width: 100%;
            padding: 0.5rem;
            margin-bottom: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        textarea {
            width: 100%;
            padding: 0.5rem;
            margin-bottom: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            resize: vertical;
        }
        .error {
            color: red;
            font-size: 0.9em;
            margin-bottom: 1rem;
        }
        button {
            width: 100%;
            padding: 0.6rem;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            font-weight: bold;
        }
        button:hover {
            background: #218838;
        }
        .back-link {
            text-align: center;
            margin-top: 1rem;
        }
    </style>
</head>
<body>
<div class="register-container">
    <h2>Create Account</h2>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="register" method="post">
        <input type="text" name="username" placeholder="Username" required />
        <input type="password" name="password" placeholder="Password" required />
        <input type="text" name="firstName" placeholder="First Name" required />
        <input type="text" name="lastName" placeholder="Last Name" required />
        <input type="email" name="email" placeholder="Email" required />
        <input type="text" name="imageURL" placeholder="Image URL (optional)" />
        <textarea name="bio" placeholder="Tell us about yourself (optional)"></textarea>
        <button type="submit">Register</button>
    </form>

    <div class="back-link">
        <a href="login.jsp">Back to Login</a>
    </div>
</div>
</body>
</html>
