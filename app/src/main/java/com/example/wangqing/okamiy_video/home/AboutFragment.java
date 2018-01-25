package com.example.wangqing.okamiy_video.home;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseFragment;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class AboutFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView() {
        TextView textView =bindViewId(R.id.tv_app_des);
        //处理文字连接
        textView.setAutoLinkMask(Linkify.ALL);//表示文字链接点击有效
        textView.setMovementMethod(LinkMovementMethod.getInstance());//小时文字可以滚动
    }

    @Override
    protected void initData() {

    }
}
