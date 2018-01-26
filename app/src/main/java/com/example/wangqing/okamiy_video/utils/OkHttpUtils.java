package com.example.wangqing.okamiy_video.utils;

import com.example.wangqing.okamiy_video.AppManager;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 网络访问类
 */

public class OkHttpUtils {
    //标识
    private static final String REQUEST_TAG = ">> okhttp >>";

    /**
     * 构建Request
     * 对Url基本的声明
     *
     * @param url
     * @return
     */
    public static Request buildRuquest(String url) {
        //检测网络是否可用
        if (AppManager.isNetWorkAvailable()) {
            Request request = new Request.Builder()
                    .tag(REQUEST_TAG)
                    .url(url)
                    .build();
            return request;
        }
        return null;//网络不可用
    }

    /**
     * 封装请求方法
     *
     * @param url
     * @param callback
     */
    public static void excute(String url, Callback callback) {
        Request request = buildRuquest(url);
        excute(request, callback);
    }

    /**
     * 加入队列，执行请求
     *
     * @param request
     * @param callback
     */
    public static void excute(Request request, Callback callback) {
        AppManager.getHttpClient().newCall(request).enqueue(callback);
    }
}
