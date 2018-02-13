package com.example.wangqing.okamiy_video.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;

/**
 * Created by Okamiy on 2018/2/1.
 * Email: 542839122@qq.com
 */

public class SysUtils {
    //获取亮度
    public static int getBrightness(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_brightness", -1);
    }

    //设置亮度
    public static void setBrightness(Context context, int param) {
        Settings.System.putInt(context.getContentResolver(), "screen_brightness", param);
    }

    //获取亮度的sharedPreferences文件
    public static int getDefaultBrightness(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("shared_preferences_light", -1);
    }

}
