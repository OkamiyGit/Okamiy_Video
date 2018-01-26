package com.example.wangqing.okamiy_video.api;

import com.example.wangqing.okamiy_video.AppManager;
import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.AlbumList;
import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.Site;
import com.example.wangqing.okamiy_video.model.sohu.Result;
import com.example.wangqing.okamiy_video.model.sohu.ResultAlbum;
import com.example.wangqing.okamiy_video.utils.OkHttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * LSohu分类接口：获取该Site下面的所有频道/该频道下的专辑
 */

public class SohuApi extends BaseSiteApi {
    private static final String TAG = SohuApi.class.getSimpleName();
    private static final int SOHU_CHANNELID_MOVIE = 1; //搜狐电影频道ID
    private static final int SOHU_CHANNELID_SERIES = 2; //搜狐电视剧频道ID
    private static final int SOHU_CHANNELID_VARIETY = 7; //搜狐综艺频道ID
    private static final int SOHU_CHANNELID_DOCUMENTRY = 8; //搜狐纪录片频道ID
    private static final int SOHU_CHANNELID_COMIC = 16; //搜狐动漫频道ID
    private static final int SOHU_CHANNELID_MUSIC = 24; //搜狐音乐频道ID

    //某一专辑详情
    //http://api.tv.sohu.com/v4/album/info/9112373.json?plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47
    private final static String API_KEY = "plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47";
    private final static String API_ALBUM_INFO = "http://api.tv.sohu.com/v4/album/info/";
    //http://api.tv.sohu.com/v4/search/channel.json?cid=2&o=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47&page=1&page_size=1
    private final static String API_CHANNEL_ALBUM_FORMAT = "http://api.tv.sohu.com/v4/search/channel.json" +
            "?cid=%s&o=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&" +
            "sver=6.2.0&sysver=4.4.2&partner=47&page=%s&page_size=%s";
    //http://api.tv.sohu.com/v4/album/videos/9112373.json?page=1&page_size=50&order=0&site=1&with_trailer=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47
    private final static String API_ALBUM_VIDOES_FORMAT = "http://api.tv.sohu.com/v4/album/videos/%s.json?page=%s&page_size=%s&order=0&site=1&with_trailer=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=6.2.0&sysver=4.4.2&partner=47";
    // 播放url
    //http://api.tv.sohu.com/v4/video/info/3669315.json?site=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=4.5.1&sysver=4.4.2&partner=47&aid=9112373
    private final static String API_VIDEO_PLAY_URL_FORMAT = "http://api.tv.sohu.com/v4/video/info/%s.json?site=1&plat=6&poid=1&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=4.5.1&sysver=4.4.2&partner=47&aid=%s";
    //真实url格式 m3u8
    //http://hot.vrs.sohu.com/ipad3669271_4603585256668_6870592.m3u8?plat=6uid=f5dbc7b40dad477c8516885f6c681c01&pt=5&prod=app&pg=1

    @Override
    public void onGetChannelAlbums(Channel channel, int pageNo, int pageSize, OnGetChannelAlbumListener listener) {
        //封装url
        String url = getChannelAlbumUrl(channel, pageNo, pageSize);
        //执行请求
        doGetChannelAlbumsByUrl(url, listener);
    }

