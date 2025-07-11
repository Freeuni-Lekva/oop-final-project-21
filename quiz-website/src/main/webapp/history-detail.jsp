<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.QuizCompletion" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Map<Quiz, List<QuizCompletion>> quizCompletionsMap =
            (Map<Quiz, List<QuizCompletion>>) request.getAttribute("quizCompletionsMap");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    boolean isAdmin = user.isAdmin();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz History Details - Quiz App</title>
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

        .quiz-card {
            background-color: #240955;
            border-radius: 10px;
            margin-bottom: 30px;
            overflow: hidden;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }

        .quiz-header {
            background: linear-gradient(45deg, #6d28d9, #4f46e5);
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .quiz-title {
            font-size: 20px;
            font-weight: bold;
            color: white;
            margin: 0;
        }

        .quiz-category {
            background-color: #8b5cf6;
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 12px;
            color: white;
        }

        .attempts-table {
            width: 100%;
            border-collapse: collapse;
        }

        .attempts-table th, .attempts-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #4c3a85;
        }

        .attempts-table th {
            background-color: #3a2480;
            color: white;
        }

        .attempts-table tr:hover {
            background-color: #2c1c60;
        }

        .score-cell {
            font-weight: bold;
        }

        .improved {
            color: #10b981;
        }

        .worse {
            color: #ef4444;
        }

        .same {
            color: #f59e0b;
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
        📊 Detailed Quiz History
    </div>

    <div class="view-options">
        <a href="${pageContext.request.contextPath}/history" class="view-btn">All History</a>
        <a href="${pageContext.request.contextPath}/history?view=summary" class="view-btn">Summary View</a>
        <a href="${pageContext.request.contextPath}/history?view=detail" class="view-btn" style="background: linear-gradient(45deg, #6d28d9, #4f46e5);">Detailed View</a>
    </div>

    <% if (quizCompletionsMap != null && !quizCompletionsMap.isEmpty()) { %>
    <% for (Map.Entry<Quiz, List<QuizCompletion>> entry : quizCompletionsMap.entrySet()) {
        Quiz quiz = entry.getKey();
        List<QuizCompletion> completions = entry.getValue();

        if (quiz != null && completions != null && !completions.isEmpty()) { %>
    <div class="quiz-card">
        <div class="quiz-header">
            <h2 class="quiz-title"><%= quiz.getTestTitle() %></h2>
            <span class="quiz-category"><%= quiz.getCategoryId() != null ? quiz.getCategoryId() : "N/A" %></span>
        </div>

        <table class="attempts-table">
            <thead>
            <tr>
                <th>Attempt #</th>
                <th>Score</th>
                <th>Completion %</th>
                <th>Time Taken</th>
                <th>Date</th>
                <th>Progress</th>
            </tr>
            </thead>
            <tbody>
            <%
                double previousScore = 0;
                for (int i = 0; i < completions.size(); i++) {
                    QuizCompletion completion = completions.get(i);
                    String progressClass = "same";
                    String progressIcon = "➡️";
                    double currentScore = completion.getCompletionPercentage().doubleValue();

                    if (i > 0) {
                        double prevCompletionScore = completions.get(i-1).getCompletionPercentage().doubleValue();
                        if (currentScore > prevCompletionScore) {
                            progressClass = "improved";
                            progressIcon = "📈";
                        } else if (currentScore < prevCompletionScore) {
                            progressClass = "worse";
                            progressIcon = "📉";
                        }
                    }
            %>
            <tr>
                <td><%= completions.size() - i %></td>
                <td class="score-cell"><%= String.format("%.1f", completion.getFinalScore()) %>/<%= String.format("%.1f", completion.getTotalPossible()) %></td>
                <td class="score-cell"><%= String.format("%.1f", completion.getCompletionPercentage().doubleValue()) %>%</td>
                <td><%= completion.getTotalTimeMinutes() %> min</td>
                <td><%= completion.getFinishedAt().format(formatter) %></td>
                <td class="<%= progressClass %>">
                    <%= progressIcon %>
                    <% if (i == 0) { %>
                    First attempt
                    <% } else {
                        double diff = currentScore - completions.get(i-1).getCompletionPercentage().doubleValue();
                    %>
                    <%= String.format("%+.1f%%", diff) %>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
    <% } %>
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