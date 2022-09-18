<%@page import="results.DataResult"%>
<%@page import="entities.concretes.Product"%>
<%@page import="managers.concretes.ProductManager"%>
<%@page import="managers.abstracts.ProductManagerI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	ProductManagerI productManager = new ProductManager();
	long productId;
	Product product = null;
	
	productId = Long.parseLong(request.getParameter("productId"));
	DataResult<Product> result = productManager.find(productId);
	
	product = result.getData();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Product Detail</title>
</head>
<body>
	<% if(product != null) { %>

	<b>Product Id :</b> <%=product.getProductId()%> <br/>
	<b>Product Name :</b> <%=product.getProductName()%> <br/>
	<b>Sales Price :</b> <%=product.getSalesPrice()%> <br/>
	<b>Category Name :</b> <%=product.getCategory().getCategoryName()%> <br/>
	
	<%} %>
</body>
</html>