package com.example.wangqing.okamiy_video.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public abstract class BaseFragment extends Fragment {
    protected View mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = getActivity().getLayoutInflater().inflate(getLayoutId(), container, false);
        initView();
        initData();
        return mContentView;
    }

    /**
     *  添加资源文件：xml布局
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected <T extends View> T bindViewId(int resId) {
        return (T) mContentView.findViewById(resId);
    }
}
