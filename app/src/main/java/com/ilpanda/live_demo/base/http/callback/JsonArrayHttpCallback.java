package com.ilpanda.live_demo.base.http.callback;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ilpanda.live_demo.base.http.exception.ResponseEmptyException;
import com.ilpanda.live_demo.base.http.exception.UnExpectedResponseCodeException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JsonArrayHttpCallback<T> extends HttpCallback<T> {

    private static final String TAG = "JsonArrayHttpCallback";
    private Class<T> mClz;

    public JsonArrayHttpCallback(Class<T> clz) {
        this.mClz = clz;
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

        List<T> result = null;
        try {
            Type type = TypeToken.getParameterized(List.class, (Type) mClz).getType();
            result = new Gson().fromJson(body.string(), type);
        } catch (Exception e) {
            sendMessageOnUI(call, e);
            return;
        }
        sendMessageOnUI(result);
    }

    @Override
    public T parseResult(Response response) throws Exception {
        return null;
    }


    public void onHttpSuccess(List<T> result) {

    }

    private void sendMessageOnUI(final List<T> result) {
        HANDLER.post(() -> onHttpSuccess(result));
    }
}
