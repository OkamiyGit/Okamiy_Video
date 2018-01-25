package com.example.wangqing.okamiy_video;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 引导页
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.ll_dots_layout)
    LinearLayout mLlDotsLayout;
    private List<View> mViewList;
    private MyViewPager adapter;
    private ImageView[] mDotsView;
    //上一个指示器位置
    private int mLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initViews();
        initViewPager();
        initDots();
    }

    /**
     * 初始化引导页指示器
     */
    private void initDots() {
        mDotsView = new ImageView[mViewList == null ? 0 : mViewList.size()];
        //根据引导页size添加指示器，并设置为未选中状态
        for (int i = 0; i < mViewList.size(); i++) {
            mDotsView[i] = (ImageView) mLlDotsLayout.getChildAt(i);
            mDotsView[i].setEnabled(false);
        }
        //设置第一个为选中状态
        mLastPosition = 0;
        mDotsView[0].setEnabled(true);
    }

    /**
     * 设置Viewpager
     */
    private void initViewPager() {
        adapter = new MyViewPager(mViewList);
        mViewpager.setAdapter(adapter);
        mViewpager.addOnPageChangeListener(this);
    }

    /**
     * 初始化三个引导页面
     */
    private void initViews() {
        mViewList = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        mViewList.add(inflater.inflate(R.layout.guide_one, null));
        mViewList.add(inflater.inflate(R.layout.guide_two, null));
        mViewList.add(inflater.inflate(R.layout.guide_three, null));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentPosition(position);
    }

    /**
     * 设置当前点为选中状态,并将上一个指示器设置为未选中状态
     *
     * @param position
     */
    private void setCurrentPosition(int position) {
        mDotsView[position].setEnabled(true);
        mDotsView[mLastPosition].setEnabled(false);
        //改变位置
        mLastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Viewpager
     */
    class MyViewPager extends PagerAdapter {

        private List<View> mViews;

        public MyViewPager(List<View> list) {
            super();
            this.mViews = list;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mViews != null) {
                if (mViews.size() > 0) {
                    container.addView(mViews.get(position));
                    /* 判断是最后一个页面，点击按钮进行跳转 */
                    if (position == mViews.size() - 1) {
                        ImageView iv = mViews.get(position).findViewById(R.id.iv_start);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GuideActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                                setGuided();
                            }
                        });
                    }
                    return mViews.get(position);
                }
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mViews != null) {
                if (mViews.size() > 0) {
                    container.removeView(mViews.get(position));
                }
            }
        }

        @Override
        public int getCount() {
            return mViews == null ? 0 : mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 引导页状态更改
     */
    private void setGuided() {
        SharedPreferences.Editor edit = getSharedPreferences("config", MODE_PRIVATE).edit();
        edit.putBoolean("mIsFirstIn", false);
        edit.apply();
    }
}
