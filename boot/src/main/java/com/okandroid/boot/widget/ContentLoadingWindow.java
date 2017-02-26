package com.okandroid.boot.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by idonans on 2017/2/26.
 */

public class ContentLoadingWindow {

    private final Activity mActivity;
    private final Context mContext;
    private final WindowManager mWindowManager;
    private ContentLoadingView mContentView;

    private boolean mCancelable;

    public ContentLoadingWindow(Activity activity) {
        mActivity = activity;
        mContext = activity;
        mWindowManager = mActivity.getWindowManager();
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public void show() {
        dismiss();

        mContentView = new ContentLoadingView(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (event.getAction()) {
                    case KeyEvent.ACTION_UP:
                        if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                            onBackPressed();
                            return true;
                        }
                        break;
                }

                return true;
            }
        };

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.token = mActivity.findViewById(Window.ID_ANDROID_CONTENT).getWindowToken();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        mWindowManager.addView(mContentView, params);
        mContentView.showLoading();
    }

    public boolean onBackPressed() {
        if (mCancelable) {
            dismiss();
            return true;
        }
        return false;
    }

    public void dismiss() {
        if (mContentView != null) {
            mContentView.hideLoading();
            mWindowManager.removeViewImmediate(mContentView);
            mContentView = null;
        }
    }

}
