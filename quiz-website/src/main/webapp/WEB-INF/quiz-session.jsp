<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.Question" %>
<%@ page import="com.freeuni.quiz.bean.QuestionType" %>
<%@ page import="com.freeuni.quiz.bean.QuizSession" %>
<%@ page import="java.util.List" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    QuizSession quizSession = (QuizSession) request.getAttribute("quizSession");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Integer currentQuestionIndex = (Integer) request.getAttribute("currentQuestionIndex");
    if (currentQuestionIndex == null) currentQuestionIndex = 0;
    
    Question currentQuestion = (questions != null && !questions.isEmpty() && currentQuestionIndex < questions.size()) 
        ? questions.get(currentQuestionIndex) : null;
        
    Long remainingTimeSeconds = (Long) request.getAttribute("remainingTimeSeconds");
    Long quizStartTime = (Long) request.getAttribute("quizStartTime");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Session - <%= quiz != null ? quiz.getTestTitle() : "Quiz" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buttons.css">
    <style>
        .quiz-session-header {
            background-color: #240955;
            padding: 20px;
            margin-bottom: 30px;
            border-radius: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }
        
        .quiz-timer {
            font-size: 24px;
            color: #ffffff;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .quiz-timer.warning {
            color: #ffc107;
        }
        
        .quiz-timer.danger {
            color: #dc3545;
        }
        
        .quiz-progress {
            flex: 1;
            max-width: 400px;
        }
        
        .progress-bar {
            background-color: #13081f;
            height: 10px;
            border-radius: 5px;
            overflow: hidden;
        }
        
        .progress-fill {
            background: linear-gradient(45deg, #6a5acd, #8a2be2);
            height: 100%;
            transition: width 0.3s ease;
        }
        
        .progress-text {
            color: #d1d8ff;
            font-size: 14px;
            margin-top: 5px;
            text-align: center;
        }
        
        .question-container {
            background-color: #240955;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
        }
        
        .question-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .question-number {
            color: #b19cd9;
            font-size: 18px;
        }
        
        .question-points {
            color: #ffffff;
            font-size: 16px;
            background-color: #4b3d6e;
            padding: 5px 15px;
            border-radius: 20px;
        }
        
        .question-text {
            color: #ffffff;
            font-size: 20px;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .answer-options {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .answer-option {
            background-color: #13081f;
            border: 2px solid #4b3d6e;
            padding: 15px 20px;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            color: #d1d8ff;
        }
        
        .answer-option:hover {
            background-color: #4b3d6e;
            transform: translateX(5px);
        }
        
        .answer-option.selected {
            background-color: #6a5acd;
            border-color: #8a2be2;
            color: #ffffff;
        }
        
        .answer-option input[type="radio"],
        .answer-option input[type="checkbox"] {
            margin-right: 10px;
        }
        
        .text-answer {
            width: 100%;
            padding: 15px;
            background-color: #13081f;
            border: 2px solid #4b3d6e;
            color: #ffffff;
            border-radius: 10px;
            font-size: 16px;
        }
        
        .text-answer:focus {
            outline: none;
            border-color: #b19cd9;
        }
        
        .quiz-navigation {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 30px;
            flex-wrap: wrap;
            gap: 15px;
        }
        
        .nav-btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .nav-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .nav-btn-primary {
            background: linear-gradient(45deg, #6a5acd, #8a2be2);
            color: white;
        }
        
        .nav-btn-primary:hover:not(:disabled) {
            background: linear-gradient(45deg, #5a4abd, #7a1bd2);
            transform: scale(1.05);
        }
        
        .nav-btn-secondary {
            background: transparent;
            border: 2px solid #b19cd9;
            color: #b19cd9;
        }
        
        .nav-btn-secondary:hover:not(:disabled) {
            background: #b19cd9;
            color: #13081f;
        }
        
        .nav-btn-danger {
            background: linear-gradient(45deg, #dc3545, #c82333);
            color: white;
        }
        
        .nav-btn-danger:hover {
            background: linear-gradient(45deg, #c82333, #bd2130);
        }
        
        .image-question img {
            max-width: 100%;
            max-height: 400px;
            margin: 20px 0;
            border-radius: 10px;
        }
        
        @media (max-width: 768px) {
            .quiz-session-header {
                flex-direction: column;
                text-align: center;
            }
            
            .quiz-progress {
                width: 100%;
            }
            
            .quiz-navigation {
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <div class="quiz-container">
        <% if (quiz == null || questions == null || questions.isEmpty()) { %>
            <div class="quiz-card" style="text-align: center; padding: 60px;">
                <p style="color: #b19cd9; font-size: 18px;">Unable to start quiz session.</p>
                <a href="quiz-browser" class="quiz-btn quiz-btn-primary" style="margin-top: 20px;">
                    Browse Quizzes
                </a>
            </div>
        <% } else { %>
            <!-- Quiz Header -->
            <div class="quiz-session-header">
                <h2 style="color: #ffffff; margin: 0;"><%= quiz.getTestTitle() %></h2>
                
                <div class="quiz-progress">
                    <div class="progress-bar">
                        <% int progressPercentage = ((currentQuestionIndex + 1) * 100) / questions.size(); %>
                        <div class="progress-fill" style="width: <%= progressPercentage %>%;"></div>
                    </div>
                    <div class="progress-text">
                        Question <%= currentQuestionIndex + 1 %> of <%= questions.size() %>
                    </div>
                </div>
                
                <% if (quiz.getTimeLimitMinutes() != null && quiz.getTimeLimitMinutes() > 0 && remainingTimeSeconds != null) { %>
                    <div class="quiz-timer" id="timer">
                        <span>⏱️</span>
                        <span id="timer-display">
                            <% 
                                long minutes = remainingTimeSeconds / 60;
                                long seconds = remainingTimeSeconds % 60;
                            %>
                            <%= minutes %>:<%= seconds < 10 ? "0" + seconds : seconds %>
                        </span>
                    </div>
                <% } %>
            </div>
            
            <!-- Current Question -->
            <% if (currentQuestion != null) { %>
                <div class="question-container">
                    <div class="question-header">
                        <span class="question-number">Question <%= currentQuestionIndex + 1 %></span>
                        <span class="question-points"><%= currentQuestion.getPoints() != null ? currentQuestion.getPoints() : 10.0 %> points</span>
                    </div>
                    
                    <div class="question-text">
                        <%= currentQuestion.getQuestionTitle() %>
                    </div>
                    
                    <form id="answer-form" method="post" action="quiz-session">
                        <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                        <input type="hidden" name="sessionId" value="<%= quizSession != null ? quizSession.getId() : "" %>">
                        <input type="hidden" name="questionIndex" value="<%= currentQuestionIndex %>">
                        <input type="hidden" name="questionId" value="<%= currentQuestion.getId() %>">
                        
                        <% if (currentQuestion.getQuestionType() == QuestionType.TEXT) { %>
                            <textarea name="answer" class="text-answer" rows="4" 
                                      placeholder="Type your answer here..."></textarea>
                        
                        <% } else if (currentQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE) { %>
                            <div class="answer-options">
                                <%
                                    if (currentQuestion.getQuestionHandler() != null) {
                                        java.util.Map<String, Object> viewData = currentQuestion.getQuestionHandler().getViewData();
                                        @SuppressWarnings("unchecked")
                                        java.util.List<String> options = (java.util.List<String>) viewData.get("choiceOptions");
                                        if (options != null) {
                                            for (int i = 0; i < options.size(); i++) {
                                %>
                                    <label class="answer-option">
                                        <input type="radio" name="answer" value="<%= i %>">
                                        <%= options.get(i) %>
                                    </label>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </div>
                        
                        <% } else if (currentQuestion.getQuestionType() == QuestionType.IMAGE) { %>
                            <div class="image-question">
                                <%
                                    if (currentQuestion.getQuestionHandler() != null) {
                                        java.util.Map<String, Object> viewData = currentQuestion.getQuestionHandler().getViewData();
                                        String imageUrl = (String) viewData.get("imageUrl");
                                        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                                %>
                                    <img src="<%= imageUrl %>" alt="Question Image" 
                                         onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                                    <div style="display:none; background-color: #4b3d6e; padding: 20px; border-radius: 10px; text-align: center; color: #b19cd9;">
                                        <p>⚠️ Failed to load image</p>
                                        <small><%= imageUrl %></small>
                                    </div>
                                <%
                                        }
                                    }
                                %>
                                <textarea name="answer" class="text-answer" rows="2" 
                                          placeholder="Enter your answer here..."></textarea>
                            </div>
                        <% } %>
                        
                        <!-- Navigation -->
                        <div class="quiz-navigation">
                            <button type="submit" name="action" value="previous" 
                                    class="nav-btn nav-btn-secondary"
                                    <%= currentQuestionIndex == 0 ? "disabled" : "" %>>
                                ← Previous
                            </button>
                            
                            <% if (currentQuestionIndex < questions.size() - 1) { %>
                                <button type="submit" name="action" value="next" 
                                        class="nav-btn nav-btn-primary">
                                    Next →
                                </button>
                            <% } else { %>
                                <button type="submit" name="action" value="finish" 
                                        class="nav-btn nav-btn-danger"
                                        onclick="return confirm('Are you sure you want to submit the quiz?')">
                                    Finish Quiz
                                </button>
                            <% } %>
                        </div>
                    </form>
                </div>
            <% } %>
        <% } %>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/quiz.js"></script>
    <script>
        // Timer functionality
        <% if (quiz != null && quiz.getTimeLimitMinutes() != null && quiz.getTimeLimitMinutes() > 0 && quizStartTime != null) { %>
            var quizStartTime = <%= quizStartTime %>;
            var timeLimit = <%= quiz.getTimeLimitMinutes() %> * 60 * 1000; // Convert to milliseconds
            
            function updateTimer() {
                let elapsed = Date.now() - quizStartTime;
                let remaining = Math.max(0, timeLimit - elapsed);
                
                let totalSeconds = Math.floor(remaining / 1000);
                let minutes = Math.floor(totalSeconds / 60);
                let seconds = totalSeconds % 60;
                
                let display = minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
                
                let timerDisplay = document.getElementById('timer-display');
                if (timerDisplay) {
                    timerDisplay.textContent = display;
                }
                
                let timerElement = document.getElementById('timer');
                if (timerElement) {
                    if (totalSeconds <= 60) {
                        timerElement.classList.add('danger');
                    } else if (totalSeconds <= 300) {
                        timerElement.classList.add('warning');
                    }
                }
                
                if (remaining <= 0) {
                    // Auto-submit when time runs out
                    let form = document.getElementById('answer-form');
                    if (form) {
                        let finishInput = document.createElement('input');
                        finishInput.type = 'hidden';
                        finishInput.name = 'action';
                        finishInput.value = 'finish';
                        form.appendChild(finishInput);
                        form.submit();
                    }
                }
            }
            
            setInterval(updateTimer, 1000);
            updateTimer();
        <% } %>
        
        // Make answer options clickable
        document.querySelectorAll('.answer-option').forEach(option => {
            option.addEventListener('click', function() {
                let radio = this.querySelector('input[type="radio"]');
                if (radio) {
                    radio.checked = true;
                    document.querySelectorAll('.answer-option').forEach(opt => {
                        opt.classList.remove('selected');
                    });
                    this.classList.add('selected');
                }
            });
        });
        
        // No beforeunload warnings - quiz saves automatically
        
        // Load saved answer from server-side session (not localStorage to avoid conflicts)
        <% 
            String currentAnswerStr = (String) request.getAttribute("currentAnswer");
            if (currentAnswerStr != null && !currentAnswerStr.isEmpty()) {
        %>
            window.addEventListener('load', function() {
                let savedAnswer = '<%= currentAnswerStr.replace("'", "\\'") %>';
                if (savedAnswer) {
                    let input = document.querySelector('input[name="answer"][value="' + savedAnswer + '"], textarea[name="answer"]');
                    if (input) {
                        if (input.type === 'radio') {
                            input.checked = true;
                            input.closest('.answer-option').classList.add('selected');
                        } else {
                            input.value = savedAnswer;
                        }
                    }
                }
            });
        <% } %>
    </script>
</body>
</html> 