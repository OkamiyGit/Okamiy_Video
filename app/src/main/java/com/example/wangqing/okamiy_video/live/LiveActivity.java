package com.example.wangqing.okamiy_video.live;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;

public class LiveActivity extends BaseActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    @Override
    protected void initView() {
        setSupportActionBar();
        setSupportArrowActionBar(true);//箭头
        setTitle(getResources().getString(R.string.live_title));

        mRecyclerView = bindViewId(R.id.ry_live);
        //1 表示一列
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        //垂直布局排列
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setLayoutManager(manager);
        //分割线
        mRecyclerView.addItemDecoration(new MyDecoration(this));
        LiveItemAdapter adapter = new LiveItemAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(0);//回到第一个位置
    }

    @Override
    protected void initData() {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    /**
     * 处理左边箭头
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://actionbar 左边箭头id
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
