package com.ilpanda.live_demo.live.msg;


import android.animation.AnimatorSet;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.bean.TCChatEntity;
import com.ilpanda.live_demo.constants.LiveConstants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Module:   TCChatMsgListAdapter
 * <p>
 * Function: 消息列表的 Adapter。
 */
public class TCChatMsgListAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private static String TAG = TCChatMsgListAdapter.class.getSimpleName();
    private static final int ITEM_COUNT = 7;
    private List<TCChatEntity> mList;
    private int mTotalHeight;
    private Context mContext;
    private ListView mListView;
    private ArrayList<TCChatEntity> mArray = new ArrayList<>();
    private boolean isBottom = true;
    private boolean isUserScroll = false;

    class AnimatorInfo {
        long createTime;

        public AnimatorInfo(long uTime) {
            createTime = uTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }

    private static final int MAXANIMATORCOUNT = 8;
    private static final int MAXLISTVIEWHEIGHT = 450;
    private static final int ANIMATORDURING = 8000;
    private static final int MAXITEMCOUNT = 50;
    private LinkedList<AnimatorSet> mAnimatorSetList;
    private LinkedList<AnimatorInfo> mAnimatorInfoList;
    private boolean mScrolling = false;

    private OnItemClickListener mOnItemClickListener;


    public TCChatMsgListAdapter(Context context, ListView listview, List<TCChatEntity> objects) {
        this.mContext = context;
        mListView = listview;
        this.mList = objects;

        mAnimatorSetList = new LinkedList<>();
        mAnimatorInfoList = new LinkedList<>();

        mListView.setOnScrollListener(this);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_msg, null);
            holder.sendContext = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(R.id.tag_first, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        final TCChatEntity item = mList.get(position);

        SpannableString spanString = null;
        if (item.getType() == LiveConstants.WARN) {// 看进入直播间时,警告信息
            spanString = new SpannableString(item.getContent());
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.danmuColor));
        } else if (item.getType() == LiveConstants.TEXT_TYPE) {// 文本信息
            spanString = new SpannableString(item.getSenderName() + item.getContent());
            spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.danmuColor)),
                    0, item.getSenderName().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            spanString = new SpannableString(item.getContent());
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.danmuColor));
        }

        holder.sendContext.setText(spanString);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onMsgItemClick(item);
                }
            }
        });

        return convertView;
    }


    private static class ViewHolder {
        public TextView sendContext;
    }


    /**
     * 重载notifyDataSetChanged方法实现渐消动画并动态调整ListView高度
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        // 自动滚动到底部
        if (!isUserScroll) {
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(mListView.getCount() - 1);
                }
            });
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
//                Log.d("ListView", "##### SCROLL_STATE_FLING ######");
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
//                if (mBLiveAnimator) {
//                    // 开始滚动时停止所有属性动画
//                    stopAnimator();
//                    resetAlpha();
//                }
                mScrolling = true;
                isUserScroll = true;
//                Log.d("ListView", "##### SCROLL_STATE_TOUCH_SCROLL ######");

                break;
            case SCROLL_STATE_IDLE:
                mScrolling = false;
//                Log.d("ListView", "##### SCROLL_STATE_IDLE ######");

//                if (mBLiveAnimator) {
//                    // 停止滚动时播放渐消动画
//                    playDisappearAnimator();
//                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            View firstVisibleItemView = mListView.getChildAt(0);
            if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                isBottom = true;
//                Log.d("ListView", "##### 滚动到顶部 #####");
            }
        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            View lastVisibleItemView = mListView.getChildAt(mListView.getChildCount() - 1);
            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mListView.getHeight()) {
                isBottom = true;
                isUserScroll = false;
//                Log.d("ListView", "##### 滚动到底部 ######");
            }
        }
    }

    public interface OnItemClickListener {
        void onMsgItemClick(TCChatEntity item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
