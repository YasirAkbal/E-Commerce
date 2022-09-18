<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String username = (String)session.getAttribute("username");
	if(username == null) {
		response.sendRedirect("UserLogin.jsp");
	} 
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Welcome</title>
</head>
<body>

	Hoşgeldin <b><%=(String)session.getAttribute("username") %></b>

</body>
</html>