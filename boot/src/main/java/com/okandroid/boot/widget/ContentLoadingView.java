package com.okandroid.boot.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 逐渐显示的一个 loading 视图，默认是隐藏的，需要明确调用 #showLoading 开启逐渐显示的过程. like ContentLoadingProgressBar
 * 该 view 默认会拦截所有 touch 事件
 * Created by idonans on 2017/2/24.
 *
 * @see #showLoading()
 * @see #hideLoading()
 */
public class ContentLoadingView extends FrameLayout {

    public ContentLoadingView(Context context) {
        super(context);
        init();
    }

    public ContentLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContentLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private ProgressBar mProgressBar;

    private void init() {
        mProgressBar = new ProgressBar(getContext());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mProgressBar, layoutParams);

        setBackgroundColor(0xff000000);

        setAlpha(0);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoadingAnimator();
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be
     * hidden until it has been shown for at least a minimum show time. If the
     * progress view was not yet visible, cancels showing the progress view.
     */
    public void hideLoading() {
        hideLoadingImmediately();
    }

    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */
    public void showLoading() {
        showLoadingWithAnimator();
    }

    private Animator mAlphaAnimator;

    private void stopLoadingAnimator() {
        if (mAlphaAnimator != null) {
            mAlphaAnimator.cancel();
            mAlphaAnimator = null;
        }
    }

    private void showLoadingWithAnimator() {
        stopLoadingAnimator();

        setAlpha(0);
        mProgressBar.setVisibility(View.GONE);

        mAlphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 0.5f);
        mAlphaAnimator.setStartDelay(500);
        mAlphaAnimator.setDuration(500L);
        mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mAlphaAnimator == animator) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        mAlphaAnimator.start();
    }

    private void hideLoadingImmediately() {
        stopLoadingAnimator();

        setAlpha(0);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 拦截所有 touch event
        return true;
    }

}
