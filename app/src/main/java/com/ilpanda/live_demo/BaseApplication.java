package com.ilpanda.live_demo;

import android.app.Application;
import android.os.Process;

import com.ilpanda.live_demo.utils.SimpleCrashHandler;
import com.ilpanda.live_demo.utils.Utils;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        if (Utils.isMainProcess(this, Process.myPid(), getPackageName())) {

            initLog();

            Thread.setDefaultUncaughtExceptionHandler(new SimpleCrashHandler(getExternalFilesDir(null) + "/xlog"));

        }

    }

    private void initLog() {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        final String SDCARD = getExternalFilesDir(null).getAbsolutePath();
        final String logPath = SDCARD + "/mars/log";

        // this is necessary, or may crash for SIGBUS
        final String cachePath = this.getFilesDir() + "/xlog";

        //init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeSync, cachePath, logPath, "mars", 0, "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "mars", 0, "");
            Xlog.setConsoleLogOpen(false);
        }

        Log.setLogImp(new Xlog());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.appenderClose();
    }

}
