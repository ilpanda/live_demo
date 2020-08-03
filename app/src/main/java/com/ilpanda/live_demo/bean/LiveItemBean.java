package com.ilpanda.live_demo.bean;

import java.util.Objects;

public class LiveItemBean {


    private String liveId; // 直播间 id

    private String title; // 直播间标题

    private String blurredImg; // 背景图

    private int onlineCount; // 人气值

    private int likeCount;  // 点赞数

    private LiveUserInfoBean user; //  用户信息

    private boolean portrait;       // 是否为竖屏

    private int pageNum;

    public int getPageNum() {
        return pageNum;
    }

    public String cover;

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public LiveUserInfoBean getUser() {
        return user;
    }

    public void setUser(LiveUserInfoBean user) {
        this.user = user;
    }

    public boolean isPortrait() {
        return portrait;
    }

    public void setPortrait(boolean portrait) {
        this.portrait = portrait;
    }


    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public String getBlurredImg() {
        return blurredImg;
    }

    public void setBlurredImg(String blurredImg) {
        this.blurredImg = blurredImg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveItemBean that = (LiveItemBean) o;
        return Objects.equals(liveId, that.liveId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liveId);
    }


}
