<%@page import="results.DataResult"%>
<%@page import="entities.concretes.User"%>
<%@page import="managers.concretes.UserManager"%>
<%@page import="managers.abstracts.UserManagerI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	UserManagerI userManager = new UserManager();
	long userId;
	User user = null;
	
	if(request.getParameter("userId") != null) {
		userId = Long.parseLong(request.getParameter("userId"));
		DataResult<User> userResult = userManager.find(userId);
		if(userResult.isSuccess()) {
			user = userResult.getData();
		}
	}
	
%>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<% if(user != null) { %>

	<b>User Id :</b> <%=user.getId()%> <br/>
	<b>Username :</b> <%=user.getUsername()%> <br/>
	
	<%} %>
</body>
</html>