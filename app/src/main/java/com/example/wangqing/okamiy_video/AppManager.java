package com.example.wangqing.okamiy_video;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 初始化一些操作
 */

public class AppManager extends Application {
    /**
     * static 的好处,一次初始化之后就在静态块里面了
     */
    private static Gson mGson;
    private static OkHttpClient mOkHttpClient;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mGson = new Gson();
        mOkHttpClient = new OkHttpClient();
    }

    public static Gson getGson() {
        return mGson;
    }

    public static OkHttpClient getHttpClient() {
        return mOkHttpClient;
    }

    public static Context getContext() {
        return mContext;
    }

    public static Resources getResource() {
        return mContext.getResources();
    }

    /**
     * 当前网络是否可用
     *
     * @return
     */
    public static boolean isNetWorkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //WiFi是否可用
    public static boolean isNetworkWifiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(1) != null) {
            NetworkInfo.State state = connectivityManager.getNetworkInfo(1).getState();
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
