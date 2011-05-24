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

import com.weibogrep.util.Config;

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
        String queryString = request.getParameter("query");
        long uid = Long.parseLong(request.getParameter("uid"));
        try {
            if (queryString == null) {
                throw new Exception("No query!");
            }
            
            response.getWriter().println("QueryString=" + queryString + "<br>");
            Query query = Config.parser.parse(queryString);
            query = query.rewrite(Config.reader);
            TopDocs hits = Config.searcher.search(query, 10);
            System.out.println(queryString + "] Searching for: " + query.toString(Config.FIELD_NAME)  + "<br>");
            
            BoldFormatter formatter = new BoldFormatter();
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(50));
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                int docId = hits.scoreDocs[i].doc;
                Document hit = Config.searcher.doc(docId);
                String text = hit.get(Config.FIELD_NAME);
                int maxNumFragmentsRequired = 5;
                String fragmentSeparator = "...";
                TermPositionVector tpv = (TermPositionVector) Config.reader.getTermFreqVector(docId, Config.FIELD_NAME);
                TokenStream tokenStream = TokenSources.getTokenStream(tpv);
                String result = highlighter.getBestFragments(tokenStream, text,
                        maxNumFragmentsRequired, fragmentSeparator);
                response.getWriter().println("<br>" + result);
            }
        } catch (Exception ex){
            response.getWriter().println("ERROR:\n" + ex.getMessage());                     
        }
        response.getWriter().flush();
    }

}
