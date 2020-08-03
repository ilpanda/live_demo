package com.ilpanda.live_demo.wiget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.utils.ScreenUtil;

public class LiveExitConfirmDialog extends BaseDialog implements View.OnClickListener{


    private Callback mCallback;

    private Context mContext;

    private TextView mTvTitle;

    private TextView mTvConfirm;

    private ImageView mIvAvatar;

    public LiveExitConfirmDialog(Context context, String url) {
        super(context);
        this.mContext = context;
        Window window = getWindow();
        if (window == null) {
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_portrait_live_exit_dialog);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.findViewById(R.id.tv_cancel).setOnClickListener(this);
        this.findViewById(R.id.tv_confirm).setOnClickListener(this);

        mTvTitle = findViewById(R.id.tv_title);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mIvAvatar = findViewById(R.id.iv_avatar);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.face)
                .error(R.drawable.face)
                .circleCrop()
                .override(ScreenUtil.dipToPx(mContext, 60))
                .dontAnimate();
        Glide.with(mContext).load(url).apply(options).into(mIvAvatar);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        dismiss();

        if (id == R.id.tv_cancel) {
            if (mCallback != null) {
                mCallback.onClickCancel();
            }
        } else if (id == R.id.tv_confirm) {
            if (mCallback != null) {
                mCallback.onClickConfirm();
            }
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {

        void onClickConfirm();

        void onClickCancel();
    }


}
