package com.example.wangqing.okamiy_video.player;

import android.util.Log;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;
import com.example.wangqing.okamiy_video.model.sohu.Video;

public class PlayActivity extends BaseActivity {
    private static final String TAG = "PlayActivity";
    private String mUrl;
    private int mStreamType;
    private int mCurrentPosition;
    private Video mVideo;
    private String mLiveTitle;//直播节目标题

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        mUrl = getIntent().getStringExtra("url");
        mLiveTitle = getIntent().getStringExtra("title");
        mStreamType = getIntent().getIntExtra("type", 0);
        mCurrentPosition = getIntent().getIntExtra("currentPosition", 0);
        mVideo = getIntent().getParcelableExtra("video");
        Log.i(TAG, ">>   播放页面   ulr      >>" + mUrl + ", mStreamType " + mStreamType + ", mCurrentPosition " + mCurrentPosition);
        Log.i(TAG, ">>      播放页面 video      >> " + mVideo);

    }

    @Override
    protected void initData() {

    }
}
