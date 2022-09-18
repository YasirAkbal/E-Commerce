<%@page import="entities.concretes.User"%>
<%@page import="java.util.List"%>
<%@page import="results.DataResult"%>
<%@page import="managers.abstracts.UserManagerI"%>
<%@page import="managers.concretes.UserManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	List<User> users = null;
	UserManagerI userManager = new UserManager();
	DataResult<List<User>> result = userManager.listAll();
	if(result.isSuccess()) {
		users = result.getData();
	}
%> 
<html>
<head>
<meta charset="UTF-8">
<title>User Summary</title>
</head>
<style>
table, th, td {
  border:1px solid black;
  text-align: center;
}
</style>
<body>

<table style="width:80%" class="center">

  <tr>
    <th>Username</th>
  </tr>
 
 <% if(users != null) { %>
	  <% for(User user : users) { 
	  	String url = "UserDetail.jsp?userId=" + user.getId();
	  %>
	  
		  <tr>
		    <td><a href=<%=url%>><%=user.getUsername()%></a></td>
		  </tr>
	  
	  <%} %>
 <%} %>
</table>

</body>
</html>