package com.example.wangqing.okamiy_video.indircator;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器布局接口
 */

public interface IPagerIndicatorLayout extends IPagerChangeListener {
    /**
     * 添加到CoolIndicatorLayout上
     */
    void onAttachCoolIndicatorLayout();

    /**
     * 从CoolIndicatorLayout下移除
     */
    void onDetachCoolIndicatorLayout();
}
