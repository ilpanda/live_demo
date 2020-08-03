package com.ilpanda.live_demo.live.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ilpanda.live_demo.R;
import com.ilpanda.live_demo.utils.ToastUtil;

import java.util.Locale;


public class TCInputTextMsgDialog extends Dialog {

    private static final String TAG = TCInputTextMsgDialog.class.getSimpleName();

    private EditText mEtMessage;

    private Activity mContext;

    private InputMethodManager mInputMethodManager;

    private RelativeLayout mRlyRootView;

    private int mLastDiff = 0;

    private int mMaxNum = 32;

    private OnTextSendListener mOnTextSendListener;

    public TCInputTextMsgDialog(Activity context, int theme) {
        super(context, theme);

        setContentView(R.layout.dialog_input_text);

        mContext = context;

        mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        mEtMessage = (EditText) findViewById(R.id.et_input_message);

        mEtMessage.setInputType(InputType.TYPE_CLASS_TEXT);

        // 修改下划线颜色
        mEtMessage.getBackground().setColorFilter(mContext.getResources().getColor(R.color.transparent), PorterDuff.Mode.CLEAR);


        mEtMessage.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = mEtMessage.getSelectionStart();
                selectionEnd = mEtMessage.getSelectionEnd();
                if (temp.length() > mMaxNum) {
                    ToastUtil.showShortToast(mContext,String.format(Locale.getDefault(), "发言最多支持%d个字", mMaxNum));
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    mEtMessage.setText(s);
                    mEtMessage.setSelection(tempSelection);
                }
            }
        });


        mEtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        if (mEtMessage.getText().length() > 0) {
                            sendMessage();
                            mInputMethodManager.hideSoftInputFromWindow(mEtMessage.getWindowToken(), 0);
                            dismiss();
                        } else {
                            ToastUtil.showLongToast(mContext, "消息不能为空");
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        mRlyRootView = (RelativeLayout) findViewById(R.id.rl_outside_view);
        mRlyRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.ll_input_view) dismiss();
            }
        });

        final LinearLayout llInputView = (LinearLayout) findViewById(R.id.ll_input_view);
        llInputView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6,
                                       int i7) {
                Rect r = new Rect();
                //获取当前界面可视部分
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (heightDifference <= 0 && mLastDiff > 0) {
                    mRlyRootView.setVisibility(View.GONE);
                    dismiss();
                }
                mLastDiff = heightDifference;
            }
        });

        llInputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputMethodManager.hideSoftInputFromWindow(mEtMessage.getWindowToken(), 0);
                dismiss();
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mRlyRootView.setVisibility(View.GONE);
                    dismiss();
                    return true;
                }
                return false;
            }
        });


    }

    public void setOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        mRlyRootView.setVisibility(View.VISIBLE);
        super.show();
    }


    private void sendMessage() {
        String msg = mEtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.showShortToast(mContext, "消息不能为空");
            return;
        }

        if (msg.length() > mMaxNum) {
            ToastUtil.showShortToast(mContext,String.format(Locale.getDefault(), "发言最多支持%d个字", mMaxNum));
            return;
        }

        if (mOnTextSendListener != null) {
            mOnTextSendListener.onTextSend(msg);
        }

        mInputMethodManager.showSoftInput(mEtMessage, InputMethodManager.SHOW_FORCED);
        mInputMethodManager.hideSoftInputFromWindow(mEtMessage.getWindowToken(), 0);
        mEtMessage.setText("");

        dismiss();
    }

    public interface OnTextSendListener {

        void onTextSend(String msg);

    }
}
