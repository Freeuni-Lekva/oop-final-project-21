<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Registration Success</title>
  <style>
    body, html {
      height: 100%;
      margin: 0;
      font-family: Arial, sans-serif;
    }
    .centered {
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;  /* vertical center */
      align-items: center;      /* horizontal center */
      text-align: center;
    }
    a {
      margin-top: 20px;
      text-decoration: none;
      color: #007BFF;
      font-weight: bold;
    }
    a:hover {
      text-decoration: underline;
    }
  </style>
</head>
<body>
<div class="centered">
  <h2>${message}</h2>
  <p><a href="login.jsp">Click here to login</a></p>
</div>
</body>
</html>
