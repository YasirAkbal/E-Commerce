<%@page import="xmlUtils.abstracts.XmlUtilI"%>
<%@page import="utils.StreamUtils"%>
<%@page import="java.net.URLConnection"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="entities.concretes.User"%>
<%@page import="utils.XmlUtils"%>
<%@page import="xmlUtils.concretes.UserXmlUtil"%>
<%@page import="frontend.WebApiUrls"%>
<%@page import="utils.WebUtils"%>
<%@page import="java.io.InputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	XmlUtilI<User> userXmlUtil = new UserXmlUtil();
	String username = "";
	String password = "";
	Boolean status = null;
	
	String param = request.getParameter("register");
	if(param != null) {
		username = request.getParameter("username");
		password = request.getParameter("password");
		User user = new User(username,password);
		
		Document document = userXmlUtil.format(user).getData();
		URLConnection connection = WebUtils.connect(WebApiUrls.userCreate);
		
		XmlUtils.dump(document, connection.getOutputStream());
		
		status = XmlUtils.getStatusFromXml(connection.getInputStream());
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Register</title>
</head>
<body>

	<form action="UserRegister.jsp" method="POST">
	Username : <input type="text" name="username" value="<%=username%>"/><br/><br/>
	Password : <input type="text" name="password" value="<%=password%>"/><br/><br/>
	<input type="submit" value="Register" name="register"/>
	</form>
	<%=status %>

</body>
</html>