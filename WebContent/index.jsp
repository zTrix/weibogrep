<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>
<%@ page language="java" import="com.weibogrep.user.*" %>

<%
AccessToken accessToken = (AccessToken) session.getAttribute("accessToken");
if (accessToken != null) {
    User u = WeiboGate.getUser(accessToken);
    UserMgmt um = new UserMgmt(u);
    boolean exist = um.exist();
    um.setup(accessToken.getToken(), accessToken.getTokenSecret());
    if (!exist) {
        List<Status> userStatus = WeiboGate.getUserTimeline(accessToken);
        um.addDoc(userStatus);
    }
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
    </body>
</html>

