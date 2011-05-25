package com.weibogrep.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.weibogrep.user.UserMgmt;
import com.weibogrep.util.ZLog;

import weibo4j.org.json.*;

public class LoginStatusServlet extends HttpServlet {

    public LoginStatusServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            boolean value = (session != null) && (session.getAttribute("user") != null);
            JSONObject o = new JSONObject().put("error", 0    )
                                           .put("value", value);
            if (value) {
                o.put("uid", ((UserMgmt)session.getAttribute("user")).getId());
            }
            o.write(response.getWriter());
        } catch(Exception e) {
            ZLog.err("in LoginStatusServlet");
            e.printStackTrace();
        }
    }
}
