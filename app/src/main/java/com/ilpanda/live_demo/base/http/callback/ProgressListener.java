package com.ilpanda.live_demo.base.http.callback;


public abstract class ProgressListener implements ProgressCallback {

    boolean started;
    long lastRefreshTime = 0L;
    long lastBytesWritten = 0L;
    private static final int MIN_REFRESH_TIME = 100;//最小回调时间100ms，避免频繁回调

    @Override
    public void onProgressChanged(long bytesRead, long contentLength, float percent) {
        if (!started) {
            onProgressStart(contentLength);
            started = true;
        }
        if (bytesRead == -1 && contentLength == -1 && percent == -1) {
            onProgressChanged(-1, -1, -1, -1);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRefreshTime >= MIN_REFRESH_TIME || bytesRead == contentLength || percent >= 1F) {
            long intervalTime = (currentTime - lastRefreshTime);
            if (intervalTime == 0) {
                intervalTime += 1;
            }
            long updateBytes = bytesRead - lastBytesWritten;
            final long networkSpeed = updateBytes / intervalTime;
            onProgressChanged(bytesRead, contentLength, percent, networkSpeed);
            lastRefreshTime = System.currentTimeMillis();
            lastBytesWritten = bytesRead;
        }
        if (bytesRead == contentLength || percent >= 1F) {
            onProgressFinish();
        }
    }

    public abstract void onProgressChanged(long bytesRead, long contentLength, float percent, float speed);


    public void onProgressStart(long contentLength) {

    }

    public void onProgressFinish() {

    }
}
