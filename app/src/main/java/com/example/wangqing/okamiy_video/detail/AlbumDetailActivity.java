package com.example.wangqing.okamiy_video.detail;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangqing.okamiy_video.AppManager;
import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.api.OnGetAlbumDetailListener;
import com.example.wangqing.okamiy_video.api.OnGetVideoPlayUrlListener;
import com.example.wangqing.okamiy_video.api.SiteApi;
import com.example.wangqing.okamiy_video.base.BaseActivity;
import com.example.wangqing.okamiy_video.comment.CommonDBHelper;
import com.example.wangqing.okamiy_video.model.Album;
import com.example.wangqing.okamiy_video.model.ErrorInfo;
import com.example.wangqing.okamiy_video.model.sohu.Video;
import com.example.wangqing.okamiy_video.player.PlayActivity;
import com.example.wangqing.okamiy_video.utils.ImageUtils;

/**
 * 专辑界面
 */
public class AlbumDetailActivity extends BaseActivity {
    private static final String TAG = "AlbumDetailActivity";
    private Album mAlbum;
    private boolean mIsShowDesc;
    private int mVideoNo;

    private ImageView mAlbumImg;//海报图
    private TextView mAlbumName;// 名称
    private TextView mDirector;//导演
    private TextView mMainActor;//主演
    private TextView mAlbumDesc;//描述
    private boolean mIsFavor;//是否已经收藏（false ：未收藏）
    private AlbumPlayGridFragment mFragment;//展示详情页中间部分
    private Button mSuperBitstreamButton;
    private Button mNormalBitstreamButton;
    private Button mHighBitstreamButton;
    private int mCurrentVideoPosition;//当前正在播的position
    private CommonDBHelper mFavoriteDBHelper;
    private CommonDBHelper mHistoryDBHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_detail;
    }

    @Override
    protected void initView() {

        //获取传递的参数
        mAlbum = getIntent().getParcelableExtra("album");
        mVideoNo = getIntent().getIntExtra("videoNo", 0);
        mIsShowDesc = getIntent().getBooleanExtra("isShowDesc", false);

        //设置Actionbar
        setSupportActionBar();
        setSupportArrowActionBar(true);
        setTitle(mAlbum.getTitle());//显示标题

        //初始化布局
        mAlbumImg = bindViewId(R.id.iv_album_image);
        mAlbumName = bindViewId(R.id.tv_album_name);
        mDirector = bindViewId(R.id.tv_album_director);
        mMainActor = bindViewId(R.id.tv_album_mainactor);
        mAlbumDesc = bindViewId(R.id.tv_album_desc);
        mSuperBitstreamButton = bindViewId(R.id.bt_super);

        //初始化数据库
        mFavoriteDBHelper = new CommonDBHelper(this);
        mFavoriteDBHelper.setParams("favorite");
        mHistoryDBHelper = new CommonDBHelper(this);
        mHistoryDBHelper.setParams("history");
        //能找到说明有记录
        mIsFavor = mFavoriteDBHelper.getAlbumById(mAlbum.getAlbumId(), mAlbum.getSite().getSiteId()) != null;

        //设置播放按钮的点击监听
        mSuperBitstreamButton.setOnClickListener(mOnSuperClickListener);
        mNormalBitstreamButton = bindViewId(R.id.bt_normal);
        mNormalBitstreamButton.setOnClickListener(mOnNormalClickListener);
        mHighBitstreamButton = bindViewId(R.id.bt_high);
        mHighBitstreamButton.setOnClickListener(mOnHighClickListener);
    }

    private View.OnClickListener mOnSuperClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonClick(v);
        }
    };

    private View.OnClickListener mOnNormalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonClick(v);
        }
    };

    private View.OnClickListener mOnHighClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonClick(v);
        }
    };

    //三个button有共同点,tag设置的id是一样,value值不一样
    private void handleButtonClick(View v) {
        Button button = (Button) v;
        //取播放源
        String url = (String) button.getTag(R.id.key_video_url);
        //码流类型
        int type = (int) button.getTag(R.id.key_video_stream);//码流类型
        //视频
        Video video = (Video) button.getTag(R.id.key_video);
        Log.i(TAG, ">>   handleButtonClick  点击播放的  video   >>" + video);
        //当前位置
        int currentPosition = (int) button.getTag(R.id.key_current_video_number);
        if (AppManager.isNetWorkAvailable()) {
            if (AppManager.isNetworkWifiAvailable()) {
                //wifi链接状态，进行跳转播放
                mHistoryDBHelper.add(mAlbum);//添加播放记录
                Intent intent = new Intent(AlbumDetailActivity.this, PlayActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("type", type);
                intent.putExtra("currentPosition", currentPosition);
                intent.putExtra("video", video);
                startActivity(intent);
            } else {
                // TODO 拓展  数据网络
            }
        }

    }

    //监听AlbumPlayGridFragment层选中那个video
    private AlbumPlayGridFragment.OnPlayVideoSelectedListener mPlayVideoSelectedListener = new AlbumPlayGridFragment.OnPlayVideoSelectedListener() {
        @Override
        public void OnPlayVideoSelected(Video video, int position) {
            mCurrentVideoPosition = position;
            //获取播放信息
            SiteApi.onGetVideoPlayUrl(video, mVideoUrlListener);
        }
    };

    //三种码流选中监听
    private OnGetVideoPlayUrlListener mVideoUrlListener = new OnGetVideoPlayUrlListener() {
        @Override
        public void onGetSuperUrl(final Video video, final String url) {
            Log.i(TAG, ">>   onGetSuperUrl  超清  地址 url    >>" + url + ", video " + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //设置一个tag，标识它来自于哪里
                    mSuperBitstreamButton.setVisibility(View.VISIBLE);
                    mSuperBitstreamButton.setTag(R.id.key_video_url, url); //视频url
                    mSuperBitstreamButton.setTag(R.id.key_video, video);//视频info
                    mSuperBitstreamButton.setTag(R.id.key_current_video_number, mCurrentVideoPosition);//当前视频
                    mSuperBitstreamButton.setTag(R.id.key_video_stream, StreamType.SUPER); //码流
                }
            });
        }

        @Override
        public void onGetNoramlUrl(final Video video, final String url) {
            Log.i(TAG, ">>     onGetNoramlUrl 标清  地址 url     >>" + url + ", video " + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNormalBitstreamButton.setVisibility(View.VISIBLE);
                    mNormalBitstreamButton.setTag(R.id.key_video_url, url); //视频url
                    mNormalBitstreamButton.setTag(R.id.key_video, video);//视频info
                    mNormalBitstreamButton.setTag(R.id.key_current_video_number, mCurrentVideoPosition);//当前视频
                    mNormalBitstreamButton.setTag(R.id.key_video_stream, StreamType.NORMAL); //码流
                }
            });
        }

        @Override
        public void onGetHighUrl(final Video video, final String url) {
            Log.i(TAG, ">>   onGetHighUrl 高清  地址  url     >>" + url + ", video " + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHighBitstreamButton.setVisibility(View.VISIBLE);
                    mHighBitstreamButton.setTag(R.id.key_video_url, url); //视频url
                    mHighBitstreamButton.setTag(R.id.key_video, video);//视频info
                    mHighBitstreamButton.setTag(R.id.key_current_video_number, mCurrentVideoPosition);//当前视频
                    mHighBitstreamButton.setTag(R.id.key_video_stream, StreamType.HIGH); //码流
                }
            });
        }

        @Override
        public void onGetFailed(ErrorInfo info) {
            Log.d(TAG, ">>   onGetFailed 失败 url   >> " + info.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideAllButton();//请求播放源失败,不展示
                }
            });
        }
    };

    //三种码流（这种静态常量比枚举快）
    public class StreamType {
        public static final int SUPER = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;
    }

    //隐藏三种button
    private void hideAllButton() {
        mSuperBitstreamButton.setVisibility(View.GONE);
        mNormalBitstreamButton.setVisibility(View.GONE);
        mHighBitstreamButton.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        //填充数据
        updateInfo();

        //补全详情页数据:链接请求数据（获取数据在子线程执行）
        SiteApi.onGetAlbumDetail(mAlbum, new OnGetAlbumDetailListener() {
            @Override
            public void onGetAlbumDetailSuccess(final Album album) {
                Log.i(TAG, ">> onGetAlbumDetailSuccess album >>" + album.getVideoTotal());
                mAlbum = album;

                //详情页中间部分需要fragment展示,并且需要在主线程执行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInfo();
                        mFragment = AlbumPlayGridFragment.newInstance(mAlbum, mIsShowDesc, 0);
                        //实例化监听，可以回调AlbumPlayGridFragment层
                        mFragment.setPlayVideoSelectedListener(mPlayVideoSelectedListener);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        //填充布局
                        ft.replace(R.id.fragment_container, mFragment);
                        ft.commit();
                        //延迟去构造
                        getFragmentManager().executePendingTransactions();
                    }
                });
            }

            @Override
            public void onGetAlbumDetailFailed(ErrorInfo info) {

            }
        });
    }

    /**
     * 设置数据
     */
    private void updateInfo() {
        mAlbumName.setText(mAlbum.getTitle());

        //导演
        if (!TextUtils.isEmpty(mAlbum.getDirector())) {
            mDirector.setText(getResources().getString(R.string.director) + mAlbum.getDirector());
            mDirector.setVisibility(View.VISIBLE);
        } else {
            mDirector.setVisibility(View.GONE);
        }

        //主演
        if (!TextUtils.isEmpty(mAlbum.getMainActor())) {
            mMainActor.setText(getResources().getString(R.string.mainactor) + mAlbum.getMainActor());
            mMainActor.setVisibility(View.VISIBLE);
        } else {
            mMainActor.setVisibility(View.GONE);
        }

        //描述
        if (!TextUtils.isEmpty(mAlbum.getAlbumDesc())) {
            mAlbumDesc.setText(mAlbum.getAlbumDesc());
            mAlbumDesc.setVisibility(View.VISIBLE);
        } else {
            mAlbumDesc.setVisibility(View.GONE);
        }

        //海报图
        if (!TextUtils.isEmpty(mAlbum.getVerImgUrl())) {
            //竖图
            ImageUtils.disPlayImage(mAlbumImg, mAlbum.getVerImgUrl());
        } else if (!TextUtils.isEmpty(mAlbum.getHorImgUrl())) {
            //横图
            ImageUtils.disPlayImage(mAlbumImg, mAlbum.getHorImgUrl());
        }
    }

    /**
     * 电视剧和动漫频道之外的启动方式
     *
     * @param activity
     * @param album      对应的专辑频道
     * @param vidoeNo    第几集
     * @param isShowDesc 是否显示描述信息
     */
    public static void launch(Activity activity, Album album, int vidoeNo, boolean isShowDesc) {
        Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("album", album);
        intent.putExtra("videoNo", vidoeNo);
        intent.putExtra("isShowDesc", isShowDesc);
        activity.startActivity(intent);
    }

    /**
     * 电视剧和动漫频道采用这个启动方式
     *
     * @param activity
     * @param album
     */
    public static void launch(Activity activity, Album album) {
        //栈顶模式，并携带参数
        Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("album", album);
        activity.startActivity(intent);
    }

    /**
     * 创建menu
     * 只会调用一次，他只会在Menu显示之前去调用一次，之后就不会在去调用。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_detail_menu, menu);
        return true;
    }

    /**
     * 必须先创建menu,否则会报空指针
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //已收藏
        MenuItem favitem = menu.findItem(R.id.action_favor_item);
        //未收藏
        MenuItem unfavitem = menu.findItem(R.id.action_unfavor_item);
        //已收餐时显示favitem
        favitem.setVisible(mIsFavor);
        unfavitem.setVisible(!mIsFavor);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 处理菜单被选中后的事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 选择了ActionBar返回箭头，直接返回
            case android.R.id.home://actionbar 左边箭头id
                finish();
                return true;
            case R.id.action_favor_item:
                if (mIsFavor) {
                    mIsFavor = false;
                    // 收藏状态更新
                    mFavoriteDBHelper.delete(mAlbum.getAlbumId(), mAlbum.getSite().getSiteId());
                    invalidateOptionsMenu();
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_unfavor_item:
                if (!mIsFavor) {
                    mIsFavor = true;
                    // 收藏状态更新
                    mFavoriteDBHelper.add(mAlbum);
                    invalidateOptionsMenu();
                    Toast.makeText(this, "已添加收藏", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
