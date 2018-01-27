package com.example.wangqing.okamiy_video.indircator;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器：标题
 */

public class ViewPagerITitleView extends AppCompatTextView implements IViewPagerTitleView {

    private int mNormalColor;//默认颜色
    private int mSelectedColor;//选中颜色

    public ViewPagerITitleView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 初始化自定义view相关
     */
    public void init(Context context) {
        //内容居中显示
        setGravity(Gravity.CENTER);
        //设置一些padding
        int padding = dip2px(context, 20);
        setPadding(padding, 0, padding, 0);
        setSingleLine();//单行
        setEllipsize(TextUtils.TruncateAt.END);//文字超过区域打点隐藏（即表示...）
    }

    /**
     * 设置颜色值
     *
     * @param selectedColor
     */
    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    /**
     * 设置颜色值
     *
     * @param normalColor
     */
    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    /**
     * dip转换成像素
     *
     * @param context
     * @param dip
     * @return
     */
    private int dip2px(Context context, int dip) {
        //拿到屏幕的密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);//比如是0.4,0.4+0.5约上来就是1，向上取整
    }

    @Override
    public int getContentLeft() {
        //矩形
        Rect bound = new Rect();
        //通过Rect与画笔映射,取到对应的宽高
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);//获取当前textView内容的值
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();//字的测量
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        //通过Rect与画笔映射,取到对应的宽高
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();//字的测量
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        setTextColor(mSelectedColor);//选中时颜色变化
    }

    @Override
    public void onDisSelected(int index, int totalCount) {
        setTextColor(mNormalColor);//离开时颜色恢复正常
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean isLeftToRight) {

    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean isLeftToRight) {

    }
}
