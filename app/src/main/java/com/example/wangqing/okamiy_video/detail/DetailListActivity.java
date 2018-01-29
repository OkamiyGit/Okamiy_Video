package com.example.wangqing.okamiy_video.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;
import com.example.wangqing.okamiy_video.indircator.CoolIndicatorLayout;
import com.example.wangqing.okamiy_video.indircator.IPagerIndicatorView;
import com.example.wangqing.okamiy_video.indircator.IPagerTitle;
import com.example.wangqing.okamiy_video.indircator.ViewPagerITitleView;
import com.example.wangqing.okamiy_video.indircator.ViewPagerIndicatorAdapter;
import com.example.wangqing.okamiy_video.indircator.ViewPagerIndicatorLayout;
import com.example.wangqing.okamiy_video.indircator.ViewPagerWrapper;
import com.example.wangqing.okamiy_video.indircator.ViewPaperIndicatorView;
import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.Site;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DetailListActivity extends BaseActivity {
    private static final String CHANNEL_ID = "channid";

    ViewPager mPager;
    private int mChannelId;
    //指示器title
    String[] mSiteNames = new String[]{"搜狐视频", "乐视视频"};
    private List<String> mDataSet = Arrays.asList(mSiteNames);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_list;
    }

    @Override
    protected void initView() {
        //接收外面传过来的intent
        Intent intent = getIntent();
        if (intent != null) {
            mChannelId = intent.getIntExtra(CHANNEL_ID, 0);
        }

        //设置标题：通过频道id拿到标题
        Channel channel = new Channel(mChannelId, this);
        String titleName = channel.getChannelName();
        //表示当前页面支持ActionBar
        setSupportActionBar();
        //显示返回箭头
        setSupportArrowActionBar(true);
        setTitle(titleName);

        mPager = bindViewId(R.id.pager);

        //viewPagerIndicator指示器依赖于ViewPager
        CoolIndicatorLayout coolIndicatorLayout = bindViewId(R.id.viewpager_indicator);
        //组配indicator及title
        ViewPagerIndicatorLayout viewPagerIndicatorLayout = new ViewPagerIndicatorLayout(this);
        viewPagerIndicatorLayout.setAdapter(new ViewPagerIndicatorAdapter() {
            @Override
            public int getCount() {
                return mDataSet.size();
            }

            //设置对应的title
            @Override
            public IPagerTitle getTitle(Context context, final int index) {
                ViewPagerITitleView viewPagerITitleView = new ViewPagerITitleView(context);
                viewPagerITitleView.setText(mDataSet.get(index));
                viewPagerITitleView.setNormalColor(Color.parseColor("#333333"));
                viewPagerITitleView.setSelectedColor(Color.parseColor("#e94220"));

                //设置title的点击事件：点击联动viewpager
                viewPagerITitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPager.setCurrentItem(index);
                    }
                });
                return viewPagerITitleView;
            }

            //指示器
            @Override
            public IPagerIndicatorView getIndicator(Context conext) {
                ViewPaperIndicatorView viewPaperIndicatorView = new ViewPaperIndicatorView(conext);
                viewPaperIndicatorView.setFillColor(Color.parseColor("#ebe4e3"));
                return viewPaperIndicatorView;
            }
        });

        //ViewPager变化则指示器也要有变化
        coolIndicatorLayout.setPagerIndicatorLayout(viewPagerIndicatorLayout);
        ViewPagerWrapper.with(coolIndicatorLayout, mPager).compose();
        mPager.setAdapter(new SitePagerAdapter(getSupportFragmentManager(), this, mChannelId));
    }

    @Override
    protected void initData() {

    }

    /**
     * 左上角返回箭头事件处理
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //选择了ActionBar返回箭头，直接返回
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建与Fragment相关的Adapter
     */
    private class SitePagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private int mChannelId;
        private HashMap<Integer, DetailListFragment> mPagerMap;

        public SitePagerAdapter(FragmentManager fm, Context context, int channelId) {
            super(fm);
            this.mChannelId = channelId;
            this.mContext = context;
            mPagerMap = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            /**
             * 根据Item的positon创建对应的Fragment
             * 参数： 平台Id，频道id
             * position 默认为0,+1为我们的搜狐视频
             *
             */
            Fragment fragment = DetailListFragment.newInstance(position + 1, mChannelId);
            return fragment;
        }

        @Override
        public int getCount() {
            return Site.MAX_SITE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof DetailListFragment) {
                mPagerMap.put(position, (DetailListFragment) object);
            }
            return object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPagerMap.remove(position);
        }
    }

    /**
     * 外部调用就会进入此页面
     *
     * @param context
     * @param channelId 频道Id
     */
    public static void launchDetailListActivity(Context context, int channelId) {
        Intent intent = new Intent(context, DetailListActivity.class);
        //栈顶模式，防止实例多次调用
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CHANNEL_ID, channelId);
        context.startActivity(intent);
    }
}
