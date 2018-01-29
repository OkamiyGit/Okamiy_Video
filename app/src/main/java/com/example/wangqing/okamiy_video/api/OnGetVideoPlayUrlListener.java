package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.sohu.Video;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 三种码流的视频播放监听
 */

public interface OnGetVideoPlayUrlListener {
    void onGetSuperUrl(Video video, String url);//超清url

    void onGetNoramlUrl(Video video, String url);//标清url

    void onGetHighUrl(Video video, String url);//高清url

    void onGetFailed(ErrorInfo info);
}
