<%@page import="xmlUtils.abstracts.XmlUtilI"%>
<%@page import="xmlUtils.concretes.CategoryXmlUtil"%>
<%@page import="entities.concretes.Category"%>
<%@page import="frontend.WebApiUrls"%>
<%@page import="java.util.List"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="utils.XmlUtils"%>
<%@page import="utils.WebUtils"%>
<%@page import="java.io.InputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	XmlUtilI<Category> categoryXmlUtil = new CategoryXmlUtil();
	InputStream in = WebUtils.get(WebApiUrls.categoryListAll);
	List<Category> categoryList = categoryXmlUtil.parseList(XmlUtils.parse(in));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Category List</title>
</head>
<body>

<ul>
  <% for(Category category : categoryList) { %>
  	<% String url = String.format("ProductList.jsp?categoryId=%d",category.getCategoryId()); %>
  	<li><a href=<%=url%>><%=category.getCategoryName()%></a></li>
 	
  <%} %>
</ul>

</body>
</html>