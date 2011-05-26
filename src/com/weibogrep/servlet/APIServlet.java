package com.weibogrep.servlet;

import java.io.IOException;
import java.util.*;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.paoding.analysis.examples.gettingstarted.BoldFormatter;
import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.weibogrep.crawler.WebOAuth;
import com.weibogrep.indexer.*;
import com.weibogrep.user.*;
import com.weibogrep.util.ZLog;

import weibo4j.http.RequestToken;
import weibo4j.org.json.*;

public class APIServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public APIServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        String[] paths = request.getRequestURI().split("/");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (paths.length <= 2) {
                new JSONObject().put("err", 0)
                                .put("msg", "this is the json service")
                                .write(response.getWriter());
            } else if (paths[2].equalsIgnoreCase("oauth_url")) {
                RequestToken requestToken = WebOAuth.request("http://weibogrep/callback");
                if (requestToken != null) {
                    session.setAttribute("requestToken",requestToken);
                    new JSONObject().put("err" , 0)
                                    .put("oauth_url", requestToken.getAuthorizationURL())
                                    .write(response.getWriter());
                } else {
                    new JSONObject().put("err", -1)
                                    .put("msg", "internal error, request token null")
                                    .write(response.getWriter());
                }
            } else if (paths[2].equalsIgnoreCase("logout")) {
                if (session.getAttribute("user") != null) {
                    session.invalidate();
                    new JSONObject().put("err", 0)
                                    .put("msg"  , "logout success")
                                    .write(response.getWriter());
                } else {
                    new JSONObject().put("err" , -1)
                                    .put("msg", "not logged in")
                                    .write(response.getWriter());
                }
            } else if (paths[2].equalsIgnoreCase("status")) {
                boolean value = (session != null) && (session.getAttribute("user") != null);
                JSONObject o = new JSONObject().put("err", 0)
                                               .put("logged_in", value);
                if (value) {
                    o.put("uid", ((UserMgmt)session.getAttribute("user")).getId());
                } else {
                    o.put("uid", (Object)null);
                }
                o.write(response.getWriter()); 
            } else if (paths[2].equalsIgnoreCase("grep")) {
                UserMgmt um = (UserMgmt) session.getAttribute("user");
                if (um == null) {
                    new JSONObject().put("err" , -1)
                                    .put("msg", "user not logged in")
                                    .write(response.getWriter());
                    return;
                } 
                String queryString = request.getParameter("q");
                if (queryString == null) {
                    new JSONObject().put("err" , -2)
                                    .put("msg", "query string not provided")
                                    .write(response.getWriter());
                    return;
                }
                try {
                    if (session.getAttribute("reader") == null) {
                        session.setAttribute("reader"
                                            ,IndexReader.open(
                                                 FSDirectory.open(um.getIndexDir())
                                             )
                        );
                    }

                    if (session.getAttribute("greper") == null) {
                        session.setAttribute("greper"
                                            ,new IndexSearcher(
                                                FSDirectory.open(um.getIndexDir())
                                            )
                        );
                    }

                    if (session.getAttribute("parser") == null) {
                        session.setAttribute("parser"
                                            ,new QueryParser(Version.LUCENE_CURRENT
                                                            ,"content"
                                                            , new PaodingAnalyzer()
                                                            )
                        );
                    }
                } catch (Exception e) {
                    new JSONObject().put("err",  -3)
                                    .put("msg", "internal error, cannot create greper: " + e.getMessage())
                                    .write(response.getWriter());
                    return;
                }
                try {
                    String[] rs = new Greper(queryString, um.getIndexDir())
                                            .grep((QueryParser  )session.getAttribute("parser")
                                                 ,(IndexReader  )session.getAttribute("reader")
                                                 ,(IndexSearcher)session.getAttribute("greper")
                                                 );
                    HashMap[] hm = new HashMap[rs.length];
                    for (int i=0; i < rs.length; i++) {
                    	hm[i] = new HashMap();
                    	hm[i].put("content", rs[i]);
                    }
                    new JSONObject().put("err", 0)
                                    .put("items", hm)
                                    .write(response.getWriter());
                    ZLog.info("user: " + um.getId() + " query: " + queryString);
                } catch (Exception e){
                	e.printStackTrace();
                    new JSONObject().put("err",  -4)
                                    .put("msg", "internal error, grep error: " + e.getMessage())
                                    .put("exception", e)
                                    .write(response.getWriter());
                    return;
                }
            } else {
                new JSONObject().put("err", -1)
                                .put("msg", "no such API")
                                .write(response.getWriter());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            response.getWriter().print("{err:-100, msg: 'json error'}");
        }
        response.getWriter().flush();
    }
}
