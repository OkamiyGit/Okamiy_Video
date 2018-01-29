package com.example.wangqing.okamiy_video.model.sohu;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 */

public class VideoList extends ArrayList<Video> {
    private static final String TAG = VideoList.class.getSimpleName();

    public void debug() {
        for (Video a : this) {
            Log.d(TAG, ">> 视频集合  videList   >>" + a.toString());
        }
    }
}
