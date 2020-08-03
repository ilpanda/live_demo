package com.ilpanda.live_demo.base.http.callback;

import okhttp3.Response;

public class StringHttpCallback extends HttpCallback<String> {
    @Override
    public String parseResult(Response response) throws Exception {
        return response.body().string();
    }
}
