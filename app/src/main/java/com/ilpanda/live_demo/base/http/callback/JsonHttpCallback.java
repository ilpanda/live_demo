package com.ilpanda.live_demo.base.http.callback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

public class JsonHttpCallback<T> extends HttpCallback<T> {


    private Class<T> mClz;

    public JsonHttpCallback(Class<T> clz) {
        this.mClz = clz;
    }

    @Override
    public T parseResult(Response response) throws Exception {
        Gson gson = new GsonBuilder().create();
        String body = response.body().string();
        return gson.fromJson(body, mClz);
    }
}
