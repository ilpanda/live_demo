package com.ilpanda.live_demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.bean.LiveDataBean;
import com.ilpanda.live_demo.bean.LiveItemBean;
import com.ilpanda.live_demo.bean.LiveListBean;
import com.ilpanda.live_demo.module.NetworkModule;
import com.ilpanda.live_demo.module.event.Event;
import com.ilpanda.live_demo.utils.ScreenUtil;
import com.ilpanda.live_demo.utils.ToastUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LiveListActivity extends FragmentActivity {


    private static final String TAG = LiveListActivity.class.getSimpleName();

    public static final String TYPE = "type";

    public RecyclerView mRecyclerView;
    public RefreshLayout mRefreshLayout;

    private List<LiveItemBean> mList = new ArrayList<>();
    private LiveListAdapter mLiveListAdapter;

    private static final int COUNT = 10;

    private int mPage = 1;


    public static void start(Context context, int type) {
        Intent intent = new Intent(context, LiveListActivity.class);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);
        EventBus.getDefault().register(this);

        int type = getIntent().getIntExtra(TYPE, 1);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mLiveListAdapter = new LiveListAdapter(this, type, mList);
        mRecyclerView.setAdapter(mLiveListAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        NetworkModule.getLiveList(1, 10, TAG);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                NetworkModule.getLiveList(1, COUNT, TAG);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                NetworkModule.getLiveList(mPage + 1, COUNT, TAG);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private static class LiveListAdapter extends RecyclerView.Adapter<LiveListViewHolder> {

        private List<LiveItemBean> datas;

        private Activity context;

        private int type;

        public LiveListAdapter(Activity context, int type, List<LiveItemBean> list) {
            this.datas = list;
            this.context = context;
            this.type = type;
        }


        @Override
        public LiveListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_live, parent, false);
            return new LiveListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LiveListViewHolder holder, int position) {
            LiveItemBean liveItemBean = this.datas.get(position);

            int corner = ScreenUtil.dipToPx(context, 5);
            Glide.with(context).load(liveItemBean.getCover()).transform(new CenterCrop(), new RoundedCorners(corner)).into(holder.ivCover);

            Glide.with(context).load(liveItemBean.getUser().getAvatar()).centerCrop().circleCrop().into(holder.ivAvatar);

            holder.tvNick.setText(liveItemBean.getUser().getNick());

            holder.tvTitle.setText(liveItemBean.getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) {
                        LiveSlideDetailActivity.start(context, liveItemBean.getLiveId());
                    } else {
                        LiveDetailActivity.start(context, liveItemBean.getLiveId(), liveItemBean.getPageNum());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private static class LiveListViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvNick;
        ImageView ivAvatar;
        ImageView ivCover;

        public LiveListViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNick = itemView.findViewById(R.id.tv_nick);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivCover = itemView.findViewById(R.id.iv_cover);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveListEvent(Event.LiveListEvent event) {
        if (!TAG.equals(event.tag)) {
            return;
        }

        if (isDestroyed() || isFinishing()) {
            return;
        }

        mRefreshLayout.finishRefresh();

        LiveDataBean dataBean = event.liveDataBean;
        if (dataBean != null && dataBean.isSuccess() && dataBean.getData() != null && dataBean.getData().getContent() != null) {
            LiveListBean data = dataBean.getData();

            List<LiveItemBean> liveList = data.getContent();

            if (data.getPageNum() == 1) {
                mList.clear();
            }

            mPage = data.getPageNum();

            if (data.getPageNum() == data.getPages()) {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            } else {
                mRefreshLayout.finishLoadMore();
            }

            mList.addAll(liveList);
            mLiveListAdapter.notifyDataSetChanged();

        } else {
            ToastUtil.showLongToast(this, "网络异常");
        }


    }

}
