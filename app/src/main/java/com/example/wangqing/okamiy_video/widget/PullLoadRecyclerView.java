package com.example.wangqing.okamiy_video.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangqing.okamiy_video.R;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 * 自定义RecyclerView
 */

public class PullLoadRecyclerView extends LinearLayout {

    private RecyclerView mRecyclerview;
    private View mFootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mLoadImg;
    private TextView mLoadText;
    //可以执行动画的Drawable
    private AnimationDrawable mAnimationDrawable;
    //是否是刷新
    private boolean mIsRefresh = false;
    //是否是加载更多
    private boolean mIsLoadMore = false;
    private Context mContext;
    private OnPullLoadMoreListener loadMoreListener;

    public PullLoadRecyclerView(Context context) {
        super(context);
        initViews(context);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    /**
     * 初始化view
     */
    private void initViews(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout, null);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mRecyclerview = view.findViewById(R.id.recyclerview);
        mFootView = view.findViewById(R.id.footer_view);
        mLoadImg = mFootView.findViewById(R.id.iv_load_img);
        mLoadText = mFootView.findViewById(R.id.tv_load_text);
        //小图标的帧动画
        mLoadImg.setBackgroundResource(R.drawable.imooc_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadImg.getBackground();
        //隐藏加载更多布局
        mFootView.setVisibility(GONE);

        //刷新的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark);
        //设置刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh());
        //设置加载更多监听
        mRecyclerview.addOnScrollListener(new RecyclerViewOnScroll());
        //处理RecyclerView
        mRecyclerview.setHasFixedSize(true);//设置固定大小，不让它随便变化
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        //在刷新/加载更多时消耗touch事件
        mRecyclerview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mIsRefresh || mIsLoadMore;
            }
        });
        mRecyclerview.setVerticalScrollBarEnabled(false);//隐藏滚动条

        //把我们LayoutInflater创建的布局加载进来,包含SwipeRefreshLayout、RecyclerView、Footview
        this.addView(view);
    }

    /**
     * 外部可以设置RecyclerView的列数，
     * 设置RecyclerView的Layoutmanager
     *
     * @param spanCount
     */
    public void setGridLayout(int spanCount) {
        GridLayoutManager manager = new GridLayoutManager(mContext, spanCount);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(manager);
    }

    /**
     * 外部设置Adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mRecyclerview.setAdapter(adapter);
        }
    }

    /**
     * 外部设置监听回调：刷新、加载更多
     * 具体实现在外部操作
     */
    public interface OnPullLoadMoreListener {
        void reFresh();

        void loadMore();
    }

    //外部设置监听，就可以回调，刷新、加载更多
    public void setOnPullLoadMoreListener(OnPullLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    /**
     * 加载更多：我们自己设置的，所以需要控制显示隐藏
     */
    private void loadMoreData() {
        if (loadMoreListener != null) {
            //设置动画:设置上下移动的位移动画，设置先加速后减速线性的插值器，时长300，监听动画在开始的时候
            mFootView.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(300).setListener(new AnimatorListenerAdapter() {
                //动画开始的时候把启动动画，并显示我们的加载更多布局,开启我们的帧动画
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mFootView.setVisibility(VISIBLE);
                    mAnimationDrawable.start();
                }
            }).start();

            //刷新view之后执行我们加载更多逻辑
            invalidate();
            loadMoreListener.loadMore();
        }
    }

    /**
     * 刷新数据：借助Googel原生的刷新
     */
    private void refreshData() {
        if (loadMoreListener != null) {
            loadMoreListener.reFresh();
        }
    }

    /**
     * 设置刷新完成
     */
    public void setRefreshCompleted() {
        mIsRefresh = false;
        //取消刷新动作
        setRefreshing(false);
    }

    /**
     * 设置加载更多完成,并有一瞬间的定格
     */
    public void setLoadMoreCompleted() {
        mIsLoadMore = false;
        mIsRefresh = false;
        setRefreshing(false);

        //设置动画:设置上下移动的位移动画，设置先加速后减速线性的插值器，时长300
        mFootView.animate().translationY(mFootView.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300).start();
    }

    /**
     * 设置是否正在加载更多
     *
     * @param isRefreshing
     */
    private void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    /**
     * 刷新监听
     */
    class SwipeRefreshLayoutOnRefresh implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (!mIsRefresh) {
                mIsRefresh = true;
                refreshData();
            }
        }
    }

    /**
     * 加载更多监听
     */
    class RecyclerViewOnScroll extends RecyclerView.OnScrollListener {
        //RecyclerView滑动之后的监听
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstItem = 0;//第一个Item的下标
            int lastItem = 0;//最后一个Item的下标
            RecyclerView.LayoutManager manager = mRecyclerview.getLayoutManager();
            int totalCount = manager.getItemCount();//获取总数量
            //判断是哪种布局类型
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                //获取完全可见的第一个Item的下标
                firstItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                //获取完全可见的最后一个Item的下标
                lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();

                /**
                 * 当firstItem为0或者为RecyclerView.NO_POSITION（-1，表示不在我们当前可见的position范围之内）
                 * lastItem应该为上一次可见Item的位置
                 */
                if (firstItem == 0 || firstItem == RecyclerView.NO_POSITION) {
                    lastItem = gridLayoutManager.findLastVisibleItemPosition();
                }
            }

            /**
             * 什么时候触发上拉加载更多？
             *  1.加载更多是false
             *  2.totalCount - 1 === lastItem
             *  3.mSwipeRefreshLayout可以用
             *  4.不是处于下拉刷新状态
             *  5.偏移量dx > 0 或dy > 0
             *
             *  注意：加载更多时下拉刷新要设置为不可用，反之启用下拉刷新
             */
            //mIsLoadMore为=true并且最后一个可见Item下标等于我们数据的总和
            //并且偏移量大于0,时触发加载更多
            if (!mIsLoadMore
                    && totalCount - 1 == lastItem
                    && mSwipeRefreshLayout.isEnabled()
                    && !mIsRefresh
                    && (dy > 0 || dx > 0)) {
                mIsLoadMore = true;
                loadMoreData();
                //在加载更多时,禁止mSwipeRefreshLayout使用
                mSwipeRefreshLayout.setEnabled(false);
            } else {//不是加载更多时需要让下拉刷新可用
                mSwipeRefreshLayout.setEnabled(true);
            }
        }
    }
}
