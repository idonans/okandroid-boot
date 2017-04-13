package com.okandroid.boot.widget.ptr;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.DimenUtil;

/**
 * Created by idonans on 2017/4/12.
 */

public class PtrHeader extends FrameLayout implements PtrLayout.HeaderView {

    public PtrHeader(@NonNull Context context) {
        super(context);
        init();
    }

    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "PtrHeader";

    /**
     * 空闲状态
     */
    private static final int STATUS_IDLE = 0;
    /**
     * 刷新状态中
     */
    private static final int STATUS_REFRESH = 1;

    private int mRefreshStatus = STATUS_IDLE;
    private int mCoreHeight;
    private int mMaxHeight;

    private TextView mTextView;

    private void init() {
        Context context = getContext();

        mCoreHeight = getCoreHeight();
        mMaxHeight = getMaxHeight();

        if (mCoreHeight <= 0 || mMaxHeight < mCoreHeight) {
            throw new IllegalArgumentException(TAG + " core height or max height invalid [" + mCoreHeight + ", " + mMaxHeight + "]");
        }

        TextView textView = new TextView(context);
        textView.setText("ptr header");
        textView.setTextColor(Color.RED);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        addView(textView);

        mTextView = textView;
    }

    /**
     * 触发下拉刷新的高度
     *
     * @return
     */
    protected int getCoreHeight() {
        return DimenUtil.dp2px(100);
    }

    /**
     * 最大展示高度
     *
     * @return
     */
    public int getMaxHeight() {
        return DimenUtil.dp2px(200);
    }

    @Override
    public boolean isStatusBusy() {
        return mRefreshStatus != STATUS_IDLE;
    }

    @Override
    public void applyOffsetYDiff(float yDiff, View target) {
        if (mRefreshStatus != STATUS_IDLE) {
            Log.d(TAG + " applyOffsetYDiff refresh status not idle " + mRefreshStatus);
            return;
        }

        clearAnyOldAnimation();

        yDiff = adjustYDiff(yDiff);

        float translationY = getTranslationY();
        translationY += yDiff;
        if (translationY < 0) {
            translationY = 0;
        }
        if (translationY > mMaxHeight) {
            translationY = mMaxHeight;
        }
        setTranslationY(translationY);

        target.setTranslationY(translationY);

        // TODO
        if (translationY < mCoreHeight) {
            mTextView.setText("下拉刷新");
        } else {
            mTextView.setText("松开刷新");
        }
    }

    protected float adjustYDiff(float yDiff) {
        float translationY = getTranslationY();
        if (translationY < mCoreHeight) {
            return yDiff;
        } else {
            return yDiff * Math.max(0.4f, 1 - (translationY - mCoreHeight) / (mMaxHeight - mCoreHeight));
        }
    }

    @Override
    public void finishOffsetY(boolean cancel, View target) {
        float translationY = getTranslationY();
        if (!cancel && translationY >= mCoreHeight) {
            // 触发刷新
            mRefreshStatus = STATUS_REFRESH;
            animateToRefresh(target);
        } else {
            mRefreshStatus = STATUS_IDLE;
            animateToStart(target);
        }
    }

    private void clearAnyOldAnimation() {
        if (mToRefreshAnimator != null) {
            mToRefreshAnimator.cancel();
            mToRefreshAnimator = null;
        }
        if (mToStartAnimator != null) {
            mToStartAnimator.cancel();
            mToStartAnimator = null;
        }
    }

    private ValueAnimator mToRefreshAnimator;

    private void animateToRefresh(final View target) {
        clearAnyOldAnimation();

        final float startTranslationY = getTranslationY();
        final float targetTranslationY = mCoreHeight;
        final long dur;
        if (Math.abs(startTranslationY - targetTranslationY) > mCoreHeight / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationY, targetTranslationY);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                setTranslationY(translationY);
                target.setTranslationY(translationY);
            }
        });
        animator.start();

        mToRefreshAnimator = animator;
    }

    private ValueAnimator mToStartAnimator;

    private void animateToStart(final View target) {
        clearAnyOldAnimation();

        final float startTranslationY = getTranslationY();
        final float targetTranslationY = 0;
        final long dur;
        if (Math.abs(startTranslationY - targetTranslationY) > mCoreHeight / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationY, targetTranslationY);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                setTranslationY(translationY);
                target.setTranslationY(translationY);
            }
        });
        animator.start();

        mToStartAnimator = animator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
