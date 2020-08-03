package com.ilpanda.live_demo.live;

import android.content.Intent;
import android.content.res.Configuration;

/**
 * 该类的方法对应 Activity 的方法
 */
public interface LiveActivityLifeCallback {

    /**
     * Activity 的 onStart
     */
    void onStart();


    /**
     * Activity 的 onResume
     */
    void onResume();

    /**
     * Activity 的 onPause
     */
    void onPause();

    /**
     * Activity 的 onStop
     */
    void onStop();

    /**
     * Activity 的 onDestroy
     */
    void onDestroy();

    /**
     * Activity 的 onRestart
     */
    void onRestart();


    /**
     * Activity 的 onConfigurationChanged
     */
    void onConfigurationChanged(Configuration newConfig);

    /**
     * Activity 的 onActivityResult
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Activity 的 onNewIntent
     */
    void onNewIntent(Intent intent);

    /**
     * Activity 的 onBackPressed
     */
    void onBackPressed();

}
