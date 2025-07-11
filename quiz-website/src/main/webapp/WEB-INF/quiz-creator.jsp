<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Category" %>
<%@ page import="java.util.List" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Category> categories = (List<Category>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Quiz - Quiz App</title>
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
        <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-creator" style="background-color: rgba(255, 255, 255, 0.2);">➕ Create Quiz</a>
        <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
        <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
        <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
        <a href="${pageContext.request.contextPath}/history">📊 History</a>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <div class="quiz-form">
                <h2>Create New Quiz</h2>
                
                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="quiz-error">
                    <%= request.getAttribute("errorMessage") %>
                </div>
                <% } %>
                
                <% if (request.getAttribute("successMessage") != null) { %>
                <div class="quiz-success">
                    <%= request.getAttribute("successMessage") %>
                </div>
                <% } %>

                <form method="post" action="quiz-creator" onsubmit="return validateForm()">
                    <div class="form-group">
                        <label for="title">Quiz Title *</label>
                        <input type="text" id="title" name="title" class="form-control" 
                               placeholder="Enter quiz title" required 
                               value="<%= request.getParameter("title") != null ? request.getParameter("title") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="description">Description *</label>
                        <textarea id="description" name="description" class="form-control" 
                                  placeholder="Enter quiz description" required rows="4"
                                  style="resize: vertical; height: auto;"><%= request.getParameter("description") != null ? request.getParameter("description") : "" %></textarea>
                    </div>

                    <div class="form-group">
                        <label for="category">Category</label>
                        <select id="category" name="categoryId" class="form-control">
                            <option value="">Select a category</option>
                            <% 
                                if (categories != null) {
                                    for (Category category : categories) {
                                        String selected = "";
                                        if (request.getParameter("categoryId") != null && 
                                            request.getParameter("categoryId").equals(String.valueOf(category.getId()))) {
                                            selected = "selected";
                                        }
                            %>
                                                                        <option value="<%= category.getId() %>" <%= selected %>>
                                            <%= category.getCategoryName() %>
                                        </option>
                            <% 
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="timeLimit">Time Limit (minutes)</label>
                        <input type="number" id="timeLimit" name="timeLimit" class="form-control" 
                               placeholder="Enter time limit in minutes (0 for no limit)" 
                               min="0" max="1440" 
                               value="<%= request.getParameter("timeLimit") != null ? request.getParameter("timeLimit") : "10" %>">
                        <small style="color: #b19cd9; margin-top: 5px; display: block;">
                            Leave 0 for no time limit. Maximum 1440 minutes (24 hours).
                        </small>
                    </div>

                    <div class="quiz-actions">
                        <button type="submit" class="quiz-btn quiz-btn-primary">Create Quiz</button>
                        <a href="quiz-browser" class="quiz-btn quiz-btn-outline">Cancel</a>
                    </div>
                </form>
            </div>

            <div class="quiz-card">
                <h3 style="color: #ffffff; margin-bottom: 15px;">📝 Next Steps</h3>
                <div style="color: #d1d8ff; line-height: 1.6;">
                    <p>After creating your quiz, you'll be able to:</p>
                    <ul style="margin-left: 20px;">
                        <li>Add questions of different types (Text, Multiple Choice, Image)</li>
                        <li>Set point values for each question</li>
                        <li>Preview and test your quiz</li>
                        <li>Publish your quiz for others to take</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script>
        function validateForm() {
            const title = document.getElementById('title').value.trim();
            const description = document.getElementById('description').value.trim();
            const timeLimit = document.getElementById('timeLimit').value;
            
            if (!title) {
                alert('Quiz title is required');
                return false;
            }
            
            if (title.length < 3 || title.length > 200) {
                alert('Quiz title must be between 3 and 200 characters');
                return false;
            }
            
            if (!description) {
                alert('Quiz description is required');
                return false;
            }
            
            if (description.length < 10 || description.length > 1000) {
                alert('Quiz description must be between 10 and 1000 characters');
                return false;
            }
            
            if (timeLimit && (timeLimit < 0 || timeLimit > 1440)) {
                alert('Time limit must be between 0 and 1440 minutes');
                return false;
            }
            
            return true;
        }
    </script>
</body>
</html> 