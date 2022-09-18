<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String username = (String)session.getAttribute("username");
	if(username != null) {
		session.removeAttribute("username");
	} else {
		response.sendRedirect("UserLogin.jsp");
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Logout</title>
</head>
<body>
	Çıkış yapıldı.
</body>
</html>