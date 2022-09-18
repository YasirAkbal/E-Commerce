<%@page import="xmlUtils.concretes.CartProductXmlUtil"%>
<%@page import="java.net.URLConnection"%>
<%@page import="entities.concretes.CartProduct"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="xmlUtils.abstracts.XmlUtilI"%>
<%@page import="utils.XmlUtils"%>
<%@page import="utils.WebUtils"%>
<%@page import="xmlUtils.concretes.ProductXmlUtil"%>
<%@page import="xmlUtils.concretes.CartXmlUtil"%>
<%@page import="java.io.InputStream"%>
<%@page import="frontend.WebApiUrls"%>
<%@page import="entities.concretes.Product"%>
<%@page import="entities.concretes.Cart"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String parameterName = null;
	XmlUtilI<Product> productXmlUtil = new ProductXmlUtil();
	XmlUtilI<Cart> cartXmlUtil = new CartXmlUtil();
	XmlUtilI<CartProduct> cartProductXmlUtil = new CartProductXmlUtil();
	long productId;
	String url;
	InputStream in;
	Product product = null;
	String username = "unknown";
	
	if(session.getAttribute("username") != null) {
		username = (String)session.getAttribute("username");
	}
	
	String productIdStr = request.getParameter("productId");
	String productIdForCartStr = request.getParameter("productIdForCart");
	if(productIdStr != null) {
		parameterName = productIdStr;
	} else if(productIdForCartStr != null) {
		parameterName = productIdForCartStr;
	}
	
	if(parameterName != null) {
		productId = Long.parseLong(parameterName);
		url = String.format(WebApiUrls.findProductById, productId);
		in = WebUtils.get(url);
		product = productXmlUtil.parse(XmlUtils.parse(in));
	}
	
	Boolean status = null;
	if(productIdForCartStr != null) {
		Long cartId = (Long)session.getAttribute("cartId");
		
		if(cartId == null) {
			url = String.format(WebApiUrls.cartCreate, username);
			in = WebUtils.get(url);
			
			Document cartCreateDocument = XmlUtils.parse(in);
			Cart cart = cartXmlUtil.parse(cartCreateDocument);
			cartId = cart.getId();
			session.setAttribute("cartId", cartId);
		}
		
		int quantity = 1;
		double salesPrice = product.getSalesPrice();
		double taxRate = 0.18;
		CartProduct cartProduct = new CartProduct(cartId,product.getProductId(),
				quantity,salesPrice,taxRate,quantity*salesPrice);
		
		Document cartProductAddDocument = cartProductXmlUtil.format(cartProduct).getData();
		URLConnection connection = WebUtils.connect(WebApiUrls.addProductToCart);
		XmlUtils.dump(cartProductAddDocument, connection.getOutputStream());
		
	 	status  = XmlUtils.getStatusFromXml(connection.getInputStream());
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

  <% 
  if(product != null) { %>
	  	<b>Product Id :</b> <%=product.getProductId()%> <br/>
		<b>Product Name :</b> <%=product.getProductName()%> <br/>
		<b>Sales Price :</b> <%=product.getSalesPrice()%> <br/>
		<b>Category Name :</b> <%=product.getCategory().getCategoryName()%> <br/>
		<form action="ProductView.jsp" method="POST">
			<input hidden type="text" name="productIdForCart" value="<%=product.getProductId()%>"/>
			<input type="submit" value="Sepete Ekle" name="Sepete Ekle"/>
		</form>
  <%} %>
  <%=status %>

</body>
</html>