package com.example.wangqing.okamiy_video.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Okamiy on 2018/2/1.
 * Email: 542839122@qq.com
 * 时间转换类
 */

public class DateUtils {
    /**
     * 获取当前时间
     *
     * @return 时：分
     */
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = format.format(curDate);
        return str;
    }
}
