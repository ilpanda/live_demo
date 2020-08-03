package com.ilpanda.live_demo.wiget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.ilpanda.live_demo.utils.ScreenUtil;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * Module:   TCDanmuMgr
 * <p>
 * Function: 弹幕管理类
 * <p>
 * 弹幕管理类，代码源自
 * https://github.com/wangpeiyuan/DanmuDemo
 * https://github.com/Bilibili/DanmakuFlameMaster
 * <p>
 * 接口调用：
 * setDanmakuView 设置弹幕view IDanmakuView，可以在xml配置到
 * addDanmu 添加一条弹幕消息
 * <p>
 * UI定制
 * BackgroundCacheStuffer 用于定制弹幕消息背景
 */
public class DanmuMgr {

    private static final String TAG = "TCDanmuMgr";

    //弹幕显示的时间(如果是list的话，会 * i)，记得加上mDanmakuView.getCurrentTime()
    private static final long ADD_DANMU_TIME = 500;

    private static final int PINK_COLOR = 0xffff5a93;//粉红 楼主
    private static final int ORANGE_COLOR = 0xffff815a;//橙色 我
    private static final int BLACK_COLOR = 0xb2000000;//黑色 普通

    private int BITMAP_WIDTH = 32;//头像的大小
    private int BITMAP_HEIGHT = 32;
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private float DANMU_CONTENT_TEXT_SIZE = 10;//弹幕字体的大小
    private float DANMU_HEIGHT = 40;//弹幕高度
//    private int   EMOJI_SIZE      = 14;//emoji的大小

    //这两个用来控制两行弹幕之间的间距
    private int DANMU_PADDING = 8;
    private int DANMU_PADDING_INNER = 7;
    private int DANMU_RADIUS = 11;//圆角半径

    private Context mContext;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;

    private HandlerThread mDanmuThread;
    private Handler mDanmuHandler;


    public DanmuMgr(Context context) {
        this.mContext = context;
        setSize(context);
        initDanmuConfig();
        mDanmuThread = new HandlerThread("DamuThread");
        mDanmuThread.start();
        mDanmuHandler = new Handler(mDanmuThread.getLooper());
    }

    /**
     * 设置弹幕view
     *
     * @param danmakuView 弹幕view
     */
    public void setDanmakuView(IDanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        initDanmuView();
    }

    /**
     * 弹幕渲染暂停
     */
    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    /**
     * 弹幕隐藏
     */
    public void hide() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    /**
     * 弹幕显示
     */
    public void show() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    /**
     * 弹幕渲染恢复
     */
    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    /**
     * 弹幕资源释放
     */
    public void destroy() {
        if (mDanmuThread != null) {
            mDanmuThread.quit();
            mDanmuThread = null;
        }
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        mContext = null;
    }

    public void addDanmu(final String text, final boolean isLocal) {
        addDanmu(text, isLocal, false);
    }

    /**
     * 添加一条弹幕消息
     *
     * @param text    弹幕消息内容
     * @param isLocal 本地弹幕立即发送
     */
    public void addDanmu(final String text, final boolean isLocal, final boolean isLandScape) {
        if (TextUtils.isEmpty(text) || mDanmuHandler == null) {
            return;
        }

        mDanmuHandler.post(new Runnable() {
            @Override
            public void run() {
                String content = text;
                if (text.trim().length() > 32) {
                    content = text.trim().substring(0, 32) + "...";
                }
                addDanmuInternal(content, isLocal, isLandScape);
            }
        });
    }

    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize(Context context) {
        BITMAP_WIDTH = ScreenUtil.dipToPx(context, BITMAP_HEIGHT);
        BITMAP_HEIGHT =  ScreenUtil.dipToPx(context, BITMAP_HEIGHT);
        DANMU_PADDING =  ScreenUtil.dipToPx(context, DANMU_PADDING);
        DANMU_PADDING_INNER =  ScreenUtil.dipToPx(context, DANMU_PADDING_INNER);
        DANMU_RADIUS =  ScreenUtil.dipToPx(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = ScreenUtil.spToPx(context, DANMU_TEXT_SIZE);
        DANMU_HEIGHT = ScreenUtil.dipToPx(context, 40);
        DANMU_CONTENT_TEXT_SIZE = ScreenUtil.spToPx(context, DANMU_CONTENT_TEXT_SIZE);
    }

    /**
     * 初始化配置
     */
    private void initDanmuConfig() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示2行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.0f)//越大速度越慢
                .setDanmakuBold(true)
                .setScaleTextSize(1.2f)
                .setDanmakuMargin(40)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
    }

    private void initDanmuView() {
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });
            mDanmakuView.prepare(new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            }, mDanmakuContext);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    private void addDanmuInternal(String text, boolean isLocal, boolean isLandScape) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.priority = (byte) (isLocal ? 1 : 0);  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;

        int offset = (int) (Math.random() * 1500);
        if (isLocal) {
            offset = 300;
        }
        danmaku.setTime(mDanmakuView.getCurrentTime() + offset);
        danmaku.textSize = ScreenUtil.spToPx(mContext, isLandScape ? 16 : 14);
        danmaku.textColor = isLocal ? Color.parseColor("#9CF7FF") : Color.WHITE;
        danmaku.textShadowColor = 0;
        mDanmakuView.addDanmaku(danmaku);

    }

    private Bitmap getDefaultBitmap(int drawableId) {
        Bitmap mDefauleBitmap = null;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(((float) BITMAP_WIDTH) / width, ((float) BITMAP_HEIGHT) / height);
            mDefauleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return mDefauleBitmap;
    }

}
