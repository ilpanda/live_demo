package com.ilpanda.live_demo.wiget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ilpanda.live_demo.R;


public class LoadingView extends FrameLayout {

    private Context mContext;
    private ImageView mIvLoading;

    public LoadingView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_portrait_live_item, this, true);
        mIvLoading = view.findViewById(R.id.iv_loading);
    }


    public void show() {
        // 加载动画
        ImageView ivLoading = mIvLoading.findViewById(R.id.iv_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivLoading.getBackground();
        frameAnimation.start();

        setVisibility(VISIBLE);
    }

    public void hide() {
        // 加载动画
        ImageView ivLoading = mIvLoading.findViewById(R.id.iv_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivLoading.getBackground();
        frameAnimation.stop();
        setVisibility(GONE);
    }

}
