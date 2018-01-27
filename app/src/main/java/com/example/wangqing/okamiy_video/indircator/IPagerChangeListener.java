package com.example.wangqing.okamiy_video.indircator;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager页面状态发生变化监听一级接口
 */

public interface IPagerChangeListener {
    /**
     * 页面选中时回调
     *
     * @param position 选中位置
     */
    void onPagerSelected(int position);

    /**
     * 当页面滑动时回调
     *
     * @param position              位置
     * @param positionOffsetPercent 0.0f-1.0f 滚动百分比
     * @param positionOffsetPixel   距离
     */
    void onPagerScrolled(int position, float positionOffsetPercent, int positionOffsetPixel);

    /**
     * 页面滑动状态发生变化时回调
     * 如从静止到滑动,或滑动到静止
     *
     * @param position
     */
    void onPagerScrollStateChanged(int position);
}
