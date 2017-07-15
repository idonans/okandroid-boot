package com.okandroid.boot.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.okandroid.boot.R;
import com.okandroid.boot.lang.ClassName;
import com.okandroid.boot.lang.WeakAvailable;
import com.okandroid.boot.util.ViewUtil;

import java.lang.reflect.Field;

/**
 * Created by idonans on 2017/5/15.
 */

public abstract class ContentDialog extends PopupWindow {

    private final String CLASS_NAME = ClassName.valueOf(this);

    protected Activity mActivity;
    protected View mActivityContentView;
    protected LayoutInflater mInflater;

    public ContentDialog(@NonNull Activity activity) {
        mActivity = activity;

        mActivityContentView = ViewUtil.findViewByID(mActivity, R.id.okandroid_content);
        if (mActivityContentView == null) {
            mActivityContentView = ViewUtil.findViewByID(mActivity, Window.ID_ANDROID_CONTENT);
        }

        mInflater = mActivity.getLayoutInflater();
        setClippingEnabled(false);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setAnimationStyle(0);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.v(CLASS_NAME, "onDismiss");
            }
        });

        addWeakLayoutForActivityContentView();
    }

    protected boolean mDimBackground;
    protected float mDimAmount;

    public void setDimAmount(float dimAmount) {
        mDimAmount = dimAmount;
    }

    public void setDimBackground(boolean dimBackground) {
        mDimBackground = dimBackground;
    }

    @CallSuper
    public void onCreate() {
    }

    @Override
    public void dismiss() {
        super.dismiss();

        removeWeakLayoutForActivityContentView();
    }

    protected int mTargetGravity = Gravity.FILL;
    protected int mTargetOffsetX = 0;
    protected int mTargetOffsetY = 0;
    protected int mTargetWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    protected int mTargetHeight = ViewGroup.LayoutParams.MATCH_PARENT;

    public void setContentView(int layoutResId) {
        FrameLayout tmpParent = new FrameLayout(mActivity);
        View contentView = mInflater.inflate(layoutResId, tmpParent, false);
        FrameLayout.LayoutParams contentParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
        mTargetGravity = contentParams.gravity;
        mTargetOffsetX = contentParams.leftMargin;
        mTargetOffsetY = contentParams.topMargin;
        mTargetWidth = contentParams.width;
        mTargetHeight = contentParams.height;

        setContentView(contentView);
        setWidth(mTargetWidth);
        setHeight(mTargetHeight);
    }

    public void show() {
        onCreate();
        showAtLocation(mActivity.getWindow().getDecorView(), mTargetGravity, mTargetOffsetX, mTargetOffsetY);
        applyDim();
    }

    @CheckResult
    public View getDecorView() {
        try {
            Field f;
            try {
                f = PopupWindow.class.getDeclaredField("mDecorView");
            } catch (Throwable e) {
                f = PopupWindow.class.getDeclaredField("mPopupView");
            }
            f.setAccessible(true);
            return (View) f.get(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void applyDim() {
        if (!mDimBackground) {
            return;
        }

        View decorView = getDecorView();
        if (decorView == null) {
            Log.e(CLASS_NAME, "decor view not found");
            return;
        }

        try {
            decorView.setFitsSystemWindows(false);
            WindowManager.LayoutParams attrs = (WindowManager.LayoutParams) decorView.getLayoutParams();
            attrs.flags = ((WindowManager.LayoutParams) mActivity.getWindow().getDecorView().getLayoutParams()).flags;
            attrs.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            attrs.dimAmount = mDimAmount;
            mActivity.getWindowManager().updateViewLayout(decorView, attrs);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void addWeakLayoutForActivityContentView() {
        if (mActivityContentView != null) {
            mActivityContentView.addOnLayoutChangeListener(mWeakLayoutChangedListener);
        }
    }

    private void removeWeakLayoutForActivityContentView() {
        if (mActivityContentView != null) {
            mActivityContentView.removeOnLayoutChangeListener(mWeakLayoutChangedListener);
        }
    }

    private View.OnLayoutChangeListener mActivityContentViewLayoutChangedListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (mActivityContentView != null) {
                onActivityContentViewLayoutChanged(mActivityContentView);
            }
        }
    };

    private final WeakLayoutChangedListener mWeakLayoutChangedListener = new WeakLayoutChangedListener(mActivityContentViewLayoutChangedListener);

    public void onActivityContentViewLayoutChanged(@NonNull View contentView) {
        Log.v(CLASS_NAME, "onActivityContentViewLayoutChanged");
        View decorView = getDecorView();
        if (decorView == null) {
            Log.e(CLASS_NAME, "decor view not found");
            return;
        }

        decorView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(), contentView.getPaddingBottom());
    }

    public static class WeakLayoutChangedListener extends WeakAvailable implements View.OnLayoutChangeListener {

        public WeakLayoutChangedListener(View.OnLayoutChangeListener listener) {
            super(listener);
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            View.OnLayoutChangeListener listener = (View.OnLayoutChangeListener) getObject();
            if (listener != null) {
                listener.onLayoutChange(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom);
            }
        }
    }

}
