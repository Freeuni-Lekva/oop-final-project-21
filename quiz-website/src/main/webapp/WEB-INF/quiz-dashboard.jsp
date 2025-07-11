<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.QuizCompletion" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Quiz> userQuizzes = (List<Quiz>) request.getAttribute("createdQuizzes");
    Map<Integer, Integer> completionCounts = (Map<Integer, Integer>) request.getAttribute("completionCounts");
    Map<Integer, Double> averageScores = (Map<Integer, Double>) request.getAttribute("averageScores");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <title>My Quizzes - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
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
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background-color: #240955;
            padding: 20px;
            border-radius: 15px;
            text-align: center;
        }
        .stat-number {
            font-size: 36px;
            color: #b19cd9;
            font-weight: bold;
        }
        .stat-label {
            color: #d1d8ff;
            margin-top: 5px;
        }
        .quiz-table {
            width: 100%;
            background-color: #240955;
            border-radius: 15px;
            overflow: hidden;
        }
        .quiz-table th {
            background-color: #4b3d6e;
            color: #ffffff;
            padding: 15px;
            text-align: left;
        }
        .quiz-table td {
            padding: 15px;
            color: #d1d8ff;
            border-bottom: 1px solid #4b3d6e;
        }
        .quiz-table tr:last-child td {
            border-bottom: none;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        .action-btn {
            padding: 5px 10px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s ease;
        }
        .action-btn-edit {
            background-color: #6a5acd;
            color: white;
        }
        .action-btn-edit:hover {
            background-color: #5a4abd;
        }
        .action-btn-view {
            background-color: #28a745;
            color: white;
        }
        .action-btn-view:hover {
            background-color: #218838;
        }
        .action-btn-delete {
            background-color: #dc3545;
            color: white;
        }
        .action-btn-delete:hover {
            background-color: #c82333;
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
        <a href="${pageContext.request.contextPath}/quiz-manager" style="background-color: rgba(255, 255, 255, 0.2);">📊 My Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
        <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
        <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
        <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
        <a href="${pageContext.request.contextPath}/history">📊 History</a>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
                <h2>My Quizzes</h2>
                <a href="quiz-creator" class="quiz-btn quiz-btn-primary">
                    ➕ Create New Quiz
                </a>
            </div>
            
            <!-- Statistics -->
            <%
                int totalQuizzes = userQuizzes != null ? userQuizzes.size() : 0;
                int totalCompletions = 0;
                double overallAverage = 0.0;
                
                if (completionCounts != null) {
                    for (Integer count : completionCounts.values()) {
                        totalCompletions += count;
                    }
                }
                
                if (averageScores != null && !averageScores.isEmpty()) {
                    double sum = 0.0;
                    for (Double avg : averageScores.values()) {
                        sum += avg;
                    }
                    overallAverage = sum / averageScores.size();
                }
            %>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number"><%= totalQuizzes %></div>
                    <div class="stat-label">Total Quizzes</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number"><%= totalCompletions %></div>
                    <div class="stat-label">Total Completions</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number"><%= String.format("%.1f", overallAverage) %>%</div>
                    <div class="stat-label">Average Score</div>
                </div>
            </div>
            
            <!-- Quiz List -->
            <% if (userQuizzes == null || userQuizzes.isEmpty()) { %>
                <div class="quiz-card" style="text-align: center; padding: 60px;">
                    <p style="color: #b19cd9; font-size: 18px;">You haven't created any quizzes yet.</p>
                    <a href="quiz-creator" class="quiz-btn quiz-btn-primary" style="margin-top: 20px;">
                        Create Your First Quiz
                    </a>
                </div>
            <% } else { %>
                <table class="quiz-table">
                    <thead>
                        <tr>
                            <th>Quiz Title</th>
                            <th>Questions</th>
                            <th>Completions</th>
                            <th>Avg Score</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Quiz quiz : userQuizzes) { 
                            String escapedTitle = quiz.getTestTitle().replace("'", "\\'").replace("\"", "\\\"");
                        %>
                            <tr>
                                <td><%= quiz.getTestTitle() %></td>
                                <td><%= quiz.getLastQuestionNumber() != null ? quiz.getLastQuestionNumber() : 0 %></td>
                                <td><%= completionCounts != null && completionCounts.containsKey(quiz.getId().intValue()) ? completionCounts.get(quiz.getId().intValue()) : 0 %></td>
                                <td>
                                    <% 
                                        Integer quizIdInt = quiz.getId().intValue();
                                        Double avgScore = averageScores != null && averageScores.containsKey(quizIdInt) ? averageScores.get(quizIdInt) : null;
                                        if (avgScore != null) {
                                    %>
                                        <%= String.format("%.1f", avgScore) %>%
                                    <% } else { %>
                                        N/A
                                    <% } %>
                                </td>
                                <td><%= quiz.getCreatedAt() != null ? dateFormat.format(java.sql.Timestamp.valueOf(quiz.getCreatedAt())) : "Unknown" %></td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="quiz-view?quizId=<%= quiz.getId() %>" class="action-btn action-btn-view">View</a>
                                        <a href="quiz-editor?quizId=<%= quiz.getId() %>" class="action-btn action-btn-edit">Edit</a>
                                        <a href="javascript:void(0)" onclick="confirmDelete(<%= quiz.getId() %>, '<%= escapedTitle %>')" class="action-btn action-btn-delete">Delete</a>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
    
    <script>
        function confirmDelete(quizId, quizTitle) {
            if (confirm('Are you sure you want to delete the quiz "' + quizTitle + '"? This action cannot be undone.')) {
                // Create a form and submit it
                var form = document.createElement('form');
                form.method = 'post';
                form.action = 'quiz-manager';
                
                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'action';
                input.value = 'delete';
                
                var idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'quizId';
                idInput.value = quizId;
                
                form.appendChild(input);
                form.appendChild(idInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html> 