package com.okandroid.boot.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import com.okandroid.boot.R;
import com.okandroid.boot.util.ViewUtil;

/**
 * Created by idonans on 2017/5/15.
 */

public class ContentLoadingDialog extends ContentDialog {

    public ContentLoadingDialog(@NonNull Context context) {
        super(context);
    }

    public ContentLoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private long mAnimationDelay = 500L;
    private long mAnimationDuration = 500L;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.okandroid_content_loading_dialog);

        View rootView = getWindow().getDecorView();
        mProgressBar = ViewUtil.findViewByID(rootView, R.id.progress_bar);

        stopLoadingAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoadingAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLoadingAnimation();
    }

    private void startLoadingAnimation() {
        mProgressBar.animate()
                .alpha(1f)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(mAnimationDuration)
                .setStartDelay(mAnimationDelay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void stopLoadingAnimation() {
        mProgressBar.animate().cancel();
        mProgressBar.setAlpha(0f);
        mProgressBar.setScaleX(1f);
        mProgressBar.setScaleY(1f);
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setAnimationDelay(long animationDelay) {
        mAnimationDelay = animationDelay;
    }

}
