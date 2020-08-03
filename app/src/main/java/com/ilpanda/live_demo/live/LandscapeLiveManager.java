package com.ilpanda.live_demo.live;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.bean.LiveItemBean;
import com.ilpanda.live_demo.bean.LiveUserInfoBean;
import com.ilpanda.live_demo.bean.TCChatEntity;
import com.ilpanda.live_demo.constants.LiveConstants;
import com.ilpanda.live_demo.live.dialog.TCInputTextMsgDialog;
import com.ilpanda.live_demo.live.msg.TCChatMsgListAdapter;
import com.ilpanda.live_demo.utils.LiveHelper;
import com.ilpanda.live_demo.utils.ScreenUtil;
import com.ilpanda.live_demo.utils.ToastUtil;
import com.ilpanda.live_demo.wiget.DanmuMgr;
import com.ilpanda.live_demo.wiget.SimpleAnimatorListener;

import java.util.ArrayList;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * 横屏直播间
 */
public class LandscapeLiveManager implements LivePlayListener, View.OnClickListener, TCInputTextMsgDialog.OnTextSendListener {

    private Activity mContext;

    private ViewGroup mRootContainer;
    private RelativeLayout mRlyVideo;       // 直播播放区域
    private ImageView mIvAvatar;            // 用户头像
    private TextView mTvPopular;            // 主播人气
    private TextView mTvLikeCount;          // 点赞数
    private LinearLayout mLLNickLayout;

    private ImageView mIvDanmu;            // 弹幕显示开关
    private RelativeLayout mRlyTitle;
    private LinearLayout mLlLiveList;      // 广场列表


    private ImageView mIvShare;           // 分享
    private ImageView mIvLockScreen;      // 横屏、锁屏
    private ImageView mIvChangeOrientation; // 全屏切换

    private boolean mIsLocked;


    // 消息相关
    private TCInputTextMsgDialog mInputTextMsgDialog;    // 消息输入框
    private ListView mListViewMsg;           // 消息列表控件
    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>(); // 消息列表集合
    private TCChatMsgListAdapter mChatMsgListAdapter;    // 消息列表的Adapter

    //弹幕
    private DanmuMgr mDanmuMgr;
    private IDanmakuView mDanmuView;
    private ProgressBar mProgressBar;

    private RelativeLayout mRlyChatMsg;


    private LiveItemBean mLiveItemBean;                                      // 直播间信息
    private LiveUserInfoBean mLiveAnchorBean;                                // 主播信息

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int mCurrentRenderRotation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private boolean mOpenDanmu = false;

    private boolean mIsLandScape = false;
    private TextView mTvInputMessage;


    private static final int HIDE_ANIMATOR = 0;
    private static final int HIDE_DELAY = 3000;
    private boolean mIsShowing = true;
    private AnimatorSet showAnimatorSet;
    private AnimatorSet hideAnimatorSet;


    public LandscapeLiveManager(Activity activity) {
        this.mContext = activity;
        initView();
    }

    @Override
    public void onAdd(LiveItemBean data) {
        LiveUserInfoBean user = data.getUser();

        mLiveItemBean = data;
        mLiveAnchorBean = user;

        // 昵称
        TextView tvNick = mRootContainer.findViewById(R.id.tv_nick);
        tvNick.setText(mLiveAnchorBean.getNick());
        // 人气
        mTvPopular.setText(String.valueOf(data.getOnlineCount()));
        // 点赞
        mTvLikeCount.setText(LiveHelper.getShowCount(data.getLikeCount()));
        // 加载用户头像
        RequestOptions options = new RequestOptions().placeholder(R.drawable.face).error(R.drawable.face).circleCrop().override(ScreenUtil.dipToPx(mContext, 30)).dontAnimate();
        Glide.with(mContext).load(user.getAvatar()).apply(options).into(mIvAvatar);

        // 弹幕管理类
        mDanmuMgr = new DanmuMgr(mContext);
        mDanmuMgr.setDanmakuView(mDanmuView);

        // 初始化弹幕开关
        mOpenDanmu = true;
        showOrHideDanmu();

        // 聊天室
        mArrayListChatEntity.clear();
        handleWarnMessage();


    }

    private void handleWarnMessage() {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("");
        entity.setContent(mContext.getResources().getString(R.string.live_warn_message));
        entity.setType(LiveConstants.WARN);
        notifyMsg(entity);
    }


