package com.okandroid.boot.widget.ptr;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
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
import com.okandroid.boot.util.FileUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;

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
    private int mCoreHeight; // 触发下拉刷新的高度
    private int mMaxHeight; // 最大展示高度

    private StatusHeaderView mStatusHeaderView;

    private void init() {
        Context context = getContext();

        mStatusHeaderView = createStatusHeaderView();

        mCoreHeight = mStatusHeaderView.mCoreHeight;
        mMaxHeight = mStatusHeaderView.mMaxHeight;

        if (mCoreHeight <= 0 || mMaxHeight < mCoreHeight) {
            throw new IllegalArgumentException(TAG + " core height or max height invalid [" + mCoreHeight + ", " + mMaxHeight + "]");
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
    public float applyOffsetYDiff(float yDiff, View target) {
        if (mRefreshStatus != STATUS_IDLE) {
            Log.d(TAG + " applyOffsetYDiff refresh status not idle " + mRefreshStatus);
            return 0f;
        }

        float oldYDiff = yDiff;

        clearAnyOldAnimation();

        yDiff = adjustYDiff(yDiff);

        float translationY = getTranslationY();
        float oldTranslationY = translationY;

        translationY += yDiff;
        if (translationY < 0) {
            translationY = 0;
        }
        if (translationY > mMaxHeight) {
            translationY = mMaxHeight;
        }
        setTranslationY(translationY);

        target.setTranslationY(translationY);

        mStatusHeaderView.updateView(translationY, false, false);

        if (oldTranslationY == translationY) {
            // 如果应用滑动之后没有产生实际位置偏移，则认为此次没有消耗 yDiff
            return 0f;
        }
        return oldYDiff;
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
        if (mRefreshStatus != STATUS_IDLE) {
            Log.d(TAG + " finishOffsetY refresh status not idle " + mRefreshStatus);
            return;
        }

        float translationY = getTranslationY();
        if (!cancel && translationY >= mCoreHeight) {
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

    private PtrLayout.OnRefreshListener mOnRefreshListener;

    @Override
    public void setOnRefreshListener(PtrLayout.OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    private void animateToRefresh(final View target, final boolean notifyRefresh) {
        clearAnyOldAnimation();

        final float startTranslationY = getTranslationY();
        final float targetTranslationY = mCoreHeight;
        final long dur;
        if (Math.abs(startTranslationY - targetTranslationY) > mCoreHeight / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        mStatusHeaderView.updateView(startTranslationY, true, true);

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationY, targetTranslationY);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                setTranslationY(translationY);
                target.setTranslationY(translationY);

                mStatusHeaderView.updateView(translationY, true, true);
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

        final float startTranslationY = getTranslationY();
        final float targetTranslationY = 0;
        final long dur;
        if (Math.abs(startTranslationY - targetTranslationY) > mCoreHeight / 3) {
            dur = 220;
        } else {
            dur = 160;
        }

        mStatusHeaderView.updateView(startTranslationY, false, true);

        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationY, targetTranslationY);
        animator.setDuration(dur);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                setTranslationY(translationY);
                target.setTranslationY(translationY);

                mStatusHeaderView.updateView(translationY, false, true);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnyOldAnimation();
    }

    public static abstract class StatusHeaderView {

        protected final LayoutInflater mInflater;

        protected final int mCoreHeight;
        protected final int mMaxHeight;

        protected StatusHeaderView(ViewGroup parent, int coreHeight, int maxHeight) {
            mInflater = LayoutInflater.from(parent.getContext());
            mCoreHeight = coreHeight;
            mMaxHeight = maxHeight;
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
         * @param translationY 下拉距离
         * @param isRefreshing 当前是否处于正在刷新的状态
         * @param inAnimation  是否处于动画中
         */
        protected abstract void updateView(float translationY, boolean isRefreshing, boolean inAnimation);

    }

    public static class DefaultStatusHeaderView extends StatusHeaderView {

        private ProgressBar mProgressBar;

        protected DefaultStatusHeaderView(ViewGroup parent, int coreHeight, int maxHeight) {
            super(parent, coreHeight, maxHeight);
        }

        @Override
        protected View createView(LayoutInflater inflater, ViewGroup parent) {
            View content = inflater.inflate(R.layout.okandroid_ptr_header_default_view, parent, false);
            mProgressBar = ViewUtil.findViewByID(content, R.id.progress_bar);

            ArrowDrawable progressDrawable = new ArrowDrawable();
            progressDrawable.setPadding(DimenUtil.dp2px(5));
            mProgressBar.setProgressDrawable(progressDrawable);

            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(0);

            return content;
        }

        @Override
        protected void updateView(float translationY, boolean isRefreshing, boolean inAnimation) {
            if (isRefreshing) {
                mProgressBar.setIndeterminate(true);
                return;
            }

            if (inAnimation) {
                return;
            }

            mProgressBar.setIndeterminate(false);

            int progress;
            if (translationY <= 0) {
                progress = 0;
            } else if (translationY < mCoreHeight) {
                progress = (int) (translationY / mCoreHeight * 100);
            } else {
                progress = 100;
            }

            mProgressBar.setProgress(progress);
        }

    }

}
