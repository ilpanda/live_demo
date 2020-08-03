package com.ilpanda.live_demo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {

    public static void showLongToast(Context context, CharSequence text) {
        showToast(context, text, true);
    }

    public static void showShortToast(Context context, CharSequence text) {
        showToast(context, text, false);
    }

    private static void showToast(Context context, CharSequence text, boolean isLong) {
        if (context == null || TextUtils.isEmpty(text)) {
            return;
        }

        context = context.getApplicationContext();
        int duration = isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }
}
