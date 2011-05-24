package com.weibogrep.util;

import java.io.File;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Config {
    public static final String FIELD_NAME = "content";
    public static File indexDir = new File("/root/study/se/final/index");   
    public static Analyzer analyzer = new PaodingAnalyzer();
    public static IndexReader reader;
    public static QueryParser parser;
    public static Searcher searcher;
    
    public static void initIndexer() {
        try {
            //the follow two should be re-created when the index is rebuilded
            Config.reader = IndexReader.open(FSDirectory.open(Config.indexDir));
            Config.searcher = new IndexSearcher(FSDirectory.open(Config.indexDir));
            
            Config.parser = new QueryParser(Version.LUCENE_CURRENT, Config.FIELD_NAME, Config.analyzer);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ;
        }       
    }
}
