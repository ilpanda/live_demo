package com.ilpanda.live_demo.base.http.exception;

import android.os.Environment;

import com.ilpanda.live_demo.utils.CrashUtil;
import com.tencent.mars.xlog.Log;


public class SimpleCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private String mCrashPath;

    public SimpleCrashHandler(String crashPath) {
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mCrashPath = crashPath;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            String crash = CrashUtil.getThreadStack(ex);
            final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
            final String logPath = SDCARD + "/" + mCrashPath;
            CrashUtil.logFileToSD(logPath, "crash.txt", crash);
            Log.e(TAG, "crash happen \n" + crash);
        } catch (Exception e) {
            Log.e(TAG, "crash write error :  \n" + CrashUtil.getThreadStack(e));
        }

        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }

    }
}
