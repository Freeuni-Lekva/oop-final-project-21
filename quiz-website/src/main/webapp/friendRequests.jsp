<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.bean.FriendshipRequest" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%
    LinkedHashMap<FriendshipRequest, UserDTO> requestsWithSenders =
            (LinkedHashMap<FriendshipRequest, UserDTO>) request.getAttribute("requestsWithSenders");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Friend Requests</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f8f9fa;
            padding: 2rem;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .request-card {
            display: flex;
            align-items: center;
            padding: 12px;
            border-bottom: 1px solid #ddd;
        }
        .request-card:last-child {
            border-bottom: none;
        }
        .profile-img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 15px;
        }
        .sender-info {
            flex: 1;
        }
        .sender-info h3 {
            margin: 0;
            font-size: 18px;
        }
        .sender-info p {
            margin: 2px 0;
            color: #555;
        }
        .no-requests {
            text-align: center;
            color: #888;
            font-size: 16px;
            padding: 2rem 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Friend Requests</h2>
    <% if (requestsWithSenders == null || requestsWithSenders.isEmpty()) { %>
    <div class="no-requests">You have no incoming friend requests.</div>
    <% } else {
        for (Map.Entry<FriendshipRequest, UserDTO> entry : requestsWithSenders.entrySet()) {
            UserDTO sender = entry.getValue();
    %>
    <div class="request-card">
        <img class="profile-img" src="<%= sender.getImageURL() != null && !sender.getImageURL().isEmpty()
        ? sender.getImageURL()
        : "https://via.placeholder.com/60" %>" alt="Sender Image">
        <div class="sender-info">
            <h3><%= sender.getUserName() %></h3>
            <p><%= sender.getFirstName() %> <%= sender.getLastName() %></p>
            <form class="friend-response-form"
                  data-sender-id="<%= sender.getId() %>"
                  data-request-id="<%= entry.getKey().getId() %>">
                <button type="submit" name="action" value="accept" class="btn btn-accept">Accept</button>
                <button type="submit" name="action" value="decline" class="btn btn-decline">Decline</button>
            </form>
        </div>
    </div>
    <% }} %>
</div>
<script>
    window.contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/js/respondToFriendRequest.js"></script>
</body>
</html>


