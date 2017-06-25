package com.okandroid.boot.widget.r2lr;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.okandroid.boot.R;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.DimenUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.boot.widget.ptr.ArrowDrawable;
import com.okandroid.boot.widget.ptr.PtrLayout;

/**
 * Created by idonans on 2017/6/25.
 */

public class R2lrHeader extends FrameLayout implements R2lrLayout.HeaderView {

    public R2lrHeader(@NonNull Context context) {
        super(context);
        init();
    }

    public R2lrHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public R2lrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public R2lrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "R2lrHeader";

    /**
     * 空闲状态
     */
    private static final int STATUS_IDLE = 0;
    /**
     * 刷新状态中
     */
    private static final int STATUS_REFRESH = 1;

    private int mRefreshStatus = STATUS_IDLE;
    private int mCoreWidth; // 触发左拉刷新的宽度
    private int mMaxWidth; // 最大展示宽度

    private StatusHeaderView mStatusHeaderView;

    private void init() {
        Context context = getContext();

        mStatusHeaderView = createStatusHeaderView();

        mCoreWidth = mStatusHeaderView.mCoreWidth;
        mMaxWidth = mStatusHeaderView.mMaxWidth;

        if (mCoreWidth <= 0 || mMaxWidth < mCoreWidth) {
            throw new IllegalArgumentException(TAG + " core width or max width invalid [" + mCoreWidth + ", " + mMaxWidth + "]");
        }

        View contentView = mStatusHeaderView.createView(LayoutInflater.from(context), this);
        if (contentView != null) {
            addView(contentView);
        }
    }

    public StatusHeaderView createStatusHeaderView() {
        return new DefaultStatusHeaderView(this, DimenUtil.dp2px(48), DimenUtil.dp2px(200));
    }

    @Override
    public boolean isStatusBusy() {
        return mRefreshStatus != STATUS_IDLE;
    }

    @Override
    public float applyOffsetXDiff(float xDiff, View target) {
        if (mRefreshStatus != STATUS_IDLE) {
            Log.d(TAG + " applyOffsetXDiff refresh status not idle " + mRefreshStatus);
            return 0f;
        }

        float oldXDiff = xDiff;

        clearAnyOldAnimation();

        xDiff = adjustXDiff(xDiff);

        float translationX = getTranslationX();
        float oldTranslationX = translationX;

        translationX += xDiff;
        if (translationX > 0) {
            translationX = 0;
        }
        if (translationX < -mMaxWidth) {
            translationX = -mMaxWidth;
        }
        setTranslationX(translationX);

        target.setTranslationX(translationX);

        mStatusHeaderView.updateView(translationX, false, false);

        if (oldTranslationX == translationX) {
            // 如果应用滑动之后没有产生实际位置偏移，则认为此次没有消耗 xDiff
            return 0f;
        }
        return oldXDiff;
    }

    protected float adjustXDiff(float xDiff) {
        float translationX = getTranslationX();
        if (-translationX < mCoreWidth) {
            return xDiff;
        } else {
            return xDiff * Math.max(0.4f, 1 - (-translationX - mCoreWidth) / (mMaxWidth - mCoreWidth));
        }
    }

    @Override
    public void finishOffsetX(boolean cancel, View target) {
        if (mRefreshStatus != STATUS_IDLE) {
            Log.d(TAG + " finishOffsetX refresh status not idle " + mRefreshStatus);
            return;
        }

        float translationX = getTranslationX();

        if (!cancel && -translationX >= mCoreWidth) {
            // 触发刷新
            setRefreshInternal(true, true, target);
        } else {
            setRefreshInternal(false, false, target);
        }
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notifyRefresh, View target) {
        setRefreshInternal(refreshing, notifyRefresh, target);
    }

