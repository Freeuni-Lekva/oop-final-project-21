<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String error = request.getParameter("error");
    String success = request.getParameter("success");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Send Challenge</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
            color: white;
        }
        .container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #1a0b2e;
            padding: 30px;
            border-radius: 10px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #e0aaff;
        }
        input, select, textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #444;
            border-radius: 5px;
            background-color: #240955;
            color: white;
            box-sizing: border-box;
        }
        input::placeholder, textarea::placeholder {
            color: #aaa;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-right: 10px;
        }
        .btn-primary { background-color: #10b981; color: white; }
        .btn-secondary { background-color: #6b7280; color: white; }
        .search-results {
            max-height: 200px;
            overflow-y: auto;
            background-color: #240955;
            border: 1px solid #444;
            border-radius: 5px;
            margin-top: 5px;
        }
        .user-item {
            padding: 10px;
            cursor: pointer;
            border-bottom: 1px solid #333;
        }
        .user-item:hover {
            background-color: #2d1065;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .error {
            background-color: #fee2e2;
            color: #dc2626;
            border: 1px solid #fecaca;
        }
        .success {
            background-color: #d1fae5;
            color: #059669;
            border: 1px solid #a7f3d0;
        }
        .selected-item {
            background-color: #2d1065;
            padding: 8px;
            margin-top: 5px;
            border-radius: 3px;
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Send Challenge</h2>

    <% if (error != null) { %>
    <div class="message error"><%= error %></div>
    <% } %>

    <% if (success != null) { %>
    <div class="message success"><%= success %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/quiz-challenges" onsubmit="return validateForm()">
        <input type="hidden" name="action" value="send">

        <div class="form-group">
            <label for="userSearch">Search User (Friends Only):</label>
            <input type="text" id="userSearch" placeholder="Type username..." autocomplete="off">
            <div id="searchResults" class="search-results" style="display: none;"></div>
            <input type="hidden" id="challengedUserId" name="challengedUserId" required>
            <div id="selectedUser" class="selected-item" style="display: none;"></div>
        </div>

        <div class="form-group">
            <label for="quizSearch">Search Quiz:</label>
            <input type="text" id="quizSearch" placeholder="Type quiz name..." autocomplete="off">
            <div id="quizResults" class="search-results" style="display: none;"></div>
            <input type="hidden" id="quizId" name="quizId" required>
            <div id="selectedQuiz" class="selected-item" style="display: none;"></div>
        </div>

        <div class="form-group">
            <label for="message">Message (Optional):</label>
            <textarea id="message" name="message" rows="3" placeholder="Challenge message..."></textarea>
        </div>

        <button type="submit" class="btn btn-primary">Send Challenge</button>
        <button type="button" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/quiz-challenges'">Cancel</button>
    </form>
</div>

<script>
    function validateForm() {
        const userId = document.getElementById('challengedUserId').value;
        const quizId = document.getElementById('quizId').value;

        if (!userId) {
            alert('Please select a user to challenge');
            return false;
        }

        if (!quizId) {
            alert('Please select a quiz');
            return false;
        }

        return true;
    }

    document.getElementById('userSearch').addEventListener('input', function() {
        const query = this.value;
        if (query.length < 2) {
            document.getElementById('searchResults').style.display = 'none';
            return;
        }

        fetch('${pageContext.request.contextPath}/search-friends?q=' + encodeURIComponent(query))
            .then(response => response.json())
            .then(users => {
                const resultsDiv = document.getElementById('searchResults');
                resultsDiv.innerHTML = '';

                if (users.length > 0) {
                    users.forEach(user => {
                        const div = document.createElement('div');
                        div.className = 'user-item';
                        div.textContent = user.userName + ' (' + user.firstName + ' ' + user.lastName + ')';
                        div.onclick = function() {
                            document.getElementById('userSearch').value = '';
                            document.getElementById('challengedUserId').value = user.id;
                            document.getElementById('selectedUser').textContent = 'Selected: ' + user.userName;
                            document.getElementById('selectedUser').style.display = 'block';
                            resultsDiv.style.display = 'none';
                        };
                        resultsDiv.appendChild(div);
                    });
                    resultsDiv.style.display = 'block';
                } else {
                    resultsDiv.innerHTML = '<div class="user-item">No friends found</div>';
                    resultsDiv.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error searching users:', error);
            });
    });

    document.getElementById('quizSearch').addEventListener('input', function() {
        const query = this.value;
        if (query.length < 2) {
            document.getElementById('quizResults').style.display = 'none';
            return;
        }

        fetch('${pageContext.request.contextPath}/search-quizzes?q=' + encodeURIComponent(query))
            .then(response => response.json())
            .then(quizzes => {
                const resultsDiv = document.getElementById('quizResults');
                resultsDiv.innerHTML = '';

                if (quizzes.length > 0) {
                    quizzes.forEach(quiz => {
                        const div = document.createElement('div');
                        div.className = 'user-item';
                        div.textContent = quiz.testTitle + ' (by ' + quiz.creatorName + ')';
                        div.onclick = function() {
                            document.getElementById('quizSearch').value = '';
                            document.getElementById('quizId').value = quiz.id;
                            document.getElementById('selectedQuiz').textContent = 'Selected: ' + quiz.testTitle;
                            document.getElementById('selectedQuiz').style.display = 'block';
                            resultsDiv.style.display = 'none';
                        };
                        resultsDiv.appendChild(div);
                    });
                    resultsDiv.style.display = 'block';
                } else {
                    resultsDiv.innerHTML = '<div class="user-item">No quizzes found</div>';
                    resultsDiv.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error searching quizzes:', error);
            });
    });

    document.addEventListener('click', function(e) {
        if (!e.target.closest('.form-group')) {
            document.getElementById('searchResults').style.display = 'none';
            document.getElementById('quizResults').style.display = 'none';
        }
    });
</script>

</body>
</html>