package com.ilpanda.live_demo.base.http.callback;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class ProgressHelper {

    public static RequestBody withProgress(RequestBody requestBody, ProgressListener progressListener) {
        if (requestBody == null) {
            throw new IllegalArgumentException("requestBody == null");
        }
        if (progressListener == null) {
            throw new IllegalArgumentException("progressListener == null");
        }
        return new ProgressRequestBody(requestBody, progressListener);
    }

    public static ResponseBody withProgress(ResponseBody responseBody, ProgressListener progressListener) {
        if (responseBody == null) {
            throw new IllegalArgumentException("responseBody == null");
        }
        if (progressListener == null) {
            throw new IllegalArgumentException("progressListener == null");
        }
        return new ProgressResponseBody(responseBody, progressListener);
    }
}
