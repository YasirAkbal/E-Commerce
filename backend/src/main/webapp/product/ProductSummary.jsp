<%@page import="entities.concretes.Product"%>
<%@page import="java.util.List"%>
<%@page import="results.DataResult"%>
<%@page import="managers.abstracts.ProductManagerI"%>
<%@page import="managers.concretes.ProductManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	ProductManagerI productManager = new ProductManager();
	DataResult<List<Product>> result = productManager.listAll();
	List<Product> products = result.getData();	
%> 
<html>
<head>
<meta charset="UTF-8">
<title>Product Summary</title>
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
    <th>Product Name</th>
  </tr>
  
  <% for(Product product : products) { 
  	String url = "ProductDetail.jsp?productId=" + product.getProductId();
  %>
  
	  <tr>
	    <td><a href=<%=url%>><%=product.getProductName()%></a></td>
	  </tr>
  
  <%} %>

</table>


</body>
</html>