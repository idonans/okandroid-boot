package com.okandroid.boot.lang;

import android.app.Dialog;
import android.view.View;
import android.widget.PopupWindow;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.AvailableUtil;

/**
 * Created by idonans on 2017/6/9.
 */

public abstract class ResumedViewClickListener implements View.OnClickListener {

    private OKAndroidActivity mActivity;
    private OKAndroidFragment mFragment;
    private Dialog mDialog;
    private PopupWindow mPopupWindow;

    public ResumedViewClickListener(OKAndroidActivity activity) {
        mActivity = activity;
    }

    public ResumedViewClickListener(OKAndroidFragment fragment) {
        mFragment = fragment;
    }

    public ResumedViewClickListener(Dialog dialog) {
        mDialog = dialog;
    }

    public ResumedViewClickListener(PopupWindow popupWindow) {
        mPopupWindow = popupWindow;
    }

    @Override
    public final void onClick(final View v) {
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null) {
                    if (AvailableUtil.isAvailable(mActivity)
                            && mActivity.isAppCompatResumed()) {
                        onClick(v, ResumedViewClickListener.this);
                    }
                } else if (mFragment != null) {
                    if (AvailableUtil.isAvailable(mFragment)
                            && mFragment.isAppCompatResumed()) {
                        onClick(v, ResumedViewClickListener.this);
                    }
                } else if (mDialog != null) {
                    if (mDialog.isShowing()) {
                        onClick(v, ResumedViewClickListener.this);
                    }
                } else if (mPopupWindow != null) {
                    if (mPopupWindow.isShowing()) {
                        onClick(v, ResumedViewClickListener.this);
                    }
                }
            }
        });
    }

    public abstract void onClick(View v, ResumedViewClickListener listener);

}
