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

import com.weibogrep.util.ZLog;

public class Indexer {
    public static final String FIELD_ID = "id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_SCREEN_NAME = "screenName";
    public static final String FIELD_REPLY_NUM = "replyNum";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_HOMEPAGE = "homepage";
    public static final String FIELD_URL = "URL";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_STATUS_TEXT = "statusText";
    public static final String FIELD_CREATED_AT = "createdAt";

    public static void index(IndexItem[] items, File indexDir) {
        try {
            Analyzer analyzer = new PaodingAnalyzer();
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir), analyzer, MaxFieldLength.UNLIMITED);

            for (int i = 0; i < items.length; i++) {
                Document document = new Document();
                Field id       = new Field(FIELD_ID
                                          ,"" + items[i].id
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field username = new Field(FIELD_USERNAME
                                          ,items[i].username
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field replyNum = new Field(FIELD_REPLY_NUM
                                          ,"" + items[i].replyNum
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field date     = new Field(FIELD_DATE
                                          ,"" + items[i].date
                                          ,Field.Store.YES
                                          ,Field.Index.NOT_ANALYZED);
                Field photo    = new Field(FIELD_PHOTO
                                          ,items[i].photo.toString()
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field homepage = new Field(FIELD_HOMEPAGE
                                          ,items[i].homepage.toString()
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field content  = new Field(FIELD_CONTENT
                                          ,items[i].content
                                          ,Field.Store.YES
                                          ,Field.Index.ANALYZED
                                          ,Field.TermVector.WITH_POSITIONS_OFFSETS);
                document.add(id);
                document.add(photo);
                document.add(homepage);
                document.add(username);
                document.add(replyNum);
                document.add(date);
                document.add(content);
                indexWriter.addDocument(document);
            }

            indexWriter.optimize();   
            indexWriter.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void indexFriends(FriendItem[] items, File indexDir) {
        try {
            Analyzer analyzer = new PaodingAnalyzer();
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir), analyzer, MaxFieldLength.UNLIMITED);

            for (int i = 0; i < items.length; i++) {
                Document document = new Document();
                Field URL      = new Field(FIELD_URL
                                          ,items[i].URL
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field profileImageURL = new Field(FIELD_PHOTO
                                                 ,items[i].profileImageURL
                                                 ,Field.Store.YES
                                                 ,Field.Index.NO);
                Field name     = new Field(FIELD_USERNAME
                                          ,items[i].name
                                          ,Field.Store.YES
                                          ,Field.Index.ANALYZED
                                          ,Field.TermVector.WITH_POSITIONS_OFFSETS);
                Field screenName = new Field(FIELD_SCREEN_NAME
                                            ,items[i].screenName
                                            ,Field.Store.YES
                                            ,Field.Index.ANALYZED
                                            ,Field.TermVector.WITH_POSITIONS_OFFSETS);
                Field location = new Field(FIELD_LOCATION
                                           ,items[i].location
                                           ,Field.Store.YES
                                           ,Field.Index.NO);
                Field statusText = new Field(FIELD_STATUS_TEXT
                                            ,items[i].statusText
                                            ,Field.Store.YES
                                            ,Field.Index.NO);
                Field id       = new Field(FIELD_ID
                                          ,"" + items[i].id
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                Field createdAt= new Field(FIELD_CREATED_AT
                                          ,"" + items[i].createdAt
                                          ,Field.Store.YES
                                          ,Field.Index.NO);
                document.add(URL);
                document.add(profileImageURL);
                document.add(name);
                document.add(screenName);
                document.add(location);
                document.add(statusText);
                document.add(id);
                document.add(createdAt);
                indexWriter.addDocument(document);
            }

            indexWriter.optimize();   
            indexWriter.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
