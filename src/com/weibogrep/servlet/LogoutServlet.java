package com.weibogrep.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import weibo4j.org.json.*;

public class LogoutServlet extends HttpServlet {

    public LogoutServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            if (session != null) {
                session.invalidate();
                new JSONObject().put("error", 0)
                                .write(response.getWriter());
            } else {
                new JSONObject().put("error" , -1)
                                .put("errmsg", "not logged in")
                                .write(response.getWriter());
            }
        } catch(Exception e) {

        }
    }
}
