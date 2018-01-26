package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.model.AlbumList;
import com.example.wangqing.okamiy_video.model.ErrorInfo;

/**
 * 监听数据
 */

public interface OnGetChannelAlbumListener {
    /**
     * 成功
     *
     * @param albumList 成功返回的BeanList
     */
    void onGetChannelAlbumSuccess(AlbumList albumList);

    /**
     * 失败信息
     *
     * @param info 包含失败的Bean
     */
    void onGetChannelAlbumFailed(ErrorInfo info);
}
