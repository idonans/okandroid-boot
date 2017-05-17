package com.okandroid.boot.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
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

    private long mAnimationDelay = 500L;
    private long mAnimationDuration = 500L;
    private ProgressBar mProgressBar;

    private void init() {
        mProgressBar = new ProgressBar(getContext());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mProgressBar, layoutParams);

        setBackgroundColor(0xff000000);

        stopLoadingAnimation();
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoadingAnimation();
    }

    public void hideLoading() {
        stopLoadingAnimation();
    }

    public void showLoading() {
        startLoadingAnimation();
    }

    private void stopLoadingAnimation() {
        animate().cancel();
        setAlpha(0);
        mProgressBar.setVisibility(View.GONE);
    }

    private void startLoadingAnimation() {
        animate().alpha(0.5f)
                .setDuration(mAnimationDuration)
                .setStartDelay(mAnimationDelay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setAnimationDelay(long animationDelay) {
        mAnimationDelay = animationDelay;
    }

}
