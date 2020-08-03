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
import com.ilpanda.live_demo.bean.LiveDataBean;
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

public class LiveDetailActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = LiveDetailActivity.class.getSimpleName();

    public static final String ENTER_LIVE_ID = "enter_live";
    public static final String PAGE_NUM = "page_num";

    private LiveGuideView mLiveGuideView;       // 新用户引导页面
    private LoadingView mLoadView;              // 加载中页面
    private VerticalViewPager mViewPager;       // 竖直滑动 ViewPager
    private PagerAdapter mPagerAdapter;         // ViewPager 的 Adapter
    private LiveProxy mLiveProxy;               // 包装类,兼容横屏播放与竖屏播放

    private static int PRELOAD_OFFSET = 3;                      // 预加载的位置

    private boolean mIsFirst = true;                            // 是否为第一次加载数据

    private int mPrePageNo = 0;                               // 向上滑动时,当前滑动请求的页数,用于加载上一页数据
    private int mNextPrePageNO = 0;                              // 向下滑动时,当前滑动请求的页数,用于加载下一页数据

    private int mCurrentItem;
    private int mLastPosition = -1;
    private int mPages;             // 总页数
    private int mPageNo;            // 当前请求的页数
    private String mLiveId = "";    // 进入直播间列表时的直播间 id.

    private List<LiveItemBean> mList = new ArrayList<>();

    public static void start(Context context, String liveId, int pageNum) {
        Intent intent = new Intent(context, LiveDetailActivity.class);
        intent.putExtra(ENTER_LIVE_ID, liveId);
        intent.putExtra(PAGE_NUM, pageNum);
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
        mPageNo = getIntent().getIntExtra(PAGE_NUM, 1);

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
        mLastPosition = -1;
        mPrePageNo = 0;
        mNextPrePageNO = 0;

        // 清空所有数据
        mList.clear();

        getListInfo(mPageNo == 0 ? 1 : mPageNo, true);
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
                if (viewGroup.getId() == mCurrentItem && (position == 0) && mCurrentItem != mLastPosition) {
                    View rootView = mLiveProxy.getRootView();
                    if (rootView.getParent() != null && rootView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) (rootView.getParent())).removeView(rootView);
                        mLiveProxy.onRemove();
                    }
                    loadData(viewGroup, mCurrentItem);
                }
            }
        });
    }


    private void loadData(ViewGroup viewGroup, int currentItem) {

        LiveItemBean data = getActualPositionBean(currentItem);
        mLiveProxy.setPortrait(data.isPortrait());
        viewGroup.addView(mLiveProxy.getRootView());
        mLiveProxy.onAdd(data);

        // 第一次进入
        if (mLastPosition == -1) {
            mLastPosition = currentItem;
        } else if (currentItem > mLastPosition) {
            // 向下滑动,当前是倒数第三个,预加载下一页数据
            if (currentItem == mList.size() - PRELOAD_OFFSET && mPages > mNextPrePageNO) {
                getListInfo(mNextPrePageNO + 1, true);
            }
            mLastPosition = currentItem;
        } else {
            // 向上滑动,当前是第三条数据.预加载上一页数据
            if (currentItem == (PRELOAD_OFFSET - 1) && mPrePageNo > 1) {
                getListInfo(mPrePageNo - 1, false);
            }
            mLastPosition = currentItem;
        }
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
            view.findViewById(R.id.iv_back).setOnClickListener(LiveDetailActivity.this);

            // 背景
            LiveItemBean liveItemBean = mList.get(position);
            ImageView ivBg = view.findViewById(R.id.iv_bg);
            Glide.with(LiveDetailActivity.this).load(liveItemBean.getBlurredImg()).centerCrop().into(ivBg);

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


    private LiveItemBean getActualPositionBean(int position) {
        return mList.get(position);
    }


    private void getListInfo(final int pageNo, final boolean isNext) {
        NetworkModule.getLiveList(pageNo, isNext, TAG);
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

    private boolean mIsNotifyData;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveListEvent(Event.LiveListEvent event) {
        if (!TAG.equals(event.tag)) {
            return;
        }

        if (isDestroyed() || isFinishing()) {
            return;
        }

        LiveDataBean dataBean = event.liveDataBean;
        if (dataBean != null && dataBean.isSuccess() && dataBean.getData() != null && dataBean.getData().getContent() != null) {
            List<LiveItemBean> liveList = dataBean.getData().getContent();

            if (event.isNext) {
                mList.addAll(liveList);
            } else {
                for (int i = liveList.size() - 1; i >= 0; i--) {
                    LiveItemBean item = liveList.get(i);
                    mList.add(0, item);
                }
            }


            mPageNo = dataBean.getData().getPageNum();
            mPages = dataBean.getData().getPages();


            // notifyDataSetChanged 会调用 transformPage, 但我们更希望手动控制 View 的添加和移除. 避免频繁的初始化、销毁数据
            mIsNotifyData = true;
            mPagerAdapter.notifyDataSetChanged();
            mIsNotifyData = false;

            if (!event.isNext) {
                int newItem = mCurrentItem + liveList.size();
                // 禁止页面滚动,页面滚动时的效果不好,手动刷新数据
                mViewPager.setCurrentItem(newItem, false);

                View rootView = mLiveProxy.getRootView();
                if (rootView.getParent() != null && rootView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) (rootView.getParent())).removeView(rootView);
                }
                ViewGroup currentView = (ViewGroup) mViewPager.getCurrentView();
                currentView.addView(mLiveProxy.getRootView());
            }

            if (mIsFirst) {
                mIsFirst = false;
                mPrePageNo = mNextPrePageNO = mPageNo;

                int enterPosition = getEnterPosition(mList);
                mViewPager.setCurrentItem(enterPosition, false);

                // 第一次进入页面时候,加载数据
                View currentView = mViewPager.getCurrentView();
                loadData((ViewGroup) currentView, mCurrentItem);

                // 显示引导页面
                showGuide();

                // 预加载数据
                preload(mPageNo, enterPosition);


                // 加载中页面
                mLoadView.hide();
            } else {
                if (event.isNext) {
                    mNextPrePageNO = mPageNo;
                } else {
                    mPrePageNo = mPageNo;
                }
            }
        } else {
            ToastUtil.showLongToast(this, "网络异常");
        }
    }

    /**
     * 用户第一次进入直播间的时候,预加载数据.
     *
     * @param pageNo        当前页数
     * @param enterPosition 观看的直播间在直播列表的位置
     */
    private void preload(int pageNo, int enterPosition) {
        if (mList.size() > 0) {
            if (mPageNo > 1 && mPages == pageNo) {   // 最后一页,预加载上一页的数据
                getListInfo(pageNo - 1, false);
            } else if (pageNo == 1 && mPages > 1) {  // 首页,预加载下一页数据
                getListInfo(pageNo + 1, true);
            } else if (pageNo > 1 && enterPosition < PRELOAD_OFFSET) {  // 中间页面进入,并且进入的位置距离上一页的偏移少于3,预加载上一页数据
                getListInfo(pageNo - 1, false);
            } else if (pageNo > 1 && enterPosition >= mList.size() - PRELOAD_OFFSET) {  // 中间页面进入,并且进入的位置距离下一页的偏移少于3,预加载下一页数据
                getListInfo(pageNo + 1, true);
            }
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