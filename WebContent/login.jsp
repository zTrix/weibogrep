<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>
<%
    RequestToken resToken = WebOAuth.request("http://weibogrep/callback");
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
        <link rel="stylesheet" href="/static/css/reset.css" />
        <link rel="stylesheet" href="/static/css/login.css" />
    </head>
    <body>
        <div class="wrapper">
            <div class="title">
                <center>微博实时搜索</center>
            </div>
            <br />
            <img src="" alt="" id="logo" />
            <br />
            <a href="<%=resToken.getAuthorizationURL()%>">
                <center><img src="/static/img/oauth_login.png" /></center>
            </a>
        </div>
    </body>
</html>
