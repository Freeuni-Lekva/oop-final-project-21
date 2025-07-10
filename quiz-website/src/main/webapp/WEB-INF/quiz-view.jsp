<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.QuizCompletion" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Integer totalQuestions = (Integer) request.getAttribute("totalQuestions");
    Boolean hasCompleted = (Boolean) request.getAttribute("hasCompleted");
    QuizCompletion bestCompletion = (QuizCompletion) request.getAttribute("bestCompletion");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= quiz != null ? quiz.getTestTitle() : "Quiz" %> - Quiz App</title>
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
        
        <a href="${pageContext.request.contextPath}/home.jsp">🏠 Home</a>
        <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
        <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <% if (quiz == null) { %>
                <div class="quiz-card" style="text-align: center; padding: 60px;">
                    <p style="color: #b19cd9; font-size: 18px;">Quiz not found.</p>
                    <a href="quiz-browser" class="quiz-btn quiz-btn-primary" style="margin-top: 20px;">
                        Browse Quizzes
                    </a>
                </div>
            <% } else { %>
                <div class="quiz-grid">
                    <!-- Quiz Info -->
                    <div class="quiz-card" style="grid-column: span 2;">
                        <h2 style="color: #ffffff; margin-bottom: 15px;"><%= quiz.getTestTitle() %></h2>
                        <p style="color: #d1d8ff; margin-bottom: 20px;"><%= quiz.getTestDescription() %></p>
                        
                        <div class="quiz-stats" style="margin-bottom: 20px;">
                            <span>⏱️ <%= quiz.getTimeLimitMinutes() == null || quiz.getTimeLimitMinutes() == 0 ? "No time limit" : quiz.getTimeLimitMinutes() + " minutes" %></span>
                            <span>📝 <%= totalQuestions != null ? totalQuestions : 0 %> questions</span>
                        </div>
                        
                        <div style="color: #999; font-size: 14px; margin-bottom: 20px;">
                            <% if (quiz.getCreatedAt() != null) { %>
                                Created on <%= quiz.getCreatedAt().format(dateFormat) %>
                            <% } %>
                        </div>
                        
                        <form method="post" action="quiz-session">
                            <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                            <button type="submit" class="quiz-btn quiz-btn-primary" style="width: 100%; font-size: 18px; padding: 15px;">
                                Start Quiz
                            </button>
                        </form>
                    </div>
                    
                    <!-- Quiz Status -->
                    <div class="quiz-card">
                        <% if (hasCompleted != null && hasCompleted && bestCompletion != null) { %>
                            <h3 style="color: #ffffff; margin-bottom: 15px;">⚡ Your Fastest Time</h3>
                            <div style="font-size: 36px; color: #b19cd9; margin-bottom: 10px;">
                                <%= Math.round(bestCompletion.getFinalScore() * 10) / 10.0 %> / <%= Math.round(bestCompletion.getTotalPossible() * 10) / 10.0 %>
                            </div>
                            <div style="color: #999; font-size: 14px;">
                                <%= Math.round(bestCompletion.getCompletionPercentage().doubleValue()) %>% Score
                            </div>
                            <div style="color: #999; font-size: 14px; margin-top: 5px;">
                                <% if (bestCompletion.getFinishedAt() != null) { %>
                                    Completed on <%= bestCompletion.getFinishedAt().format(dateFormat) %>
                                <% } %>
                            </div>
                            <div style="color: #999; font-size: 14px; margin-top: 5px;">
                                <% 
                                    Integer timeSeconds = bestCompletion.getTotalTimeMinutes(); // Actually stores seconds now
                                    if (timeSeconds != null && timeSeconds > 0) {
                                        if (timeSeconds < 60) {
                                %>
                                            Time taken: <%= timeSeconds %> seconds
                                <%      } else {
                                            int minutes = timeSeconds / 60;
                                            int remainingSeconds = timeSeconds % 60;
                                %>
                                            Time taken: <%= minutes %>m <%= remainingSeconds %>s
                                <%      }
                                    } else { %>
                                        Time taken: 0 seconds
                                <%  } %>
                            </div>
                        <% } else { %>
                            <h3 style="color: #ffffff; margin-bottom: 15px;">🎯 Ready to Start?</h3>
                            <p style="color: #d1d8ff;">You haven't taken this quiz yet. Click "Start Quiz" to begin!</p>
                        <% } %>
                    </div>
                    
                    <!-- Quiz Info -->
                    <div class="quiz-card">
                        <h3 style="color: #ffffff; margin-bottom: 15px;">📋 Quiz Details</h3>
                        <div style="color: #d1d8ff;">
                            <p><strong>Questions:</strong> <%= totalQuestions != null ? totalQuestions : 0 %></p>
                            <p><strong>Time Limit:</strong> <%= quiz.getTimeLimitMinutes() == null || quiz.getTimeLimitMinutes() == 0 ? "No time limit" : quiz.getTimeLimitMinutes() + " minutes" %></p>
                            <% if (quiz.getCreatedAt() != null) { %>
                                <p><strong>Created:</strong> <%= quiz.getCreatedAt().format(dateFormat) %></p>
                            <% } %>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html> 