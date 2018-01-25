package com.example.wangqing.okamiy_video.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wangqing.okamiy_video.R;

/**
 * Created by Okamiy on 2018/1/26.
 * Email: 542839122@qq.com
 *  banner 适配器
 */

public class HomePicAdapter extends PagerAdapter {
    private Context mContext;

    //轮播相关的文字描述
    private int[] mDes = new int[] {
            R.string.a_name,
            R.string.b_name,
            R.string.c_name,
            R.string.d_name,
            R.string.e_name,
    };

    //轮播相关的图片
    private int[] mImg = new int[] {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
    };

    HomePicAdapter(Activity activity){
        this.mContext=activity;
    }

    //创建
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.home_pic_item,null);
        TextView textView = view.findViewById(R.id.tv_des);
        ImageView imageView = view.findViewById(R.id.iv_img);
        textView.setText(mDes[position]);
        imageView.setImageResource(mImg[position]);

        //添加到Viewpager容器
        container.addView(view);
        return view;
    }

    //销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 5;
    }

    //相等则复用
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
