package com.example.wangqing.okamiy_video.detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.api.OnGetVideoListener;
import com.example.wangqing.okamiy_video.api.SiteApi;
import com.example.wangqing.okamiy_video.base.BaseFragment;
import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.sohu.Video;
import com.example.wangqing.okamiy_video.model.sohu.VideoList;
import com.example.wangqing.okamiy_video.widget.CustomGridView;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * 详情页中间部分
 */

public class AlbumPlayGridFragment extends BaseFragment {
    private static final String TAG = "AlbumPlayGridFragment";
    private static final String ARGS_ALBUM = "album";
    private static final String ARGS_IS_SHOWDESC = "isShowDesc";
    private static final String ARGS_INIT_POSITION = "initVideoPosition";

    private Album mAlbum;
    private int mInitPosition;
    private boolean mIsShowDesc;
    private int mPageNo;//页码
    private int mPageSize;//剧集数量
    private VideoItemAdapter mVideoItemAdatper;
    private CustomGridView mCustomGridView;
    private int mPageTotal;
    private View mEmptyView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsFristSelection = true;//默认给一个选中的变量
    private int mCurrentPosition;//当前是哪一个位置选中
    private OnPlayVideoSelectedListener mOnPlayVideoSelectedListener;

    public void setPlayVideoSelectedListener(OnPlayVideoSelectedListener listener) {
        mOnPlayVideoSelectedListener = listener;
    }

    //当选中需要播放的视频监听（给AlbumDetailActivity回调）
    public interface OnPlayVideoSelectedListener {
        void OnPlayVideoSelected(Video video, int position);
    }

    public AlbumPlayGridFragment() {

    }

    /**
     * 创建Fragment
     *
     * @param album             专辑
     * @param isShowDesc        是否显示描述
     * @param initVideoPosition 初始化位置
     * @return
     */
    public static AlbumPlayGridFragment newInstance(Album album, boolean isShowDesc, int initVideoPosition) {
        AlbumPlayGridFragment fragment = new AlbumPlayGridFragment();
        //通过bundle传参
        Bundle bundle = new Bundle();
        //对象传递需要序列化传递
        bundle.putParcelable(ARGS_ALBUM, album);
        bundle.putBoolean(ARGS_IS_SHOWDESC, isShowDesc);
        bundle.putInt(ARGS_INIT_POSITION, initVideoPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, ">>   oncreate   >>");

        //获取我们传递的参数
        if (getArguments() != null) {
            mAlbum = getArguments().getParcelable(ARGS_ALBUM);
            mIsShowDesc = getArguments().getBoolean(ARGS_IS_SHOWDESC);
            //默认进来选中第0个
            mInitPosition = getArguments().getInt(ARGS_INIT_POSITION);
            //进来时赋值第0个选中
            mCurrentPosition = mInitPosition;
            mPageNo = 0;
            //剧集数量
            mPageSize = 50;

            //设置Adapter
            mVideoItemAdatper = new VideoItemAdapter(getActivity(), mAlbum.getVideoTotal(), mVideoSelectedListner);
            mVideoItemAdatper.setIsShowTitleContent(mIsShowDesc);
            mPageTotal = (mAlbum.getVideoTotal() + mPageSize - 1) / mPageSize;
            loadData();
        }
    }

    //Listener
    private OnVideoSelectedListener mVideoSelectedListner = new OnVideoSelectedListener() {
        @Override
        public void onVideoSelected(Video video, int position) {
            //点击那个设置为选中
            if (mCustomGridView != null) {
                mCustomGridView.setSelection(position);
                mCustomGridView.setItemChecked(position, true);
                //更新选中位置
                mCurrentPosition = position;
                //通过接口把选中传出（AlbumDetailActivity层回调）
                if (mOnPlayVideoSelectedListener != null) {
                    mOnPlayVideoSelectedListener.OnPlayVideoSelected(video, position);
                }
            }
        }
    };

    //加载数据
    private void loadData() {
        Log.i(TAG, ">>    loadData    >>");

        mPageNo++;
        SiteApi.onGetVideo(mPageSize, mPageNo, mAlbum, new OnGetVideoListener() {
            @Override
            public void OnGetVideoSuccess(VideoList videoList) {
                Log.i(TAG, ">>    OnGetVideoSuccess    >> " + videoList.size());

                //给adapter添加数据
                for (Video video : videoList) {
                    mVideoItemAdatper.addVideo(video);
                    Log.i(TAG, ">>  OnGetVideoSuccess video   >> " + video.toString());
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.GONE);
                        if (mVideoItemAdatper != null) {
                            mVideoItemAdatper.notifyDataSetChanged();
                        }

                        if (mVideoItemAdatper.getCount() > mInitPosition && mIsFristSelection) {
                            //设置选中状态
                            mCustomGridView.setSelection(mInitPosition);
                            mCustomGridView.setItemChecked(mInitPosition, true);
                            mIsFristSelection = false;
                            //睡100毫秒，目的让我们的gridview进行平移动作
                            SystemClock.sleep(100);
                            mCustomGridView.smoothScrollToPosition(mInitPosition);
                        }
                    }
                });
            }

            @Override
            public void OnGetVideoFailed(ErrorInfo info) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album_desc;
    }

    @Override
    protected void initView() {
        mEmptyView = bindViewId(R.id.tv_empty);
        mEmptyView.setVisibility(View.VISIBLE);
        mCustomGridView = bindViewId(R.id.gv_video_layout);

        //mIsShowDesc 表示同样是剧集,综艺节目是xx期,电视剧集是数字, 1表示综艺,或纪录片类,6表示动漫,电视剧
        mCustomGridView.setNumColumns(mIsShowDesc ? 1 : 6);
        mCustomGridView.setAdapter(mVideoItemAdatper);
        //是否还有更多数据
        if (mAlbum.getVideoTotal() > 0 && mAlbum.getVideoTotal() > mPageSize) {
            mCustomGridView.setHasMoreItem(true);
        } else {
            mCustomGridView.setHasMoreItem(false);
        }

        mCustomGridView.setOnLoadMoreListener(new CustomGridView.OnLoadMoreListener() {
            @Override
            public void onLoadMoreItems() {
                loadData();
            }
        });
    }

    @Override
    protected void initData() {

    }
}
