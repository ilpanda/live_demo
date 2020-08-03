package com.ilpanda.live_demo.wiget.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.utils.ScreenUtil;


public class GiftAnimatorView extends FrameLayout implements GiftDanmuCount.Callback {

    private static final String TAG = "GiftAnimatorView";

    private Context mContext;

    private int mDuration = 6000;

    private PlayPosition mNextPosition;

    private TextView mTvTop;
    private TextView mTvCenter;
    private TextView mTvBottom;

    private ImageView mIvTop;
    private ImageView mIvCenter;
    private ImageView mIvBottom;

    private LinearLayout mLlTop;
    private LinearLayout mLlCenter;
    private LinearLayout mLlBottom;

    private ObjectAnimator mTopAnimator;
    private ObjectAnimator mCenterAnimator;
    private ObjectAnimator mBottomAnimator;

    private GiftDanmuCount mGiftDanmuCount;


    public GiftAnimatorView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftAnimatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public GiftAnimatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_gift, this);

        mTvTop = findViewById(R.id.tv_top);
        mTvCenter = findViewById(R.id.tv_center);
        mTvBottom = findViewById(R.id.tv_three);

        mIvTop = findViewById(R.id.iv_top);
        mIvCenter = findViewById(R.id.iv_center);
        mIvBottom = findViewById(R.id.iv_bottom);

        mLlTop = findViewById(R.id.ll_top);
        mLlCenter = findViewById(R.id.ll_center);
        mLlBottom = findViewById(R.id.ll_bottom);

        mGiftDanmuCount = new GiftDanmuCount();
        mGiftDanmuCount.setCallback(this);
    }


    public void showGiftDanmu(GiftAnimatorView.GiftItem item) {
        if (mIsEnd) {
            return;
        }
        mGiftDanmuCount.showMessage(item);
    }

    private boolean mIsEnd;

    private boolean mTopRunning;
    private boolean mCenterRunning;
    private boolean mBottomRunning;

    private ObjectAnimator getAnimator(View view, final PlayPosition playPosition) {
        int screenWidth = ScreenUtil.getScreenWidth(mContext);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", screenWidth, -screenWidth).setDuration(mDuration);
        animator.setInterpolator(new DecelerateAccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (playPosition == PlayPosition.TOP) {
                    mTopRunning = false;
                } else if (playPosition == PlayPosition.CENTER) {
                    mCenterRunning = false;
                } else if (playPosition == PlayPosition.BOTTOM) {
                    mBottomRunning = false;
                }

                if (!mIsEnd) {
                    mGiftDanmuCount.hideMessage();
                }
            }
        });
        return animator;
    }


    public void nextPlayPosition() {
        if (!mTopRunning) {
            mNextPosition = PlayPosition.TOP;
            return;
        }

        if (!mCenterRunning) {
            mNextPosition = PlayPosition.CENTER;
            return;
        }

        if (!mBottomRunning) {
            mNextPosition = PlayPosition.BOTTOM;
            return;
        }
    }

    @Override
    public void handleMessage(GiftAnimatorView.GiftItem item) {

        nextPlayPosition();
        TextView textView = mTvTop;
        LinearLayout linearLayout = mLlTop;
        ImageView imageView = mIvTop;
        ObjectAnimator animator;

        if (mNextPosition == PlayPosition.CENTER) {
            textView = mTvCenter;
            linearLayout = mLlCenter;
            imageView = mIvCenter;
            animator = getAnimator(linearLayout, PlayPosition.CENTER);
            mCenterAnimator = animator;
            mCenterRunning = true;
        } else if (mNextPosition == PlayPosition.BOTTOM) {
            textView = mTvBottom;
            linearLayout = mLlBottom;
            imageView = mIvBottom;
            animator = getAnimator(linearLayout, PlayPosition.BOTTOM);
            mBottomAnimator = animator;
            mBottomRunning = true;
        } else {
            animator = getAnimator(linearLayout, PlayPosition.TOP);
            mTopAnimator = animator;
            mTopRunning = true;
        }

        if (linearLayout.getVisibility() == View.INVISIBLE) {
            linearLayout.setVisibility(VISIBLE);
        }

        SpannableString spanString = null;

        spanString = new SpannableString(item.nick + item.content);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#9CF7FF")),
                0, item.nick.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setTextColor(mContext.getResources().getColor(R.color.white));

        textView.setText(spanString);

        updateBackgroundAndImage(linearLayout, imageView, item.type);

        animator.start();
    }


    public enum PlayPosition {
        TOP,
        CENTER,
        BOTTOM
    }


    public enum GiftType {
        CUSTOMER_TICKET("观众成功领券"),      // xx 成功获取直播间优惠券
        CUSTOMER_ASK("观众请求讲解"),         // xx 请求讲解
        CUSTOMER_ASK_PRICE("观众停留车系"),   // xx 正在使用实时询价功能
        CUSTOMER_GET_CLUE("询价留资成功"),   // xx 获取一条新线索
        CUSTOMER_GOLD_SALES("金牌顾问");     // xx 正在使用金牌顾问

        private String type;

        GiftType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public static class GiftItem {
        public GiftType type;
        public String nick;
        public String content;
    }


    public void updateBackgroundAndImage(LinearLayout linearLayout, ImageView imageView, GiftType type) {
        if (type == GiftType.CUSTOMER_TICKET) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_gift_ticket));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.img_gift_ticket_bg));
        } else if (type == GiftType.CUSTOMER_ASK) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_gift_ask));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.img_gift_ask_bg));
        } else if (type == GiftType.CUSTOMER_ASK_PRICE) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_gift_price));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.img_gift_price_bg));
        } else if (type == GiftType.CUSTOMER_GET_CLUE) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_gift_cule));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.img_gift_cule_bg));
        } else if (type == GiftType.CUSTOMER_GOLD_SALES) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_gift_gold_sales));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.img_gift_gold_sales_bg));
        }
    }

    public void start() {
        mIsEnd = false;
    }

    public void destroy() {

        mIsEnd = true;

        if (mTopAnimator != null) {
            mTopAnimator.end();
            mTopAnimator = null;
        }

        if (mCenterAnimator != null) {
            mCenterAnimator.end();
            mCenterAnimator = null;
        }

        if (mBottomAnimator != null) {
            mBottomAnimator.end();
            mBottomAnimator = null;
        }

        mNextPosition = null;

        mGiftDanmuCount.clear();
    }


}
