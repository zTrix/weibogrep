<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>
<%
    RequestToken resToken = WebOAuth.request("http://weibogrep/callback.jsp");
    if(resToken != null) {
        //out.println(resToken.getToken()); 
        //out.println(resToken.getTokenSecret()); 
        session.setAttribute("resToken",resToken); 
    } else {
        out.println("request error, please try again later"); 
    } 
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Weibo Grep</title>
    </head>
    <body>
        <a href="<%=resToken.getAuthorizationURL()%>">login</a>
    </body>
</html>
