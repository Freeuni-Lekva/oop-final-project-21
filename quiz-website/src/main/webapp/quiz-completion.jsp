<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.QuizRatingDTO" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Long quizId = (Long) request.getAttribute("quizId");
    String score = (String) request.getParameter("score");
    String maxScore = (String) request.getParameter("maxScore");
    QuizRatingDTO userRating = (QuizRatingDTO) request.getAttribute("userRating");
    Double averageRating = (Double) request.getAttribute("averageRating");
    Integer ratingCount = (Integer) request.getAttribute("ratingCount");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Completed! - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
            color: #ffffff;
        }

        .completion-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            text-align: center;
        }

        .completion-header {
            background: linear-gradient(135deg, #6a5acd, #8a2be2);
            padding: 40px 20px;
            border-radius: 20px;
            margin-bottom: 40px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .completion-header h1 {
            font-size: 48px;
            margin: 0 0 10px 0;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }

        .score-display {
            font-size: 36px;
            margin: 20px 0;
        }

        .score-number {
            color: #ffd700;
            font-weight: bold;
        }

        .actions-section {
            background-color: #240955;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
        }

        .action-buttons {
            display: flex;
            gap: 20px;
            justify-content: center;
            flex-wrap: wrap;
            margin-top: 20px;
        }

        .btn {
            padding: 15px 25px;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }

        .btn-rating {
            background: linear-gradient(45deg, #ffd700, #ffa500);
            color: #000;
        }

        .btn-review {
            background: linear-gradient(45deg, #6a5acd, #8a2be2);
            color: white;
        }

        .btn-home {
            background: linear-gradient(45deg, #28a745, #20c997);
            color: white;
        }

        .btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }

        .rating-info {
            background-color: #13081f;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }

        @media (max-width: 768px) {
            .completion-header h1 {
                font-size: 36px;
            }

            .score-display {
                font-size: 28px;
            }

            .action-buttons {
                flex-direction: column;
                align-items: center;
            }

            .btn {
                width: 250px;
            }
        }
    </style>
</head>
<body>
<div class="completion-container">
    <!-- Completion Header -->
    <div class="completion-header">
        <h1>🎉 Quiz Completed!</h1>
        <div class="score-display">
            Your Score:
            <span class="score-number">
                    <% if (score != null && maxScore != null) { %>
                        <%= score %> / <%= maxScore %>
                    <% } else { %>
                        Great Job!
                    <% } %>
                </span>
        </div>
        <% if (score != null && maxScore != null) { %>
        <%
            try {
                double scoreVal = Double.parseDouble(score);
                double maxVal = Double.parseDouble(maxScore);
                double percentage = (scoreVal / maxVal) * 100;
        %>
        <div style="font-size: 24px; margin-top: 10px;">
            <%= Math.round(percentage) %>% Score
        </div>
        <%  } catch (Exception e) { } %>
        <% } %>
    </div>

    <!-- Rating Info -->
    <% if (averageRating != null && ratingCount != null && ratingCount > 0) { %>
    <div class="rating-info">
        <h3 style="margin-bottom: 10px;">📊 Quiz Rating</h3>
        <div>Average: <%= String.format("%.1f", averageRating) %>/5 ⭐
            (<%= ratingCount %> rating<%= ratingCount == 1 ? "" : "s" %>)</div>
    </div>
    <% } %>

    <!-- Actions Section -->
    <div class="actions-section">
        <h2 style="margin-bottom: 20px;">📝 Share Your Experience</h2>
        <p style="color: #d1d8ff; margin-bottom: 30px;">
            Help other users by rating this quiz and sharing your thoughts!
        </p>

        <div class="action-buttons">
            <a href="rate-quiz?quizId=<%= quizId %>" class="btn btn-rating">
                ⭐ Rate Quiz
            </a>

            <a href="quiz-reviews?quizId=<%= quizId %>" class="btn btn-review">
                💬 Write Review
            </a>

            <a href="quiz-view?quizId=<%= quizId %>" class="btn btn-home">
                🔄 View Quiz Details
            </a>
        </div>

        <div style="margin-top: 30px;">
            <a href="quiz-browser" style="color: #b19cd9; text-decoration: none;">
                🔍 Browse More Quizzes
            </a>
            <span style="margin: 0 15px; color: #666;">|</span>
            <a href="home" style="color: #b19cd9; text-decoration: none;">
                🏠 Home
            </a>
        </div>
    </div>

    <!-- User's Current Rating Display -->
    <% if (userRating != null) { %>
    <div style="background-color: #4b3d6e; padding: 15px; border-radius: 10px; margin-top: 20px;">
        <div style="color: #ffffff;">
            ✅ You already rated this quiz: <%= userRating.getRating() %>/5 ⭐
        </div>
        <div style="color: #d1d8ff; font-size: 14px; margin-top: 5px;">
            Click "Rate Quiz" to update your rating
        </div>
    </div>
    <% } %>
</div>
</body>
</html>