    /**
     * 根据Url执行网路请求
     *
     * @param url
     * @param listener
     */
    public void doGetChannelAlbumsByUrl(final String url, final OnGetChannelAlbumListener listener) {
        //发起请求
        OkHttpUtils.excute(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    ErrorInfo info = buildErrorInfo(url, "doGetChannelAlbumsByUrl", e, ErrorInfo.ERROR_TYPE_URL);
                    //通过listener回调出去
                    listener.onGetChannelAlbumFailed(info);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //不成功时我们也需要做处理：比如重定向
                if (!response.isSuccessful()) {
                    ErrorInfo info = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetChannelAlbumFailed(info);
                    return;
                }

                // 1、取到数据映射Result
                Result result = AppManager.getGson().fromJson(response.body().string(), Result.class);
                // 2、转换ResultAlbum变成Album
                // 3、Album存到AlbumLis中
                AlbumList albumList = toConvertAlbumList(result);
                if (albumList != null) {
                    if (albumList.size() > 0 && listener != null) {
                        listener.onGetChannelAlbumSuccess(albumList);
                    }
                } else {
                    ErrorInfo info = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_DATA_CONVERT);
                    listener.onGetChannelAlbumFailed(info);
                }

            }
        });
    }

    /**
     * 从搜狐的Result取出ResultAlbum数据集合
     * 再将搜狐的ResultAlbum模型转化为我们自定义的AlbumList
     *
     * @param result
     * @return
     */
    private AlbumList toConvertAlbumList(Result result) {
        if (result.getData().getResultAlbumList().size() > 0) { //说明有数据
            AlbumList albumList = new AlbumList();
            //循环遍历出每一个对象并存储到我们的Album对象
            for (ResultAlbum resultAlbum : result.getData().getResultAlbumList()) {
                Album album = new Album(Site.SOHU);
                album.setAlbumDesc(resultAlbum.getTvDesc());
                album.setAlbumId(resultAlbum.getAlbumId());
                album.setHorImgUrl(resultAlbum.getHorHighPic());
                album.setMainActor(resultAlbum.getMainActor());
                album.setTip(resultAlbum.getTip());
                album.setTitle(resultAlbum.getAlbumName());
                album.setVerImgUrl(resultAlbum.getVerHighPic());
                album.setDirector(resultAlbum.getDirector());
                albumList.add(album);
            }
            return albumList;
        }
        return null;
    }

    /**
     * 将失败的信息封装为ErrorInfo
     *
     * @param url
     * @param functionName 函数的名字：在哪里出异常、崩掉
     * @param e            异常
     * @param type         那种类型
     * @return
     */
    private ErrorInfo buildErrorInfo(String url, String functionName, Exception e, int type) {
        ErrorInfo info = new ErrorInfo(Site.SOHU, type);
        info.setExceptionString(e.getMessage());
        info.setFunctionName(functionName);
        info.setUrl(url);
        info.setTag(TAG);
        info.setClassName(TAG);
        return info;
    }

    /**
     * 封装Url
     *
     * @param channel
     * @param pageNo
     * @param pageSize
     * @return
     */
    private String getChannelAlbumUrl(Channel channel, int pageNo, int pageSize) {
        //格式化url
        return String.format(API_CHANNEL_ALBUM_FORMAT, toConvertChannelId(channel), pageNo, pageSize);
    }

    /**
     * 自定义频道Id和真实频道Id的转换
     *
     * @param channel
     * @return
     */
    private int toConvertChannelId(Channel channel) {
        int channelId = -1;//-1 无效值
        switch (channel.getChannelId()) {
            case Channel.SHOW:
                channelId = SOHU_CHANNELID_SERIES;
                break;
            case Channel.MOVIE:
                channelId = SOHU_CHANNELID_MOVIE;
                break;
            case Channel.COMIC:
                channelId = SOHU_CHANNELID_COMIC;
                break;
            case Channel.MUSIC:
                channelId = SOHU_CHANNELID_MUSIC;
                break;
            case Channel.DOCUMENTRY:
                channelId = SOHU_CHANNELID_DOCUMENTRY;
                break;
            case Channel.VARIETY:
                channelId = SOHU_CHANNELID_VARIETY;
                break;
            default:
                break;
        }
        return channelId;
    }

    //    public void onGetAlbumDetail(final Album album, final OnGetAlbumDetailListener listener) {
    //        final String url = API_ALBUM_INFO + album.getAlbumId() + ".json?" + API_KEY;
    //        OkHttpUtils.excute(url, new Callback() {
    //            @Override
    //            public void onFailure(Call call, IOException e) {
    //                if (listener != null) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetAlbumDetail", e, ErrorInfo.ERROR_TYPE_URL);
    //                    listener.onGetAlbumDetailFailed(info);
    //                }
    //            }
    //
    //            @Override
    //            public void onResponse(Call call, Response response) throws IOException {
    //                if (!response.isSuccessful()) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetAlbumDetail", null, ErrorInfo.ERROR_TYPE_HTTP);
    //                    listener.onGetAlbumDetailFailed(info);
    //                    return;
    //                }
    //                //Data
    //                DetailResult result = AppManager.getGson().fromJson(response.body().string(), DetailResult.class);
    //                if (result.getResultAlbum() != null) {
    //                    if (result.getResultAlbum().getLastVideoCount() > 0) {
    //                        album.setVideoTotal(result.getResultAlbum().getLastVideoCount());
    //                    } else {
    //                        album.setVideoTotal(result.getResultAlbum().getTotalVideoCount());
    //                    }
    //                }
    //                //set完数据后,进行通知
    //                if (listener != null) {
    //                    listener.onGetAlbumDetailSuccess(album);
    //                }
    //            }
    //        });
    //    }
    //
    //    public void onGetVideo(final Album album, int pageSize, int pageNo, final OnGetVideoListener listener) {
    //        final String url = String.format(API_ALBUM_VIDOES_FORMAT, album.getAlbumId(), pageNo, pageSize);
    //        OkHttpUtils.excute(url, new Callback() {
    //            @Override
    //            public void onFailure(Call call, IOException e) {
    //                if (listener != null) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetVideo", e, ErrorInfo.ERROR_TYPE_URL);
    //                    listener.OnGetVideoFailed(info);
    //                }
    //            }
    //
    //            @Override
    //            public void onResponse(Call call, Response response) throws IOException {
    //                if (!response.isSuccessful()) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetVideo", null, ErrorInfo.ERROR_TYPE_HTTP);
    //                    listener.OnGetVideoFailed(info);
    //                    return;
    //                }
    //                //                Log.d(TAG, " >> onGetVideo response  " + response.body().string());
    //                VideoResult result = AppManager.getGson().fromJson(response.body().string(), VideoResult.class);
    //                if (result != null) {
    //                    //                    Log.d(TAG, " >> onGetVideo result  " + result.toString());
    //                    VideoList videoList = new VideoList();
    //                    if (result.getData() != null) {
    //                        for (Video video : result.getData().getVideoList()) {
    //                            Video v = new Video();
    //                            v.setSite(album.getSite().getSiteId());
    //                            v.setHorHighPic(video.getHorHighPic());
    //                            v.setVerHighPic(video.getVerHighPic());
    //                            v.setVid(video.getVid());
    //                            v.setAid(video.getAid());
    //                            v.setVideoName(video.getVideoName());
    //                            videoList.add(v);
    //                        }
    //                    }
    //                    if (listener != null) {
    //                        Log.d(TAG, " >> listener notify success!! ");
    //                        listener.OnGetVideoSuccess(videoList);
    //                    }
    //                }
    //            }
    //        });
    //    }
    //
    //    //取视频播放url
    //    public void onGetVideoPlayUrl(final Video video, final OnGetVideoPlayUrlListener listener) {
    //        final String url = String.format(API_VIDEO_PLAY_URL_FORMAT, video.getVid(), video.getAid());
    //        OkHttpUtils.excute(url, new Callback() {
    //            @Override
    //            public void onFailure(Call call, IOException e) {
    //                if (listener != null) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetVideoPlayUrls", e, ErrorInfo.ERROR_TYPE_URL);
    //                    listener.onGetFailed(info);
    //                }
    //            }
    //
    //            @Override
    //            public void onResponse(Call call, Response response) throws IOException {
    //                if (!response.isSuccessful()) {
    //                    ErrorInfo info = buildErrorInfo(url, "onGetVideoPlayUrls", null, ErrorInfo.ERROR_TYPE_HTTP);
    //                    listener.onGetFailed(info);
    //                    return;
    //                }
    //                try {
    //                    JSONObject result = new JSONObject(response.body().string());
    //                    JSONObject data = result.optJSONObject("data");
    //                    String normalUrl = data.optString("url_nor");
    //                    if (!TextUtils.isEmpty(normalUrl)) {
    //                        normalUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
    //                        video.setNormalUrl(normalUrl);
    //                        // 通知获取到标清码流url
    //                        if (listener != null) {
    //                            listener.onGetNoramlUrl(video, normalUrl);
    //                        }
    //                    }
    //                    String superUrl = data.optString("url_super");
    //                    if (!TextUtils.isEmpty(superUrl)) {
    //                        superUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
    //                        video.setSuperUrl(superUrl);
    //                        // 通知获取到超清码流url
    //                        if (listener != null) {
    //                            listener.onGetSuperUrl(video, superUrl);
    //                        }
    //                    }
    //                    String highUrl = data.optString("url_high");
    //                    if (!TextUtils.isEmpty(highUrl)) {
    //                        highUrl += "uid=" + getUUID() + "&pt=5&prod=app&pg=1";
    //                        video.setSuperUrl(highUrl);
    //                        // 通知获取到高清码流url
    //                        if (listener != null) {
    //                            listener.onGetHighUrl(video, highUrl);
    //                        }
    //                    }
    //                } catch (JSONException e) {
    //                    e.printStackTrace();
    //                }
    //
    //            }
    //        });
    //    }
    //
    //    private String getUUID() {
    //        UUID uuid = UUID.randomUUID();
    //        return uuid.toString().replace("-", "");
    //    }
}
