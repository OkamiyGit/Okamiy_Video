package com.example.wangqing.okamiy_video.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.wangqing.okamiy_video.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolBar;
    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        bind = ButterKnife.bind(this);
        initView();
        initData();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected <T extends View> T bindViewId(int resId) {
        return (T) findViewById(resId);
    }

    protected void setSupportActionBar() {
        mToolBar = bindViewId(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }
    }

    //是否需要ActionBar返回箭头
    protected void setSupportArrowActionBar(boolean isSupport) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isSupport);
    }

    protected void setActionBarIcon(int resId) {
        if (mToolBar != null) {
            mToolBar.setNavigationIcon(resId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
