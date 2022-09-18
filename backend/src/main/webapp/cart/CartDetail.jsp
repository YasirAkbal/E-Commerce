<%@page import="entities.concretes.CartProduct"%>
<%@page import="java.util.List"%>
<%@page import="results.DataResult"%>
<%@page import="entities.concretes.Cart"%>
<%@page import="managers.concretes.CartManager"%>
<%@page import="managers.abstracts.CartManagerI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	CartManagerI cartManager = new CartManager();
	long cartId;
	Cart cart = null;
	List<CartProduct> cartProducts = null;
	
	cartId = Long.parseLong(request.getParameter("cartId"));
	DataResult<Cart> result = cartManager.find(cartId);
	
	cart = result.getData();
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<style>
table, th, td {
  border:1px solid black;
  text-align: center;
}
</style>
<body>

<%if(cart != null) { %>
	<b>Cart Id : </b> <%=cart.getId() %> <br/>
	<b>Total Amount : </b> <%=cart.getTotalAmount() %> <br/>
	<b>Customer Name : </b> <%=cart.getCustomerName() %> <br/>
	
	<%
		cartProducts = cart.getCartProducts();
		if(cartProducts != null) {
	%>
		<h3>Sepetteki Ürünler</h3>
		<table style="width:80%" class="center">
		<tr>
			<th>Id</th>
			<th>Product Id</th>
			<th>Sales Quantity</th>
			<th>Sales Price</th>
			<th>Tax Rate</th>
			<th>Line Amount</th>
		</tr>
		<%for(CartProduct cartProduct : cartProducts) {%> 
			<tr>
				<td><%=cartProduct.getId()%></td>
				<td><%=cartProduct.getProductId()%></td>
				<td><%=cartProduct.getSalesQuantity()%></td>
				<td><%=cartProduct.getSalesPrice()%></td>
				<td><%=cartProduct.getTaxRate()%></td>
				<td><%=cartProduct.getLineAmount()%></td>
			</tr>
		<%} %>
		</table>
	<%} %>
	
<%} %>

</body>
</html>