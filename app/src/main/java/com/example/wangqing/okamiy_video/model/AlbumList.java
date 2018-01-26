package com.example.wangqing.okamiy_video.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 */

public class AlbumList extends ArrayList<Album> {
    private static final String TAG = AlbumList.class.getSimpleName();

    public void debug() {
        for (Album a : this) {
            Log.d(TAG, ">> 专辑集合（albumlist）>> " + a.toString());
        }
    }
}
