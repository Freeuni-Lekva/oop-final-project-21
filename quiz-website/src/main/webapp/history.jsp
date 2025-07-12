<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.UserHistoryDTO" %>
<%@ page import="com.freeuni.quiz.bean.QuizCompletion" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    UserHistoryDTO userHistory = (UserHistoryDTO) request.getAttribute("userHistory");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    boolean isAdmin = user.isAdmin();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz History - Quiz App</title>
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
            color: white;
        }

        .history-header {
            font-size: 28px;
            color: white;
            margin: 20px 0;
            text-align: center;
            padding: 15px;
            background: linear-gradient(45deg, #8b5cf6, #3b82f6);
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }

        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background-color: #240955;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .stat-value {
            font-size: 32px;
            font-weight: bold;
            margin: 10px 0;
            color: #a5b4fc;
        }

        .stat-label {
            font-size: 14px;
            color: #d1d5db;
        }

        .history-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: #1f1635;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .history-table th, .history-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #4c3a85;
        }

        .history-table th {
            background-color: #3a2480;
            color: white;
            font-weight: bold;
        }

        .history-table tr:hover {
            background-color: #2c1c60;
        }

        .score-cell {
            font-weight: bold;
        }

        .view-options {
            margin: 20px 0;
            text-align: center;
        }

        .view-btn {
            display: inline-block;
            padding: 10px 20px;
            margin: 0 10px;
            background: linear-gradient(45deg, #8b5cf6, #3b82f6);
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }

        .view-btn:hover {
            opacity: 0.9;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
        }

        .empty-history {
            text-align: center;
            padding: 40px;
            color: #a5b4fc;
            font-style: italic;
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
    <a href="#" onclick="showAnnouncements()">📢 Announcements</a>
    <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
    <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
    <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history" style="background-color: rgba(255, 255, 255, 0.2);">📊 History</a>
    <% if (isAdmin) { %>
    <a href="${pageContext.request.contextPath}/admin">🛠️ Admin Panel</a>
    <% } %>
</div>

<div class="content">
    <div class="history-header">
        📊 Your Quiz History
    </div>

    <div class="view-options">
        <a href="${pageContext.request.contextPath}/history" class="view-btn" style="background: linear-gradient(45deg, #6d28d9, #4f46e5);">All History</a>
        <a href="${pageContext.request.contextPath}/history?view=summary" class="view-btn">Summary View</a>
        <a href="${pageContext.request.contextPath}/history?view=detail" class="view-btn">Detailed View</a>
    </div>

    <% if (userHistory != null && userHistory.getCompletions() != null && !userHistory.getCompletions().isEmpty()) { %>
    <!-- Stats Overview -->
    <div class="stats-container">
        <div class="stat-card">
            <div class="stat-value"><%= userHistory.getTotalQuizzesTaken() %></div>
            <div class="stat-label">Quizzes Taken</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= String.format("%.1f", userHistory.getAverageScore()) %>%</div>
            <div class="stat-label">Average Score</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= userHistory.getBestScore() %>%</div>
            <div class="stat-label">Best Score</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= userHistory.getTotalTimeTaken() %></div>
            <div class="stat-label">Total Minutes</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= userHistory.getMostPlayedCategory() %></div>
            <div class="stat-label">Favorite Category</div>
        </div>
    </div>

    <!-- History Table -->
    <h2>Quiz Completion History</h2>
    <table class="history-table">
        <thead>
        <tr>
            <th>Quiz</th>
            <th>Category</th>
            <th>Score</th>
            <th>Completion %</th>
            <th>Time Taken</th>
            <th>Date Completed</th>
        </tr>
        </thead>
        <tbody>
        <% for (Map.Entry<QuizCompletion, Quiz> entry : userHistory.getCompletions().entrySet()) {
            QuizCompletion completion = entry.getKey();
            Quiz quiz = entry.getValue();
            if (quiz != null) { %>
        <tr>
            <td>
                <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= quiz.getId() %>"
                   style="color: #a5b4fc; text-decoration: none;">
                    <%= quiz.getTestTitle() %>
                </a>
            </td>
            <td><%= quiz.getCategoryId() != null ? quiz.getCategoryId() : "N/A" %></td>
            <td class="score-cell"><%= String.format("%.1f", completion.getFinalScore()) %>/<%= String.format("%.1f", completion.getTotalPossible()) %></td>
            <td class="score-cell"><%= String.format("%.1f", completion.getCompletionPercentage().doubleValue()) %>%</td>
            <td><%= completion.getTotalTimeMinutes() %> min</td>
            <td><%= completion.getFinishedAt().format(formatter) %></td>
        </tr>
        <% }
        } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="empty-history">
        <h2>No Quiz History Found</h2>
        <p>You haven't completed any quizzes yet. Start taking quizzes to build your history!</p>
        <a href="${pageContext.request.contextPath}/quiz-browser" class="view-btn" style="margin-top: 20px;">
            Browse Available Quizzes
        </a>
    </div>
    <% } %>
</div>

</body>
</html>