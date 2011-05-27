package com.weibogrep.indexer;

import java.net.URL;
import java.util.*;
import org.apache.commons.lang3.StringEscapeUtils;

public class IndexItem {
    public long id = -1;
    public String content = "";
    public long date = -1;
    public String username = "";
    public long replyNum = 0;
    public URL photo;
    public URL homepage;

    public IndexItem() {

    }

    public IndexItem (long v_id, String v_content, long v_date) {
        id = v_id;
        content = StringEscapeUtils.escapeHtml4(v_content);
        date = v_date;
    }
}
