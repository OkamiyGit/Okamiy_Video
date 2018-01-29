package com.example.wangqing.okamiy_video.detail;

import com.example.wangqing.okamiy_video.model.sohu.Video;

/**
 * Created by Okamiy on 2018/1/28.
 * Email: 542839122@qq.com
 * <p>
 * Video被选中的一个状态监听
 */

public interface OnVideoSelectedListener {
    /**
     * Video被选中的一个状态监听
     * @param video
     * @param position 点击的位置
     */
    void onVideoSelected(Video video, int position);
}
