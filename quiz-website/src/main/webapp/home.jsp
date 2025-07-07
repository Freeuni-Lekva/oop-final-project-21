<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Home - Quiz App</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
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

        .content {
            margin-left: 240px;
            padding: 20px;
        }
        .search-bar {
            width: 100%;
            text-align: center;
            margin-bottom: 30px;
        }
        .search-bar input {
            width: 60%;
            padding: 12px;
            font-size: 16px;
            border-radius: 25px;
            border: 1px solid #ccc;
        }

        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            grid-template-rows: 300px 300px;
            gap: 20px;
        }

        .box {
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            display: flex;
            flex-direction: column;
        }

        .box h2 {
            text-align: center;
            margin-bottom: 15px;
        }

        .box-content {
            flex-grow: 1;
            color: #555;
        }

        .wide-box {
            grid-column: span 2;
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

    <a href="profile">Profile</a>
    <a href="#">Friend Requests</a>
    <a href="#">Messages</a>
    <a href="#">Challenges</a>
    <a href="#">History</a>
    <a href="#">Achievements</a>
    <a href="#">Create Quiz</a>
</div>

<div class="content">
    <div class="search-bar" style="text-align:center; position: relative;">
        <select id="search-type" name="type"
                style="padding: 10px; font-size: 16px; border-radius: 25px;">
            <option value="users">Users</option>
            <option value="quizzes">Quizzes</option>
        </select>

        <div style="display: inline-block; position: relative;">
            <input
                    id="search-input"
                    name="query"
                    type="text"
                    placeholder="Search..."
                    style="width: 300px; padding: 10px; font-size: 16px; border-radius: 25px; border: 1px solid #ccc;"
                    autocomplete="off"
            >

            <ul
                    id="search-results"
                    style="display: none;
                   position: absolute;
                   top: 110%;
                   left: 0;
                   width: 100%;
                   background: white;
                   border: 1px solid #ccc;
                   border-radius: 5px;
                   list-style: none;
                   padding: 0;
                   margin: 5px 0 0 0;
                   z-index: 1000;
                   max-height: 200px;
                   overflow-y: auto;">
            </ul>
        </div>
    </div>

    <div class="grid">
        <div class="box">
            <h2>Popular Quizzes</h2>
            <div class="box-content">
                <!-- Popular quiz content here -->
                <p>Example: Java Basics Quiz - 98% success rate</p>
            </div>
        </div>
        <div class="box">
            <h2>Recent Quizzes</h2>
            <div class="box-content">
                <!-- Recent quiz content here -->
                <p>Example: Python Loops Quiz added 2 hours ago</p>
            </div>
        </div>
        <div class="box wide-box">
            <h2>See what your friends are up to</h2>
            <div class="box-content">
                <!-- Friend activity -->
                <p><strong>alice123</strong> completed <em>HTML Basics Quiz</em></p>
                <p><strong>bob_the_dev</strong> completed <em>JavaScript Challenge</em></p>
            </div>
        </div>
    </div>
</div>
<script src="js/search.js?v=2"></script>
</body>
</html>
