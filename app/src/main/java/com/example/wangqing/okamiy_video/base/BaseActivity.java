package com.example.wangqing.okamiy_video.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.wangqing.okamiy_video.R;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
    }

    /**
     * 加载布局
     * @return
     */
    protected abstract int getLayoutId();

    /**
     *  view相关
     */
    protected abstract void initView();

    /**
     *  数据相关
     */
    protected abstract void initData();

    /**
     *  简化初始化view
     * @param resId
     * @param <T>
     * @return
     */
    protected <T extends View> T bindViewId(int resId) {
        return (T) findViewById(resId);
    }

    /**
     *  支持Toolbar
     */
    protected void setSupportActionBar() {
        mToolBar = bindViewId(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }
    }

    /**
     * 是否需要ActionBar返回箭头
     * @param isSupport
     */
    protected void setSupportArrowActionBar(boolean isSupport) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isSupport);
    }

    /**
     *  设置Toolbar的图片
     * @param resId
     */
    protected void setActionBarIcon(int resId) {
        if (mToolBar != null) {
            mToolBar.setNavigationIcon(resId);
        }
    }
}
