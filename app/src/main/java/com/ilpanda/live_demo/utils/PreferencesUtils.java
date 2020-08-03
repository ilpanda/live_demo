package com.ilpanda.live_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    public PreferencesUtils() {
    }

    public static void setPreferences(Context context, String preference, String key, boolean value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static void setPreferences(Context context, String preference, String key, long value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static void setPreferences(Context context, String preference, String key, String value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static void setPreferences(Context context, String preference, String key, int value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static String getPreference(Context context, String preference, String key, String defaultValue) {
        if (context == null) {
            return null;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getString(key, defaultValue);
        }
    }

    public static boolean getPreference(Context context, String preference, String key, boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getBoolean(key, defaultValue);
        }
    }

    public static long getPreference(Context context, String preference, String key, long defaultValue) {
        if (context == null) {
            return -1L;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getLong(key, defaultValue);
        }
    }

    public static int getPreference(Context context, String preference, String key, int defaultValue) {
        if (context == null) {
            return -1;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getInt(key, defaultValue);
        }
    }

    public static void clearPreference(Context context, String preference) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


}
