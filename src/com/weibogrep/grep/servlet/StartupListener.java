package com.weibogrep.grep.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import net.paoding.analysis.examples.gettingstarted.ContentReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.weibogrep.grep.util.Config;
import com.weibogrep.indexer.test.ch2.Chinese;

/**
 * Application Lifecycle Listener implementation class StartupListener
 *
 */
public class StartupListener implements ServletContextListener {
	
    /**
     * Default constructor. 
     */
    public StartupListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
			/*
	    	// 将庖丁封装成符合Lucene要求的Analyzer规范
			Analyzer analyzer = new PaodingAnalyzer();			
			//读取本类目录下的text.txt文件
			String content = ContentReader.readText(Chinese.class);
	
			//接下来是标准的Lucene建立索引和检索的代码
			Directory ramDir = new RAMDirectory();
			IndexWriter writer = new IndexWriter(ramDir, analyzer, MaxFieldLength.UNLIMITED);
			Document doc = new Document();
			Field fd = new Field(Config.FIELD_NAME, content, Field.Store.YES,
					Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
			doc.add(fd);
			writer.addDocument(doc);
			writer.optimize();
			writer.close();
			*/
    	Config.initIndexer();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
