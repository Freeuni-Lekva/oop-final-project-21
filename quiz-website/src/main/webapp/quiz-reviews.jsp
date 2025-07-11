<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.QuizReviewDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<QuizReviewDTO> reviews = (List<QuizReviewDTO>) request.getAttribute("reviews");
    QuizReviewDTO userReview = (QuizReviewDTO) request.getAttribute("userReview");
    Long quizId = (Long) request.getAttribute("quizId");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Reviews - Quiz App</title>
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

        .reviews-container {
            max-width: 800px;
            margin: 0 auto;
        }

        .reviews-header {
            background-color: #240955;
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            margin-bottom: 30px;
        }

        .review-form-section {
            background-color: #240955;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
        }

        .review-form textarea {
            width: 100%;
            min-height: 120px;
            padding: 15px;
            background-color: #13081f;
            border: 2px solid #4b3d6e;
            border-radius: 10px;
            color: #ffffff;
            font-size: 16px;
            resize: vertical;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        .review-form textarea:focus {
            outline: none;
            border-color: #b19cd9;
        }

        .review-form textarea::placeholder {
            color: #999;
        }

        .reviews-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .review-item {
            background-color: #240955;
            padding: 25px;
            border-radius: 15px;
            position: relative;
        }

        .review-item.user-review {
            border: 2px solid #6a5acd;
            background-color: #2a0f5f;
        }

        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
            gap: 10px;
        }

        .review-author {
            color: #b19cd9;
            font-weight: bold;
            font-size: 16px;
        }

        .review-date {
            color: #999;
            font-size: 14px;
        }

        .review-text {
            color: #d1d8ff;
            line-height: 1.6;
            font-size: 16px;
            margin-bottom: 15px;
        }

        .review-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .user-review-badge {
            background: linear-gradient(45deg, #6a5acd, #8a2be2);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }

        .no-reviews {
            background-color: #240955;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            color: #d1d8ff;
        }

        .form-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
            flex-wrap: wrap;
            gap: 15px;
        }

        .character-counter {
            color: #999;
            font-size: 14px;
        }

        .form-buttons {
            display: flex;
            gap: 15px;
        }

        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
                padding: 10px;
            }

            .sidebar {
                display: none;
            }

            .review-header {
                flex-direction: column;
                align-items: flex-start;
            }

            .form-actions {
                flex-direction: column;
                align-items: stretch;
            }

            .form-buttons {
                justify-content: center;
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
    <div class="reviews-container">
        <!-- Header -->
        <div class="reviews-header">
            <h2 style="color: #ffffff; margin: 0;">💬 Quiz Reviews</h2>
            <p style="color: #d1d8ff; margin: 10px 0 0 0;">
                <%= reviews != null ? reviews.size() : 0 %> review<%= (reviews == null || reviews.size() != 1) ? "s" : "" %>
            </p>
        </div>

        <!-- Review Form -->
        <div class="review-form-section">
            <h3 style="color: #ffffff; margin-bottom: 20px;">
                <% if (userReview != null) { %>
                ✏️ Edit Your Review
                <% } else { %>
                ✏️ Write a Review
                <% } %>
            </h3>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <div style="background-color: #dc3545; color: white; padding: 10px; border-radius: 5px; margin-bottom: 20px;">
                <%= request.getAttribute("errorMessage") %>
            </div>
            <% } %>

            <% if (request.getAttribute("successMessage") != null) { %>
            <div style="background-color: #28a745; color: white; padding: 10px; border-radius: 5px; margin-bottom: 20px;">
                <%= request.getAttribute("successMessage") %>
            </div>
            <% } %>

            <form id="review-form" method="post" action="quiz-reviews">
                <input type="hidden" name="quizId" value="<%= quizId %>">

                <div class="review-form">
                        <textarea name="reviewText" id="review-text"
                                  placeholder="Share your thoughts about this quiz..."
                                  maxlength="1000" required><%= userReview != null ? userReview.getReviewText() : "" %></textarea>
                </div>

                <div class="form-actions">
                    <div class="character-counter">
                        <span id="char-count">0</span>/1000 characters
                    </div>
                    <div class="form-buttons">
                        <% if (userReview != null) { %>
                        <button type="submit" class="quiz-btn quiz-btn-primary">
                            Update Review
                        </button>
                        <button type="button" class="quiz-btn quiz-btn-danger" onclick="deleteReview()">
                            Delete Review
                        </button>
                        <% } else { %>
                        <button type="submit" class="quiz-btn quiz-btn-primary">
                            Submit Review
                        </button>
                        <% } %>
                        <a href="quiz-view?quizId=<%= quizId %>" class="quiz-btn quiz-btn-outline">
                            Back to Quiz
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- Reviews List -->
        <% if (reviews == null || reviews.isEmpty()) { %>
        <div class="no-reviews">
            <h3 style="color: #ffffff; margin-bottom: 15px;">No Reviews Yet</h3>
            <p>Be the first to review this quiz!</p>
        </div>
        <% } else { %>
        <div class="reviews-list">
            <% for (QuizReviewDTO review : reviews) {
                boolean isUserReview = review.getUserId().equals(user.getId());
            %>
            <div class="review-item <%= isUserReview ? "user-review" : "" %>">
                <div class="review-header">
                    <div style="display: flex; align-items: center; gap: 10px;">
                                    <span class="review-author">
                                        <%= isUserReview ? "You" : "User #" + review.getUserId() %>
                                    </span>
                        <% if (isUserReview) { %>
                        <span class="user-review-badge">Your Review</span>
                        <% } %>
                    </div>
                    <div class="review-date">
                        <%= review.getCreatedAt().toLocalDateTime().format(dateFormat) %>
                        <% if (review.getUpdatedAt() != null && !review.getUpdatedAt().equals(review.getCreatedAt())) { %>
                        <span style="color: #b19cd9;"> (edited)</span>
                        <% } %>
                    </div>
                </div>

                <div class="review-text">
                    <%= review.getReviewText().replace("\n", "<br>") %>
                </div>

                <% if (isUserReview) { %>
                <div class="review-actions">
                    <button onclick="editReview()" class="quiz-btn quiz-btn-outline" style="font-size: 14px; padding: 8px 16px;">
                        Edit
                    </button>
                    <button onclick="deleteReview()" class="quiz-btn quiz-btn-danger" style="font-size: 14px; padding: 8px 16px;">
                        Delete
                    </button>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>
        <% } %>
    </div>
</div>

<script>
    const reviewText = document.getElementById('review-text');
    const charCount = document.getElementById('char-count');

    function updateCharCount() {
        const current = reviewText.value.length;
        charCount.textContent = current;

        const counter = charCount.parentElement;
        if (current > 900) {
            counter.style.color = '#dc3545';
        } else if (current > 800) {
            counter.style.color = '#ffc107';
        } else {
            counter.style.color = '#999';
        }
    }

    reviewText.addEventListener('input', updateCharCount);
    updateCharCount();

    document.getElementById('review-form').addEventListener('submit', function(e) {
        const text = reviewText.value.trim();
        if (!text) {
            e.preventDefault();
            alert('Please write a review before submitting.');
            return false;
        }
        if (text.length > 1000) {
            e.preventDefault();
            alert('Review is too long. Please keep it under 1000 characters.');
            return false;
        }
        if (text.length < 10) {
            e.preventDefault();
            alert('Review is too short. Please write at least 10 characters.');
            return false;
        }
    });

    function editReview() {
        document.getElementById('review-text').focus();
        document.querySelector('.review-form-section').scrollIntoView({
            behavior: 'smooth'
        });
    }

    function deleteReview() {
        if (confirm('Are you sure you want to delete your review? This action cannot be undone.')) {
            fetch('quiz-reviews?quizId=<%= quizId %>', {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert('Failed to delete review. Please try again.');
                }
            }).catch(error => {
                alert('Network error. Please try again.');
            });
        }
    }

    reviewText.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = Math.max(120, this.scrollHeight) + 'px';
    });

    if (reviewText.value) {
        reviewText.style.height = 'auto';
        reviewText.style.height = Math.max(120, reviewText.scrollHeight) + 'px';
    }
</script>
</body>
</html>