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
            RequestToken resToken = (RequestToken) session.getAttribute("resToken");

            if(resToken!=null) {
                AccessToken accessToken = WebOAuth.requstAccessToken(resToken,verifier);
                if (accessToken != null) {
                        session.setAttribute("accessToken", accessToken);
                        response.sendRedirect("search.html");
                        //String input = WeiboGate.getUserTimeline(accessToken);
                        //out.println("读取用户微博成功<br>" + input);
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

