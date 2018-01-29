package com.example.wangqing.okamiy_video.model.sohu;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * VideoResult 表示详情页面返回Video信息,二级结构,包含Video
 */

public class VideoData {
    @Expose
    private int count;

    @Expose
    private List<Video> videos = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Video> getVideoList() {
        return videos;
    }

    public void setVideoList(List<Video> videoList) {
        this.videos = videoList;
    }
}
