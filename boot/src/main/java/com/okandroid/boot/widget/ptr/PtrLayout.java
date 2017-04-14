package com.okandroid.boot.widget.ptr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.okandroid.boot.lang.Log;

/**
 * Created by idonans on 2017/4/12.
 */

public class PtrLayout extends ViewGroup {

    public PtrLayout(Context context) {
        super(context);
        init();
    }

    public PtrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PtrLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PtrLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "PtrLayout";

    private int mTouchSlop;
    private View mHeader; // 下拉头
    private View mTarget; // 主要内容

    private int mActivePointerId = -1; // 用于计算滑动的手指
    private float mLastMotionX;
    private float mLastMotionY;

    private boolean mIsBeingDragged;

    private boolean mNestedScrollInProgress;

    private void init() {
        Context context = getContext();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        if (!isEnabled()
                || isHeaderStatusBusy()
                || canChildScrollUp()
                || mNestedScrollInProgress) {
            // 排除不能触发新下拉的情况
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
                || canChildScrollUp()
                || mNestedScrollInProgress) {
            // 排除不能触发新下拉的情况
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
                    final float yDiff = y - mLastMotionY;
                    applyOffsetYDiff(yDiff);
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
                    finishOffsetY(false);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    finishOffsetY(true);
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

    public void applyOffsetYDiff(float yDiff) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return;
        }

        HeaderView headerView = (HeaderView) mHeader;
        headerView.applyOffsetYDiff(yDiff, mTarget);
    }

    /**
     * @param cancel 如果是 cancel, 则忽略计算是否触发刷新，直接滚动到初始状态
     */
    private void finishOffsetY(boolean cancel) {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return;
        }

        HeaderView headerView = (HeaderView) mHeader;
        headerView.finishOffsetY(cancel, mTarget);
    }

    private void startDragging(float x, float y) {
        final float yDiff = y - mLastMotionY;
        final float xDiff = x - mLastMotionX;
        if (!mIsBeingDragged
                && yDiff > mTouchSlop
                && Math.abs(yDiff) > Math.abs(xDiff)) {
            // 垂直滑动并且有一定距离时，触发下拉
            mLastMotionX = x;
            mLastMotionY = y;
            mIsBeingDragged = true;
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

    public boolean canChildScrollUp() {
        ensureTargetAndHeader();

        if (mTarget == null || mHeader == null) {
            return false;
        }

        return ViewCompat.canScrollVertically(mTarget, -1);
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
        int headerLeft = getPaddingLeft();
        int headerTop = -headerHeight;
        mHeader.layout(headerLeft, headerTop, headerLeft + headerWidth, 0);
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
         * 是否处于忙状态(处于忙状态时不会触发新的下拉事件)
         *
         * @return
         */
        boolean isStatusBusy();

        /**
         * 处理下拉距离变更值
         */
        void applyOffsetYDiff(float yDiff, View target);

        void finishOffsetY(boolean cancel, View target);

    }

}