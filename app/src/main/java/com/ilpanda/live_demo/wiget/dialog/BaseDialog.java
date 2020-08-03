package com.ilpanda.live_demo.wiget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BaseDialog extends Dialog {

    private Activity context;

    public BaseDialog(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = (Activity) context;

    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = (Activity) context;
    }


    @Override
    public void show() {
        if (context != null && !isInvalidContext(context)) {
            if (!this.isShowing()) {
                super.show();
            }
        }
    }

    @Override
    public void dismiss() {
        // if  you use dialog  in  handler.postDelay,but activity destroy,cause java.lang.IllegalArgumentException:
        // View not attached to window manager
        if (context != null && getWindow() != null) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (!isInvalidContext(activity)) {
                    super.dismiss();
                }
            } else {
                super.dismiss();
            }
        }
    }

    public static boolean isInvalidContext(Activity activity) {
        return ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) || activity.isFinishing());
    }
}
