package com.example.wangqing.okamiy_video.model;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class Site {
    public static final int SOHU = 1;
    public static final int LETV = 2;

    private int siteId;
    private String siteName;
    public static final int MAX_SITE = 2;

    public Site(int id) {
        siteId = id;
        switch (siteId) {
            case SOHU:
                siteName = "搜狐视频";
                break;
            case LETV:
                siteName = "乐视视频";
                break;
        }
    }

    public int getSiteId() {
        return siteId;
    }
}
