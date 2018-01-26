package com.example.wangqing.okamiy_video;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangqing.okamiy_video.api.OnGetChannelAlbumListener;
import com.example.wangqing.okamiy_video.api.SiteApi;
import com.example.wangqing.okamiy_video.base.BaseFragment;
import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.AlbumList;
import com.example.wangqing.okamiy_video.model.Channel;
import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.Site;
import com.example.wangqing.okamiy_video.widget.PullLoadRecyclerView;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class DetailListFragment extends BaseFragment {
    private static final String TAG = "DetailListFragment";
    private int mSiteId;
    private int mChannelId;
    private static final String CHANNEL_ID = "channelid";
    private static final String SITE_ID = "siteid";
    //刷新时长
    private static final int REFREASH_DURATION = 1500;
    //加载更多时长（加在一页或者更多数据）
    private static final int LOADMORE_DURATION = 3000;
    private PullLoadRecyclerView mRecyclerView;
    private DetailListAdapter mAdapter;
    //没有数据、网络原因等造成数据为空时的布局
    private TextView mEmptyView;

    //RecyclerView显示几列
    private int mColumns;
    //第几页
    private int pageNo;
    //每页显示的数据条目
    private int pageSize = 30;
    /**
     * 刷数据相关的handler
     * 直接创建Handler可能会引发内存泄漏，故采用这种方式
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static Fragment newInstance(int siteId, int channelId) {
        DetailListFragment fragment = new DetailListFragment();
        //通过Bundle传值到Fragment
        Bundle bundle = new Bundle();
        bundle.putInt(SITE_ID, siteId);
        bundle.putInt(CHANNEL_ID, channelId);
        //如：乐视，电视剧页面，fragment需要知道
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 重写onCreate，为网络相关做一些操作
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //此时Fragment以创立，可以直接获取bundle
        if (getArguments() != null) {
            mSiteId = getArguments().getInt(SITE_ID);
            mChannelId = getArguments().getInt(CHANNEL_ID);
        }

        pageNo = 0;
        mAdapter = new DetailListAdapter(getActivity(), new Channel(mChannelId, getActivity()));
        //第一次加载数据
        loadData();

        //根据分类添加不同的数据
        if (mSiteId == Site.LETV) {
            mColumns = 2;
            //在乐视下相关频道为2列
            mAdapter.setColumns(mColumns);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detaillist;
    }

    @Override
    protected void initView() {
        mEmptyView = bindViewId(R.id.tv_empty);
        mRecyclerView = bindViewId(R.id.my_recyclerview);

        //开始正在加载数据
        mEmptyView.setText(getActivity().getResources().getString(R.string.load_more_text));

        //RecyclerView
        mRecyclerView.setGridLayout(3);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());


    }

    @Override
    protected void initData() {

    }

    /**
     * 回调，刷新和加载更多
     */
    class PullLoadMoreListener implements PullLoadRecyclerView.OnPullLoadMoreListener {

        @Override
        public void reFresh() {
            //1.5秒执行刷新
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reFreshData();
                    //通知RecyclerView刷新完成
                    mRecyclerView.setRefreshCompleted();
                }
            }, REFREASH_DURATION);
        }

        @Override
        public void loadMore() {
            //3.0秒执行加载更多
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    //通知RecyclerView加载完成
                    mRecyclerView.setLoadMoreCompleted();
                }
            }, LOADMORE_DURATION);
        }
    }

    /**
     * 请求接口加载更多数据(首次进来加载数据和加载更多)
     */
    private void loadData() {
        pageNo++;
        Log.i(TAG, "loadData: " + mChannelId);
        SiteApi.onGetChannelAlbums(getActivity(), pageNo, pageSize, mSiteId, mChannelId, new OnGetChannelAlbumListener() {
            @Override
            public void onGetChannelAlbumSuccess(AlbumList albumList) {
                /**
                 * 成功获取了数据：
                 *    01.把正在加载隐藏掉---> 这里是子线程，需要用handler进行post到主线程操作
                 */
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.GONE);
                    }
                });

                /**
                 *  02.遍历拿到的数据添加到我们的Adapter
                 */
                for (Album album : albumList) {
                    if (mAdapter != null) {
                        mAdapter.setData(album);
                    }
                }

                /**
                 * 拿到数据进行刷新：拿handler进行post刷新操作
                 */
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                //                //打印返回数据
                //                for (Album album : albumList) {
                //                    Log.i(TAG, "onGetChannelAlbumSuccess: " + album.toJson());
                //                }
            }

            @Override
            public void onGetChannelAlbumFailed(ErrorInfo info) {
                /**
                 * 失败的时候 ：
                 * 显示数据加载失败提示---> 这里是子线程，需要用handler进行post到主线程操作
                 */
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText(getActivity().getResources().getString(R.string.data_failed_tip));
                    }
                });

            }
        });

    }

    /**
     * 请求接口加载数据
     */
    private void reFreshData() {

    }

    /**
     * 设置adapter
     */
    class DetailListAdapter extends RecyclerView.Adapter {
        private int mColumns;
        private Context mContext;
        private Channel mChannel;
        private AlbumList mAlbumList;

        public DetailListAdapter(Activity activity, Channel channel) {
            this.mChannel = channel;
            this.mContext = activity;
            this.mAlbumList = new AlbumList();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.detail_list_item, null);
            ItemViewHolder holder = new ItemViewHolder(view);
            view.setTag(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mAlbumList.size() == 0) {
                return;
            }

            //获取Ablum
            Album album = getItem(position);
            //进行匹配,之后设置数据
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.albumName.setText(album.getTitle());
                if (album.getTip().isEmpty()) {
                    itemViewHolder.albumTip.setVisibility(View.GONE);
                } else {
                    itemViewHolder.albumTip.setText(album.getTip());
                }

                //                Point point = null;
                //                //重新计算宽高
                //                if (mColumns == 2) {
                //                    point = ImageUtils.getHorPostSize(mContext, mColumns);
                //                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x, point.y);
                //                    itemViewHolder.albumPoster.setLayoutParams(params);
                //                } else {
                //                    point = ImageUtils.getVerPostSize(mContext, mColumns);
                //                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x, point.y);
                //                    itemViewHolder.albumPoster.setLayoutParams(params);
                //                }
                //
                //                if (album.getVerImgUrl() != null) {
                //                    ImageUtils.disPlayImage(itemViewHolder.albumPoster, album.getVerImgUrl(), point.x, point.y);
                //                } else if (album.getHorImgUrl() != null) {
                //                    ImageUtils.disPlayImage(itemViewHolder.albumPoster, album.getHorImgUrl(), point.x, point.y);
                //                } else {
                //                    //TOD 默认图
                //                }
                //                itemViewHolder.resultContainer.setOnClickListener(new View.OnClickListener() {
                //                    @Override
                //                    public void onClick(View v) {
                //                        if (mChannelId == Channel.DOCUMENTRY || mChannelId == Channel.MOVIE || mChannelId == Channel.VARIETY || mChannelId == Channel.MUSIC) {
                //                            AlbumDetailActivity.launch(getActivity(), album, 0, true);
                //                        } else {
                //                            AlbumDetailActivity.launch(getActivity(), album);
                //                        }
                //                    }
                //                });

            }

        }

        @Override
        public int getItemCount() {
            return mAlbumList.size() > 0 ? mAlbumList.size() : 0;
        }

        /**
         * 拿到该位置的Album
         *
         * @param position
         */
        private Album getItem(int position) {
            return mAlbumList.get(position);
        }

        /**
         * 设置列数
         *
         * @param columns
         */
        public void setColumns(int columns) {
            this.mColumns = columns;
        }

        /**
         * 设置数据
         */
        public void setData(Album album) {
            mAlbumList.add(album);
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout resultContainer;
            private ImageView albumPoster;
            private TextView albumName;
            private TextView albumTip;

            public ItemViewHolder(View view) {
                super(view);
                resultContainer = (LinearLayout) view.findViewById(R.id.album_container);
                albumPoster = (ImageView) view.findViewById(R.id.iv_album_poster);
                albumTip = (TextView) view.findViewById(R.id.tv_album_tip);
                albumName = (TextView) view.findViewById(R.id.tv_album_name);
            }
        }
    }


}
