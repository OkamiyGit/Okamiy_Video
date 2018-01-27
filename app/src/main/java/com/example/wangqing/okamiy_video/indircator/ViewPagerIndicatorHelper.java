package com.example.wangqing.okamiy_video.indircator;

import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPagerIndicatorLayout的辅助类
 */

public class ViewPagerIndicatorHelper {
    private int mCurrentIndex;
    private int mTotalCount;
    //滑动状态
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
    //标记下标
    private int mLastIndex;
    //上一次偏移量
    private float mLastPositionOffsetSum;
    //计数
    private SparseArray<Float> mLeavePercents = new SparseArray<>();
    private SparseBooleanArray mDeSelectedItems = new SparseBooleanArray();

    public ViewPagerIndicatorHelper() {
    }

    /**
     * 当ViewPagerIndicatorLayout变化时，接收外面的listener
     *
     * @param listener
     */
    public void setScrollListener(IPagerTitle listener) {
        mScrollListener = listener;
    }


    private IPagerTitle mScrollListener;

    /**
     * 滑动状态
     *
     * @param position
     * @param positionOffset
     * @param positionPixel
     */
    public void onPageScrolled(int position, float positionOffset, int positionPixel) {
        //偏移量
        float currentPositionOffsetSum = position + positionOffset;
        //判断移动是否从左到右：通过计算
        boolean isLeftToRight = currentPositionOffsetSum > mLastPositionOffsetSum;
        int safePosition = getSafeIndex(position);

        //不是就绪状态时
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE) {
            //滑动下标
            int enterIndex, leaveIndex;
            //偏移多少
            float enterPerent, leavePercent;
            if (isLeftToRight) {
                enterIndex = getSafeIndex(position + 1);
                enterPerent = positionOffset;
                leaveIndex = safePosition;
                leavePercent = positionOffset;
            } else {
                enterIndex = safePosition;
                enterPerent = 1.0f - positionOffset;
                leaveIndex = getSafeIndex(safePosition + 1);
                leavePercent = 1.0f - positionOffset;
            }

            for (int i = 0; i < mTotalCount; i++) {
                if (i == enterIndex || i == leaveIndex) {
                    continue;
                }
                Float leavedPercent = mLeavePercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {//1.0f表示已经完全离开
                    //通知出去：表示离开状态
                    mScrollListener.onLeave(i, mTotalCount, 1.0f, isLeftToRight);
                    //计数
                    mLeavePercents.put(i, 1.0f);
                }
            }

            if (enterIndex == leaveIndex) {
                if (enterIndex == mTotalCount - 1 && mLeavePercents.get(enterIndex, 0.0f) != 0.0f && enterPerent == 0.0f && isLeftToRight) {
                    //是否继续进入
                    boolean disPatchEnterEvent = mScrollState == ViewPager.SCROLL_STATE_DRAGGING || enterIndex == mCurrentIndex;
                    if (disPatchEnterEvent) {
                        mScrollListener.onEnter(enterIndex, mTotalCount, 1.0f, true);
                        mLeavePercents.put(enterIndex, 0.0f);
                    }
                }
                return;
            }

            if (1.0f - mLeavePercents.get(enterIndex, 0.0f) != enterPerent) {
                boolean disPatchEnterEvent = mScrollState == ViewPager.SCROLL_STATE_DRAGGING || enterIndex == mCurrentIndex;
                if (disPatchEnterEvent) {
                    mScrollListener.onEnter(enterIndex, mTotalCount, enterPerent, isLeftToRight);
                    mLeavePercents.put(enterIndex, 1.0f - enterIndex);
                }
            }

            if (mLeavePercents.get(leaveIndex, 0.0f) != leavePercent) {
                if (isLeftToRight && leaveIndex == getSafeIndex(mCurrentIndex) && leavePercent == 0.0f) {
                    boolean disPatchEnterEvent = mScrollState == ViewPager.SCROLL_STATE_DRAGGING || leaveIndex == mCurrentIndex;
                    if (disPatchEnterEvent) {
                        mScrollListener.onEnter(leaveIndex, mTotalCount, 1.0f, true);
                        mLeavePercents.put(leaveIndex, 0.0f);
                    }
                } else {
                    boolean disPatchLeaveEvent = mScrollState == ViewPager.SCROLL_STATE_DRAGGING
                            || leaveIndex == mLastIndex
                            || (leaveIndex == mCurrentIndex - 1) && mLeavePercents.get(leaveIndex, 0.0f) != 1.0f
                            || (leaveIndex == mCurrentIndex + 1) && mLeavePercents.get(leaveIndex, 0.0f) != 1.0f;
                    if (disPatchLeaveEvent) {
                        mScrollListener.onLeave(leaveIndex, mTotalCount, leavePercent, isLeftToRight);
                    }
                }
            }
        }
        //滚动状态时
        else {
            for (int i = 0; i < mTotalCount; i++) {
                if (i == mCurrentIndex) {
                    continue;
                }
                //未选中状态判断
                boolean deSelected = mDeSelectedItems.get(i);
                if (!deSelected) {
                    //没有被选中
                    mScrollListener.onDisSelected(i, mTotalCount);
                }
                Float leavedPercent = mLeavePercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    mScrollListener.onLeave(i, mTotalCount, 1.0f, isLeftToRight);
                    mLeavePercents.put(i, 1.0f);
                }
            }
            mScrollListener.onEnter(mCurrentIndex, mTotalCount, 1.0f, false);
            //每通知一次存一次mCurrentIndex
            mLeavePercents.put(mCurrentIndex, 0.0f);
            mScrollListener.onSelected(mCurrentIndex, mTotalCount);
            mDeSelectedItems.put(mCurrentIndex, false);
        }
        mLastPositionOffsetSum = currentPositionOffsetSum;
    }

    /**
     * 获取一个下标值
     *
     * @param position
     * @return
     */
    public int getSafeIndex(int position) {
        return Math.max(Math.min(position, mTotalCount - 1), 0);
    }

    /**
     * 选中状态
     *
     * @param position
     */
    public void onPageSelected(int position) {
        int currentIndex = setCurrentIndex(position);
        if (mScrollListener != null) {
            //通知选中
            mScrollListener.onSelected(mCurrentIndex, mTotalCount);
            mDeSelectedItems.put(mCurrentIndex, false);
            for (int i = 0, j = mTotalCount; i < j; i++) {
                if (i == mCurrentIndex) {
                    continue;
                }
                boolean disSelected = mDeSelectedItems.get(i);
                if (!disSelected) {
                    //选中状态
                    mScrollListener.onDisSelected(i, mTotalCount);
                    mDeSelectedItems.put(i, true);
                }
            }
        }
    }

    /**
     * 状态变化
     *
     * @param scrollState
     */
    public void onPageScrollStateChanged(int scrollState) {
        mScrollState = scrollState;
    }

    /**
     * 获取当前下标
     *
     * @param index
     * @return
     */
    public int setCurrentIndex(int index) {
        mLastIndex = mCurrentIndex;
        mCurrentIndex = getSafeIndex(index);//重新赋值
        return mCurrentIndex;
    }

    /**
     * 外部获取我们当前下标
     */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 外部设置总数
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    /**
     * 外部获取我们的状态
     */
    public int getScrollState() {
        return mScrollState;
    }

    /**
     * 外部获取我们总数
     */
    public int getTotalCount() {
        return mTotalCount;
    }

}
