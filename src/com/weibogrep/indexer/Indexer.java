package com.weibogrep.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class Indexer {

    public static void index(IndexItem[] items, File indexDir) {
        try {
            Analyzer analyzer = new PaodingAnalyzer();
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir), analyzer, MaxFieldLength.UNLIMITED);

            for (int i = 0; i < items.length; i++) {
                Document document = new Document();   
                // Field keyword = Field.Keyword("date", items[i].date);
                Field keyword = new Field("date", "" + items[i].date, Field.Store.YES, Field.Index.NO);
                Field body = new Field("content", items[i].content, Field.Store.YES,  Field.Index.ANALYZED,  Field.TermVector.WITH_POSITIONS_OFFSETS);
                document.add(keyword);
                document.add(body);
                indexWriter.addDocument(document);
            }

            indexWriter.optimize();   
            indexWriter.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
