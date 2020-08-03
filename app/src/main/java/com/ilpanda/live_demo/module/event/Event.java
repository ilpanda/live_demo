package com.ilpanda.live_demo.module.event;

import com.ilpanda.live_demo.bean.LiveDataBean;
import com.ilpanda.live_demo.bean.LiveInfoBean;

import okhttp3.Call;

public class Event {

    private static class BaseEvent {
        public Call call;
        public Exception exception;

        public BaseEvent() {

        }

        public BaseEvent(Call call, Exception exception) {
            this.call = call;
            this.exception = exception;
        }
    }

    public static class LiveListEvent extends BaseEvent {

        public LiveDataBean liveDataBean;

        public boolean isNext; // 向下翻页

        public int pageNo;   // 请求页数

        public String tag;

        public LiveListEvent(Call call, Exception e, String tag) {
            super(call, e);
            this.tag = tag;
        }

        public LiveListEvent(LiveDataBean liveDataBean, int pageNo, boolean isNext, String tag) {
            this.liveDataBean = liveDataBean;
            this.isNext = isNext;
            this.pageNo = pageNo;
            this.tag = tag;
        }
    }

    public static class SlideEvent extends BaseEvent {

        public LiveInfoBean result;

        public SlideEvent(Call call, Exception e) {
            super(call, e);
        }

        public SlideEvent(LiveInfoBean result) {
            this.result = result;
        }

    }

}
