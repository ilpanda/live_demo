package com.ilpanda.live_demo.bean;

public class BasePageBean {

    private int pages;     //总页数

    private int pageNum; // 当前第几页

    private int size;   // content的大小

    private long total;  // 一共有多少条记录


    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
