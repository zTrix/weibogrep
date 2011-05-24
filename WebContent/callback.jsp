<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" import="weibo4j.http.*" %>
<%@ page language="java" import="weibo4j.*" %>
<%@ page language="java" import="com.weibogrep.crawler.*" %>

<%
    String verifier = request.getParameter("oauth_verifier");
    if(verifier != null) {
        RequestToken resToken = (RequestToken) session.getAttribute("resToken");

        if(resToken!=null) {
            AccessToken accessToken = WebOAuth.requstAccessToken(resToken,verifier);
            if (accessToken != null) {
                    session.setAttribute("accessToken", accessToken);
                    response.sendRedirect("index.jsp");
                    //String input = WeiboGate.getUserTimeline(accessToken);
                    //out.println("读取用户微博成功<br>" + input);
            } else {
                out.println("access token request error");
            }
        } else {
            out.println("request token session error");
        }
    } else {
        out.println("verifier String error");
    }
%>
