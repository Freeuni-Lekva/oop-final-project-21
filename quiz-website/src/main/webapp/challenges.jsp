<%-- challenges.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.QuizChallengeDTO" %>
<%@ page import="java.util.List" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<QuizChallengeDTO> receivedChallenges = (List<QuizChallengeDTO>) request.getAttribute("receivedChallenges");
    List<QuizChallengeDTO> sentChallenges = (List<QuizChallengeDTO>) request.getAttribute("sentChallenges");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Challenges</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
            color: white;
        }
        .sidebar {
            width: 220px;
            background-color: #240955;
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
        }
        .sidebar .username {
            text-align: center;
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
        }
        .section {
            background-color: #1a0b2e;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .challenge {
            background-color: #240955;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 5px;
        }
        .challenge h4 {
            margin: 0 0 10px 0;
            color: #e0aaff;
        }
        .challenge p {
            margin: 5px 0;
            font-size: 14px;
        }
        .score-info {
            background-color: #1a0b2e;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
            border-left: 3px solid #10b981;
        }
        .score-row {
            display: flex;
            justify-content: space-between;
            margin: 5px 0;
            font-size: 13px;
        }
        .score-label {
            color: #b19cd9;
        }
        .score-value {
            color: #10b981;
            font-weight: bold;
        }
        .buttons {
            margin-top: 10px;
        }
        .btn {
            padding: 6px 12px;
            margin-right: 5px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }
        .btn-green { background-color: #10b981; color: white; }
        .btn-red { background-color: #ef4444; color: white; }
        .btn-blue { background-color: #3b82f6; color: white; }
        .btn-gray { background-color: #6b7280; color: white; }

        .empty {
            text-align: center;
            color: #9ca3af;
            padding: 20px;
        }
        .send-challenge-btn {
            position: fixed;
            bottom: 30px;
            right: 30px;
            background-color: #10b981;
            color: white;
            border: none;
            border-radius: 50px;
            padding: 15px 25px;
            cursor: pointer;
            font-size: 16px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
        }
        .send-challenge-btn:hover {
            background-color: #059669;
        }
    </style>
</head>
<body>

<div class="sidebar">
    <% if (user.getImageURL() != null && !user.getImageURL().isEmpty()) { %>
    <img src="<%= user.getImageURL() %>" alt="Profile">
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
    <a href="${pageContext.request.contextPath}/challenges" style="background-color: rgba(255, 255, 255, 0.2);">🎯 Challenges</a>
    <a href="${pageContext.request.contextPath}/history">📊 History</a>
</div>

<div class="content">
    <h2>Challenges</h2>

    <div class="section">
        <h3>Received</h3>
        <% if (receivedChallenges != null && !receivedChallenges.isEmpty()) { %>
        <% for (QuizChallengeDTO challenge : receivedChallenges) { %>
        <div class="challenge">
            <h4><%= challenge.getQuiz().getTestTitle() %></h4>
            <p>From: <%= challenge.getChallenger().getUserName() %></p>
            <p>Status: <%= challenge.getStatus() %></p>
            
            <% if (challenge.getChallengerScore() != null || challenge.getChallengedScore() != null) { %>
            <div class="score-info">
                <div style="font-weight: bold; color: #e0aaff; margin-bottom: 8px;">📊 Quiz Scores:</div>
                <% if (challenge.getChallengerScore() != null) { %>
                <div class="score-row">
                    <span class="score-label"><%= challenge.getChallenger().getUserName() %>:</span>
                    <span class="score-value"><%= String.format("%.1f", challenge.getChallengerScore().getCompletionPercentage()) %>%</span>
                </div>
                <% } %>
                <% if (challenge.getChallengedScore() != null) { %>
                <div class="score-row">
                    <span class="score-label">You:</span>
                    <span class="score-value"><%= String.format("%.1f", challenge.getChallengedScore().getCompletionPercentage()) %>%</span>
                </div>
                <% } %>
            </div>
            <% } %>
            
            <div class="buttons">
                <% if ("PENDING".equals(challenge.getStatus())) { %>
                <form method="post" style="display: inline;">
                    <input type="hidden" name="action" value="accept">
                    <input type="hidden" name="challengeId" value="<%= challenge.getId() %>">
                    <button type="submit" class="btn btn-green">Accept</button>
                </form>
                <form method="post" style="display: inline;">
                    <input type="hidden" name="action" value="decline">
                    <input type="hidden" name="challengeId" value="<%= challenge.getId() %>">
                    <button type="submit" class="btn btn-red">Decline</button>
                </form>
                <% } else if ("ACCEPTED".equals(challenge.getStatus())) { %>
                <a href="<%= challenge.getQuizUrl() %>" class="btn btn-blue">Take Quiz</a>
                <% } %>
            </div>
        </div>
        <% } %>
        <% } else { %>
        <div class="empty">No received challenges</div>
        <% } %>
    </div>

    <div class="section">
        <h3>Sent</h3>
        <% if (sentChallenges != null && !sentChallenges.isEmpty()) { %>
        <% for (QuizChallengeDTO challenge : sentChallenges) { %>
        <div class="challenge">
            <h4><%= challenge.getQuiz().getTestTitle() %></h4>
            <p>To: <%= challenge.getChallenged().getUserName() %></p>
            <p>Status: <%= challenge.getStatus() %></p>
            
            <% if (challenge.getChallengerScore() != null || challenge.getChallengedScore() != null) { %>
            <div class="score-info">
                <div style="font-weight: bold; color: #e0aaff; margin-bottom: 8px;">📊 Quiz Scores:</div>
                <% if (challenge.getChallengerScore() != null) { %>
                <div class="score-row">
                    <span class="score-label">You:</span>
                    <span class="score-value"><%= String.format("%.1f", challenge.getChallengerScore().getCompletionPercentage()) %>%</span>
                </div>
                <% } %>
                <% if (challenge.getChallengedScore() != null) { %>
                <div class="score-row">
                    <span class="score-label"><%= challenge.getChallenged().getUserName() %>:</span>
                    <span class="score-value"><%= String.format("%.1f", challenge.getChallengedScore().getCompletionPercentage()) %>%</span>
                </div>
                <% } %>
            </div>
            <% } %>
            
            <div class="buttons">
                <% if ("PENDING".equals(challenge.getStatus())) { %>
                <form method="post" style="display: inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="challengeId" value="<%= challenge.getId() %>">
                    <button type="submit" class="btn btn-gray">Delete</button>
                </form>
                <% } %>
            </div>
        </div>
        <% } %>
        <% } else { %>
        <div class="empty">No sent challenges</div>
        <% } %>
    </div>
</div>

<!-- Send Challenge Button -->
<button class="send-challenge-btn" onclick="window.location.href='${pageContext.request.contextPath}/send-challenge'">
    + Send Challenge
</button>

</body>
</html>

<script>
    if (window.location.pathname.includes('challenges.jsp')) {
        window.history.replaceState({}, '', '${pageContext.request.contextPath}/quiz-challenges');
    }
</script>