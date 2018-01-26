package com.example.wangqing.okamiy_video.model.sohu;

import com.google.gson.annotations.Expose;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 搜狐数据频道数据返回集
 */

public class Result {
    /**
     * 根据返回结果创建对应的属性
     */
    @Expose
    private long status;

    @Expose
    private String statusText;

    //for 列表页（lsit）
    @Expose
    private Data data;

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
