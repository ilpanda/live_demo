package com.ilpanda.live_demo.wiget.gift;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


public class GiftDanmuCount {

    private static final String TAG = GiftDanmuCount.class.getSimpleName();

    private List<GiftAnimatorView.GiftItem> mMessages = new LinkedList<>();

    public Callback mCallback;

    private int mShowCount;

    private static final int MAX_GIFT_COUNT = 3;

    private MyHandler mHandler = new MyHandler(new WeakReference<>(this));

    private static class MyHandler extends Handler {


        private WeakReference<GiftDanmuCount> weakReference;

        public MyHandler(WeakReference<GiftDanmuCount> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            GiftDanmuCount giftDanmuCount = weakReference.get();
            if (giftDanmuCount == null) {
                return;
            }
            if (giftDanmuCount.mCallback == null) {
                return;
            }
            if (giftDanmuCount.mShowCount == MAX_GIFT_COUNT) {
                return;
            }
            if (giftDanmuCount.mMessages.size() > 0) {
                giftDanmuCount.mShowCount++;
                giftDanmuCount.mCallback.handleMessage(giftDanmuCount.mMessages.remove(0));
            }
        }
    }

    ;

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void showMessage(GiftAnimatorView.GiftItem item) {
        mMessages.add(item);
        if (mShowCount == MAX_GIFT_COUNT) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    public void hideMessage() {
        this.mShowCount--;
        if (mMessages.isEmpty()) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    public void clear() {
        mMessages.clear();
        mHandler.removeCallbacksAndMessages(null);
        this.mShowCount = 0;
    }

    public interface Callback {
        void handleMessage(GiftAnimatorView.GiftItem item);
    }

}


