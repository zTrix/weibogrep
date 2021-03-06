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
            } else if (paths[2].equalsIgnoreCase("update")) {
                UserMgmt um = (UserMgmt) session.getAttribute("user");
                if (um == null) {
                    new JSONObject().put("err" , -1)
                                    .put("msg", "user not logged in")
                                    .write(response.getWriter());
                    return;
                } 
                um.update();
                new JSONObject().put("err" , 0)
                                .put("msg", "update finished")
                                .write(response.getWriter());
            } else if (paths[2].equalsIgnoreCase("user_info")) {
                UserMgmt um = (UserMgmt) session.getAttribute("user");
                if (um == null) {
                    new JSONObject().put("err" , -1)
                                    .put("msg", "user not logged in")
                                    .write(response.getWriter());
                } else {
                    new JSONObject(um.getUser()).put("grep_indexing_num", um.getIndexNum())
                                                .write(response.getWriter());
                }
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
                String queryType = request.getParameter("type");
                if (queryType == null || queryType.equalsIgnoreCase("timeline")) {
                    try {
                        boolean needUpdate = false;
                        if (session.getAttribute("lastIndex") == null) {
                            session.setAttribute("lastIndex", new Long(um.getLastPost()));
                            needUpdate = true;
                        } else {
                            Long lastIndex = (Long)session.getAttribute("lastIndex");
                            if (lastIndex.compareTo((Long)um.getLastPost()) != 0) {
                                needUpdate = true;
                            }
                        }
                        if (needUpdate) {
                            session.setAttribute("reader"
                                                ,IndexReader.open(
                                                     FSDirectory.open(um.getIndexDir())
                                                 )
                            );
                            session.setAttribute("greper"
                                                ,new IndexSearcher(
                                                    FSDirectory.open(um.getIndexDir())
                                                )
                            );
                            session.setAttribute("parser"
                                                ,new QueryParser(Version.LUCENE_CURRENT
                                                                ,"content"
                                                                , new PaodingAnalyzer()
                                                                )
                            );
                            ZLog.info("indexer in session updated");
                        }
                    } catch (Exception e) {
                        new JSONObject().put("err",  -3)
                                        .put("msg", "internal error, cannot create greper: " + e.getMessage())
                                        .write(response.getWriter());
                        return;
                    }
                    try {
                        IndexItem[] rs = new Greper(queryString, um.getIndexDir())
                                                .grep((QueryParser  )session.getAttribute("parser")
                                                     ,(IndexReader  )session.getAttribute("reader")
                                                     ,(IndexSearcher)session.getAttribute("greper")
                                                     );
                        ArrayList<JSONObject> items = new ArrayList<JSONObject>();
                        long newerthan = -1;
                        try {
                            newerthan = Long.parseLong(request.getParameter("newerthan"));
                        } catch (Exception e) {
                            newerthan = -1;
                        }
                        long newerthanTosend = -1;
                        if (rs != null) {
	                        for (int i = 0; i < rs.length; i++) {
	                            if (newerthan <= 0 || rs[i].date > newerthan) {
	                                items.add( new JSONObject().put("content", rs[i].content)
	                                                           .put("username", rs[i].username)
	                                                           .put("date", rs[i].date)
	                                                           .put("id", rs[i].id)
	                                                           .put("homepage", rs[i].homepage)
	                                                           .put("replyNum", rs[i].replyNum)
	                                                           .put("photo", rs[i].photo)
	                                );
	                                if (rs[i].date > newerthanTosend) {
	                                	newerthanTosend = rs[i].date;
	                                }
	                            }
	                        }
                        }
                        if (newerthanTosend < 0) {
                        	newerthanTosend = newerthan;
                        }
                        new JSONObject().put("err", 0)
                                        .put("items", items)
                                        .put("newerthan", newerthanTosend)
                                        .put("type", "timeline")
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
                    return;
                }
                if (queryType.equalsIgnoreCase("friend")) {
                    // json service for type: friend
                    try {
                        boolean needUpdate = false;
                        if (session.getAttribute("friendReader") == null) {
                            needUpdate = true;
                        }
                        if (needUpdate) {
                            session.setAttribute("friendReader"
                                                ,IndexReader.open(
                                                     FSDirectory.open(um.getFriendIndexDir())
                                                 )
                            );
                            session.setAttribute("friendGreper"
                                                ,new IndexSearcher(
                                                    FSDirectory.open(um.getFriendIndexDir())
                                                )
                            );
                            session.setAttribute("friendParser"
                                                ,new QueryParser(Version.LUCENE_CURRENT
                                                                ,"screenName"
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
                        FriendItem[] rs = new Greper(queryString, um.getFriendIndexDir())
                                          .grepFriend((QueryParser  )session.getAttribute("friendParser")
                                                     ,(IndexReader  )session.getAttribute("friendReader")
                                                     ,(IndexSearcher)session.getAttribute("friendGreper")
                                                     );
                        ArrayList<JSONObject> items = new ArrayList<JSONObject>();
                        if (rs != null) {
	                        for (int i = 0; i < rs.length; i++) {
                                items.add( new JSONObject().put("name", rs[i].name)
                                                           .put("screenName", rs[i].screenName)
                                                           .put("createdAt", rs[i].createdAt)
                                                           .put("id", rs[i].id)
                                                           .put("URL", rs[i].URL)
                                                           .put("profileImageURL", rs[i].profileImageURL)
                                                           .put("statusText", rs[i].statusText)
                                                           .put("location", rs[i].location)
                                );
	                        }
                        }
                        new JSONObject().put("err", 0)
                                        .put("items", items)
                                        .put("type", "friend")
                                        .write(response.getWriter());
                    } catch (Exception e){
                        e.printStackTrace();
                        new JSONObject().put("err",  -4)
                                        .put("msg", "internal error, grep error: " + e.getMessage())
                                        .put("exception", e)
                                        .write(response.getWriter());
                        return;
                    }
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
