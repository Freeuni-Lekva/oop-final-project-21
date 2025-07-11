<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.QuizRatingDTO" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    QuizRatingDTO userRating = (QuizRatingDTO) request.getAttribute("userRating");
    Double averageRating = (Double) request.getAttribute("averageRating");
    Integer ratingCount = (Integer) request.getAttribute("ratingCount");
    Long quizId = (Long) request.getAttribute("quizId");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Rate Quiz - Quiz App</title>
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

        .rating-container {
            max-width: 600px;
            margin: 0 auto;
        }

        .rating-card {
            background-color: #240955;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }

        .rating-stats {
            background-color: #13081f;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
        }

        .rating-stats h3 {
            color: #ffffff;
            margin-bottom: 15px;
        }

        .current-average {
            font-size: 48px;
            color: #b19cd9;
            margin: 10px 0;
        }

        .rating-info {
            color: #d1d8ff;
            font-size: 16px;
        }

        .star-rating {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin: 30px 0;
        }

        .star {
            font-size: 48px;
            color: #4b3d6e;
            cursor: pointer;
            transition: all 0.2s ease;
            user-select: none;
        }

        .star:hover,
        .star.active {
            color: #ffd700;
            transform: scale(1.1);
        }

        .rating-message {
            font-size: 18px;
            margin: 20px 0;
            min-height: 30px;
            color: #d1d8ff;
        }

        .rating-actions {
            display: flex;
            gap: 20px;
            justify-content: center;
            margin-top: 30px;
            flex-wrap: wrap;
        }

        .current-rating-display {
            background-color: #4b3d6e;
            padding: 15px;
            border-radius: 10px;
            margin: 20px 0;
            color: #ffffff;
        }

        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
                padding: 10px;
            }

            .sidebar {
                display: none;
            }

            .star {
                font-size: 36px;
            }

            .current-average {
                font-size: 36px;
            }

            .rating-actions {
                flex-direction: column;
                align-items: center;
            }
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
    <a href="#">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>
</div>

<div class="main-content">
    <div class="rating-container">
        <div class="rating-card">
            <h2 style="color: #ffffff; margin-bottom: 30px;">⭐ Rate This Quiz</h2>

            <!-- Current Rating Stats -->
            <% if (averageRating != null && ratingCount != null && ratingCount > 0) { %>
            <div class="rating-stats">
                <h3>Current Rating</h3>
                <div class="current-average">
                    <%= String.format("%.1f", averageRating) %>
                </div>
                <div class="rating-info">
                    Based on <%= ratingCount %> rating<%= ratingCount == 1 ? "" : "s" %>
                </div>
            </div>
            <% } else { %>
            <div class="rating-stats">
                <h3>No Ratings Yet</h3>
                <div class="rating-info">
                    Be the first to rate this quiz!
                </div>
            </div>
            <% } %>

            <!-- User's Current Rating -->
            <% if (userRating != null) { %>
            <div class="current-rating-display">
                <strong>Your Current Rating: <%= userRating.getRating() %>/5 ⭐</strong>
                <div style="font-size: 14px; margin-top: 5px; opacity: 0.8;">
                    Rated on <%= userRating.getCreatedAt() %>
                </div>
            </div>
            <% } %>

            <!-- Rating Form -->
            <form id="rating-form" method="post" action="rate-quiz">
                <input type="hidden" name="quizId" value="<%= quizId %>">
                <input type="hidden" name="rating" id="rating-value" value="<%= userRating != null ? userRating.getRating() : "" %>">

                <div class="star-rating" id="star-rating">
                    <% for (int i = 1; i <= 5; i++) { %>
                    <span class="star" data-rating="<%= i %>">⭐</span>
                    <% } %>
                </div>

                <div class="rating-message" id="rating-message">
                    <% if (userRating != null) { %>
                    Click to change your rating
                    <% } else { %>
                    Click on a star to rate this quiz
                    <% } %>
                </div>

                <div class="rating-actions">
                    <button type="submit" class="quiz-btn quiz-btn-primary" id="submit-rating" style="display: none;">
                        <%= userRating != null ? "Update Rating" : "Submit Rating" %>
                    </button>

                    <% if (userRating != null) { %>
                    <button type="button" class="quiz-btn quiz-btn-danger" onclick="deleteRating()">
                        Remove Rating
                    </button>
                    <% } %>

                    <a href="quiz-view?quizId=<%= quizId %>" class="quiz-btn quiz-btn-outline">
                        Back to Quiz
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    const stars = document.querySelectorAll('.star');
    const ratingValue = document.getElementById('rating-value');
    const ratingMessage = document.getElementById('rating-message');
    const submitButton = document.getElementById('submit-rating');

    let currentRating = <%= userRating != null ? userRating.getRating() : 0 %>;

    // Initialize stars based on existing rating
    updateStars(currentRating);

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            currentRating = rating;
            ratingValue.value = rating;
            updateStars(rating);
            updateMessage(rating);
            submitButton.style.display = 'inline-block';
        });

        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.dataset.rating);
            highlightStars(rating);
        });
    });

    document.getElementById('star-rating').addEventListener('mouseleave', function() {
        updateStars(currentRating);
    });

    function updateStars(rating) {
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < rating);
        });
    }

    function highlightStars(rating) {
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < rating);
        });
    }

    function updateMessage(rating) {
        const messages = {
            1: '1⭐ - Poor: Needs significant improvement',
            2: '2⭐ - Fair: Below average, could be better',
            3: '3⭐ - Good: Average quiz, decent content',
            4: '4⭐ - Very Good: Great quiz, well designed',
            5: '5⭐ - Excellent: Outstanding quiz, highly recommended'
        };
        ratingMessage.textContent = messages[rating] || (currentRating > 0 ? 'Click to change your rating' : 'Click on a star to rate this quiz');
    }

    function deleteRating() {
        if (confirm('Are you sure you want to remove your rating?')) {
            fetch('rate-quiz?quizId=<%= quizId %>', {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert('Failed to remove rating. Please try again.');
                }
            }).catch(error => {
                alert('Network error. Please try again.');
            });
        }
    }

    document.getElementById('rating-form').addEventListener('submit', function(e) {
        const rating = ratingValue.value;
        if (!rating || rating < 1 || rating > 5) {
            e.preventDefault();
            alert('Please select a rating between 1 and 5 stars.');
            return false;
        }
    });
</script>
</body>
</html>