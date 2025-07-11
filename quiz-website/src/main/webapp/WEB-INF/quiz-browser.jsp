<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String searchQuery = request.getParameter("search") != null ? request.getParameter("search") : "";
    String categoryId = request.getParameter("categoryId") != null ? request.getParameter("categoryId") : "";
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Browse Quizzes - Quiz App</title>
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
        
        <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
        <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
        <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
        <a href="${pageContext.request.contextPath}/quiz-browser" style="background-color: rgba(255, 255, 255, 0.2);">🔍 Browse Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
        <a href="#">🏆 Achievements</a>
        <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
        <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
        <a href="${pageContext.request.contextPath}/history">📊 History</a>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <h2>Browse Quizzes</h2>
            
            <!-- Search and Filter Section -->
            <div class="quiz-card">
                <form method="get" action="quiz-browser" style="display: flex; gap: 15px; flex-wrap: wrap;">
                    <input type="text" name="search" placeholder="Search quizzes..." 
                           value="<%= searchQuery %>" 
                           class="form-control" style="flex: 1; min-width: 250px;">
                    
                    <select name="categoryId" class="form-control" style="width: 200px;">
                        <option value="">All Categories</option>
                        <% 
                            if (categories != null) {
                                for (Category category : categories) {
                                    String selected = categoryId.equals(String.valueOf(category.getId())) ? "selected" : "";
                        %>
                            <option value="<%= category.getId() %>" <%= selected %>>
                                <%= category.getName() %>
                            </option>
                        <% 
                                }
                            }
                        %>
                    </select>
                    
                    <button type="submit" class="quiz-btn quiz-btn-primary">Search</button>
                </form>
            </div>
            
            <!-- Quiz List -->
            <% if (quizzes == null || quizzes.isEmpty()) { %>
                <div class="quiz-card" style="text-align: center; padding: 60px;">
                    <p style="color: #b19cd9; font-size: 18px;">No quizzes found.</p>
                    <a href="quiz-creator" class="quiz-btn quiz-btn-primary" style="margin-top: 20px;">
                        Create Your First Quiz
                    </a>
                </div>
            <% } else { %>
                <div class="quiz-grid">
                    <% for (Quiz quiz : quizzes) { %>
                        <div class="quiz-card">
                            <h3 style="color: #ffffff; margin-bottom: 10px;"><%= quiz.getTitle() %></h3>
                            <p style="color: #d1d8ff; margin-bottom: 15px;"><%= quiz.getDescription() %></p>
                            
                            <div class="quiz-stats">
                                <span>⏱️ <%= quiz.getTimeLimit() == 0 ? "No time limit" : quiz.getTimeLimit() + " minutes" %></span>
                                <span>📝 <%= quiz.getQuestionCount() != null ? quiz.getQuestionCount() : 0 %> questions</span>
                            </div>
                            
                            <% if (quiz.getCategoryId() != null) { %>
                                <div style="margin: 10px 0;">
                                    <span style="background: #4b3d6e; padding: 5px 10px; border-radius: 15px; font-size: 12px;">
                                        <%= quiz.getCategoryName() != null ? quiz.getCategoryName() : "Uncategorized" %>
                                    </span>
                                </div>
                            <% } %>
                            
                            <div style="margin-top: 10px; color: #999; font-size: 12px;">
                                Created by <%= quiz.getCreatorName() != null ? quiz.getCreatorName() : "Unknown" %>
                                <% if (quiz.getCreatedDate() != null) { %>
                                    on <%= dateFormat.format(quiz.getCreatedDate()) %>
                                <% } %>
                            </div>
                            
                            <div style="margin-top: 15px;">
                                <a href="quiz-view?id=<%= quiz.getId() %>" class="quiz-btn quiz-btn-primary" style="width: 100%;">
                                    View Quiz
                                </a>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html> 