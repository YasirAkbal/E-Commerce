<%@page import="xmlParsers.abstracts.XmlUtilI"%>
<%@page import="utils.XmlUtils"%>
<%@page import="xmlParsers.concretes.ProductXmlUtil"%>
<%@page import="utils.WebUtils"%>
<%@page import="entities.concretes.Product"%>
<%@page import="java.util.List"%>
<%@page import="frontend.WebApiUrls"%>
<%@page import="java.io.InputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	XmlUtilI<Product> productXmlUtil = new ProductXmlUtil();
	long categoryId;
	List<Product> productList = null;
	if(request.getParameter("categoryId") != null) {
		categoryId = Long.parseLong(request.getParameter("categoryId"));
		String url = String.format(WebApiUrls.listProductsByCategory, categoryId);
		InputStream in = WebUtils.get(url);
		productList = productXmlUtil.parseList(XmlUtils.parse(in));
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Product List</title>
</head>
<body>

  <% 
  if(productList != null) { %>
		<ul>
		  <% for(Product product : productList) { %>
		  	<% String url = String.format("ProductView.jsp?productId=%d",product.getProductId()); %>
		  	<li><a href=<%=url%>><%=product.getProductName()%></a></li>
		 	
		  <%} %>
		</ul>
  <%} %>


</body>
</html>