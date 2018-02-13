package com.example.wangqing.okamiy_video.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;
import com.example.wangqing.okamiy_video.detail.AlbumDetailActivity;
import com.example.wangqing.okamiy_video.model.sohu.Video;
import com.example.wangqing.okamiy_video.utils.DateUtils;
import com.example.wangqing.okamiy_video.utils.SysUtils;
import com.example.wangqing.okamiy_video.widget.media.IjkVideoView;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayActivity extends BaseActivity implements GestureDetectorController.IGestureListener {
    private static final String TAG = "PlayActivity";
    private String mUrl;
    private int mStreamType;
    private int mCurrentPosition;
    private Video mVideo;
    private String mLiveTitle;//直播节目标题
    private static final int CHECK_TIME = 1;//时间
    private static final int CHECK_BATTERY = 2;//电量
    private static final int CHECK_PROGRESS = 3;//进度
    private static final int AUTO_HIDE_TIME = 10000;//菜单面板显示多久隐藏
    private static final int AFTER_DRAGGLE_HIDE_TIME = 3000;//3秒消失菜单面板
    private IjkVideoView mVideoView;
    private RelativeLayout mLoadingLayout;
    private TextView mLoadingText;
    private FrameLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private ImageView mBackButton;
    private TextView mVideoNameView;
    private TextView mSysTimeView;
    private ImageView mBigPauseButton;
    private CheckBox mPlayOrPauseButton;
    private TextView mVideoCurrentTime;
    private TextView mVideoTotalTime;
    private TextView mBitStreamView;
    private EventHandler mEventHandler;
    private boolean mIsPanelShowing = false;//是否显示菜单面板
    private int mBatteryLevel;//电量
    private ImageView mBatteryView;
    private boolean mIsMove = false;//是否在屏幕上滑动
    private SeekBar mSeekBar;
    private Formatter mFormatter; //用于格式化播放时间
    private StringBuilder mFormatterBuilder;
    private boolean mIsDragging;
    private GestureDetectorController mGestureController;
    private TextView mDragHorizontalView;
    private TextView mDragVerticalView;
    private long mScrollProgress;
    private boolean mIsHorizontalScroll;//水平滑动
    private boolean mIsVerticalScroll;//垂直滑动
    private int mCurrentLight;
    private int mMaxLight = 255;
    private int mCurrentVolume;
    private int mMaxVolume = 10;
    private AudioManager mAudioManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        mUrl = getIntent().getStringExtra("url");
        mLiveTitle = getIntent().getStringExtra("title");
        mStreamType = getIntent().getIntExtra("type", 0);
        mCurrentPosition = getIntent().getIntExtra("currentPosition", 0);
        mVideo = getIntent().getParcelableExtra("video");
        Log.i(TAG, ">>   播放页面   ulr      >>" + mUrl + ", mStreamType " + mStreamType + ", mCurrentPosition " + mCurrentPosition);
        Log.i(TAG, ">>      播放页面 video      >> " + mVideo);

        mEventHandler = new EventHandler(Looper.myLooper());
        initAudio();
        initLight();
        initGesture();
        initTopAndBottomView();
        initCenterView();
        initListener();

        //init player
        mVideoView = bindViewId(R.id.video_view);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mLoadingLayout = bindViewId(R.id.rl_loading_layout);
        mLoadingText = bindViewId(R.id.tv_loading_info);
        mLoadingText.setText("正在加载中...");
        mVideoView.setVideoURI(Uri.parse(mUrl));
        //播放就绪状态
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mVideoView.start();
            }
        });

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START://缓冲
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://有视频画面开始
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                        mLoadingLayout.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //注册广播接收电量变化：电量发生变化广播
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //显示菜单面板
        toggleTopAndBottomLayout();
    }

    //监听
    private void initListener() {
        //返回
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //中间暂停按钮：播放
        mBigPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.start();
                //刷新状态
                updatePlayPauseStatus(true);
            }
        });

        //暂停/播放
        mPlayOrPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayPause();
            }
        });
    }

    //初始化顶部、底部布局
    private void initTopAndBottomView() {
        mTopLayout = bindViewId(R.id.fl_player_top_container);
        mBottomLayout = bindViewId(R.id.ll_player_bottom_layout);
        mBackButton = bindViewId(R.id.iv_player_close);//返回按钮
        mVideoNameView = bindViewId(R.id.tv_player_video_name);//video标题
        mBatteryView = bindViewId(R.id.iv_battery);
        mSysTimeView = bindViewId(R.id.tv_sys_time);//系统时间
        mBigPauseButton = bindViewId(R.id.iv_player_center_pause);//屏幕中央暂停按钮
        mPlayOrPauseButton = bindViewId(R.id.cb_play_pause);//底部播放暂停按钮
        mVideoCurrentTime = bindViewId(R.id.tv_current_video_time);//当前播放进度
        mVideoTotalTime = bindViewId(R.id.tv_total_video_time);//视频总时长
        mBitStreamView = bindViewId(R.id.tv_bitstream);//码流
        mSeekBar = bindViewId(R.id.sb_player_seekbar);
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mFormatterBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatterBuilder, Locale.getDefault());
    }

    //初始化手势监听
    private void initGesture() {
        mGestureController = new GestureDetectorController(this, this);
    }

    //初始化亮度控件
    private void initLight() {
        mCurrentLight = SysUtils.getDefaultBrightness(this);
        if (mCurrentLight == -1) {//获取不到亮度sharedpreferences文件
            mCurrentLight = SysUtils.getBrightness(this);
        }
    }

    private void initAudio() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //获取焦点
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 10;// 系统声音取值是0-10,*10为了和百分比相关
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 10;
    }

    //初始化中间控件
    private void initCenterView() {
        mDragHorizontalView = bindViewId(R.id.tv_horiontal_gesture);
        mDragVerticalView = bindViewId(R.id.tv_vertical_gesture);
    }

    @Override
    protected void initData() {
        Log.i(TAG, ">> 视频播放页面 initData mVideo=" + mVideo);
        if (mVideo != null) {
            Log.i(TAG, ">> 视频播放页面 initData mVideoName：" + mVideo.getVideoName());
            //设置视频名称
            mVideoNameView.setText(mVideo.getVideoName());
        }
        if (mLiveTitle != null) {
            mVideoNameView.setText(mLiveTitle);
        }
    }

    /**
     * 菜单面板开关
     */
    private void toggleTopAndBottomLayout() {
        if (mIsPanelShowing) {
            //隐藏
            hideTopAndBottomLayout();
        } else {
            showTopAndBottomLayout();
            //先显示,没有任何操作,就5s后隐藏
            mEventHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideTopAndBottomLayout();
                }
            }, AUTO_HIDE_TIME);
        }
    }

    /**
     * 显示菜单
     */
    private void showTopAndBottomLayout() {
        mIsPanelShowing = true;
        mTopLayout.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);
        //更新进度
        updateProgress();

        if (mEventHandler != null) {

            //时间更新
            mEventHandler.removeMessages(CHECK_TIME);
            Message msg = mEventHandler.obtainMessage(CHECK_TIME);
            mEventHandler.sendMessage(msg);

            //时间电量
            mEventHandler.removeMessages(CHECK_BATTERY);
            Message batterymsg = mEventHandler.obtainMessage(CHECK_BATTERY);
            mEventHandler.sendMessage(batterymsg);

            //时间进度
            mEventHandler.removeMessages(CHECK_PROGRESS);
            Message progressmsg = mEventHandler.obtainMessage(CHECK_PROGRESS);
            mEventHandler.sendMessage(progressmsg);
        }

        //码流
        switch (mStreamType) {
            //超清
            case AlbumDetailActivity.StreamType.SUPER:
                mBitStreamView.setText(getResources().getString(R.string.stream_super));
                break;
            //标清
            case AlbumDetailActivity.StreamType.NORMAL:
                mBitStreamView.setText(getResources().getString(R.string.stream_normal));
                break;
            //高清
            case AlbumDetailActivity.StreamType.HIGH:
                mBitStreamView.setText(getResources().getString(R.string.stream_high));
                break;
            default:
                break;
        }
    }

    /**
     * 播放暂停按钮
     */
    private void handlePlayPause() {
        if (mVideoView.isPlaying()) {//视频正在播放
            mVideoView.pause();
            updatePlayPauseStatus(false);
        } else {
            mVideoView.start();
            updatePlayPauseStatus(true);
        }
    }

    /**
     * 更新播放状态
     *
     * @param isPlaying
     */
    private void updatePlayPauseStatus(boolean isPlaying) {
        //屏幕中间暂停
        mBigPauseButton.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        mPlayOrPauseButton.invalidate();
        mPlayOrPauseButton.setChecked(isPlaying);
        mPlayOrPauseButton.refreshDrawableState();
    }

    /**
     * 隐藏菜单
     */
    private void hideTopAndBottomLayout() {
        //拖拽时不隐藏菜单
        if (mIsDragging == true) {
            return;
        }
        mIsPanelShowing = false;
        mTopLayout.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.GONE);
    }

    /**
     * 更新进度
     */
    private void updateProgress() {
        int currentPosition = mVideoView.getCurrentPosition();//当前的视频位置
        int duration = mVideoView.getDuration();//视频时长
        if (mSeekBar != null) {
            if (duration > 0) {
                //转成long型,避免溢出
                long pos = currentPosition * 1000L / duration;
                mSeekBar.setProgress((int) pos);
            }

            int perent = mVideoView.getBufferPercentage();//已经缓冲的进度
            mSeekBar.setSecondaryProgress(perent);//设置缓冲进度
            mVideoCurrentTime.setText(stringForTime(currentPosition));
            mVideoTotalTime.setText(stringForTime(duration));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //UP触发事件
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mIsMove == false) {
                toggleTopAndBottomLayout();
            } else {
                mIsMove = false;
            }

            //水平方向,up时,seek到对应位置播放
            if (mIsHorizontalScroll) {
                mIsHorizontalScroll = false;
                //更新视频进度
                mVideoView.seekTo((int) mScrollProgress);
                //一次down,up结束后mDragHorizontalView隐藏
                mDragHorizontalView.setVisibility(View.GONE);
            }
            if (mIsVerticalScroll) {
                mDragVerticalView.setVisibility(View.GONE);
                mIsVerticalScroll = false;
            }
        }
        return mGestureController.onTouchEvent(event);
    }

    /**
     * 进度条
     */
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        // seekbar进度发生变化时回调
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            long duration = mVideoView.getDuration();//视频时长
            long nowPosition = (duration * progress) / 1000L;
            //设置进度
            mVideoCurrentTime.setText(stringForTime((int) nowPosition));
        }

        // seekbar开始拖动时回调
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsDragging = true;
        }

        // seekbar拖动完成后回调
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mIsDragging = false;
            int progress = seekBar.getProgress();//最后拖动停止的进度
            long duration = mVideoView.getDuration();//视频时长
            long newPosition = (duration * progress) / 1000L;//当前的进度
            //停止时更新视频
            mVideoView.seekTo((int) newPosition);
            mEventHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideTopAndBottomLayout();
                }
            }, AFTER_DRAGGLE_HIDE_TIME);
        }
    };

    //创建一个Handler
    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_TIME:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSysTimeView.setText(DateUtils.getCurrentTime());
                        }
                    });
                    break;

                case CHECK_BATTERY:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentBattery();
                        }
                    });
                    break;

                case CHECK_PROGRESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long duration = mVideoView.getDuration();
                            long nowduration = (mSeekBar.getProgress() * duration) / 1000L;
                            mVideoCurrentTime.setText(stringForTime((int) nowduration));
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 通过广播获取系统电量情况
     */
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra("level", 0);
            Log.i(TAG, ">> 视频播放页面 mBatteryReceiver onReceive mBatteryLevel= >>" + mBatteryLevel);
        }
    };

    /**
     * 设置当前电量
     */
    private void setCurrentBattery() {
        Log.i(TAG, ">> 视频播放页面 setCurrentBattery level " + mBatteryLevel);
        if (0 < mBatteryLevel && mBatteryLevel <= 10) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (10 < mBatteryLevel && mBatteryLevel <= 20) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (20 < mBatteryLevel && mBatteryLevel <= 50) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_50);
        } else if (50 < mBatteryLevel && mBatteryLevel <= 80) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (80 < mBatteryLevel && mBatteryLevel <= 100) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 时间转化
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60; //换成秒
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / 3600);

        //先清空，再把时分秒存进去
        mFormatterBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    //开始滑动
    @Override
    public void onScrollStart(GestureDetectorController.ScrollType type) {
        mIsMove = true;
        switch (type) {
            case HORIZONTAL:
                mDragHorizontalView.setVisibility(View.VISIBLE);
                mScrollProgress = -1;//初始值
                mIsHorizontalScroll = true;//水平滑动标识
                break;

            case VERTICAL_LEFT:
                //申请权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getApplication())) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                    } else {
                        //有了权限，具体的动作
                        Log.i(TAG, "onScrollVerticalLeft: 0000000000000");
                        setComposeDrawableAndText(mDragVerticalView, R.drawable.ic_light, this);
                        mDragVerticalView.setVisibility(View.VISIBLE);
                        updateVerticalText(mCurrentLight, mMaxLight);
                        mIsVerticalScroll = true;
                    }
                }
                break;

            case VERTICAL_RIGH:
                if (mCurrentVolume > 0) {
                    setComposeDrawableAndText(mDragVerticalView, R.drawable.volume_normal, this);
                } else {
                    //静音
                    setComposeDrawableAndText(mDragVerticalView, R.drawable.volume_no, this);
                }
                mDragVerticalView.setVisibility(View.VISIBLE);
                updateVerticalText(mCurrentVolume, mMaxVolume);
                mIsVerticalScroll = true;
                break;
            default:
                break;
        }

    }

    //水平滑动：更新进度
    @Override
    public void onScrollHorizontal(float x1, float x2) {
        int width = getResources().getDisplayMetrics().widthPixels;
        int MAX_SEEK_STEP = 300000;//最大滑动5分钟
        int offset = (int) (x2 / width * MAX_SEEK_STEP) + mVideoView.getCurrentPosition();
        long progress = Math.max(0, Math.min(mVideoView.getDuration(), offset));
        mScrollProgress = progress;
        updateHorizontalText(progress);
    }

    @Override
    public void onScrollVerticalLeft(float y1, float y2) {
        try {
            int height = getResources().getDisplayMetrics().heightPixels;
            int offset = (int) (mMaxLight * y1) / height;
            if (Math.abs(offset) > 0) {
                mCurrentLight += offset;//得到变化后的亮度
                mCurrentLight = Math.max(0, Math.min(mMaxLight, mCurrentLight));
                // 更新系统亮度
                SysUtils.setBrightness(this, mCurrentLight);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("shared_preferences_light", mCurrentLight);
                editor.commit();
                updateVerticalText(mCurrentLight, mMaxLight);
            }
        } catch (SecurityException e) {
            Log.i(TAG, ">> 视频播放页面 SecurityException: >>" + e.toString());
        }
    }

    @Override
    public void onScrollVerticalRight(float y1, float y2) {
        int height = getResources().getDisplayMetrics().heightPixels;
        int offset = (int) (mMaxVolume * y1) / height;
        if (Math.abs(offset) > 0) {
            mCurrentVolume += offset;//得到变化后的声音
            mCurrentVolume = Math.max(0, Math.min(mMaxVolume, mCurrentVolume));
            // 更新系统声音
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume / 10, 0);
            updateVerticalText(mCurrentVolume, mMaxVolume);
        }
    }

    //用于组合图片及文字：亮度或者声音

    private void setComposeDrawableAndText(TextView textView, int drawableId, Context context) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        //这四个参数表示把drawable绘制在矩形区域
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        //设置图片在文字的上方
        //The Drawables must already have had drawable.setBounds called.
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    //更新垂直方向上滑动时的百分比
    private void updateVerticalText(int current, int total) {
        //转化为百分比
        NumberFormat formater = NumberFormat.getPercentInstance();
        formater.setMaximumFractionDigits(0);//设置整数部分允许最大小数位 66.5%->66%
        String percent = formater.format((double) (current) / (double) total);
        mDragVerticalView.setText(percent);
    }

    //更新水平方向seek的进度, duration表示变化后的duration
    private void updateHorizontalText(long duration) {
        String text = stringForTime((int) duration) + "/" + stringForTime(mVideoView.getDuration());
        mDragHorizontalView.setText(text);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBatteryReceiver != null) {
            //解除广播
            unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }

        //释放audiofocus
        mAudioManager.abandonAudioFocus(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //居于后台不可见时销毁
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    //从直播模块跳转过来
    public static void launch(Activity activity, String url, String title) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }
}
