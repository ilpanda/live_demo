package com.ilpanda.live_demo.base.http.callback;

import android.os.Handler;
import android.os.Looper;

import com.ilpanda.live_demo.base.http.exception.ResponseEmptyException;
import com.ilpanda.live_demo.base.http.exception.UnExpectedResponseCodeException;
import com.ilpanda.live_demo.utils.CrashUtil;
import com.tencent.mars.xlog.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class HttpCallback<T> implements Callback {

    private static final String TAG = "HttpCallback";

    protected static Handler HANDLER = new Handler(Looper.getMainLooper());

    @Override
    public void onFailure(Call call, IOException e) {
        sendMessageOnUI(call, e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        if (!response.isSuccessful()) {
            response.close();
            sendMessageOnUI(call, new UnExpectedResponseCodeException("UnExpectedResponseCodeException Code :" + response.code()));
            return;
        }

        ResponseBody body = response.body();

        if (body == null) {
            response.close();
            sendMessageOnUI(call, new ResponseEmptyException("response body is null" + call.request().url()));
            return;
        }
        T result = null;
        try {
            result = parseResult(response);
        } catch (Exception e) {
            sendMessageOnUI(call, e);
            return;
        }
        sendMessageOnUI(result);

    }

    public void onHttpFailure(Call call, Exception e) {

    }

    public void onHttpResponse(T result) {

    }

    public abstract T parseResult(Response response) throws Exception;


    void sendMessageOnUI(final T result) {
        HANDLER.post(() -> onHttpResponse(result));
    }

    void sendMessageOnUI(final Call call, final Exception e) {
        Log.e(TAG, String.format(Locale.getDefault(),
                "error  url :  %s %n the error:%s  \n  the exception %s ",
                call.request().url(), e, CrashUtil.getThreadStack(e)));
        HANDLER.post(() -> onHttpFailure(call, e));
    }
}
