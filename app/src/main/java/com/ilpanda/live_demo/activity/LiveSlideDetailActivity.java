package com.ilpanda.live_demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.bean.LiveInfoBean;
import com.ilpanda.live_demo.bean.LiveItemBean;
import com.ilpanda.live_demo.constants.LiveConstants;
import com.ilpanda.live_demo.live.LiveProxy;
import com.ilpanda.live_demo.module.NetworkModule;
import com.ilpanda.live_demo.module.event.Event;
import com.ilpanda.live_demo.utils.AudioUtils;
import com.ilpanda.live_demo.utils.PreferencesUtils;
import com.ilpanda.live_demo.utils.StatusBarUtils;
import com.ilpanda.live_demo.utils.ToastUtil;
import com.ilpanda.live_demo.wiget.LiveGuideView;
import com.ilpanda.live_demo.wiget.LoadingView;
import com.ilpanda.live_demo.wiget.VerticalViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LiveSlideDetailActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = LiveSlideDetailActivity.class.getSimpleName();

    public static final String ENTER_LIVE_ID = "enter_live";

    private LiveGuideView mLiveGuideView;       // 新用户引导页面
    private LoadingView mLoadView;              // 加载中页面
    private VerticalViewPager mViewPager;       // 竖直滑动 ViewPager
    private PagerAdapter mPagerAdapter;         // ViewPager 的 Adapter
    private LiveProxy mLiveProxy;               // 包装类,兼容横屏播放与竖屏播放

    private boolean mIsNotifyData;

    private boolean mIsFirst = true;            // 是否为第一次加载数据

    private int mCurrentItem;                   // 当前 ViewPager 的位置.
    private int mLastItem;                      // 避免数据重复加载

    private String mLiveId = "";    // 进入直播间列表时的直播间 id.

    private List<LiveItemBean> mList = new ArrayList<>();

    public static void start(Context context, String liveId) {
        Intent intent = new Intent(context, LiveSlideDetailActivity.class);
        intent.putExtra(ENTER_LIVE_ID, liveId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live);
        StatusBarUtils.transparencyBar(this);

        mLiveId = getIntent().getStringExtra(ENTER_LIVE_ID);

        EventBus.getDefault().register(this);

        // 停止后台播放的 BGM,如进入直播间,用户在后台播放音乐,暂停音乐.
        AudioUtils.stopBgm(this);

        // 初始化布局
        initView();

        // 服务器数据返回后,交由 LiveProxy 处理.
        mLiveProxy = new LiveProxy(this);

        // 初始化数据
        initData();

    }

    private void initData() {
        mIsFirst = true;

        // 清空所有数据
        mList.clear();

        getLiveSlide(mLiveId);
    }

    private void initView() {

        // 新用户引导 View : 上下滑动
        mLiveGuideView = findViewById(R.id.guide_view);

        mLoadView = findViewById(R.id.activity_view_loading);
        mLoadView.show();

        // 竖直滑动 ViewPager
        mViewPager = findViewById(R.id.view_pager);
        mPagerAdapter = new PagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mIsNotifyData) return;
                mCurrentItem = position;
            }
        });

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (mList.size() == 0 || mIsNotifyData) {
                    return;
                }
                ViewGroup viewGroup = (ViewGroup) page;
                if ((position < 0 && viewGroup.getId() != mCurrentItem)) {
                    // room_container 为视频播放的根布局 id
                    View rootView = viewGroup.findViewById(R.id.room_container);
                    if (rootView != null && rootView.getParent() != null && rootView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) (rootView.getParent())).removeView(rootView);
                        mLiveProxy.onRemove();
                    }
                }

                // 满足此种条件，表明需要加载直播视频，以及聊天室了
                if (viewGroup.getId() == mCurrentItem && position == 0 && mLastItem != mCurrentItem) {
                    View rootView = mLiveProxy.getRootView();
                    if (rootView.getParent() != null && rootView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) (rootView.getParent())).removeView(rootView);
                        mLiveProxy.onRemove();
                    }
                    loadData(viewGroup, mCurrentItem);

                    // 页面滑动时,预加载上一页和下一页的数据
                    LiveItemBean data = mList.get(mCurrentItem);
                    getLiveSlide(data.getLiveId());
                }
            }
        });
    }


    private void loadData(ViewGroup viewGroup, int currentItem) {
        LiveItemBean data = mList.get(currentItem);
        mLiveProxy.setPortrait(data.isPortrait());
        viewGroup.addView(mLiveProxy.getRootView());
        mLiveProxy.onAdd(data);
        mLastItem = currentItem;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 右上角返回
        if (id == R.id.iv_back) {
            finish();
        }

    }

    private class PagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_portrait_live_item, null);
            // 右上角返回
            view.findViewById(R.id.iv_back).setOnClickListener(LiveSlideDetailActivity.this);

            // 背景
            LiveItemBean liveItemBean = mList.get(position);
            ImageView ivBg = view.findViewById(R.id.iv_bg);
            Glide.with(LiveSlideDetailActivity.this).load(liveItemBean.getBlurredImg()).placeholder(R.drawable.img_live_bg).centerCrop().into(ivBg);


            // 加载动画
            ImageView ivLoading = view.findViewById(R.id.iv_loading);
            AnimationDrawable frameAnimation = (AnimationDrawable) ivLoading.getBackground();
            frameAnimation.start();

            view.setId(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container.findViewById(position));
        }
    }


    private void getLiveSlide(String liveId) {
        NetworkModule.getLiveSlide(liveId);
    }

    @Override
    public void onBackPressed() {
        mLiveProxy.onBackPressed();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mViewPager.setEnableScroll(true);
        } else {
            mViewPager.setEnableScroll(false);
        }
        mLiveProxy.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLiveProxy.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mLiveProxy.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mLiveProxy.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLiveProxy.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLiveProxy.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLiveProxy.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLiveProxy.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveProxy.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveSlideEvent(Event.SlideEvent event) {

        if (isDestroyed() || isFinishing()) {
            return;
        }

        LiveInfoBean dataBean = event.result;
        if (dataBean != null && dataBean.isSuccess() && dataBean.getData() != null && dataBean.getData().getCurrent() != null) {
            LiveItemBean current = dataBean.getData().getCurrent(); // 当前直播间
            LiveItemBean next = dataBean.getData().getNext();       // 下一个直播间
            LiveItemBean pre = dataBean.getData().getPre();         // 上一个直播间

            // 用户快速来回滑动
            if (mList.size() > 0 && !current.equals(mList.get(mCurrentItem))) {
                return;
            }

            // 上一个直播间信息
            boolean addPre = false;
            if (pre != null) {
                if (!mList.contains(pre)) {
                    addPre = true;
                    mList.add(0, pre);
                }
            }

            // 第一次进入页面,当前直播间信息为空
            boolean addCurrent = false;
            if (!mList.contains(current)) {
                addCurrent = true;
                mList.add(current);
            }

            // 下一个直播间信息
            boolean addNext = false;
            if (next != null) {
                if (!mList.contains(next)) {
                    addNext = true;
                    mList.add(next);
                }
            }

            // 网络延迟:用户来回滑动. 返回的数据都已存在,不做任何处理.
            if (!addCurrent && !addPre && !addNext) {
                return;
            }

            // notifyDataSetChanged 会调用 transformPage, 但我们更希望手动控制 View 的添加和移除. 避免频繁的初始化、销毁数据
            mIsNotifyData = true;
            mPagerAdapter.notifyDataSetChanged();
            mIsNotifyData = false;

            if (mIsFirst) {
                mIsFirst = false;

                int enterPosition = getEnterPosition(mList);
                mViewPager.setCurrentItem(enterPosition, false);

                // 第一次进入页面时候,加载数据
                View currentView = mViewPager.getCurrentView();
                loadData((ViewGroup) currentView, mCurrentItem);

                // 显示引导页面
                showGuide();

                // 加载中页面
                mLoadView.hide();
            } else {
                if (addPre) {
                    int newItem = mCurrentItem + 1;
                    // 禁止页面滚动,页面滚动时的效果不好,手动刷新数据
                    mViewPager.setCurrentItem(newItem, false);

                    View rootView = mLiveProxy.getRootView();
                    if (rootView.getParent() != null && rootView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) (rootView.getParent())).removeView(rootView);
                    }
                    ViewGroup currentView = (ViewGroup) mViewPager.getCurrentView();
                    currentView.addView(mLiveProxy.getRootView());
                }
            }
        } else {
            ToastUtil.showLongToast(this, "网络异常");
        }
    }

    private int getEnterPosition(List<LiveItemBean> liveList) {
        for (int i = 0; i < liveList.size(); i++) {
            LiveItemBean item = liveList.get(i);
            if (mLiveId.equals(String.valueOf(item.getLiveId()))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 显示引导页面: 上下滑动
     */
    private void showGuide() {
        boolean hasShow = PreferencesUtils.getPreference(this, LiveConstants.SP, LiveConstants.SP_SCROLL_GUIDE, false);
        if (!hasShow) {
            PreferencesUtils.setPreferences(this, LiveConstants.SP, LiveConstants.SP_SCROLL_GUIDE, true);
            mLiveGuideView.showGuide();
        }

    }


}