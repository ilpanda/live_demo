package com.ilpanda.live_demo.base.http.callback;


public interface ProgressCallback {

    void onProgressChanged(long bytesRead, long contentLength, float percent);
}
