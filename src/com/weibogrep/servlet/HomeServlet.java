package com.weibogrep.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import weibo4j.*;
import weibo4j.http.*;

import com.weibogrep.crawler.*;
import com.weibogrep.user.*;

public class HomeServlet extends HttpServlet {

    public HomeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
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
            response.sendRedirect("search.html");
        } else {
            response.sendRedirect("login.jsp");
        }
    }
}

