package com.ilpanda.live_demo.live;

import android.view.ViewGroup;

import com.ilpanda.live_demo.bean.LiveItemBean;


public interface LivePlayListener extends LiveActivityLifeCallback {

    /**
     * 用户滑动显示该页面
     *
     * @param data 房间信息
     */
    void onAdd(LiveItemBean data);

    /**
     * 用户滑动移除该页面
     */
    void onRemove();

    /**
     * @return 页面的根布局 View
     */
    ViewGroup getRootView();

}
