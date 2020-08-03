/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ilpanda.live_demo.wiget.heart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.ilpanda.live_demo.R;

import java.util.Random;


/**
 * Module:   TCHeartLayout
 * <p>
 * Function: 飘心动画界面布局类
 * <p>
 * 通过动画控制每个心形界面的显示
 * TCPathAnimator 控制显示路径
 * TCHeartView 单个心形界面
 */
public class TCHeartLayout extends RelativeLayout {

    private TCAbstractPathAnimator mAnimator;
    private int defStyleAttr = 0;

    private int textHight;
    private int dHeight;
    private int dWidth;
    private int initX;
    private int pointx;
    private Context context;

    public TCHeartLayout(Context context) {
        super(context);
        this.context = context;
    }

    public TCHeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        findViewById(context);
        initHeartDrawable();
        init(attrs, defStyleAttr);
    }

    private void findViewById(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_periscope, this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_portrait_like);
        dHeight = bitmap.getWidth();
        dWidth = bitmap.getHeight();
        textHight = sp2px(getContext(), 20) + dHeight / 2;

        pointx = dWidth;//随机上浮方向的x坐标

        bitmap.recycle();
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);

        //todo:获取确切值
        initX = 30;
        if (pointx <= initX && pointx >= 0) {
            pointx -= 10;
        } else if (pointx >= -initX && pointx <= 0) {
            pointx += 10;
        } else pointx = initX;

        mAnimator = new TCPathAnimator(
                TCAbstractPathAnimator.Config.fromTypeArray(a, initX, textHight, pointx, dWidth, dHeight));

        a.recycle();
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void resourceLoad() {
        mHearts = new Bitmap[drawableIds.length];
        mHeartsDrawable = new BitmapDrawable[drawableIds.length];
        for (int i = 0; i < drawableIds.length; i++) {
            mHearts[i] = BitmapFactory.decodeResource(getResources(), drawableIds[i]);
            mHeartsDrawable[i] = new BitmapDrawable(getResources(), mHearts[i]);
        }
    }

    private static int[] drawableIds = new int[]{R.drawable.heart0, R.drawable.heart1, R.drawable.heart2, R.drawable.heart3, R.drawable.heart4, R.drawable.heart5, R.drawable.heart6, R.drawable.heart7, R.drawable.heart8,};
    private Random mRandom = new Random();
    private static Drawable[] sDrawables;
    private Bitmap[] mHearts;
    private BitmapDrawable[] mHeartsDrawable;

    private void initHeartDrawable() {
        int size = drawableIds.length;
        sDrawables = new Drawable[size];
        for (int i = 0; i < size; i++) {
            sDrawables[i] = getResources().getDrawable(drawableIds[i]);
        }
        resourceLoad();
    }


    public void addFavor() {
        addFavor(0);
    }

    public void addFavor(int padding) {

        TCHeartView heartView = new TCHeartView(getContext());
        if (padding != 0) {
            heartView.setPadding(padding, padding, padding, padding);
        }
        heartView.setDrawable(mHeartsDrawable[mRandom.nextInt(8)]);
//        heartView.setImageDrawable(sDrawables[random.nextInt(8)]);
//        init(attrs, defStyleAttr);
        mAnimator.start(heartView, this);
    }

}
