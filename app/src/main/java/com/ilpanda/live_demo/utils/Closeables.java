package com.ilpanda.live_demo.utils;


import java.io.Closeable;


public class Closeables {

    private static final String TAG = "Closeables";

    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
