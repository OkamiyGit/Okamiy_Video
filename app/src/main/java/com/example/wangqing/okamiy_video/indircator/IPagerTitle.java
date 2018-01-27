package com.example.wangqing.okamiy_video.indircator;

/**
 * Created by Okamiy on 2018/1/27.
 * Email: 542839122@qq.com
 * ViewPager指示器名称一级接口
 */

public interface IPagerTitle {
    /**
     * 选中状态
     *
     * @param index      第几个
     * @param totalCount 总共多少个
     */
    void onSelected(int index, int totalCount);

    /**
     * 未选中
     *
     * @param index      第几个
     * @param totalCount 总共多少个
     */
    void onDisSelected(int index, int totalCount);

    /**
     * 离开
     *
     * @param index         当前下标
     * @param totalCount    总共多个个
     * @param leavePercent  离开时的比例：取值 0.0f - 1.0f (1.0f表示完全离开)
     * @param isLeftToRight 是否从左向右离开
     */
    void onLeave(int index, int totalCount, float leavePercent, boolean isLeftToRight);

    /**
     * 进入
     *
     * @param index
     * @param totalCount
     * @param enterPercent  进入时的比例：取值 0.0f - 1.0f (1.0f表示完全进入)
     * @param isLeftToRight 是否从左向右进入
     */
    void onEnter(int index, int totalCount, float enterPercent, boolean isLeftToRight);
}
