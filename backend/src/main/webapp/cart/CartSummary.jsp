<%@page import="entities.concretes.Cart"%>
<%@page import="java.util.List"%>
<%@page import="results.DataResult"%>
<%@page import="managers.concretes.CartManager"%>
<%@page import="managers.abstracts.CartManagerI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	CartManagerI cartManager = new CartManager();
	DataResult<List<Cart>> result = cartManager.listAll();
	List<Cart> carts = result.getData();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Cart Summary</title>
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
	    <th>Cart Id</th>
	    <th>Total Amount</th>
	    <th>Customer Name</th>
	    <th>Details</th>
	  </tr>
	  
	  <% if(carts != null)  {%>
		  <% for(Cart cart : carts) { 
		  	String url = String.format("CartDetail.jsp?cartId=%d", cart.getId());
		  %>
			  <tr>
			  	<td><%=cart.getId()%></td>
			  	<td><%=cart.getTotalAmount()%></td>
			  	<td><%=cart.getCustomerName()%></td>
			    <td><a href=<%=url%>>İncele</a></td>
			  </tr>
		  <%} %>
	<%} %>
	
	</table>

</body>
</html>