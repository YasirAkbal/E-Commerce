l<%@page import="entities.concretes.CartProduct"%>
<%@page import="utils.XmlUtils"%>
<%@page import="utils.WebUtils"%>
<%@page import="java.io.InputStream"%>
<%@page import="frontend.WebApiUrls"%>
<%@page import="xmlUtils.concretes.CartXmlUtil"%>
<%@page import="entities.concretes.Cart"%>
<%@page import="xmlUtils.abstracts.XmlUtilI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	XmlUtilI<Cart> cartXmlUtil = new CartXmlUtil();
	long cartId;
	Cart cart = null;
	if(session.getAttribute("cartId") != null) {
		cartId = (Long)session.getAttribute("cartId");
		String url = String.format(WebApiUrls.cartList, cartId);
		InputStream in = WebUtils.get(url);
		cart = cartXmlUtil.parse(XmlUtils.parse(in));
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Cart View</title>
</head>
<body>

  <% 
  if(cart != null) { %>
	<b> Cart Id: </b> <%=cart.getId() %> <br/>
	<b> Customer Name: </b> <%=cart.getCustomerName() %> <br/>
	<b> Total Amount: </b> <%=cart.getTotalAmount() %> <br/>
	
	<hr/>
	
	<% if(cart.getCartProducts() != null) { %>
		<% for(CartProduct cartProduct : cart.getCartProducts()) { %>
			<b> Product Id: </b> <%=cartProduct.getProductId() %> <br/>
			<b> Sales Quantity: </b> <%=cartProduct.getSalesQuantity() %> <br/>
			<b> Sales Price: </b> <%=cartProduct.getSalesPrice() %> <br/>
			<b> Tax Rate: </b> <%=cartProduct.getTaxRate() %> <br/>
			<b> Line Amount: </b> <%=cartProduct.getLineAmount() %> <br/>
			<br/>
		<%} %>
	<%} %>
		
  <%} %>

</body>
</html>