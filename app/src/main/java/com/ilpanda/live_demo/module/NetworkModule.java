package com.ilpanda.live_demo.module;

import com.ilpanda.live_demo.base.http.OkHttpManager;
import com.ilpanda.live_demo.base.http.callback.JsonHttpCallback;
import com.ilpanda.live_demo.bean.LiveDataBean;
import com.ilpanda.live_demo.bean.LiveInfoBean;
import com.ilpanda.live_demo.module.event.Event;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import okhttp3.Call;

public class NetworkModule {


    public static void getLiveList(int pageNo, int count, boolean isNext, String tag) {

        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(pageNo));
        params.put("count", String.valueOf(count));

        OkHttpManager.getInstance().get(Urls.LIVE_LIST_URL, params, new JsonHttpCallback<LiveDataBean>(LiveDataBean.class) {
            @Override
            public void onHttpResponse(LiveDataBean result) {
                EventBus.getDefault().post(new Event.LiveListEvent(result, pageNo, isNext, tag));
            }

            @Override
            public void onHttpFailure(Call call, Exception e) {
                EventBus.getDefault().post(new Event.LiveListEvent(call, e, tag));
            }
        });
    }

    public static void getLiveList(int pageNo, int count, String tag) {
        getLiveList(pageNo, count, true, tag);
    }

    public static void getLiveList(int pageNo, boolean isNext, String tag) {
        getLiveList(pageNo, 10, isNext, tag);
    }


    public static void getLiveSlide(String liveId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("liveId", String.valueOf(liveId));
        OkHttpManager.getInstance().get(Urls.LIVE_SLIDE_URL, params, new JsonHttpCallback<LiveInfoBean>(LiveInfoBean.class) {
            @Override
            public void onHttpResponse(LiveInfoBean result) {
                EventBus.getDefault().post(new Event.SlideEvent(result));
            }

            @Override
            public void onHttpFailure(Call call, Exception e) {
                EventBus.getDefault().post(new Event.SlideEvent(call, e));
            }
        });

    }


}