    private void setRefreshInternal(boolean refresh, boolean notifyRefresh, View target) {
        if (!refresh) {
            mRefreshStatus = STATUS_IDLE;
            animateToStart(target);
        } else {
            if (mRefreshStatus == STATUS_REFRESH) {
                notifyRefresh = false;
            }

            mRefreshStatus = STATUS_REFRESH;
            animateToRefresh(target, notifyRefresh);
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

    private R2lrLayout.OnRefreshListener mOnRefreshListener;

    @Override
    public void setOnRefreshListener(R2lrLayout.OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    private void animateToRefresh(final View target, final boolean notifyRefresh) {
        clearAnyOldAnimation();

        final float startTranslationX = getTranslationX();
        final float targetTranslationX = -mCoreWidth;
        final long dur;
        if (Math.abs(startTranslationX - targetTranslationX) > mCoreWidth / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        mStatusHeaderView.updateView(startTranslationX, true, true);

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationX, targetTranslationX);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (float) animation.getAnimatedValue();
                setTranslationX(translationX);
                target.setTranslationX(translationX);

                mStatusHeaderView.updateView(translationX, true, true);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (notifyRefresh) {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();

        mToRefreshAnimator = animator;
    }

    private ValueAnimator mToStartAnimator;

    private void animateToStart(final View target) {
        clearAnyOldAnimation();

        final float startTranslationX = getTranslationX();
        final float targetTranslationX = 0;
        final long dur;
        if (Math.abs(startTranslationX - targetTranslationX) > mCoreWidth / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        mStatusHeaderView.updateView(startTranslationX, false, true);

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationX, targetTranslationX);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (float) animation.getAnimatedValue();
                setTranslationX(translationX);
                target.setTranslationX(translationX);

                mStatusHeaderView.updateView(translationX, false, true);
            }
        });
        animator.start();

        mToStartAnimator = animator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnyOldAnimation();
    }

    public static abstract class StatusHeaderView {

        protected final LayoutInflater mInflater;

        protected final int mCoreWidth;
        protected final int mMaxWidth;

        protected StatusHeaderView(ViewGroup parent, int coreWidth, int maxWidth) {
            mInflater = LayoutInflater.from(parent.getContext());
            mCoreWidth = coreWidth;
            mMaxWidth = maxWidth;
        }

        /**
         * 创建初始内容
         *
         * @param inflater
         * @param parent
         * @return
         */
        protected abstract View createView(LayoutInflater inflater, ViewGroup parent);

        /**
         * 更新内容
         *
         * @param translationX 左拉距离
         * @param isRefreshing 当前是否处于正在刷新的状态
         * @param inAnimation  是否处于动画中
         */
        protected abstract void updateView(float translationX, boolean isRefreshing, boolean inAnimation);

    }

    public static class DefaultStatusHeaderView extends StatusHeaderView {

        private ProgressBar mProgressBar;

        protected DefaultStatusHeaderView(ViewGroup parent, int coreHeight, int maxHeight) {
            super(parent, coreHeight, maxHeight);
        }

        @Override
        protected View createView(LayoutInflater inflater, ViewGroup parent) {
            View content = inflater.inflate(R.layout.okandroid_r2lr_header_default_view, parent, false);
            mProgressBar = ViewUtil.findViewByID(content, R.id.progress_bar);

            ArrowDrawable progressDrawable = new ArrowDrawable();
            progressDrawable.setPadding(DimenUtil.dp2px(5));
            mProgressBar.setProgressDrawable(progressDrawable);

            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(0);

            return content;
        }

        @Override
        protected void updateView(float translationX, boolean isRefreshing, boolean inAnimation) {
            if (isRefreshing) {
                mProgressBar.setIndeterminate(true);
                return;
            }

            if (inAnimation) {
                return;
            }

            mProgressBar.setIndeterminate(false);

            int progress;
            if (translationX >= 0) {
                progress = 0;
            } else if (-translationX < mCoreWidth) {
                progress = (int) (-translationX / mCoreWidth * 100);
            } else {
                progress = 100;
            }

            mProgressBar.setProgress(progress);
        }

    }

}
