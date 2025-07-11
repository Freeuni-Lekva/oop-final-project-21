<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quiz.bean.User" %>
<%@ page import="com.freeuni.quiz.bean.Category" %>
<%@ page import="com.freeuni.quiz.DTO.AnnouncementDTO" %>
<%@ page import="java.util.List" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<AnnouncementDTO> announcements = (List<AnnouncementDTO>) request.getAttribute("announcements");
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Panel - Quiz App</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .section {
            margin-bottom: 40px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .section h2 {
            color: #555;
            margin-top: 0;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            height: 80px;
            resize: vertical;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
        .form-row {
            display: flex;
            gap: 15px;
            align-items: end;
        }
        .form-row .form-group {
            flex: 1;
            margin-bottom: 0;
        }
        .admin-badge {
            background-color: #ffc107;
            color: #000;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="back-link">← Back to Home</a>
        
        <h1>🛠️ Admin Panel</h1>
        
        <% if (message != null) { %>
            <div class="alert alert-<%= messageType %>">
                <%= message %>
            </div>
        <% } %>
        
        <!-- Announcement Management Section -->
        <div class="section">
            <h2>📢 Announcement Management</h2>
            
            <!-- Create Announcement Form -->
            <form method="post" action="admin">
                <input type="hidden" name="action" value="createAnnouncement">
                <div class="form-group">
                    <label for="announcementTitle">Title:</label>
                    <input type="text" id="announcementTitle" name="announcementTitle" required maxlength="255">
                </div>
                <div class="form-group">
                    <label for="announcementContent">Content:</label>
                    <textarea id="announcementContent" name="announcementContent" required maxlength="1000"></textarea>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-success">Create Announcement</button>
                </div>
            </form>
            
            <!-- Announcements Table -->
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Content</th>
                        <th>Author</th>
                        <th>Created</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (announcements != null && !announcements.isEmpty()) { %>
                        <% for (AnnouncementDTO announcement : announcements) { %>
                            <tr>
                                <td><%= announcement.getId() %></td>
                                <td><%= announcement.getTitle() %></td>
                                <td><%= announcement.getContent().length() > 100 ? announcement.getContent().substring(0, 100) + "..." : announcement.getContent() %></td>
                                <td><%= announcement.getAuthorName() != null ? announcement.getAuthorName() : "Unknown" %></td>
                                <td><%= announcement.getCreatedAt() %></td>
                                <td>
                                    <% if (announcement.isActive()) { %>
                                        <span style="color: green;">Active</span>
                                    <% } else { %>
                                        <span style="color: red;">Inactive</span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (announcement.isActive()) { %>
                                        <form method="post" action="admin" style="display: inline;">
                                            <input type="hidden" name="action" value="deactivateAnnouncement">
                                            <input type="hidden" name="announcementId" value="<%= announcement.getId() %>">
                                            <button type="submit" class="btn btn-danger" 
                                                    onclick="return confirm('Are you sure you want to deactivate this announcement?')">
                                                Deactivate
                                            </button>
                                        </form>
                                    <% } %>
                                    <form method="post" action="admin" style="display: inline;">
                                        <input type="hidden" name="action" value="deleteAnnouncement">
                                        <input type="hidden" name="announcementId" value="<%= announcement.getId() %>">
                                        <button type="submit" class="btn btn-danger" 
                                                onclick="return confirm('Are you sure you want to delete this announcement?')">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="7" style="text-align: center; color: #666;">No announcements found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <!-- Category Management Section -->
        <div class="section">
            <h2>📁 Category Management</h2>
            
            <!-- Create Category Form -->
            <form method="post" action="admin">
                <input type="hidden" name="action" value="createCategory">
                <div class="form-row">
                    <div class="form-group">
                        <label for="categoryName">Category Name:</label>
                        <input type="text" id="categoryName" name="categoryName" required maxlength="100">
                    </div>
                    <div class="form-group">
                        <label for="categoryDescription">Description:</label>
                        <input type="text" id="categoryDescription" name="categoryDescription" maxlength="500">
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-success">Create Category</button>
                    </div>
                </div>
            </form>
            
            <!-- Categories Table -->
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Active</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (categories != null && !categories.isEmpty()) { %>
                        <% for (Category category : categories) { %>
                            <tr>
                                <td><%= category.getId() %></td>
                                <td><%= category.getCategoryName() %></td>
                                <td><%= category.getDescription() != null ? category.getDescription() : "" %></td>
                                <td><%= category.isActive() ? "Yes" : "No" %></td>
                                <td>
                                    <form method="post" action="admin" style="display: inline;">
                                        <input type="hidden" name="action" value="deleteCategory">
                                        <input type="hidden" name="categoryId" value="<%= category.getId() %>">
                                        <button type="submit" class="btn btn-danger" 
                                                onclick="return confirm('Are you sure you want to delete this category?')">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="5" style="text-align: center; color: #666;">No categories found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <!-- User Management Section -->
        <div class="section">
            <h2>👥 User Management</h2>
            
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (users != null && !users.isEmpty()) { %>
                        <% for (User user : users) { %>
                            <tr>
                                <td><%= user.getId() %></td>
                                <td><%= user.getUserName() %></td>
                                <td><%= user.getEmail() %></td>
                                <td><%= user.getFirstName() %></td>
                                <td><%= user.getLastName() %></td>
                                <td>
                                    <% if (user.isAdmin()) { %>
                                        <span class="admin-badge">Admin</span>
                                    <% } else { %>
                                        User
                                    <% } %>
                                </td>
                                <td>
                                    <form method="post" action="admin" style="display: inline;">
                                        <input type="hidden" name="action" value="deleteUser">
                                        <input type="hidden" name="userId" value="<%= user.getId() %>">
                                        <button type="submit" class="btn btn-danger" 
                                                onclick="return confirm('Are you sure you want to delete this user?')"
                                                <% if (user.isAdmin()) { %>disabled title="Cannot delete admin users"<% } %>>
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="7" style="text-align: center; color: #666;">No users found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <!-- Statistics Section -->
        <div class="section">
            <h2>📊 Statistics</h2>
            <p><strong>Total Users:</strong> <%= users != null ? users.size() : 0 %></p>
            <p><strong>Total Categories:</strong> <%= categories != null ? categories.size() : 0 %></p>
            <p><strong>Total Announcements:</strong> <%= announcements != null ? announcements.size() : 0 %></p>
            <p><strong>Active Announcements:</strong> 
                <%
                    int activeAnnouncementCount = 0;
                    if (announcements != null) {
                        for (AnnouncementDTO announcement : announcements) {
                            if (announcement.isActive()) {
                                activeAnnouncementCount++;
                            }
                        }
                    }
                %>
                <%= activeAnnouncementCount %>
            </p>
            <p><strong>Admin Users:</strong> 
                <%
                    int adminCount = 0;
                    if (users != null) {
                        for (User user : users) {
                            if (user.isAdmin()) {
                                adminCount++;
                            }
                        }
                    }
                %>
                <%= adminCount %>
            </p>
        </div>
    </div>
</body>
</html> 