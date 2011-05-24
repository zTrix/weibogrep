<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>

<jsp:useBean id="weboauth" scope="session" class="weibo4j.examples.WebOAuth" />
<%
	AccessToken accessToken = (AccessToken)session.getAttribute("accessToken");
	String imageName = (String)session.getAttribute("imageName");
	String hint = weboauth.uploadByFile(accessToken, imageName);
%>
<p><%=hint%></p><br>