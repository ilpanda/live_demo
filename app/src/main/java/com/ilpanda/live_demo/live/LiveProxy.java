package com.ilpanda.live_demo.live;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.ViewGroup;

import com.ilpanda.live_demo.bean.LiveItemBean;

public class LiveProxy implements LivePlayListener {

    private HorizontalScrollPortraitLiveManager mPortraitLiveManager;

    private LandscapeLiveManager mLandscapeLiveManager;

    private boolean mIsPortrait = true; // 是否为竖屏直播

    public LiveProxy(Activity activity) {
        mLandscapeLiveManager = new LandscapeLiveManager(activity);
        mPortraitLiveManager = new HorizontalScrollPortraitLiveManager(activity);
    }


    @Override
    public ViewGroup getRootView() {
        if (mIsPortrait) {
            return mPortraitLiveManager.getRootView();
        } else {
            return mLandscapeLiveManager.getRootView();
        }
    }

    @Override
    public void onAdd(LiveItemBean data) {
        if (mIsPortrait) {
            mPortraitLiveManager.onAdd(data);
        } else {
            mLandscapeLiveManager.onAdd(data);
        }
    }

    @Override
    public void onRemove() {
        if (mIsPortrait) {
            mPortraitLiveManager.onRemove();
        } else {
            mLandscapeLiveManager.onRemove();
        }
    }

    @Override
    public void onStart() {
        if (mIsPortrait) {
            mPortraitLiveManager.onStart();
        } else {
            mLandscapeLiveManager.onStart();
        }
    }

    @Override
    public void onResume() {
        if (mIsPortrait) {
            mPortraitLiveManager.onResume();
        } else {
            mLandscapeLiveManager.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mIsPortrait) {
            mPortraitLiveManager.onPause();
        } else {
            mLandscapeLiveManager.onPause();
        }
    }

    @Override
    public void onStop() {
        if (mIsPortrait) {
            mPortraitLiveManager.onStop();
        } else {
            mLandscapeLiveManager.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (mIsPortrait) {
            mPortraitLiveManager.onDestroy();
        } else {
            mLandscapeLiveManager.onDestroy();
        }
    }

    @Override
    public void onRestart() {
        if (mIsPortrait) {
            mPortraitLiveManager.onRestart();
        } else {
            mLandscapeLiveManager.onRestart();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mIsPortrait) {
            mPortraitLiveManager.onConfigurationChanged(newConfig);
        } else {
            mLandscapeLiveManager.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIsPortrait) {
            mPortraitLiveManager.onActivityResult(requestCode, resultCode, data);
        } else {
            mLandscapeLiveManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mIsPortrait) mPortraitLiveManager.onNewIntent(intent);
        else {
            mLandscapeLiveManager.onNewIntent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsPortrait) {
            mPortraitLiveManager.onBackPressed();
        } else {
            mLandscapeLiveManager.onBackPressed();
        }
    }

    public void setPortrait(boolean isPortrait) {
        mIsPortrait = isPortrait;
    }

}
