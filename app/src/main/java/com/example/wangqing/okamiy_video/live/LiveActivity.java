package com.example.wangqing.okamiy_video.live;

import android.app.Activity;
import android.content.Intent;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;

public class LiveActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }
}
