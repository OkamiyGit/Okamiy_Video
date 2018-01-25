package com.example.wangqing.okamiy_video;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends Activity {

    @BindView(R.id.tv)
    TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailListActivity.launchDetailListActivity(HomeActivity.this,2);
            }
        });
    }
}
