<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.DTO.PopularQuizDTO" %>
<%@ page import="com.freeuni.quiz.DTO.QuizChallengeDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.QuizCompletion" %>
<%@ page import="com.freeuni.quiz.bean.FriendshipRequest" %>
<%@ page import="com.freeuni.quiz.bean.Message" %>
<%@ page import="com.freeuni.quiz.DTO.AnnouncementDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Check if current user is admin
    boolean isAdmin = user.isAdmin();
    
    // Quiz-related data
    List<PopularQuizDTO> popularQuizzes = (List<PopularQuizDTO>) request.getAttribute("popularQuizzes");
    List<Quiz> recentlyCreatedQuizzes = (List<Quiz>) request.getAttribute("recentlyCreatedQuizzes");
    List<Quiz> userRecentCreatedQuizzes = (List<Quiz>) request.getAttribute("userRecentCreatedQuizzes");
    List<QuizCompletion> userRecentCompletions = (List<QuizCompletion>) request.getAttribute("userRecentCompletions");
    List<QuizCompletion> friendsRecentCompletions = (List<QuizCompletion>) request.getAttribute("friendsRecentCompletions");
    Map<Long, Quiz> quizMap = (Map<Long, Quiz>) request.getAttribute("quizMap");
    Map<Long, Quiz> friendsQuizMap = (Map<Long, Quiz>) request.getAttribute("friendsQuizMap");
    Map<Long, UserDTO> friendsUserMap = (Map<Long, UserDTO>) request.getAttribute("friendsUserMap");
    
    // Social data
    List<QuizChallengeDTO> recentChallenges = (List<QuizChallengeDTO>) request.getAttribute("recentChallenges");
    List<FriendshipRequest> recentFriendRequests = (List<FriendshipRequest>) request.getAttribute("recentFriendRequests");
    LinkedHashMap<Message, UserDTO> recentConversations = (LinkedHashMap<Message, UserDTO>) request.getAttribute("recentConversations");
    Map<Integer, UserDTO> friendRequestSenders = (Map<Integer, UserDTO>) request.getAttribute("friendRequestSenders");
    
    // Announcement data
    List<AnnouncementDTO> recentAnnouncements = (List<AnnouncementDTO>) request.getAttribute("recentAnnouncements");
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Home - Quiz App</title>
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
        }
        .search-bar {
            width: 100%;
            text-align: center;
            margin-bottom: 30px;
        }
        .search-bar input {
            width: 60%;
            padding: 12px;
            font-size: 16px;
            border-radius: 25px;
            border: 1px solid #ccc;
        }

        .section-header {
            font-size: 28px;
            color: white;
            margin: 30px 0 20px 0;
            text-align: center;
            padding: 15px;
            background: linear-gradient(45deg, #8b5cf6, #3b82f6);
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }

        .quiz-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            grid-template-rows: 300px 300px 300px;
            gap: 20px;
            margin-bottom: 40px;
        }

        .social-grid {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            grid-template-rows: 300px;
            gap: 20px;
        }

        .box {
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            display: flex;
            flex-direction: column;
        }

        .box h2 {
            text-align: center;
            margin-bottom: 15px;
        }

        .box-content {
            flex-grow: 1;
            color: #555;
            overflow-y: auto;
        }

        .box-content p {
            margin: 8px 0;
        }

        .box-content a {
            text-decoration: none;
        }

        .box-content a:hover {
            text-decoration: underline;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 5% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            max-width: 800px;
            border-radius: 10px;
            max-height: 80vh;
            overflow-y: auto;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }

        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
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

    <a href="${pageContext.request.contextPath}/home" style="background-color: rgba(255, 255, 255, 0.2);">🏠 Home</a>
    <a href="#" onclick="showAnnouncements()">📢 Announcements</a>
    <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
    <a href="${pageContext.request.contextPath}/friendshipRequests">👋 Friend Requests</a>
    <a href="${pageContext.request.contextPath}/quiz-browser">🔍 Browse Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-manager">📊 My Quizzes</a>
    <a href="${pageContext.request.contextPath}/quiz-creator">➕ Create Quiz</a>
    <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
    <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
    <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
    <% if (isAdmin) { %>
    <a href="${pageContext.request.contextPath}/admin">🛠️ Admin Panel</a>
    <% } %>
    <a href="#">📊 History</a>
</div>

