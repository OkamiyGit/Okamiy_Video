package com.example.wangqing.okamiy_video.indircator;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.wangqing.okamiy_video.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器布局：组配通知title及indicator
 */

public class ViewPagerIndicatorLayout extends FrameLayout implements IPagerIndicatorLayout, IPagerTitle {

    private HorizontalScrollView mScrollView;//主布局
    private LinearLayout mTitleContainer; // title容器
    private LinearLayout mIndicatorContainer; // indicator容器
    private ViewPagerIndicatorAdapter mAdapter;//外面设置的，需要通过观察者实现
    private IPagerIndicatorView mIndicator; // indicator
    private List<PositionData> mPositionData = new ArrayList<>();// indicator数据容器
    private float mScrollPivotX = 0.5f;//滚动的中心点,外部可设置

    private ViewPagerIndicatorHelper mViewPagerIndicatorHelper; //indicator辅助类
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            //变化时,总是用adapter最新的count
            mViewPagerIndicatorHelper.setTotalCount(mAdapter.getCount());
            //因为数据变化了，整个容器需要重新填充
            init();
        }

        @Override
        public void onInvalidated() {
        }
    };

    public ViewPagerIndicatorLayout(Context context) {
        super(context);
        mViewPagerIndicatorHelper = new ViewPagerIndicatorHelper();
        mViewPagerIndicatorHelper.setScrollListener(this);
    }

    //初始化布局
    private void init() {
        //因为是FrameLayout，所以需要移除其他view
        removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pager_indicator_layout, this);
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);
        mIndicatorContainer = (LinearLayout) view.findViewById(R.id.ll_indicator_container);
        mTitleContainer = (LinearLayout) view.findViewById(R.id.ll_title_container);

        //设置title及indicator
        initTitleAndIndicator();
    }

    /**
     * 添加title及indicator到对应容器
     */
    private void initTitleAndIndicator() {
        //从Helper里面取出totalCount
        for (int i = 0, j = mViewPagerIndicatorHelper.getTotalCount(); i < j; i++) {
            if (mAdapter != null) {
                //通过Adapter获取Titleview，之后进行布局
                IPagerTitle v = mAdapter.getTitle(getContext(), i);
                if (v instanceof ViewPagerITitleView) {
                    View view = (View) v;
                    //根布局保持一致
                    LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                    mTitleContainer.addView(view, LP);//添加到Title容器
                }
            }
        }

        //拿到指示器
        if (mAdapter != null) {
            mIndicator = mAdapter.getIndicator(getContext());
            if (mIndicator instanceof View) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mIndicatorContainer.addView((View) mIndicator, lp);
            }
        }
    }

    /**
     * 设置数据适配器（外面）：注册观察者模式
     *
     * @param adapter
     */
    public void setAdapter(ViewPagerIndicatorAdapter adapter) {
        //外面传进来的adapter相同就直接返回
        if (mAdapter == adapter) {
            return;
        }

        //需要通过数据观察者实现，所以adapter不为空时，先要解除注册
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObservable(mDataSetObserver);
        }

        //付值，并注册观察者
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObservable(mDataSetObserver);
            mAdapter.notifySetDataChanged();//通知数据变化
        } else {
            // adapter为空,设置total为0
            mViewPagerIndicatorHelper.setTotalCount(0);
            init();
        }
    }

    /**
     * 滚动的中心点,外部可设置
     *
     * @param PivotX
     */
    public void setScrollPivotX(float PivotX) {
        mScrollPivotX = PivotX;
    }

    /**
     * 重新layout
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter != null) {
            //准备indicator的数据
            preParePositionData();
            if (mIndicator != null) {
                mIndicator.setPostionDataList(mPositionData);
            }
            if (mViewPagerIndicatorHelper.getScrollState() == ViewPager.SCROLL_STATE_IDLE) {
                // 通知onPageScolled,onPageSelected
                onPagerScrolled(mViewPagerIndicatorHelper.getCurrentIndex(), 0.0f, 0);
                onPagerSelected(mViewPagerIndicatorHelper.getCurrentIndex());
            }
        }
    }

    /**
     * 给indicator准备数据
     */
    private void preParePositionData() {
        //先清除数据
        mPositionData.clear();
        //从Helper里面取出totalCount，之后根据totalCount将PositionData添加到我们的lsit中
        for (int i = 0, k = mViewPagerIndicatorHelper.getTotalCount(); i < k; i++) {
            PositionData data = new PositionData();
            View v = mTitleContainer.getChildAt(i);//title容器的子view
            //计算位置坐标
            if (v != null) {
                data.mLeft = v.getLeft();
                data.mRight = v.getRight();
                data.mBottom = v.getBottom();
                data.mTop = v.getTop();
            }

            if (v instanceof IViewPagerTitleView) { //文字
                IViewPagerTitleView view = ((IViewPagerTitleView) v);
                data.mContentLeft = view.getContentLeft();
                data.mContentRight = view.getContentRight();
                data.mContentTop = view.getContentTop();
                data.mContentBottom = view.getContentBottom();
            } else {//Indicator
                data.mContentLeft = v.getLeft();
                data.mContentRight = v.getRight();
                data.mContentTop = v.getTop();
                data.mContentBottom = v.getBottom();
            }
            mPositionData.add(data);
        }
    }

    /**
     * 延迟到onAttachCoolIndicatorLayout：就是添加到CoolIndicatorLayout上,才初始化
     */
    @Override
    public void onAttachCoolIndicatorLayout() {
        init();
    }

    @Override
    public void onDetachCoolIndicatorLayout() {

    }

    /**
     * 通知title onSelected
     *
     * @param index      第几个
     * @param totalCount 总共多少个
     */
    @Override
    public void onSelected(int index, int totalCount) {
        if (mAdapter != null) {
            if (mTitleContainer == null) {
                return;
            }
            View v = mTitleContainer.getChildAt(index);
            if (v instanceof IPagerTitle) {
                ((IPagerTitle) v).onSelected(index, totalCount);
            }
        }
    }

    /**
     * 通知title onDisSelected
     *
     * @param index
     * @param totalCount
     */
    @Override
    public void onDisSelected(int index, int totalCount) {
        if (mAdapter != null) {
            if (mTitleContainer == null) {
                return;
            }
            View v = mTitleContainer.getChildAt(index);
            if (v instanceof IPagerTitle) {
                ((IPagerTitle) v).onDisSelected(index, totalCount);
            }
        }
    }

    /**
     * 离开时，通知title onLeave
     *
     * @param index
     * @param totalCount
     * @param leavePercent  取值 0.0f - 1.0f (1.0f表示完全离开)
     * @param isLeftToRight 是否从左向右离开
     */
    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean isLeftToRight) {
        if (mAdapter != null) {
            if (mTitleContainer == null) {
                return;
            }
            View v = mTitleContainer.getChildAt(index);
            if (v instanceof IPagerTitle) {
                ((IPagerTitle) v).onLeave(index, totalCount, leavePercent, isLeftToRight);
            }
        }
    }

    /**
     * 进入时，通知title onEnter
     *
     * @param index
     * @param totalCount
     * @param enterPercent  取值 0.0f - 1.0f (1.0f表示完全进入)
     * @param isLeftToRight 是否从左向右进入
     */
    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean isLeftToRight) {
        if (mAdapter != null) {
            if (mTitleContainer == null) {
                return;
            }
            View v = mTitleContainer.getChildAt(index);
            if (v instanceof IPagerTitle) {
                ((IPagerTitle) v).onEnter(index, totalCount, enterPercent, isLeftToRight);
            }
        }
    }

    /**
     * 通知Indicator onPagerSelected
     *
     * @param position
     */
    @Override
    public void onPagerSelected(int position) {
        if (mAdapter != null) {
            mViewPagerIndicatorHelper.onPageSelected(position);
            if (mIndicator != null) {
                mIndicator.onPagerSelected(position);
            }
        }

    }

    /**
     * 通知Indicator onPagerScrolled
     *
     * @param position              位置
     * @param positionOffsetPercent 0.0f-1.0f 滚动百分比
     * @param positionOffsetPixel   距离
     */
    @Override
    public void onPagerScrolled(int position, float positionOffsetPercent, int positionOffsetPixel) {
        if (mAdapter != null) {
            mViewPagerIndicatorHelper.onPageScrolled(position, positionOffsetPercent, positionOffsetPixel);
            if (mIndicator != null) {
                mIndicator.onPagerScrolled(position, positionOffsetPercent, positionOffsetPixel);
            }
        }

        //通过HorizonalScrollView进行滚动
        if (mScrollView != null && mPositionData.size() > 0) {
            int currentPosition = Math.min(mPositionData.size() - 1, position);
            int nextPosition = Math.min(mPositionData.size() - 1, position + 1);
            PositionData current = mPositionData.get(currentPosition);
            PositionData next = mPositionData.get(nextPosition);
            //滑动距离
            float scrollTo = current.horizonalCenter() - mScrollView.getWidth() * mScrollPivotX;
            float nextscrollTo = next.horizonalCenter() - mScrollView.getWidth() * mScrollPivotX;
            //HorizontalScrollView 进行滑动
            mScrollView.scrollTo((int) (scrollTo + (nextscrollTo - scrollTo) * positionOffsetPercent), 0);
        }
    }

    /**
     * 通知Indicator onPagerScrollStateChanged
     *
     * @param state
     */
    @Override
    public void onPagerScrollStateChanged(int state) {

        if (mAdapter != null) {
            mViewPagerIndicatorHelper.onPageScrollStateChanged(state);
            if (mIndicator != null) {
                mIndicator.onPagerScrollStateChanged(state);
            }
        }
    }
}