    @Override
    public void onRemove() {
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
    }

    @Override
    public ViewGroup getRootView() {
        return mRootContainer;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        if (mDanmuMgr != null) {
            mDanmuMgr.resume();
        }
    }

    @Override
    public void onPause() {
        if (mDanmuMgr != null) {
            mDanmuMgr.pause();
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
    }

    @Override
    public void onRestart() {

    }


    @Override

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mIsLandScape = false;
            mCurrentRenderRotation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            changeOrientation(false);
        } else {
            mIsLandScape = true;
            mCurrentRenderRotation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            changeOrientation(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (mCurrentRenderRotation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mContext.finish();
        }
    }


    private void initView() {
        mRootContainer = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.layout_landscape_live, null);


        mRlyVideo = mRootContainer.findViewById(R.id.rly_video);
        mRlyVideo.setOnClickListener(this);
        RelativeLayout.LayoutParams playerLayoutParams = (RelativeLayout.LayoutParams) mRlyVideo.getLayoutParams();
        playerLayoutParams.height = ScreenUtil.getScreenWidth(mContext) * 9 / 16;

        //主播头像
        mIvAvatar = mRootContainer.findViewById(R.id.iv_avatar);

        // 主播人气
        mTvPopular = mRootContainer.findViewById(R.id.tv_popular);

        // 主播点赞数
        mTvLikeCount = mRootContainer.findViewById(R.id.tv_like_count);

        mLLNickLayout = mRootContainer.findViewById(R.id.ll_avatar);

        // 竖屏下方的: 想说点什么
        mTvInputMessage = mRootContainer.findViewById(R.id.btn_message_input);
        mTvInputMessage.setOnClickListener(this);

        //用户发布点击消息
        mInputTextMsgDialog = new TCInputTextMsgDialog(mContext, R.style.InputDialog);
        mInputTextMsgDialog.setOnTextSendListener(this);

        // 横向弹幕
        mDanmuView = (IDanmakuView) mRootContainer.findViewById(R.id.anchor_danmu_view);


        // 聊天室消息列表
        mListViewMsg = (ListView) mRootContainer.findViewById(R.id.lv_chat_msg);
        ViewGroup.LayoutParams layoutParams = mListViewMsg.getLayoutParams();
        // 动态布局
        layoutParams.width = ScreenUtil.getScreenWidth(mContext) * 3 / 5;
        mListViewMsg.setLayoutParams(layoutParams);
        mChatMsgListAdapter = new TCChatMsgListAdapter(mContext, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        //全屏
        mIvChangeOrientation = mRootContainer.findViewById(R.id.iv_change_orientation);
        mIvChangeOrientation.setOnClickListener(this);

        // 记载进度:
        mProgressBar = mRootContainer.findViewById(R.id.progress_bar);

        // 右上角关闭退出:
        mRootContainer.findViewById(R.id.btn_back).setOnClickListener(this);

        //  分享
        mIvShare = mRootContainer.findViewById(R.id.iv_share);
        mIvShare.setOnClickListener(this);

        // 开关弹幕
        mIvDanmu = mRootContainer.findViewById(R.id.iv_danmu);
        mIvDanmu.setOnClickListener(this);

        // 锁定屏幕
        mIvLockScreen = mRootContainer.findViewById(R.id.iv_lock_screen);
        mIvLockScreen.setOnClickListener(this);

        // 顶部布局:
        mRlyTitle = mRootContainer.findViewById(R.id.layout_live_anchor_info);
        mRlyChatMsg = mRootContainer.findViewById(R.id.rly_chat_msg);


        // 点击进入直播广场
        mLlLiveList = mRootContainer.findViewById(R.id.ll_live_list);
        mLlLiveList.setOnClickListener(this);

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) { // 右上角返回按钮
            mContext.finish();
        } else if (id == R.id.btn_message_input) {    // 点击输入框
            showInputMsgDialog();
        } else if (id == R.id.iv_share) {      //  分享
            ToastUtil.showShortToast(mContext, "你点到分享了!");
        } else if (id == R.id.iv_danmu) { // 开/关弹幕
            showOrHideDanmu();
        } else if (id == R.id.iv_lock_screen) { // 锁定屏幕
            clickLockScreen();
        } else if (id == R.id.rly_video && mIsLandScape) { // 锁定屏幕
            showOrHideControllerView(!mIsShowing);
        } else if (id == R.id.iv_change_orientation) { // 全屏/退出全屏
            if (mCurrentRenderRotation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (mCurrentRenderRotation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mIsLocked = false;
                mIvLockScreen.setImageResource(R.drawable.img_lock_screen);
                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

    }

    @Override
    public void onTextSend(String msg) {

        //消息回显
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我: ");
        entity.setContent(msg);
        entity.setType(LiveConstants.TEXT_TYPE);
        notifyMsg(entity);

        mDanmuMgr.addDanmu(msg, true, mIsLandScape);


    }

    private void notifyMsg(final TCChatEntity entity) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mArrayListChatEntity.size() > 1000) {
                    while (mArrayListChatEntity.size() > 900) {
                        mArrayListChatEntity.remove(0);
                    }
                }
                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }


    private void showInputMsgDialog() {
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
        lp.width = ScreenUtil.getScreenWidth(mContext);
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    private void showOrHideDanmu() {
        if (!mOpenDanmu) {
            // 关闭弹幕
            mDanmuMgr.hide();
            mIvDanmu.setImageResource(R.drawable.img_danmu_close);
            mOpenDanmu = true;
        } else {
            // 开启弹幕
            mDanmuMgr.show();
            mIvDanmu.setImageResource(R.drawable.img_danmu_open);
            mOpenDanmu = false;
        }
    }


    private void changeOrientation(boolean isLandscape) {

        View rlyVideo = mRootContainer.findViewById(R.id.rly_video);
        View danmuView = mRootContainer.findViewById(R.id.anchor_danmu_view);
        ImageView ivChangeOrientation = mRootContainer.findViewById(R.id.iv_change_orientation);
        RelativeLayout rlyChatMsg = mRootContainer.findViewById(R.id.rly_chat_msg);
        Button btnInputMessage = mRootContainer.findViewById(R.id.btn_message_input);

        RelativeLayout.LayoutParams videoLayoutParams = (RelativeLayout.LayoutParams) rlyVideo.getLayoutParams();
        RelativeLayout.LayoutParams danmuViewLayoutParams = (RelativeLayout.LayoutParams) danmuView.getLayoutParams();
        RelativeLayout.LayoutParams ivChangeOrientationLayoutParams = (RelativeLayout.LayoutParams) ivChangeOrientation.getLayoutParams();
        RelativeLayout.LayoutParams rlyChatMsgLayoutParams = (RelativeLayout.LayoutParams) rlyChatMsg.getLayoutParams();
        RelativeLayout.LayoutParams listViewMsgLayoutParams = (RelativeLayout.LayoutParams) mListViewMsg.getLayoutParams();
        RelativeLayout.LayoutParams ivDanmuLayoutParams = (RelativeLayout.LayoutParams) mIvDanmu.getLayoutParams();
        RelativeLayout.LayoutParams btnInputMessageLayoutParams = (RelativeLayout.LayoutParams) btnInputMessage.getLayoutParams();


        if (isLandscape) {

            //视频播放布局全屏显示
            videoLayoutParams.topMargin = 0;
            videoLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;

            //横屏关注覆盖在视频
            videoLayoutParams.removeRule(RelativeLayout.BELOW);

            // 弹幕参数
            danmuViewLayoutParams.topMargin = ScreenUtil.dipToPx(mContext, 66);
            danmuViewLayoutParams.removeRule(RelativeLayout.BELOW);

            // 下方聊天室
            rlyChatMsgLayoutParams.removeRule(RelativeLayout.BELOW);
            rlyChatMsgLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rly_video);
            rlyChatMsgLayoutParams.bottomMargin = ScreenUtil.dipToPx(mContext, 8);

            listViewMsgLayoutParams.height = ScreenUtil.dipToPx(mContext, 82);

            //聊天室暂时隐藏
            mListViewMsg.setVisibility(View.INVISIBLE);

            // 分享
            mIvShare.setVisibility(View.GONE);


            //全屏按钮
            ivChangeOrientation.setImageResource(R.drawable.img_change_landscape);
            ivChangeOrientationLayoutParams.bottomMargin = ScreenUtil.dipToPx(mContext, 20);

            // 弹幕开关
            ivDanmuLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ivDanmuLayoutParams.rightMargin = ScreenUtil.dipToPx(mContext, 40);
            ivDanmuLayoutParams.width = ScreenUtil.dipToPx(mContext, 30);
            ivDanmuLayoutParams.height = ScreenUtil.dipToPx(mContext, 30);

            // 锁屏
            mIvLockScreen.setVisibility(View.VISIBLE);

            btnInputMessageLayoutParams.width = ScreenUtil.dipToPx(mContext, 451);
            btnInputMessageLayoutParams.removeRule(RelativeLayout.LEFT_OF);

            handler.removeMessages(HIDE_ANIMATOR);
            handler.sendEmptyMessageDelayed(HIDE_ANIMATOR, HIDE_DELAY);

        } else {

            resetAnimator();

            //视频播放布局
            videoLayoutParams.topMargin = ScreenUtil.dipToPx(mContext, 48);
            videoLayoutParams.height = ScreenUtil.getScreenWidth(mContext) * 9 / 16;

            //竖屏关注布局
//            mRootContainer.findViewById(R.id.layout_live_pusher_info).setVisibility(View.VISIBLE);
            //竖屏屏幕下方发布弹幕
            mRootContainer.findViewById(R.id.layout_Bottom).setVisibility(View.VISIBLE);

            //竖屏关注在视频上方
            videoLayoutParams.addRule(RelativeLayout.BELOW, R.id.layout_live_anchor_info);

            danmuViewLayoutParams.topMargin = ScreenUtil.dipToPx(mContext, 20);

            // 下方聊天室
            rlyChatMsgLayoutParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
            rlyChatMsgLayoutParams.addRule(RelativeLayout.BELOW, R.id.rly_video);
            rlyChatMsgLayoutParams.bottomMargin = 0;

            listViewMsgLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mListViewMsg.setVisibility(View.VISIBLE);
//            listViewMsgLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            listViewMsgLayoutParams.addRule(RelativeLayout.ABOVE, R.id.layout_Bottom);

            // 分享
            mIvShare.setVisibility(View.VISIBLE);

            //全屏按钮
            ivChangeOrientation.setImageResource(R.drawable.img_change_landscape);
            ivChangeOrientationLayoutParams.bottomMargin = ScreenUtil.dipToPx(mContext, 8);


            // 弹幕开关
            ivDanmuLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ivDanmuLayoutParams.rightMargin = 0;
            ivDanmuLayoutParams.width = ScreenUtil.dipToPx(mContext, 36);
            ivDanmuLayoutParams.height = ScreenUtil.dipToPx(mContext, 36);

            // 锁屏
            mIsLocked = false;
            mIvLockScreen.setVisibility(View.GONE);

            btnInputMessageLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            btnInputMessageLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.iv_danmu);

        }
    }


