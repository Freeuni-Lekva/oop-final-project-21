<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
        }
        .pagination button {
            padding: 8px 16px;
            border: 2px solid #b19cd9;
            border-radius: 8px;
            background-color: transparent;
            color: #b19cd9;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .pagination button:hover:not(:disabled) {
            background-color: #b19cd9;
            color: #13081f;
        }
        .pagination button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        .pagination .current-page {
            color: #ffffff;
            font-weight: bold;
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
        <a href="${pageContext.request.contextPath}/quiz-browser" style="background-color: rgba(255, 255, 255, 0.2);">🔍 Browse Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
        <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <h1 style="color: #ffffff; text-align: center; margin-bottom: 30px;">Browse Quizzes</h1>
            
            <!-- Search and Filter Section -->
            <div class="quiz-search">
                <form method="get" action="quiz-browser" class="quiz-search-form">
                    <input type="text" name="search" placeholder="Search quizzes..." 
                           value="${param.search}" id="searchInput">
                    
                    <select name="categoryId" id="categoryFilter">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category.id}" 
                                    ${param.categoryId == category.id ? 'selected' : ''}>
                                ${category.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                    
                    <button type="submit" class="quiz-btn quiz-btn-primary">Search</button>
                    <a href="quiz-browser" class="quiz-btn quiz-btn-outline">Clear</a>
                </form>
            </div>
            
            <!-- Quiz Results -->
            <div class="quiz-results">
                <c:choose>
                    <c:when test="${empty quizzes}">
                        <div class="quiz-card" style="text-align: center; padding: 40px;">
                            <h3 style="color: #b19cd9;">No quizzes found</h3>
                            <p style="color: #d1d8ff;">Try adjusting your search criteria or browse all quizzes.</p>
                            <a href="quiz-creator" class="quiz-btn quiz-btn-primary">Create Your First Quiz</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="quiz-grid">
                            <c:forEach items="${quizzes}" var="quiz">
                                <div class="quiz-card">
                                    <div class="quiz-header">
                                        <h3 class="quiz-title">${quiz.testTitle}</h3>
                                        <div class="quiz-meta-item">
                                            <c:choose>
                                                <c:when test="${quiz.timeLimitMinutes != null && quiz.timeLimitMinutes > 0}">
                                                    ⏱️ ${quiz.timeLimitMinutes} min
                                                </c:when>
                                                <c:otherwise>
                                                    ⏱️ No limit
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    
                                    <div class="quiz-description">
                                        ${quiz.testDescription}
                                    </div>
                                    
                                    <div class="quiz-meta">
                                        <c:if test="${quiz.categoryId != null}">
                                            <div class="quiz-meta-item">
                                                📁 
                                                <c:forEach items="${categories}" var="category">
                                                    <c:if test="${category.id == quiz.categoryId}">
                                                        ${category.categoryName}
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:if>
                                        
                                        <c:if test="${quiz.createdAt != null}">
                                            <div class="quiz-meta-item">
                                                📅 ${quiz.createdAt.toLocalDate()}
                                            </div>
                                        </c:if>
                                        
                                        <c:if test="${quiz.lastQuestionNumber != null}">
                                            <div class="quiz-meta-item">
                                                📊 ${quiz.lastQuestionNumber} questions
                                            </div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="quiz-actions">
                                        <a href="quiz-view?quizId=${quiz.id}" class="quiz-btn quiz-btn-primary">
                                            View Quiz
                                        </a>
                                        <c:if test="${quiz.creatorUserId == user.id}">
                                            <a href="quiz-editor?quizId=${quiz.id}" class="quiz-btn quiz-btn-secondary">
                                                Edit
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- Pagination -->
            <div class="pagination">
                <c:if test="${currentPage > 0}">
                    <a href="quiz-browser?page=${currentPage - 1}&search=${param.search}&categoryId=${param.categoryId}" 
                       class="quiz-btn quiz-btn-outline">← Previous</a>
                </c:if>
                
                <span class="current-page">Page ${currentPage + 1}</span>
                
                <c:if test="${not empty quizzes and quizzes.size() >= 10}">
                    <a href="quiz-browser?page=${currentPage + 1}&search=${param.search}&categoryId=${param.categoryId}" 
                       class="quiz-btn quiz-btn-outline">Next →</a>
                </c:if>
            </div>
        </div>
    </div>
    
    <script>
        // Enhanced search functionality
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                document.querySelector('.quiz-search-form').submit();
            }
        });
        
        // Auto-search on category change
        document.getElementById('categoryFilter').addEventListener('change', function() {
            document.querySelector('.quiz-search-form').submit();
        });
        
        // Add loading state to search button
        document.querySelector('.quiz-search-form').addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.textContent = 'Searching...';
            submitBtn.disabled = true;
        });
        
        // Add hover effects to quiz cards
        document.querySelectorAll('.quiz-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-5px)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        });
    </script>
</body>
</html> 