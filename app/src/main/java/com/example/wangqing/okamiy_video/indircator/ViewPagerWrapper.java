package com.example.wangqing.okamiy_video.indircator;

import android.support.v4.view.ViewPager;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * 用来组合viewpager和布局CoolIndcatorLayout
 * Wrapper：包装、包装纸、包装器
 */

public class ViewPagerWrapper {
    private CoolIndicatorLayout mCoolIndicatorLayout;
    private ViewPager mViewPager;

    public ViewPagerWrapper(CoolIndicatorLayout layout, ViewPager viewPager) {
        mCoolIndicatorLayout = layout;
        mViewPager = viewPager;
    }

    /**
     * 外部可以直接使用
     *
     * @param layout
     * @param viewPager
     * @return 返回ViewPagerWrapper对象
     */
    public static ViewPagerWrapper with(CoolIndicatorLayout layout, ViewPager viewPager) {
        return new ViewPagerWrapper(layout, viewPager);
    }

    /**
     * 组合方法：只要有viewpager变化,指示器及title都会有变化
     */
    public void compose() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCoolIndicatorLayout.onPagerScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mCoolIndicatorLayout.onPagerSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mCoolIndicatorLayout.onPagerScrollStateChanged(state);
            }
        });
    }
}
