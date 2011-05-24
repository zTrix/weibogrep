package com.weibogrep.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.paoding.analysis.examples.gettingstarted.BoldFormatter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import com.weibogrep.indexer.*;
import com.weibogrep.user.*;

public class QueryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public QueryServlet() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        try {
        	if (request.getParameter("uid") == null || request.getParameter("query") == null) {
            	throw new Exception("No query or uid");
            }

        	String queryString = request.getParameter("query");
            long uid = Long.parseLong(request.getParameter("uid"));
            
            response.getWriter().println("queryString = " + queryString);
            String[] rs = new Greper(queryString, new UserMgmt(uid).getIndexDir()).result();
            for (int i = 0; i < rs.length; i++) {
            	response.getWriter().println(rs[i]);
            }
        } catch (Exception ex){
            response.getWriter().println("ERROR:\n" + ex.getMessage());                     
        }
        response.getWriter().flush();
    }

}
