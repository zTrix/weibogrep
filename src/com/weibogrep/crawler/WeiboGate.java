package com.weibogrep.crawler;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

import weibo4j.User;
import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;
import weibo4j.http.RequestToken;

public class WeiboGate {
    public static User getUser(AccessToken access) {
        Weibo wb = new Weibo();
        User u;
        wb.setToken(access.getToken(), access.getTokenSecret());
        try {
            u = wb.verifyCredentials();
        } catch (Exception e) {
            return null;
        }
        return u;
    }

    public static List<Status> getUserTimeline(AccessToken access) {
        Weibo wb = new Weibo();
        User u;
        wb.setToken(access.getToken(), access.getTokenSecret());
        List<Status> statuses;
        try {
            u = wb.verifyCredentials();
            statuses = wb.getUserTimeline("" + u.getId(), new Paging(1, 200));
        } catch (WeiboException e) {
            e.printStackTrace();
            return null;
        }
        return statuses;
    }

    
    public static List<Status> getHomeTimeline(AccessToken access) {
        return getHomeTimeline(access, -1);
    }

    public static List<Status> getHomeTimeline(AccessToken access, long since) {
    	Weibo wb = new Weibo();
        wb.setToken(access.getToken(), access.getTokenSecret());
        List<Status> statuses;
        Paging pg = since > 0 ? new Paging(1, 200, since) : new Paging(1, 200);
        try {
            statuses = wb.getHomeTimeline(pg);
        } catch (WeiboException e) {
            e.printStackTrace();
            return null;
        }
        return statuses;
    }
}

