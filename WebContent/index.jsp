<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>
<%@ page language="java" import="com.weibogrep.user.*" %>

<%
AccessToken accessToken = (AccessToken) session.getAttribute("accessToken");
User u = null;
if (accessToken != null) {
    u = WeiboGate.getUser(accessToken);
    if (u == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    UserMgmt um = new UserMgmt(u);
    boolean exist = um.exist();
    um.setup(accessToken.getToken(), accessToken.getTokenSecret());
    um.update();
    session.setAttribute("user", um);
} else {
    response.sendRedirect("login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Weibo Grep</title>
    </head>
    <body>
        欢迎使用微博搜索, <%=u.getName()%>
        <form action="query.do" method="GET" accept-charset="UTF-8">
            <input type="text" name="query" value="" maxlength="100" />
            <input type="submit" name="submit" value="搜索" />
        </form>
        <script type="text/javascript" src="/static/js/jquery.js"></script>
        <script type="text/javascript" src="/static/js/index.js" ></script>
    </body>
</html>

