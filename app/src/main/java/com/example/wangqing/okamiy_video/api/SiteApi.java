package com.example.wangqing.okamiy_video.api;

import android.content.Context;

import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.Site;
import com.example.wangqing.okamiy_video.model.sohu.Video;

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

    /**
     * 详情页访问接口
     *
     * @param album    专辑
     * @param listener
     */
    public static void onGetAlbumDetail(Album album, OnGetAlbumDetailListener listener) {

        int siteId = album.getSite().getSiteId();
        switch (siteId) {
            case Site.LETV:
                new LetvApi().onGetAlbumDetail(album, listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetAlbumDetail(album, listener);
                break;
        }
    }

    /**
     * 取video相关信息
     * @param album
     * @param listener
     */
    public static void onGetVideo(int pageSize, int pageNo, Album album, OnGetVideoListener listener) {
        int siteId = album.getSite().getSiteId();
        switch (siteId) {
            case Site.LETV:
                new LetvApi().onGetVideo(album, pageSize, pageNo, listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetVideo(album,  pageSize, pageNo, listener);
                break;
        }
    }

    //获取播放信息
    public static void onGetVideoPlayUrl(Video video, OnGetVideoPlayUrlListener listener) {
        int siteId = video.getSite();
        switch (siteId) {
            case Site.LETV:
                new LetvApi().onGetVideoPlayUrl(video,  listener);
                break;
            case Site.SOHU:
                new SohuApi().onGetVideoPlayUrl(video,   listener);
                break;
        }
    }
}
