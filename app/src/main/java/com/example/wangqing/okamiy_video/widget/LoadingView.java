package com.example.wangqing.okamiy_video.widget;

import android.content.Context;
import android.widget.LinearLayout;

import com.example.wangqing.okamiy_video.R;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 *  加载数据的view
 */

public class LoadingView extends LinearLayout {
    public LoadingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.loading_view_layout, this);
    }
}
