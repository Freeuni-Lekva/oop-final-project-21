<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.DTO.UserDTO" %>
<%@ page import="com.freeuni.quiz.bean.Quiz" %>
<%@ page import="com.freeuni.quiz.bean.Question" %>
<%@ page import="com.freeuni.quiz.bean.QuestionType" %>
<%@ page import="com.freeuni.quiz.bean.Category" %>
<%@ page import="java.util.List" %>
<%
    UserDTO user = (session != null) ? (UserDTO) session.getAttribute("user") : null;
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    boolean isAdmin = user.isAdmin();
    
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Quiz - <%= quiz != null ? quiz.getTestTitle() : "Quiz" %></title>
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
        .editor-tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 30px;
            border-bottom: 2px solid #4b3d6e;
        }
        .editor-tab {
            padding: 10px 20px;
            background-color: transparent;
            color: #b19cd9;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 16px;
        }
        .editor-tab.active {
            background-color: #4b3d6e;
            color: #ffffff;
            border-radius: 8px 8px 0 0;
        }
        .editor-content {
            display: none;
        }
        .editor-content.active {
            display: block;
        }
        .question-list {
            display: grid;
            gap: 15px;
        }
        .question-item {
            background-color: #240955;
            padding: 20px;
            border-radius: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .question-info {
            flex: 1;
        }
        .question-type-badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            margin-bottom: 5px;
        }
        .question-type-text {
            background-color: #28a745;
            color: white;
        }
        .question-type-multiple {
            background-color: #ffc107;
            color: #000;
        }
        .question-type-image {
            background-color: #17a2b8;
            color: white;
        }
        .question-actions {
            display: flex;
            gap: 10px;
        }
        .add-question-section {
            background-color: #240955;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 20px;
        }
        .question-type-selector {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }
        .question-type-option {
            background-color: #13081f;
            border: 2px solid #4b3d6e;
            padding: 20px;
            border-radius: 10px;
            cursor: pointer;
            text-align: center;
            transition: all 0.3s ease;
        }
        .question-type-option:hover {
            background-color: #4b3d6e;
            transform: scale(1.05);
        }
        .question-type-option.selected {
            background-color: #6a5acd;
            border-color: #8a2be2;
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
        <a href="${pageContext.request.contextPath}/achievements">🏆 Achievements</a>
        <a href="${pageContext.request.contextPath}/inbox">💬 Messages</a>
        <a href="${pageContext.request.contextPath}/challenges">🎯 Challenges</a>
        <a href="${pageContext.request.contextPath}/history">📊 History</a>
        <% if (isAdmin) { %>
        <a href="${pageContext.request.contextPath}/admin">🛠️ Admin Panel</a>
        <% } %>
    </div>

    <div class="main-content">
        <div class="quiz-container">
            <% if (quiz == null) { %>
                <div class="quiz-card" style="text-align: center; padding: 60px;">
                    <p style="color: #b19cd9; font-size: 18px;">Quiz not found.</p>
                    <a href="quiz-manager" class="quiz-btn quiz-btn-primary" style="margin-top: 20px;">
                        Back to My Quizzes
                    </a>
                </div>
            <% } else { %>
                <h2>Edit Quiz: <%= quiz.getTestTitle() %></h2>
                
                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="quiz-error">
                    <%= request.getAttribute("errorMessage") %>
                </div>
                <% } %>
                
                <% if (request.getAttribute("successMessage") != null) { %>
                <div class="quiz-success">
                    <%= request.getAttribute("successMessage") %>
                </div>
                <% } %>
                
                <!-- Editor Tabs -->
                <div class="editor-tabs">
                    <button class="editor-tab active" onclick="showTab('details')">Quiz Details</button>
                    <button class="editor-tab" onclick="showTab('questions')">Questions</button>
                    <button class="editor-tab" onclick="showTab('add-question')">Add Question</button>
                </div>
                
                <!-- Quiz Details Tab -->
                <div id="details-tab" class="editor-content active">
                    <div class="quiz-form">
                        <form method="post" action="quiz-editor">
                            <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                            <input type="hidden" name="action" value="updateDetails">
                            
                            <div class="form-group">
                                <label for="title">Quiz Title *</label>
                                <input type="text" id="title" name="title" class="form-control" 
                                       required value="<%= quiz.getTestTitle() %>">
                            </div>
                            
                            <div class="form-group">
                                <label for="description">Description *</label>
                                <textarea id="description" name="description" class="form-control" 
                                          required rows="4"><%= quiz.getTestDescription() %></textarea>
                            </div>
                            
                            <div class="form-group">
                                <label for="category">Category</label>
                                <select id="category" name="categoryId" class="form-control">
                                    <option value="">Select a category</option>
                                    <% 
                                        if (categories != null) {
                                            for (Category category : categories) {
                                                String selected = "";
                                                if (quiz.getCategoryId() != null && 
                                                    quiz.getCategoryId().equals(category.getId())) {
                                                    selected = "selected";
                                                }
                                    %>
                                        <option value="<%= category.getId() %>" <%= selected %>>
                                            <%= category.getCategoryName() %>
                                        </option>
                                    <% 
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="timeLimit">Time Limit (minutes)</label>
                                <input type="number" id="timeLimit" name="timeLimit" class="form-control" 
                                       min="0" max="1440" value="<%= quiz.getTimeLimitMinutes() %>">
                            </div>
                            
                            <div class="quiz-actions">
                                <button type="submit" class="quiz-btn quiz-btn-primary">Save Changes</button>
                                <a href="quiz-manager" class="quiz-btn quiz-btn-outline">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Questions Tab -->
                <div id="questions-tab" class="editor-content">
                    <% if (questions == null || questions.isEmpty()) { %>
                        <div class="quiz-card" style="text-align: center; padding: 40px;">
                            <p style="color: #b19cd9;">No questions added yet.</p>
                            <button class="quiz-btn quiz-btn-primary" onclick="showTab('add-question')" style="margin-top: 20px;">
                                Add First Question
                            </button>
                        </div>
                    <% } else { %>
                        <div class="question-list">
                            <% 
                                int questionIndex = 1;
                                for (Question question : questions) { 
                                    String typeBadgeClass = "";
                                    String typeLabel = "";
                                    
                                    if (question.getQuestionType() == QuestionType.TEXT) {
                                        typeBadgeClass = "question-type-text";
                                        typeLabel = "Text";
                                    } else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                                        typeBadgeClass = "question-type-multiple";
                                        typeLabel = "Multiple Choice";
                                    } else if (question.getQuestionType() == QuestionType.IMAGE) {
                                        typeBadgeClass = "question-type-image";
                                        typeLabel = "Image";
                                    }
                            %>
                                <div class="question-item">
                                    <div class="question-info">
                                        <span class="question-type-badge <%= typeBadgeClass %>"><%= typeLabel %></span>
                                        <h4 style="color: #ffffff; margin: 10px 0;">Question <%= questionIndex %></h4>
                                        <p style="color: #d1d8ff;"><%= question.getQuestionTitle() %></p>
                                        <p style="color: #b19cd9; font-size: 14px;">Points: <%= question.getPoints() %></p>
                                    </div>
                                    <div class="question-actions">
                                        <button onclick="deleteQuestion(<%= question.getId() %>)" class="quiz-btn quiz-btn-danger">Delete</button>
                                    </div>
                                </div>
                            <%
                                    questionIndex++;
                                } 
                            %>
                        </div>
                    <% } %>
                </div>
                
                <!-- Add Question Tab -->
                <div id="add-question-tab" class="editor-content">
                    <div class="add-question-section">
                        <h3 style="color: #ffffff; margin-bottom: 20px;">Add New Question</h3>
                        
                        <div class="question-type-selector">
                            <div class="question-type-option" onclick="selectQuestionType('TEXT')">
                                <h4 style="color: #ffffff;">📝 Text Question</h4>
                                <p style="color: #d1d8ff; font-size: 14px;">Open-ended text response</p>
                            </div>
                            <div class="question-type-option" onclick="selectQuestionType('MULTIPLE_CHOICE')">
                                <h4 style="color: #ffffff;">☑️ Multiple Choice</h4>
                                <p style="color: #d1d8ff; font-size: 14px;">Single correct answer</p>
                            </div>
                            <div class="question-type-option" onclick="selectQuestionType('IMAGE')">
                                <h4 style="color: #ffffff;">🖼️ Image Question</h4>
                                <p style="color: #d1d8ff; font-size: 14px;">Question with image</p>
                            </div>
                        </div>
                        
                        <form id="add-question-form" method="post" action="quiz-editor" style="display: none;">
                            <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                            <input type="hidden" name="action" value="addQuestion">
                            <input type="hidden" name="questionType" id="questionType">
                            
                            <div class="form-group">
                                <label for="questionText">Question Text *</label>
                                <textarea id="questionText" name="questionText" class="form-control" 
                                          required rows="3" placeholder="Enter your question"></textarea>
                            </div>
                            
                            <div class="form-group">
                                <label for="points">Points *</label>
                                <input type="number" id="points" name="points" class="form-control" 
                                       required min="1" max="100" value="10">
                            </div>
                            
                            <!-- Multiple Choice Options -->
                            <div id="multiple-choice-options" style="display: none;">
                                <div class="form-group">
                                    <label>Answer Options *</label>
                                    <div id="options-container">
                                        <div class="option-input" style="margin-bottom: 10px;">
                                            <input type="text" name="options[]" class="form-control" placeholder="Option 1">
                                        </div>
                                        <div class="option-input" style="margin-bottom: 10px;">
                                            <input type="text" name="options[]" class="form-control" placeholder="Option 2">
                                        </div>
                                    </div>
                                    <button type="button" onclick="addOption()" class="quiz-btn quiz-btn-outline" style="margin-top: 10px;">
                                        Add Option
                                    </button>
                                </div>
                                
                                <div class="form-group">
                                    <label for="correctAnswer">Correct Answer Index (0-based) *</label>
                                    <input type="number" id="correctAnswer" name="correctAnswer" class="form-control" 
                                           min="0" placeholder="0 for first option, 1 for second, etc.">
                                </div>
                            </div>
                            
                            <!-- Image Question Options -->
                            <div id="image-options" style="display: none;">
                                <div class="form-group">
                                    <label for="imageUrl">Image URL *</label>
                                    <input type="text" id="imageUrl" name="imageUrl" class="form-control" 
                                           placeholder="Enter image URL">
                                </div>
                                <div class="form-group">
                                    <label for="expectedAnswer">Expected Answer *</label>
                                    <textarea id="expectedAnswer" name="expectedAnswer" class="form-control" 
                                              required rows="2" placeholder="Enter the expected answer for this image question"></textarea>
                                </div>
                            </div>
                            
                            <!-- Text Question Options -->
                            <div id="text-options" style="display: none;">
                                <div class="form-group">
                                    <label for="expectedAnswer">Expected Answer (Optional)</label>
                                    <textarea id="expectedAnswer" name="expectedAnswer" class="form-control" 
                                              rows="2" placeholder="Enter the expected answer for reference"></textarea>
                                </div>
                            </div>
                            
                            <div class="quiz-actions">
                                <button type="submit" class="quiz-btn quiz-btn-primary">Add Question</button>
                                <button type="button" onclick="cancelAddQuestion()" class="quiz-btn quiz-btn-outline">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
    
    <script>
        function showTab(tabName) {
            // Hide all tabs
            document.querySelectorAll('.editor-content').forEach(tab => {
                tab.classList.remove('active');
            });
            document.querySelectorAll('.editor-tab').forEach(tab => {
                tab.classList.remove('active');
            });
            
            // Show selected tab
            document.getElementById(tabName + '-tab').classList.add('active');
            event.target.classList.add('active');
        }
        
        function selectQuestionType(type) {
            // Update hidden field
            document.getElementById('questionType').value = type;
            
            // Show form
            document.getElementById('add-question-form').style.display = 'block';
            
            // Hide all type-specific options
            document.getElementById('multiple-choice-options').style.display = 'none';
            document.getElementById('image-options').style.display = 'none';
            document.getElementById('text-options').style.display = 'none';
            
            // Show relevant options
            if (type === 'MULTIPLE_CHOICE') {
                document.getElementById('multiple-choice-options').style.display = 'block';
            } else if (type === 'IMAGE') {
                document.getElementById('image-options').style.display = 'block';
            } else if (type === 'TEXT') {
                document.getElementById('text-options').style.display = 'block';
            }
            
            // Update selected state
            document.querySelectorAll('.question-type-option').forEach(option => {
                option.classList.remove('selected');
            });
            event.currentTarget.classList.add('selected');
        }
        
        function addOption() {
            const container = document.getElementById('options-container');
            const optionCount = container.querySelectorAll('.option-input').length;
            const newOption = document.createElement('div');
            newOption.className = 'option-input';
            newOption.style.marginBottom = '10px';
            newOption.innerHTML = '<input type="text" name="options[]" class="form-control" placeholder="Option ' + (optionCount + 1) + '">';
            container.appendChild(newOption);
        }
        
        function cancelAddQuestion() {
            document.getElementById('add-question-form').style.display = 'none';
            document.getElementById('add-question-form').reset();
            document.querySelectorAll('.question-type-option').forEach(option => {
                option.classList.remove('selected');
            });
        }
        
        function deleteQuestion(questionId) {
            if (confirm('Are you sure you want to delete this question?')) {
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'quiz-editor';
                
                const quizIdInput = document.createElement('input');
                quizIdInput.type = 'hidden';
                quizIdInput.name = 'quizId';
                quizIdInput.value = '<%= quiz != null ? quiz.getId() : "" %>';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'deleteQuestion';
                
                const questionIdInput = document.createElement('input');
                questionIdInput.type = 'hidden';
                questionIdInput.name = 'questionId';
                questionIdInput.value = questionId;
                
                form.appendChild(quizIdInput);
                form.appendChild(actionInput);
                form.appendChild(questionIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html> 