    private void resetAnimator() {
        handler.removeMessages(HIDE_ANIMATOR);
        clearAnimator();

        mIvLockScreen.setVisibility(View.VISIBLE);
        mIvChangeOrientation.setVisibility(View.VISIBLE);

        mRlyTitle.setTranslationY(0);
        mTvInputMessage.setTranslationY(0);
        mIvDanmu.setTranslationY(0);
        mIvChangeOrientation.setTranslationY(0);
    }


    private void clearAnimator() {
        if (showAnimatorSet != null) {
            showAnimatorSet.cancel();
        }

        if (hideAnimatorSet != null) {
            hideAnimatorSet.cancel();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == HIDE_ANIMATOR) {
                showOrHideControllerView(false);
            }
        }
    };


    private void showOrHideControllerView(boolean show) {

        // 横屏才有动画
        if (!mIsLandScape) {
            return;
        }

        handler.removeMessages(HIDE_ANIMATOR);


        int bottomDistance = ScreenUtil.dipToPx(mContext, 60);
        int titleHeight = ScreenUtil.dipToPx(mContext, 84);
        int lockDistance = ScreenUtil.dipToPx(mContext, 15);
        int liveListTop = ScreenUtil.dipToPx(mContext, 84 + 48);

        int inputMessageLayoutDistance = ScreenUtil.dipToPx(mContext, 120);

        int duration = 300;


        if (show) {
            ObjectAnimator lockScreenAnimator;
            if (mIvLockScreen.getTranslationY() == 0) {
                lockScreenAnimator = ObjectAnimator.ofFloat(mIvLockScreen, "translationY", 0, 0);
            } else {
                lockScreenAnimator = ObjectAnimator.ofFloat(mIvLockScreen, "translationY", -lockDistance, 0);
            }
            if (mIsLocked) {
                lockScreenAnimator.setInterpolator(new LinearInterpolator());
                lockScreenAnimator.setDuration(duration);
                lockScreenAnimator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIvLockScreen.setVisibility(View.VISIBLE);
                        mIsShowing = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        handler.sendEmptyMessageDelayed(HIDE_ANIMATOR, HIDE_DELAY);
                    }
                });
                lockScreenAnimator.start();
                return;
            }

            // 显示动画
            ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(mRlyTitle, "translationY", -titleHeight, 0);
            ObjectAnimator liveListAnimator = ObjectAnimator.ofFloat(mLlLiveList, "translationY", -liveListTop, 0);

            ObjectAnimator inputMsgAnimator = ObjectAnimator.ofFloat(mTvInputMessage, "translationY", inputMessageLayoutDistance, 0);
            ObjectAnimator danmuAnimator = ObjectAnimator.ofFloat(mIvDanmu, "translationY", inputMessageLayoutDistance, 0);
            ObjectAnimator changeOrientationAnimator = ObjectAnimator.ofFloat(mIvChangeOrientation, "translationY", inputMessageLayoutDistance, 0);

            showAnimatorSet = new AnimatorSet();
            showAnimatorSet.playTogether(titleAnimator, lockScreenAnimator, liveListAnimator, inputMsgAnimator, danmuAnimator, changeOrientationAnimator);
            showAnimatorSet.setInterpolator(new LinearInterpolator());
            showAnimatorSet.setDuration(duration);
            showAnimatorSet.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIvLockScreen.setVisibility(View.VISIBLE);
                    mIvChangeOrientation.setVisibility(View.VISIBLE);
                    mIsShowing = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    handler.sendEmptyMessageDelayed(HIDE_ANIMATOR, HIDE_DELAY);
                }
            });
            showAnimatorSet.start();
        } else {
            ObjectAnimator lockScreenAnimator = ObjectAnimator.ofFloat(mIvLockScreen, "translationY", 0, -lockDistance);
            if ((mIsLocked && mIvChangeOrientation.getVisibility() == View.GONE) || (!mIsLocked && mIvChangeOrientation.getVisibility() == View.GONE)) {
                lockScreenAnimator.setInterpolator(new LinearInterpolator());
                lockScreenAnimator.setDuration(duration);
                lockScreenAnimator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIvLockScreen.setVisibility(View.GONE);
                        mIsShowing = false;
                    }
                });
                lockScreenAnimator.start();
                return;
            }
            // 隐藏动画
            ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(mRlyTitle, "translationY", 0, -titleHeight);
            ObjectAnimator liveListAnimator = ObjectAnimator.ofFloat(mLlLiveList, "translationY", 0, -liveListTop);

            ObjectAnimator inputMsgAnimator = ObjectAnimator.ofFloat(mTvInputMessage, "translationY", 0, inputMessageLayoutDistance);
            ObjectAnimator danmuAnimator = ObjectAnimator.ofFloat(mIvDanmu, "translationY", 0, inputMessageLayoutDistance);
            ObjectAnimator changeOrientationAnimator = ObjectAnimator.ofFloat(mIvChangeOrientation, "translationY", 0, inputMessageLayoutDistance);

//            ObjectAnimator chatMsgAnimator = ObjectAnimator.ofFloat(mListViewMsg, "translationY", 0, bottomDistance);

            hideAnimatorSet = new AnimatorSet();
            hideAnimatorSet.playTogether(titleAnimator, lockScreenAnimator, liveListAnimator, inputMsgAnimator, danmuAnimator, changeOrientationAnimator);
            hideAnimatorSet.setInterpolator(new LinearInterpolator());
            hideAnimatorSet.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mIsLandScape) {
                        mIvChangeOrientation.setVisibility(View.GONE);
                        mIvLockScreen.setVisibility(View.GONE);
                    }
                    mIsShowing = false;
                }
            });
            hideAnimatorSet.setDuration(duration);
            hideAnimatorSet.start();
        }
    }

    private void clickLockScreen() {
        if (!mIsLocked) {
            //锁屏
            mIsLocked = true;
            mIvLockScreen.setImageResource(R.drawable.img_unlock_screen);
            showOrHideControllerView(false);
        } else {
            //不锁屏
            mIsLocked = false;
            mIvLockScreen.setImageResource(R.drawable.img_lock_screen);
            showOrHideControllerView(true);
        }
    }

}
