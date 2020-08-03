package com.ilpanda.live_demo.live;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.ilpanda.live_demo.wiget.dialog.LiveExitConfirmDialog;
import com.ilpanda.live_demo.wiget.gift.GiftAnimatorView;
import com.ilpanda.live_demo.wiget.heart.TCHeartLayout;

import java.util.ArrayList;

/**
 * 竖屏直播间
 */
public class PortraitLiveManager implements LivePlayListener, View.OnClickListener, TCInputTextMsgDialog.OnTextSendListener, TCChatMsgListAdapter.OnItemClickListener {

    private Activity mContext;


    private RelativeLayout mRoomContainer;              // 视频播放根布局
    private View mLoadingView;                          // 加载中页面
    private GiftAnimatorView mGiftAnimatorView;         // 礼物弹幕


    private TextView mTvNick;           // 昵称
    private TextView mTvLocation;       // 定位
    private ImageView mIvAvatar;        // 头像
    private TextView mTvLikeCount;      // 点赞
    private TextView mTvFollow;         // 关注
    private Button mBtnInput;           // 聊天输入框
    private LinearLayout mLlLocation;   // 位置布局

    // 消息相关
    private ListView mListViewMsg;                                            // 消息列表控件
    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>(); // 消息列表集合
    private TCChatMsgListAdapter mChatMsgListAdapter;                         // 消息列表的 Adapter
    private TCInputTextMsgDialog mInputTextMsgDialog;                         // 消息输入框

    private TCHeartLayout mHeartLayout;                                       // 点赞动画

    private LiveItemBean mLiveItemBean;                                      // 直播间信息
    private LiveUserInfoBean mLiveAnchorBean;                                // 主播信息

    private Handler mHandler = new Handler();
    private int mLikeCount;                                                  // 点赞数量


    public PortraitLiveManager(Activity activity) {
        this.mContext = activity;
        initView();
    }

    @Override
    public void onAdd(LiveItemBean data) {
        LiveUserInfoBean user = data.getUser();

        mLiveItemBean = data;
        mLiveAnchorBean = user;
        mLikeCount = mLiveItemBean.getLikeCount();

        mTvNick.setText(data.getUser().getNick());
        mTvLocation.setText("深圳");


        // 加载主播头像
        RequestOptions options = new RequestOptions().placeholder(R.drawable.face).error(R.drawable.face).circleCrop().override(ScreenUtil.dipToPx(mContext, 30)).dontAnimate();
        Glide.with(mContext).load(user.getAvatar()).apply(options).into(mIvAvatar);

        // 点赞
        if (data.getLikeCount() != 0) {
            mTvLikeCount.setVisibility(View.VISIBLE);
            mTvLikeCount.setText(LiveHelper.getShowCount(data.getLikeCount()));
        } else {
            mTvLikeCount.setVisibility(View.INVISIBLE);
        }

        // 关注
        if (user.isFollow()) {
            mTvFollow.setText("已关注");
            mTvFollow.setTextColor(Color.parseColor("#CCCCCC"));
            mTvFollow.setBackground(mContext.getResources().getDrawable(R.drawable.bg_portrait_live_follow_selecetd));
        } else {
            mTvFollow.setText("关注");
            mTvFollow.setTextColor(mContext.getResources().getColor(R.color.white));
            mTvFollow.setBackground(mContext.getResources().getDrawable(R.drawable.bg_portrait_live_follow));
        }

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

    /**
     * 资源销毁工作
     */
    @Override
    public void onRemove() {
        mHeartLayout.clearAnimation();
    }

    @Override
    public ViewGroup getRootView() {
        return mRoomContainer;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        onRemove();
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onBackPressed() {
        showFollowExitDialog();
    }


    private void initView() {
        // 播放器根布局
        mRoomContainer = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_portrait_live, null);

        // 加载中
        mLoadingView = mRoomContainer.findViewById(R.id.play_loading);
        mRoomContainer.findViewById(R.id.play_iv_back).setOnClickListener(this);

        // 昵称
        mTvNick = mRoomContainer.findViewById(R.id.tv_nick);
        // 位置
        mTvLocation = mRoomContainer.findViewById(R.id.tv_location);
        // 关注
        mTvFollow = mRoomContainer.findViewById(R.id.tv_follow);
        mTvFollow.setOnClickListener(this);

        // 礼物弹幕
        mGiftAnimatorView = mRoomContainer.findViewById(R.id.view_gift_animator);
        RelativeLayout.LayoutParams giftLayoutParams = (RelativeLayout.LayoutParams) mGiftAnimatorView.getLayoutParams();
        giftLayoutParams.topMargin = ScreenUtil.getScreenHeight(mContext) / 2 - ScreenUtil.dipToPx(mContext, 130);
        mGiftAnimatorView.setLayoutParams(giftLayoutParams);

        // 头像
        mIvAvatar = mRoomContainer.findViewById(R.id.iv_avatar);
        mIvAvatar.setOnClickListener(this);

        // 主播地理位置
        mLlLocation = mRoomContainer.findViewById(R.id.ll_location);

        // 点击进入直播广场
        mRoomContainer.findViewById(R.id.ll_live_list).setOnClickListener(this);

        // 分享
        mRoomContainer.findViewById(R.id.iv_share).setOnClickListener(this);

        // 点赞
        mRoomContainer.findViewById(R.id.rly_like).setOnClickListener(this);

        // 右上角返回
        mRoomContainer.findViewById(R.id.iv_back).setOnClickListener(this);

        // 聊天输入框
        mBtnInput = mRoomContainer.findViewById(R.id.btn_message_input);
        mBtnInput.setOnClickListener(this);

        // 聊天室消息列表
        mListViewMsg = (ListView) mRoomContainer.findViewById(R.id.lv_chat_msg);
        ViewGroup.LayoutParams layoutParams = mListViewMsg.getLayoutParams();

        // 动态布局
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.6);
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.33);
        mListViewMsg.setLayoutParams(layoutParams);
        mChatMsgListAdapter = new TCChatMsgListAdapter(mContext, mListViewMsg, mArrayListChatEntity);
        mChatMsgListAdapter.setOnItemClickListener(this);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        // 用户发布点击消息对话框
        mInputTextMsgDialog = new TCInputTextMsgDialog(mContext, R.style.InputDialog);
        mInputTextMsgDialog.setOnTextSendListener(this);

