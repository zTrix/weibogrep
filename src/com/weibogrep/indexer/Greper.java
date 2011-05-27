package com.weibogrep.indexer;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import com.weibogrep.util.ZLog;

import net.paoding.analysis.examples.gettingstarted.BoldFormatter;

public class Greper {

    private String queryStr;
    private File indexDir;

    public Greper(String query, File indexDir) {
        this.queryStr = query;
        this.indexDir = indexDir;
        assert(indexDir != null);
    }

    public IndexItem[] grep(QueryParser parser, IndexReader reader, IndexSearcher greper) {
        try {
            Query query = parser.parse(queryStr);
            query = query.rewrite(reader);
            Sort sort = new Sort(new SortField(Indexer.FIELD_DATE, SortField.LONG, true));
            TopDocs hits = greper.search(query, null, 10000, sort);
            BoldFormatter formatter = new BoldFormatter();
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(50));
            IndexItem[] ret = new IndexItem[hits.scoreDocs.length];
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ret[i] = new IndexItem();
                int docId = hits.scoreDocs[i].doc;
                Document hit = greper.doc(docId);
                String text = hit.get(Indexer.FIELD_CONTENT);
                int maxNumFragmentsRequired = 5;
                String fragmentSeparator = "...";
                TermPositionVector tpv = (TermPositionVector) reader.getTermFreqVector(docId, Indexer.FIELD_CONTENT);
                TokenStream tokenStream = TokenSources.getTokenStream(tpv);
                ret[i].content = highlighter.getBestFragments(tokenStream, 
                                                      text, 
                                                      maxNumFragmentsRequired, 
                                                      fragmentSeparator);
                ret[i].id = Long.parseLong(hit.get(Indexer.FIELD_ID));
                ret[i].date = Long.parseLong(hit.get(Indexer.FIELD_DATE));
                ret[i].username = hit.get(Indexer.FIELD_USERNAME);
                ret[i].replyNum = Long.parseLong(hit.get(Indexer.FIELD_REPLY_NUM));
                ret[i].photo = new URL(hit.get(Indexer.FIELD_PHOTO));
                ret[i].homepage = new URL(hit.get(Indexer.FIELD_HOMEPAGE));
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            ZLog.err(e.toString());
            return null;
        }
    }

    public FriendItem[] grepFriend(QueryParser parser, IndexReader reader, IndexSearcher greper) {
        try {
            Query query = parser.parse(queryStr);
            query = query.rewrite(reader);
            TopDocs hits = greper.search(query, 10000);
            BoldFormatter formatter = new BoldFormatter();
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(50));
            FriendItem[] ret = new FriendItem[hits.scoreDocs.length];
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ret[i] = new FriendItem();
                int docId = hits.scoreDocs[i].doc;
                Document hit = greper.doc(docId);
                String text = hit.get(Indexer.FIELD_SCREEN_NAME);
                int maxNumFragmentsRequired = 5;
                String fragmentSeparator = "...";
                TermPositionVector tpv = (TermPositionVector) reader.getTermFreqVector(docId, Indexer.FIELD_SCREEN_NAME);
                TokenStream tokenStream = TokenSources.getTokenStream(tpv);
                ret[i].screenName = highlighter.getBestFragments(tokenStream, 
                                                      text, 
                                                      maxNumFragmentsRequired, 
                                                      fragmentSeparator);
                ret[i].URL = hit.get(Indexer.FIELD_URL);
                ret[i].profileImageURL = hit.get(Indexer.FIELD_PROFILE_IMAGE_URL);
                ret[i].name = hit.get(Indexer.FIELD_NAME);
                ret[i].location = hit.get(Indexer.FIELD_LOCATION);
                ret[i].statusText = hit.get(Indexer.FIELD_STATUS_TEXT);
                ret[i].id = Long.parseLong(hit.get(Indexer.FIELD_ID));
                ret[i].createdAt = Long.parseLong(hit.get(Indexer.FIELD_CREATED_AT));
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            ZLog.err(e.toString());
            return null;
        }
    }

}

