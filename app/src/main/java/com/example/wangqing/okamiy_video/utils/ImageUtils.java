package com.example.wangqing.okamiy_video.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wangqing.okamiy_video.R;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 * 图片工具类
 */

public class ImageUtils {
    private static final float VER_POSTER_RATIO = 0.73f;
    private static final float HOR_POSTER_RATIO = 1.5f;

    /**
     * 加载图片
     *
     * @param view
     * @param url
     * @param width
     * @param height
     */
    public static void disPlayImage(ImageView view, String url, int width, int height) {
        if (view != null && url != null && width > 0 && height > 0) {
            if (width > height) {//横图
                Glide.with(view.getContext())
                        .load(url) //加载图片url
                        .diskCacheStrategy(DiskCacheStrategy.ALL)// 设置缓存
                        .error(R.drawable.ic_loading_hor)//出错时使用默认图
                        .fitCenter()//设置图片居中, centerCrop会截断大图,不会自适应, fitCenter居中自适应
                        .override(height, width)//重写宽高,注意顺序
                        .into(view);//加载imageview上
            } else {//竖图
                Glide.with(view.getContext())
                        .load(url) //加载图片url
                        .diskCacheStrategy(DiskCacheStrategy.ALL)// 设置缓存
                        .error(R.drawable.ic_loading_hor)//出错时使用默认图
                        .centerCrop()//设置图片居中
                        .override(width, height)//重写宽高
                        .into(view);//加载imageview上
            }
        }
    }

    /**
     * 让图片获得最佳比例
     * 动态处理水平方向上的海报的大小
     *
     * @param context
     * @param columns 列数
     * @return
     */
    public static Point getVerPostSize(Context context, int columns) {
        //算出一个Item的宽度
        int width = getScreenWidthPixel(context) / columns;
        width = width - (int) context.getResources().getDimension(R.dimen.dimen_8dp);
        int height = Math.round((float) width / VER_POSTER_RATIO);
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }

    public static Point getHorPostSize(Context context, int columns) {
        int width = getScreenWidthPixel(context) / columns;
        width = width - (int) context.getResources().getDimension(R.dimen.dimen_8dp);
        int height = Math.round((float) width / HOR_POSTER_RATIO);
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }

    /**
     * 计算屏幕的宽度
     *
     * @param conetxt
     * @return
     */
    public static int getScreenWidthPixel(Context conetxt) {
        WindowManager wm = (WindowManager) conetxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        return width;
    }
}
