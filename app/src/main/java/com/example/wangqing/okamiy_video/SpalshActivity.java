package com.example.wangqing.okamiy_video;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.wangqing.okamiy_video.home.HomeActivity;

import java.lang.ref.WeakReference;

public class SpalshActivity extends Activity {
    private SharedPreferences mPreferences;
    private static final int GO_HOME = 1;
    private static final int GO_GUIDE = 2;
    private static final int ENTER_DURATION = 2000;
    /**
     * 用handler发送一个2秒延时
     * 直接创建Handler可能会引发内存泄漏，故采用这种方式
     */
    private final SafetyHandler mHandler = new SafetyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取配置文件，进行判断是不是首次登录
        mPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //初始化
        init();

    }

    private void init() {
        //先判断是不是首次进入，延迟2秒首次进入加载引导页，否则直接跳转主Activity
        boolean isFirstIn = mPreferences.getBoolean("mIsFirstIn", true);
        if (isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, ENTER_DURATION);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_HOME, ENTER_DURATION);
        }
    }

    /**
     * 非静态内部类或匿名内部类都会隐式地持有其对外部类持有强引用
     */
    private static class SafetyHandler extends Handler {
        private final WeakReference<SpalshActivity> mActivity;

        public SafetyHandler(SpalshActivity activity) {
            mActivity = new WeakReference<SpalshActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SpalshActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case GO_HOME:
                        Intent homeIntent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(homeIntent);
                        activity.finish();
                        break;
                    case GO_GUIDE:
                        Intent guideIntent = new Intent(activity, GuideActivity.class);
                        activity.startActivity(guideIntent);
                        activity.finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
