package com.weibogrep.crawler;

import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;
import weibo4j.http.RequestToken;

public class WebOAuth {

    public static String ConsumerKey = "2790022981";
    public static String ConsumerSecret = "f714dca6b10a2763cb866000c8fcabc3";
    static {
        System.setProperty("weibo4j.oauth.consumerKey", ConsumerKey);
        System.setProperty("weibo4j.oauth.consumerSecret", ConsumerSecret);
    }

    public static RequestToken request(String backUrl) {
        try {
            
            Weibo weibo = new Weibo();
            RequestToken requestToken = weibo.getOAuthRequestToken(backUrl);

            System.out.println("Got request token.");
            System.out.println("Request token: " + requestToken.getToken());
            System.out.println("Request token secret: "
                    + requestToken.getTokenSecret());
            return requestToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AccessToken requstAccessToken(RequestToken requestToken, String verifier) {
        try {
            Weibo weibo = new Weibo();
            AccessToken accessToken = weibo.getOAuthAccessToken(requestToken
                    .getToken(), requestToken.getTokenSecret(), verifier);

            System.out.println("Got access token.");
            System.out.println("access token: " + accessToken.getToken());
            System.out.println("access token secret: "
                    + accessToken.getTokenSecret());
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
