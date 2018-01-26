package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.model.Channel;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 数据接口设计:不同的分类下有不同的频道
 */

public abstract class BaseSiteApi {
    /**
     * 获取分类下面的不同频道/专辑
     * @param channel  频道
     * @param pageNo   第几页
     * @param pageSize 一页多少数据
     */
    public abstract void onGetChannelAlbums(Channel channel, int pageNo, int pageSize,OnGetChannelAlbumListener listener);
}
