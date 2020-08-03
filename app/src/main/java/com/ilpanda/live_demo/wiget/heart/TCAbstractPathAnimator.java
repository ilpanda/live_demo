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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ilpanda.live_demo.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Module:   TCAbstractPathAnimator
 * <p>
 * Function: 心心飘扬的动画基类
 */
public abstract class TCAbstractPathAnimator {
    private final Random mRandom;
    protected final Config mConfig;


    private static final String DEFAULT_ROTATION = "right";

    public TCAbstractPathAnimator(Config config) {
        mConfig = config;
        mRandom = new Random();
    }

    public float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

    public Path createPath(AtomicInteger counter, View view, int factor) {
        Random r = mRandom;
        int x = DEFAULT_ROTATION.endsWith(mConfig.rotation) ? r.nextInt(mConfig.xRand) : -r.nextInt(mConfig.xRand);
        int x2 = DEFAULT_ROTATION.endsWith(mConfig.rotation) ? r.nextInt(mConfig.xRand) : -r.nextInt(mConfig.xRand);
        int y = view.getHeight() - mConfig.initY;
        int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
        factor = y2 / mConfig.bezierFactor;
        x = mConfig.xPointFactor + x;
        x2 = mConfig.xPointFactor + x2;
        int y3 = y - y2;
        y2 = y - y2 / 2;
        Path p = new Path();
        p.moveTo(mConfig.initX, y);
        p.cubicTo(mConfig.initX, y - factor, x, y2 + factor, x, y2);
        p.moveTo(x, y2);
        p.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);
        return p;
    }

    public abstract void start(View child, ViewGroup parent);

    public static class Config {
        public int initX;
        public int initY;
        public int xRand;
        public int animLengthRand;
        public int bezierFactor;
        public int xPointFactor;
        public int animLength;
        public int heartWidth;
        public int heartHeight;
        public int animDuration;
        public String rotation;

        static public Config fromTypeArray(TypedArray typedArray, float x, float y, int pointx, int heartWidth, int heartHeight) {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout_initX,
                    x);
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout_initY,
                    y);
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));//动画长度
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                    res.getInteger(R.integer.heart_anim_bezier_factor));
            config.xPointFactor = pointx;
            String rotation = typedArray.getString(R.styleable.HeartLayout_rotation);
            config.rotation = TextUtils.isEmpty(rotation) ? "right" : rotation;
//            config.heartWidth = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_width,
//                    res.getDimensionPixelOffset(R.dimen.heart_size_width));//动画图片宽度
//            config.heartHeight = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_height,
//                    res.getDimensionPixelOffset(R.dimen.heart_size_height));//动画图片高度
            config.heartWidth = heartWidth;
            config.heartHeight = heartHeight;
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                    res.getInteger(R.integer.anim_duration));//持续期
            return config;
        }


    }


}

