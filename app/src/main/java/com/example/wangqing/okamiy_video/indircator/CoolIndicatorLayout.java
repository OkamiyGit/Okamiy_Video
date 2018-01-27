package com.example.wangqing.okamiy_video.indircator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * 最终指示器布局：我们最终引用次布局就可以实现我们的自定义指示器
 */

public class CoolIndicatorLayout extends FrameLayout {

    private IPagerIndicatorLayout mPagerIndicatorLayout;

    public CoolIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoolIndicatorLayout(Context context) {
        super(context);
    }

    /**
     * 通过外部设置IPagerIndicatorLayout接口
     *
     * @param pagerIndicatorLayout
     */
    public void setPagerIndicatorLayout(IPagerIndicatorLayout pagerIndicatorLayout) {
        //相同则不用管
        if (mPagerIndicatorLayout == pagerIndicatorLayout) {
            return;
        }

        //先Detach
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onDetachCoolIndicatorLayout();
        }
        //后Attach
        mPagerIndicatorLayout = pagerIndicatorLayout;
        //清除之前view
        removeAllViews();
        if (mPagerIndicatorLayout != null) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(((View) mPagerIndicatorLayout), lp);
            mPagerIndicatorLayout.onAttachCoolIndicatorLayout();
        }

    }

    /**
     * 页面选中时回调
     *
     * @param position
     */
    public void onPagerSelected(int position) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerSelected(position);
        }
    }

    /**
     * 当页面滑动时回调
     *
     * @param position
     * @param positionOffsetPercent
     * @param positionOffsetPixel
     */
    public void onPagerScrolled(int position, float positionOffsetPercent, int positionOffsetPixel) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerScrolled(position, positionOffsetPercent, positionOffsetPixel);
        }
    }

    /**
     * 页面滑动状态发生变化时回调
     * 如从静止到滑动,或滑动到静止
     *
     * @param state
     */
    public void onPagerScrollStateChanged(int state) {
        if (mPagerIndicatorLayout != null) {
            mPagerIndicatorLayout.onPagerScrollStateChanged(state);
        }
    }
}
