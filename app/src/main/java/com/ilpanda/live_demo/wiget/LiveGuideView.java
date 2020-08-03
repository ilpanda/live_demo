package com.ilpanda.live_demo.wiget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ilpanda.live_demo.R;


public class LiveGuideView extends FrameLayout implements View.OnClickListener {
    private Context mContext;

    public LiveGuideView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LiveGuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LiveGuideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_portrait_live_guide, this);
        setOnClickListener(this);
    }


    public void showGuide() {
            setVisibility(VISIBLE);
    }

    public void hideGuide() {
        setVisibility(GONE);
        ((ViewGroup) getParent()).removeView(this);
    }

    @Override
    public void onClick(View v) {
        hideGuide();
    }


}
