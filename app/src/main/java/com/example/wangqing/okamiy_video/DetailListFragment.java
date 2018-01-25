package com.example.wangqing.okamiy_video;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.wangqing.okamiy_video.base.BaseFragment;
import com.example.wangqing.okamiy_video.widget.PullLoadRecyclerView;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class DetailListFragment extends BaseFragment {
    private static int mSiteId;
    private static int mChannelId;
    private static final String CHANNEL_ID = "channelid";
    private static final String SITE_ID = "siteid";
    private PullLoadRecyclerView mRecyclerView;

    public static Fragment newInstance(int siteId, int channelId) {
        DetailListFragment fragment = new DetailListFragment();
        mChannelId = channelId;
        mSiteId = siteId;
        //通过Bundle传值到Fragment
        Bundle bundle = new Bundle();
        bundle.putInt(SITE_ID, siteId);
        bundle.putInt(CHANNEL_ID, channelId);
        //如：乐视，电视剧页面，fragment需要知道
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detaillist;
    }

    @Override
    protected void initView() {
        mRecyclerView = bindViewId(R.id.my_recyclerview);
        mRecyclerView.setGridLayout(3);
        mRecyclerView.setAdapter(new DetailListAdapter());
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
    }

    @Override
    protected void initData() {

    }

    /**
     * 设置adapter
     */
    class DetailListAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    /**
     * 回调，刷新和加载更多
     */
    class PullLoadMoreListener implements PullLoadRecyclerView.OnPullLoadMoreListener {

        @Override
        public void reFresh() {

        }

        @Override
        public void loadMore() {

        }
    }
}
