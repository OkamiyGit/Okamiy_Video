package com.example.wangqing.okamiy_video.api;

import android.content.Context;

import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.Site;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 组合不同分类的Api：根据分类执行不同的Api接口调用
 */

public class SiteApi {
    /**
     * 根据不同分类执行不同的Api接口调用
     *
     * @param siteId    分类类型
     * @param channelId 频道Id
     */
    public static void onGetChannelAlbums(Context context, int pageNo, int pageSize, int siteId, int channelId, OnGetChannelAlbumListener listener) {

        switch (siteId) {
            case Site.LETV:
                new LetvApi().onGetChannelAlbums(new Channel(channelId, context), pageNo, pageSize, listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetChannelAlbums(new Channel(channelId, context), pageNo, pageSize, listener);
                break;
            default:
                break;
        }

    }
}
