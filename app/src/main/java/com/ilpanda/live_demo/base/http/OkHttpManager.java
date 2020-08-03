package com.ilpanda.live_demo.base.http;

import com.ilpanda.live_demo.base.http.callback.SimpleCallback;
import com.ilpanda.live_demo.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ilpanda on 2018/1/16.
 */

public class OkHttpManager {

    private static final OkHttpManager mOkHttpManager = new OkHttpManager();

    private OkHttpClient mOkHttpClient;

    public static OkHttpManager getInstance() {
        return mOkHttpManager;
    }

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void get(String url) {
        get(url, null);
    }

    public void get(String url, Object tag) {
        get(url, tag, null);
    }

    public void get(String url, Callback callback) {
        get(url, null, callback);
    }

    public void get(String url, Map<String, String> params, Callback callback) {
        get(url, params, null, callback);
    }

    public void get(String url, Object tag, Callback callback) {
        get(url, null, tag, callback);
    }

    public void get(String url, Map<String, String> params, Object tag, Callback callback) {
        Preconditions.checkNotNull(url);
        Request.Builder reqBuild = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        Map<String, String> commonParams = getCommonParams();
        if (params == null) {
            params = commonParams;
        } else {
            params.putAll(commonParams);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        reqBuild.url(urlBuilder.build()).tag(tag);
        if (callback == null) callback = new SimpleCallback();
        mOkHttpClient.newCall(reqBuild.build()).enqueue(callback);
    }


    public void post(String url) {
        post(url, null);
    }

    public void post(String url, Object tag) {
        post(url, tag, null);
    }

    public void post(String url, Callback callback) {
        post(url, null, callback);
    }

    public void post(String url, Object tag, Callback callback) {
        post(url, null, tag, callback);
    }

    public void post(String url, Map<String, String> params, Callback callback) {
        post(url, params, null, callback);
    }

    public void post(String url, Map<String, String> params, Object tag, Callback callback) {

        FormBody.Builder builder = new FormBody.Builder();

        Map<String, String> commonParams = getCommonParams();
        if (params == null) {
            params = commonParams;
        } else {
            params.putAll(commonParams);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .tag(tag)
                .build();
        if (callback == null) callback = new SimpleCallback();
        mOkHttpClient.newCall(request).enqueue(callback);
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public void postJson(String url, String json, Callback callback) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .method("Post", body)
                .build();

        if (callback == null) callback = new SimpleCallback();
        mOkHttpClient.newCall(request).enqueue(callback);
    }


    public void cancel(Object url) {
        Preconditions.checkNotNull(url);
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(url))
                call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call.request().tag().equals(url))
                call.cancel();
        }
    }

    private Map<String, String> getCommonParams() {
        HashMap<String, String> params = new HashMap<>();

        return params;
    }


}
