<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #13081f;
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .error-container {
            max-width: 600px;
            padding: 40px;
            text-align: center;
        }
        .error-icon {
            font-size: 72px;
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 36px;
            color: #ffffff;
            margin-bottom: 15px;
        }
        .error-message {
            font-size: 18px;
            color: #d1d8ff;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .error-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        .error-btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }
        .error-btn-primary {
            background: linear-gradient(45deg, #6a5acd, #8a2be2);
            color: white;
        }
        .error-btn-primary:hover {
            background: linear-gradient(45deg, #5a4abd, #7a1bd2);
            transform: scale(1.05);
        }
        .error-btn-secondary {
            background: transparent;
            border: 2px solid #b19cd9;
            color: #b19cd9;
        }
        .error-btn-secondary:hover {
            background: #b19cd9;
            color: #13081f;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-title">Oops! Something went wrong</h1>
        <div class="error-message">
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
                <%= errorMessage %>
            <% } else { %>
                We encountered an unexpected error while processing your request. 
                Please try again or contact support if the problem persists.
            <% } %>
        </div>
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/home.jsp" class="error-btn error-btn-primary">
                🏠 Go to Home
            </a>
            <a href="javascript:history.back()" class="error-btn error-btn-secondary">
                ← Go Back
            </a>
        </div>
    </div>
</body>
</html> 