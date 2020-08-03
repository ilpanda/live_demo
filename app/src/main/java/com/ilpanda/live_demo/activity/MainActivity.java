package com.ilpanda.live_demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ilpanda.live_demo.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
    }


    private void initView() {
        // 跳转直播到直播广场页面,点击直播间后,跳转到 LiveSlideDetailActivity 直播间的数据类型为:
        // https://www.hi-cat.cn/api/live/slide
        findViewById(R.id.tv_style_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveListActivity.start(MainActivity.this, 1);
            }
        });

        // 跳转直播到直播广场页面,点击直播间后,跳转到 LiveDetailActivity 直播间的数据类型为:
        // https://www.hi-cat.cn/api/live
        findViewById(R.id.tv_style_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveListActivity.start(MainActivity.this, 2);
            }
        });

    }


}
