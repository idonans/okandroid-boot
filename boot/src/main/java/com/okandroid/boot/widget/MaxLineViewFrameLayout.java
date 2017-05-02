package com.okandroid.boot.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by idonans on 2017/5/2.
 */

public class MaxLineViewFrameLayout extends FrameLayout implements MaxLineViewHelper.MaxLineView {

    public MaxLineViewFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MaxLineViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxLineViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaxLineViewFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private MaxLineViewHelper.OnItemViewMeasureListener mOnItemViewMeasureListener;

    @Override
    public void setOnItemViewMeasureListener(MaxLineViewHelper.OnItemViewMeasureListener listener) {
        mOnItemViewMeasureListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mOnItemViewMeasureListener != null) {
            mOnItemViewMeasureListener.onItemViewMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("WrongCall")
    @Override
    public void callOnItemViewMeasureSuper(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}