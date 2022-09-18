<%@page import="entities.concretes.Category"%>
<%@page import="java.util.List"%>
<%@page import="results.DataResult"%>
<%@page import="managers.concretes.CategoryManager"%>
<%@page import="managers.abstracts.CategoryManagerI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	CategoryManagerI categoryManager = new CategoryManager();
	DataResult<List<Category>> result = categoryManager.listAll();
	List<Category> categories = result.getData();	
%>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Category Summary</title>
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
    <th>Category Name</th>
  </tr>
  
  <% for(Category category : categories) { 
  	String url = "CategoryDetail.jsp?categoryId=" + category.getCategoryId();
  %>
  
	  <tr>
	    <td><a href=<%=url%>><%=category.getCategoryName()%></a></td>
	  </tr>
  
  <%} %>

</table>



</body>
</html>