        // 点赞数目
        mTvLikeCount = mRoomContainer.findViewById(R.id.tv_like_count);

        // 点赞动画
        mHeartLayout = mRoomContainer.findViewById(R.id.heart_layout);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_back) {
            showFollowExitDialog();
        } else if (id == R.id.btn_message_input) {    // 点击输入框
            showInputMsgDialog();
        } else if (id == R.id.iv_share) {      //  分享
            ToastUtil.showShortToast(mContext, "你点击到了分享");
        } else if (id == R.id.iv_avatar) {     // 主播头像
            ToastUtil.showShortToast(mContext, "你点到了头像");
        } else if (id == R.id.tv_follow) {     // 关注
            ToastUtil.showShortToast(mContext, "你点到了关注");
        } else if (id == R.id.rly_like) {     // 点赞
            clickLike();
        }

    }

    @Override
    public void onMsgItemClick(TCChatEntity item) {
        ToastUtil.showShortToast(mContext, "你点到了聊天记录");
    }


    /**
     * 用户退出时,显示关注弹出
     */
    private void showFollowExitDialog() {
        if (mLiveAnchorBean.isFollow()) {
            mContext.finish();
            return;
        }

        LiveExitConfirmDialog dialog = new LiveExitConfirmDialog(mContext, mLiveAnchorBean.getAvatar());
        dialog.setCallback(new LiveExitConfirmDialog.Callback() {
            @Override
            public void onClickConfirm() {
                ToastUtil.showShortToast(mContext, "感谢关注，欢迎下次再来");
                mContext.finish();
            }

            @Override
            public void onClickCancel() {
                mContext.finish();
            }
        });
        dialog.show();
    }


    /**
     * 聊天输入框
     */
    private void showInputMsgDialog() {
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
        lp.width = ScreenUtil.getScreenWidth(mContext);
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    @Override
    public void onTextSend(String msg) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我: ");
        entity.setContent(msg);
        entity.setType(LiveConstants.TEXT_TYPE);
        notifyMsg(entity);
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


    private static final int HEART_PADDING = 3;

    /**
     * 点赞动画
     */
    private void clickLike() {
        mHeartLayout.addFavor(ScreenUtil.dipToPx(mContext, HEART_PADDING));
        mLikeCount++;
        mLiveItemBean.setLikeCount(mLikeCount);
        if (mTvLikeCount.getVisibility() != View.VISIBLE) {
            mTvLikeCount.setVisibility(View.VISIBLE);
        }
        mTvLikeCount.setText(LiveHelper.getShowCount(mLikeCount));
    }


}
