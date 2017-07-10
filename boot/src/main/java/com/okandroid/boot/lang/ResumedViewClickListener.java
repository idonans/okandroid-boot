package com.okandroid.boot.lang;

import android.app.Dialog;
import android.view.View;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.AvailableUtil;

/**
 * Created by idonans on 2017/6/9.
 */

public abstract class ResumedViewClickListener implements View.OnClickListener {

    private final OKAndroidActivity mActivity;
    private final OKAndroidFragment mFragment;

    private final Dialog mDialog;

    public ResumedViewClickListener(OKAndroidActivity activity) {
        mActivity = activity;
        mFragment = null;
        mDialog = null;
    }

    public ResumedViewClickListener(OKAndroidFragment fragment) {
        mActivity = null;
        mFragment = fragment;
        mDialog = null;
    }

    public ResumedViewClickListener(Dialog dialog) {
        mActivity = null;
        mFragment = null;
        mDialog = dialog;
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
                }
            }
        });
    }

    public abstract void onClick(View v, ResumedViewClickListener listener);

}