<div class="content">
    <div class="search-bar" style="text-align:center; position: relative;">
        <select id="search-type" name="type"
                style="padding: 10px; font-size: 16px; border-radius: 25px;">
            <option value="users">Users</option>
            <option value="quizzes">Quizzes</option>
        </select>

        <div style="display: inline-block; position: relative;">
            <input
                    id="search-input"
                    name="query"
                    type="text"
                    placeholder="Search..."
                    style="width: 300px; padding: 10px; font-size: 16px; border-radius: 25px; border: 1px solid #ccc;"
                    autocomplete="off"
            >

            <ul
                    id="search-results"
                    style="display: none;
                   position: absolute;
                   top: 110%;
                   left: 0;
                   width: 100%;
                   background: white;
                   border: 1px solid #ccc;
                   border-radius: 5px;
                   list-style: none;
                   padding: 0;
                   margin: 5px 0 0 0;
                   z-index: 1000;
                   max-height: 200px;
                   overflow-y: auto;">
            </ul>
        </div>
    </div>

    <% if (errorMessage != null) { %>
        <div style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 20px;">
            <strong>Error:</strong> <%= errorMessage %>
        </div>
    <% } %>

    <!-- Announcements Section -->
    <div class="section-header">
        📢 Latest Announcements
    </div>

    <div style="display: grid; grid-template-columns: 1fr; gap: 20px; margin-bottom: 40px;">
        <div class="box">
            <h2>📢 Recent Announcements</h2>
            <div class="box-content">
                <% if (recentAnnouncements != null && !recentAnnouncements.isEmpty()) { %>
                    <% for (AnnouncementDTO announcement : recentAnnouncements) { %>
                        <div style="border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 10px;">
                            <h4 style="margin: 0 0 5px 0; color: #333;"><%= announcement.getTitle() %></h4>
                            <p style="margin: 0 0 5px 0; color: #666;"><%= announcement.getContent() %></p>
                            <small style="color: #888;">
                                By <%= announcement.getAuthorName() != null ? announcement.getAuthorName() : "Admin" %> 
                                - <%= announcement.getCreatedAt() != null ? announcement.getCreatedAt().toLocalDateTime().format(formatter) : "" %>
                            </small>
                        </div>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No announcements available</p>
                <% } %>
            </div>
        </div>
    </div>

    <!-- Social Section -->
    <div class="section-header">
        👥 Social Activities
    </div>

    <div class="social-grid">
        <div class="box">
            <h2>🏆 Recent Challenges</h2>
            <div class="box-content">
                <% if (recentChallenges != null && !recentChallenges.isEmpty()) { %>
                    <% for (QuizChallengeDTO challenge : recentChallenges) { %>
                        <p>
                            <strong><%= challenge.getChallenger().getUserName() %></strong> challenged you<br>
                            <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= challenge.getQuiz().getId() %>"
                               style="color: #f59e0b; text-decoration: none; font-weight: bold;">
                                🏆 <%= challenge.getQuiz().getTestTitle() %>
                            </a>
                            <br><small style="color: #666;">Status: <%= challenge.getStatus() %> - <%= challenge.getCreatedAt().toLocalDateTime().format(formatter) %></small>
                        </p>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No recent challenges</p>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>👋 Friend Requests</h2>
            <div class="box-content">
                <% if (recentFriendRequests != null && !recentFriendRequests.isEmpty()) { %>
                    <% for (FriendshipRequest friendRequest : recentFriendRequests) { 
                        UserDTO sender = friendRequestSenders.get(friendRequest.getRequestSenderId());
                        if (sender != null) { %>
                            <p>
                                <strong><%= sender.getUserName() %></strong> wants to be friends<br>
                                <small style="color: #666;"><%= friendRequest.getTimestamp().toLocalDateTime().format(formatter) %></small>
                            </p>
                        <% } %>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No recent friend requests</p>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>💬 Recent Messages</h2>
            <div class="box-content">
                <% if (recentConversations != null && !recentConversations.isEmpty()) { %>
                    <% for (Map.Entry<Message, UserDTO> entry : recentConversations.entrySet()) { 
                        Message message = entry.getKey();
                        UserDTO otherUser = entry.getValue();
                        if (otherUser != null) { %>
                            <p>
                                <strong><%= otherUser.getUserName() %></strong><br>
                                <span style="color: #888; font-style: italic;"><%= message.getContent().length() > 30 ? message.getContent().substring(0, 30) + "..." : message.getContent() %></span>
                                <br><small style="color: #666;"><%= message.getSentAt().format(formatter) %></small>
                            </p>
                        <% } %>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No recent messages</p>
                <% } %>
                <div style="margin-top: 15px;">
                    <a href="${pageContext.request.contextPath}/inbox"
                       style="color: #3b82f6; text-decoration: none; font-weight: bold;">
                        View All Messages →
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Social Actions -->
    <div style="text-align: center; margin-top: 30px; padding: 20px;">
        <a href="${pageContext.request.contextPath}/challenges" 
           style="background: linear-gradient(45deg, #f59e0b, #d97706); color: white; padding: 12px 20px; border-radius: 8px; text-decoration: none; font-weight: bold; margin: 0 10px; display: inline-block;">
            🏆 View All Challenges
        </a>
        <a href="${pageContext.request.contextPath}/friendshipRequests" 
           style="background: linear-gradient(45deg, #10b981, #059669); color: white; padding: 12px 20px; border-radius: 8px; text-decoration: none; font-weight: bold; margin: 0 10px; display: inline-block;">
            👋 Manage Friend Requests
        </a>
        <a href="${pageContext.request.contextPath}/inbox" 
           style="background: linear-gradient(45deg, #3b82f6, #2563eb); color: white; padding: 12px 20px; border-radius: 8px; text-decoration: none; font-weight: bold; margin: 0 10px; display: inline-block;">
            💬 View All Messages
        </a>
    </div>

    <!-- Quiz-Related Section -->
    <div class="section-header">
        🎯 Quiz Activities
    </div>

    <div class="quiz-grid">
        <div class="box">
            <h2>🔥 Popular Quizzes</h2>
            <div class="box-content">
                <% if (popularQuizzes != null && !popularQuizzes.isEmpty()) { %>
                    <% for (PopularQuizDTO popularQuiz : popularQuizzes) { %>
                        <p>
                            <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= popularQuiz.getQuiz().getId() %>"
                               style="color: #8a2be2; text-decoration: none; font-weight: bold;">
                                📊 <%= popularQuiz.getQuiz().getTestTitle() %>
                            </a>
                            <br><small style="color: #666;"><%= popularQuiz.getCompletionCount() %> completions</small>
                        </p>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No popular quizzes available</p>
                <% } %>
                <div style="margin-top: 15px;">
                    <a href="${pageContext.request.contextPath}/quiz-browser"
                       style="color: #8a2be2; text-decoration: none; font-weight: bold;">
                        View All Quizzes →
                    </a>
                </div>
            </div>
        </div>
        <div class="box">
            <h2>🆕 Recently Created Quizzes</h2>
            <div class="box-content">
                <% if (recentlyCreatedQuizzes != null && !recentlyCreatedQuizzes.isEmpty()) { %>
                    <% for (Quiz quiz : recentlyCreatedQuizzes) { %>
                        <p>
                            <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= quiz.getId() %>"
                               style="color: #28a745; text-decoration: none; font-weight: bold;">
                                📝 <%= quiz.getTestTitle() %>
                            </a>
                            <br><small style="color: #666;"><%= quiz.getCreatedAt().format(formatter) %></small>
                        </p>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No recently created quizzes</p>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>📝 Your Recent Quiz Activities</h2>
            <div class="box-content">
                <% if (userRecentCompletions != null && !userRecentCompletions.isEmpty()) { %>
                    <% for (QuizCompletion completion : userRecentCompletions) { 
                        Quiz quiz = quizMap.get(completion.getTestId());
                        if (quiz != null) { %>
                            <p>
                                <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= quiz.getId() %>"
                                   style="color: #007bff; text-decoration: none; font-weight: bold;">
                                    🎯 <%= quiz.getTestTitle() %>
                                </a>
                                <br><small style="color: #666;">Score: <%= String.format("%.1f", completion.getCompletionPercentage()) %>% - <%= completion.getFinishedAt().format(formatter) %></small>
                            </p>
                        <% } %>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No recent quiz activities</p>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>🏗️ Your Recent Creations</h2>
            <div class="box-content">
                <% if (userRecentCreatedQuizzes != null && !userRecentCreatedQuizzes.isEmpty()) { %>
                    <% for (Quiz quiz : userRecentCreatedQuizzes) { %>
                        <p>
                            <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= quiz.getId() %>"
                               style="color: #6a5acd; text-decoration: none; font-weight: bold;">
                                🎨 <%= quiz.getTestTitle() %>
                            </a>
                            <br><small style="color: #666;"><%= quiz.getCreatedAt().format(formatter) %></small>
                        </p>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">You haven't created any quizzes yet</p>
                    <div style="margin-top: 15px;">
                        <a href="${pageContext.request.contextPath}/quiz-creator"
                           style="background: linear-gradient(45deg, #28a745, #20c997); color: white;
                                  padding: 8px 16px; border-radius: 6px; text-decoration: none;
                                  font-weight: bold; display: inline-block; font-size: 14px;">
                            🚀 Create Your First Quiz
                        </a>
                    </div>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>👥 Friends' Recent Activities</h2>
            <div class="box-content">
                <% if (friendsRecentCompletions != null && !friendsRecentCompletions.isEmpty()) { %>
                    <% for (QuizCompletion completion : friendsRecentCompletions) { 
                        Quiz quiz = friendsQuizMap.get(completion.getTestId());
                        UserDTO friend = friendsUserMap.get(completion.getParticipantUserId());
                        if (quiz != null && friend != null) { %>
                            <p>
                                <strong><%= friend.getUserName() %></strong> completed<br>
                                <a href="${pageContext.request.contextPath}/quiz-view?quizId=<%= quiz.getId() %>"
                                   style="color: #ff6b6b; text-decoration: none; font-weight: bold;">
                                    👥 <%= quiz.getTestTitle() %>
                                </a>
                                <br><small style="color: #666;">Score: <%= String.format("%.1f", completion.getCompletionPercentage()) %>% - <%= completion.getFinishedAt().format(formatter) %></small>
                            </p>
                        <% } %>
                    <% } %>
                <% } else { %>
                    <p style="color: #666; font-style: italic;">No friends' activities yet</p>
                <% } %>
            </div>
        </div>
        <div class="box">
            <h2>➕ Quick Actions</h2>
            <div class="box-content">
                <p>
                    <a href="${pageContext.request.contextPath}/quiz-creator"
                       style="color: #28a745; text-decoration: none; font-weight: bold;">
                        📝 Create New Quiz
                    </a>
                </p>
                <p>
                    <a href="${pageContext.request.contextPath}/quiz-manager"
                       style="color: #007bff; text-decoration: none; font-weight: bold;">
                        📊 My Quiz Dashboard
                    </a>
                </p>
                <p>
                    <a href="${pageContext.request.contextPath}/quiz-browser"
                       style="color: #6a5acd; text-decoration: none; font-weight: bold;">
                        🔍 Browse & Take Quizzes
                    </a>
                </p>
                <p>
                    <a href="${pageContext.request.contextPath}/challenges"
                       style="color: #e91e63; text-decoration: none; font-weight: bold;">
                        🏆 View Challenges
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>

<!-- Announcements Modal -->
<div id="announcementsModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeAnnouncements()">&times;</span>
        <h2>📢 All Announcements</h2>
        <div id="announcementsContent">
            <% if (recentAnnouncements != null && !recentAnnouncements.isEmpty()) { %>
                <% for (AnnouncementDTO announcement : recentAnnouncements) { %>
                    <div style="border-bottom: 1px solid #eee; padding: 15px 0; margin-bottom: 15px;">
                        <h3 style="margin: 0 0 10px 0; color: #333;"><%= announcement.getTitle() %></h3>
                        <p style="margin: 0 0 10px 0; color: #666; line-height: 1.5;"><%= announcement.getContent() %></p>
                        <small style="color: #888;">
                            By <%= announcement.getAuthorName() != null ? announcement.getAuthorName() : "Admin" %> 
                            - <%= announcement.getCreatedAt() != null ? announcement.getCreatedAt().toLocalDateTime().format(formatter) : "" %>
                        </small>
                    </div>
                <% } %>
            <% } else { %>
                <p style="color: #666; font-style: italic; text-align: center; padding: 20px;">No announcements available</p>
            <% } %>
        </div>
    </div>
</div>

<script src="js/search.js?v=2"></script>
<script>
function showAnnouncements() {
    document.getElementById('announcementsModal').style.display = 'block';
}

function closeAnnouncements() {
    document.getElementById('announcementsModal').style.display = 'none';
}

window.onclick = function(event) {
    var modal = document.getElementById('announcementsModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}
</script>
</body>
</html>
