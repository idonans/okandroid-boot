package com.okandroid.boot.widget.r2lr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.okandroid.boot.lang.Log;

/**
 * right to left refresh 从右向左拉动刷新
 * Created by idonans on 2017/6/25.
 */
public class R2lrLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    public R2lrLayout(Context context) {
        super(context);
        init();
    }

    public R2lrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public R2lrLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public R2lrLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "R2lrLayout";

    private int mTouchSlop;
    private View mHeader; // 刷新头
    private View mTarget; // 主要内容

    private int mActivePointerId = -1; // 用于计算滑动的手指
    private float mLastMotionX;
    private float mLastMotionY;

    private boolean mIsBeingDragged;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private boolean mNestedScrollInProgress;

    private void init() {
        Context context = getContext();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        if (!isEnabled()
                || isHeaderStatusBusy()
                || canChildScrollRight()
                || mNestedScrollInProgress) {
            // 排除不能触发新左拉的情况
            return false;
        }

        final int action = event.getActionMasked();
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;
                mLastMotionX = event.getX(0);
                mLastMotionY = event.getY(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId < 0) {
                    Log.e(TAG + " onInterceptTouchEvent ACTION_MOVE but no active pointer id.");
                    return false;
                }

                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG + " onInterceptTouchEvent ACTION_MOVE but active pointer id invalid.");
                    return false;
                }

                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                startDragging(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        if (!isEnabled()
                || isHeaderStatusBusy()
                || canChildScrollRight()
                || mNestedScrollInProgress) {
            // 排除不能触发新左拉的情况
            return false;
        }

        final int action = event.getActionMasked();
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;
                mLastMotionX = event.getX(0);
                mLastMotionY = event.getY(0);
                break;

            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId < 0) {
                    Log.e(TAG + " onTouchEvent ACTION_MOVE but no active pointer id.");
                    return false;
                }

                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG + " onTouchEvent ACTION_MOVE but active pointer id invalid.");
                    return false;
                }

                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                if (!mIsBeingDragged) {
                    startDragging(x, y);
                } else {
                    final float xDiff = x - mLastMotionX;
                    applyOffsetXDiff(xDiff);
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;

            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    finishOffsetX(false);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    finishOffsetX(true);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;
                break;
        }

        return true;
    }

    private boolean isHeaderStatusBusy() {
        if (mHeader == null) {
            return true;
        }
        HeaderView headerView = (HeaderView) mHeader;
        return headerView.isStatusBusy();
    }

    public float applyOffsetXDiff(float xDiff) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return 0f;
        }

        HeaderView headerView = (HeaderView) mHeader;
        return headerView.applyOffsetXDiff(xDiff, mTarget);
    }

    /**
     * @param cancel 如果是 cancel, 则忽略计算是否触发刷新，直接滚动到初始状态
     */
    private void finishOffsetX(boolean cancel) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return;
        }

        HeaderView headerView = (HeaderView) mHeader;
        headerView.finishOffsetX(cancel, mTarget);
    }

    private void startDragging(float x, float y) {
        final float yDiff = y - mLastMotionY;
        final float xDiff = x - mLastMotionX;
        if (!mIsBeingDragged
                && xDiff < -mTouchSlop
                && Math.abs(xDiff) > Math.abs(yDiff)) {
            // 想做滑动并且有一定距离时，触发左拉
            mLastMotionX = x;
            mLastMotionY = y;
            mIsBeingDragged = true;

            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // 抬起的手指正是当前用于计算滑动的手指
            // 重新设置计算滑动的手指和对应的滑动坐标
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(newPointerIndex);
            mLastMotionX = event.getX(newPointerIndex);
            mLastMotionY = event.getY(newPointerIndex);
        }
    }

    public boolean canChildScrollRight() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        return ViewCompat.canScrollHorizontally(mTarget, 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return;
        }

        measureChild(mTarget, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHeader, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return;
        }

        int targetWidth = mTarget.getMeasuredWidth();
        int targetHeight = mTarget.getMeasuredHeight();
        int targetLeft = getPaddingLeft();
        int targetTop = getPaddingTop();
        mTarget.layout(targetLeft, targetTop, targetLeft + targetWidth, targetTop + targetHeight);

        int headerWidth = mHeader.getMeasuredWidth();
        int headerHeight = mHeader.getMeasuredHeight();
        int headerLeft = getWidth() - getPaddingRight();
        int headerTop = getPaddingTop();
        mHeader.layout(headerLeft, headerTop, headerLeft + headerWidth, headerTop + headerHeight);
    }

    private void ensureTargetAndHeader() {
        // 此时可能还没有 layout
        if (mHeader == null || mTarget == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof HeaderView) {
                    if (mHeader == null) {
                        mHeader = child;
                    }
                } else {
                    if (mTarget == null) {
                        mTarget = child;
                    }
                }
            }
        }

        if (mHeader != null) {
            HeaderView headerView = (HeaderView) mHeader;
            headerView.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }
            });
        }
    }

    public void setRefreshing(boolean refreshing) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            Log.e(TAG + " target or header not found");
            return;
        }

        HeaderView headerView = (HeaderView) mHeader;
        headerView.setRefreshing(refreshing, false, mTarget);
    }

    // nested scroll parent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL);
        mNestedScrollInProgress = true;
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;

        finishOffsetX(false);

        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        ensureTargetAndHeader();

        int[] parentOffsetInWindow = new int[2];

        if (mTarget == null || mHeader == null) {
            dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow);
            return;
        }

        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow);

        final int dx = dxUnconsumed + parentOffsetInWindow[0];

        if (dx > 0 && !canChildScrollRight()) {
            applyOffsetXDiff(-dx);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            dispatchNestedPreScroll(dx, dy, consumed, null);
            return;
        }

        if (dx < 0) {
            float usedDx = applyOffsetXDiff(-dx);
            usedDx = -usedDx;
            dx -= usedDx;

            final int[] parentConsumed = new int[2];
            dispatchNestedPreScroll(dx, dy, parentConsumed, null);

            consumed[0] = (int) usedDx;
            consumed[1] = 0;
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        } else {
            dispatchNestedPreScroll(dx, dy, consumed, null);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    // nested scroll child

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public interface HeaderView {

        void setOnRefreshListener(OnRefreshListener onRefreshListener);

        void setRefreshing(boolean refreshing, boolean notifyRefresh, View target);

        /**
         * 是否处于忙状态(处于忙状态时不会触发新的左拉事件)
         *
         * @return
         */
        boolean isStatusBusy();

        /**
         * 处理左拉距离变更值, 返回实际消耗的变更值
         */
        float applyOffsetXDiff(float xDiff, View target);

        void finishOffsetX(boolean cancel, View target);

    }

}
