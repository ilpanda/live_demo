package com.ilpanda.live_demo.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.os.StatFs;

import java.io.File;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

public class Utils {

    private static long MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024;

    private static long MAX_DISK_CACHE_SIZE = 5 * 1024 * 1024;

    private Utils() {

    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    @TargetApi(JELLY_BEAN_MR2)
    public static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            //noinspection deprecation
            long blockCount =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong();
            //noinspection deprecation
            long blockSize =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong();
            long available = blockCount * blockSize;
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {

        }
        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }


    public static long getAvailableDiskSize(File file) {
        try {
            StatFs statFs = new StatFs(file.getAbsolutePath());
            //noinspection deprecation
            long blockCount =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong();
            //noinspection deprecation
            long blockSize =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong();
            long available = blockCount * blockSize;
            return available;
        } catch (IllegalArgumentException ignored) {


        }
        return 0;
    }

    public static int calculateMaxMemorySize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        return largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
    }


    public static int calculateMemoryCacheSize(Context context) {
        int maxMemorySize = calculateMaxMemorySize(context);
        // Target ~15% of the available heap.
        return (int) (1024L * 1024L * maxMemorySize / 7);
    }


    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }


    public static boolean isMainProcess(Context context, int pid, String packageName) {
        return packageName.equals(getProcessName(context, pid));
    }


    private static String getProcessName(Context context, int pid) {
        ActivityManager am = getService(context, Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        if (processInfoList == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

}
