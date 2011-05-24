<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>

<%
AccessToken accessToken = (AccessToken) session.getAttribute("accessToken");
String userStatus = "";
if (accessToken != null) {
    userStatus = WeiboGate.getUserTimeline(accessToken);
} else {
    response.sendRedirect("login.jsp"); 
}
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Weibo Grep</title>
    </head>
  
    <body>
        <%= userStatus%>
    </body>
</html>

