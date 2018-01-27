package com.example.wangqing.okamiy_video.indircator;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器名字相关的2级接口
 */

public interface IViewPagerTitleView extends IPagerTitle {
    /**
     * 获取内容左侧位置
     *
     * @return
     */
    int getContentLeft();

    /**
     * 获取内容顶部位置
     *
     * @return
     */
    int getContentTop();

    /**
     * 获取内容右侧位置
     *
     * @return
     */
    int getContentRight();

    /**
     * 获取内容底部位置
     *
     * @return
     */
    int getContentBottom();
}
