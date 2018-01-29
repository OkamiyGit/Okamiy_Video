package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.sohu.VideoList;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 */

public interface OnGetVideoListener {
    void OnGetVideoSuccess(VideoList videoList);

    void OnGetVideoFailed(ErrorInfo info);
}
