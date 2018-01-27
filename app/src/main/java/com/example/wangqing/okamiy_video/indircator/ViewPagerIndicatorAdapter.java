package com.example.wangqing.okamiy_video.indircator;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * 用来适配我们的ViewPager的title和indircator
 */

public abstract class ViewPagerIndicatorAdapter {
    /**
     * 获取数目
     *
     * @return
     */
    public abstract int getCount();

    /**
     * 获取名字
     *
     * @param context
     * @param index   对应的下标
     * @return
     */
    public abstract IPagerTitle getTitle(Context context, int index);

    /**
     * 获取指示器
     *
     * @param conext
     * @return
     */
    public abstract IPagerIndicatorView getIndicator(Context conext);

    /**
     * 获取title的权重
     *
     * @return
     */
    public float getTitleWeight() {
        return 1;
    }

    /**
     * 观察者模式
     */
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    /**
     * 注册观察者
     *
     * @param Observable
     */
    public final void registerDataSetObservable(DataSetObserver Observable) {
        mDataSetObservable.registerObserver(Observable);
    }

    /**
     * 解除注册
     *
     * @param Observable
     */
    public final void unregisterDataSetObservable(DataSetObserver Observable) {
        mDataSetObservable.unregisterObserver(Observable);
    }

    /**
     * 观察数据发生变化，通知
     */
    public final void notifySetDataChanged() {
        mDataSetObservable.notifyChanged();
    }
}
