package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.ErrorInfo;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 *  详情页获取数据监听借口
 */

public interface OnGetAlbumDetailListener {
    /**
     *  成功
     * @param album
     */
    void onGetAlbumDetailSuccess(Album album);

    /**
     *  失败
     * @param info
     */
    void onGetAlbumDetailFailed(ErrorInfo info);
}
