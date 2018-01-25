package com.example.wangqing.okamiy_video;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.wangqing.okamiy_video.base.BaseActivity;
import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.Site;

import java.util.HashMap;

public class DetailListActivity extends BaseActivity {
    private static final String CHANNEL_ID = "channid";

    ViewPager mPager;
    private int mChannelId;

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
        mToolBar.setTitle(titleName);

        mPager = bindViewId(R.id.pager);
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
            //平台Id，频道id
            Fragment fragment = DetailListFragment.newInstance(new Site(1).getSiteId(), mChannelId);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
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
