package com.example.wangqing.okamiy_video.indircator;

import java.util.List;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器的View接口
 */

public interface IPagerIndicatorView extends IPagerChangeListener {

    /**
     * 设置指示器View的数据
     *
     * @param list
     */
    void setPostionDataList(List<PositionData> list);
}
