package com.ilpanda.live_demo.bean;

import java.util.List;

public class LiveListBean extends BasePageBean {

    public List<LiveItemBean> content;

    public List<LiveItemBean> getContent() {
        return content;
    }

    public void setContent(List<LiveItemBean> content) {
        this.content = content;
    }
}
