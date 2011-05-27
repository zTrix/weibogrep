package com.weibogrep.user;

import java.io.*;
import com.weibogrep.util.*;

public class Updater {

    public static void main(String []args) {
        if (args.length == 0) {
            updateAll();
            return;
        }
        int i = 0;
        while (true) {
            try {
                ZLog.info("update : " + i);
                updateAll();
                Thread.sleep(1000 * 120);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public static void updateAll() {
        File baseDir = new File(UserMgmt.BASE_DIR);
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                try {
                    long uid = Long.parseLong(files[i].getName());
                    UserMgmt um = new UserMgmt(uid);
                    ZLog.info("updating " + uid + " , last post id is " + um.getLastPost());
                    um.update();
                    //um.updateFriends();
                } catch (Exception e) {
                    ZLog.err("updating " + files[i].getName() + " error");
                    e.printStackTrace();
                }
            }
        }
    }
}

