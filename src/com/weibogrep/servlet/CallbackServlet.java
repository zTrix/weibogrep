package com.weibogrep.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.weibogrep.crawler.WebOAuth;
import com.weibogrep.crawler.WeiboGate;
import com.weibogrep.user.UserMgmt;

import weibo4j.User;
import weibo4j.http.AccessToken;
import weibo4j.http.RequestToken;

public class CallbackServlet extends HttpServlet {

    public CallbackServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String verifier = request.getParameter("oauth_verifier");
        if(verifier != null) {
            RequestToken resToken = (RequestToken) session.getAttribute("requestToken");

            if(resToken != null) {
                AccessToken accessToken = WebOAuth.requstAccessToken(resToken,verifier);
                if (accessToken != null) {
                    session.setAttribute("accessToken", accessToken);
                    User u = WeiboGate.getUser(accessToken);
                    
                    if (u == null) {
                        response.sendRedirect("login.html");
                        return;
                    }
                	response.sendRedirect("grep.html");
                    response.getWriter().flush();
                    UserMgmt um = new UserMgmt(u);
                    um.setup(accessToken.getToken(), accessToken.getTokenSecret());
                    session.setAttribute("user", um);
                } else {
                    response.getWriter().write("access token request error");
                }
            } else {
            	response.getWriter().write("request token session error");
            }
        } else {
        	response.getWriter().write("verifier String error");
        }

    }
}

