<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>
<%
    RequestToken resToken = WebOAuth.request("http://weibogrep/callback.jsp");
    if(resToken != null) {
        out.println(resToken.getToken()); 
        out.println(resToken.getTokenSecret()); 
        session.setAttribute("resToken",resToken); 
        response.sendRedirect(resToken.getAuthorizationURL()); 
    } else {
        out.println("request error, please try again later"); 
    } 
%>
