<%@page import="managers.abstracts.CategoryManagerI"%>
<%@page import="results.DataResult"%>
<%@page import="managers.concretes.CategoryManager"%>
<%@page import="entities.concretes.Category"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	CategoryManagerI categoryManager = new CategoryManager();
	long categoryId;
	Category category = null;
	
	categoryId = Long.parseLong(request.getParameter("categoryId"));
	DataResult<Category> result = categoryManager.find(categoryId);
	
	category = result.getData();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Category Detail</title>
</head>
<body>
	<% if(category != null) { %>

	<b>Category Id :</b> <%=category.getCategoryId()%> <br/>
	<b>Category Name :</b> <%=category.getCategoryName()%> <br/>
	
	<%} %>
</body>
</html>