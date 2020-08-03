package com.ilpanda.live_demo.bean;

public class LiveSlideBean {

    private LiveItemBean current;   // 当前直播间信息

    private LiveItemBean pre;       // 上一个直播间信息

    private LiveItemBean next;      // 下一个直播间信息


    public LiveItemBean getCurrent() {
        return current;
    }

    public void setCurrent(LiveItemBean current) {
        this.current = current;
    }

    public LiveItemBean getPre() {
        return pre;
    }

    public void setPre(LiveItemBean pre) {
        this.pre = pre;
    }

    public LiveItemBean getNext() {
        return next;
    }

    public void setNext(LiveItemBean next) {
        this.next = next;
    }


}
