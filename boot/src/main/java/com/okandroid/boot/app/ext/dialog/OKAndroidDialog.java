package com.okandroid.boot.app.ext.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.backpressed.BackPressedActivity;
import com.okandroid.boot.app.ext.backpressed.BackPressedFragment;
import com.okandroid.boot.lang.ResumedViewClickListener;
import com.okandroid.boot.util.AvailableUtil;

/**
 * Created by idonans on 2017/7/17.
 */

public abstract class OKAndroidDialog extends BackPressedFragment {

    protected Drawable mBackground;

    protected boolean mCancelable = true;
    protected int mEnterAnimation;
    protected int mOuterAnimation;

    public void show(BackPressedActivity activity) {
        int containerId = Window.ID_ANDROID_CONTENT;
        View dialogParent = activity.findViewById(R.id.okandroid_content);
        if (dialogParent != null) {
            containerId = R.id.okandroid_content;
        }

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(containerId, this, null).commitAllowingStateLoss();
    }

    public void setBackground(Drawable background) {
        mBackground = background;

        View backgroundView = getView();
        if (backgroundView != null) {
            backgroundView.setBackground(mBackground);
        }
    }

    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public Drawable getBackground() {
        return mBackground;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    private View mContentView;

    public View getContentView() {
        return mContentView;
    }

    public void setEnterAnimation(int enterAnimation) {
        mEnterAnimation = enterAnimation;
    }

    public void setOuterAnimation(int outerAnimation) {
        mOuterAnimation = outerAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            new IllegalAccessError("container is null").printStackTrace();
            return null;
        }

        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return null;
        }

        Activity activity = getActivity();
        if (activity == null) {
            new IllegalAccessError("activity is null").printStackTrace();
            return null;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            new IllegalAccessError("activity is not available").printStackTrace();
            return null;
        }

        return onCreateViewSafety(activity, inflater, container);
    }

    protected View onCreateViewSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        FrameLayout view = new FrameLayout(activity);
        view.setId(R.id.okandroid_dynamic_content);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new ResumedViewClickListener(OKAndroidDialog.this) {
            @Override
            public void onClick(View v, ResumedViewClickListener listener) {
                onBackgroundClick();
            }
        });

        mContentView = onCreateContentView(activity, inflater, view);
        if (mEnterAnimation > 0) {
            mContentView.startAnimation(AnimationUtils.loadAnimation(activity, mEnterAnimation));
        }

        view.addView(mContentView);

        view.setBackground(mBackground);
        return view;
    }

    protected abstract View onCreateContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container);

    protected void onBackgroundClick() {
        if (isCancelable()) {
            dismiss();
        }
    }

    public void dismiss() {
        if (mContentView != null && mOuterAnimation > 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), mOuterAnimation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismissNow();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mContentView.startAnimation(animation);
        } else {
            dismissNow();
        }
    }

    private void dismissNow() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public boolean onBackPressed() {
        if (isCancelable()) {
            dismiss();
        }
        return true;
    }

}
