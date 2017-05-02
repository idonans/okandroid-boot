package com.okandroid.boot.widget;

import android.view.View;
import android.view.ViewTreeObserver;

import com.okandroid.boot.lang.Log;

/**
 * Created by idonans on 2017/5/2.
 * <p>
 * 协助处理展示最大行数，如：展开-收起逻辑
 * <pre>
 *     示例：一个竖向的视图中, 如果超过 5 行, 则使用一个 '展开' 按钮代替第 5 行.
 *     可以切换展开和收起. 如果视图中所有的数据不超过 5 行, 则不展示该按钮.
 * </pre>
 */

public class MaxLineViewHelper {

    private static final String TAG = "MaxLineViewHelper";
    private static final int ALL_LINES_UNKNOWN = -1;

    private final View mItemView;

    private int mExpandableLines;
    private int mAllLines = ALL_LINES_UNKNOWN;
    private boolean mExpand;
    private final ExpandUpdateListener mListener;

    public MaxLineViewHelper(View itemView, int expandableLines, boolean expand, ExpandUpdateListener listener) {
        mItemView = itemView;
        mExpandableLines = expandableLines;
        mExpand = expand;
        mListener = listener;

        mItemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                resetOnPreDrawListener();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                clearOnPreDrawListener();
            }
        });
    }

    private OnPreDrawImpl mOnPreDraw;

    private class OnPreDrawImpl implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
            try {
                if (mOnPreDraw != this) {
                    return mOnPreDraw == null;
                }

                int currentLines = mListener.getCurrentLines();

                Log.d(TAG + " currentLines: " + currentLines + ", all lines: " + mAllLines);

                if (mAllLines == ALL_LINES_UNKNOWN) {
                    mAllLines = currentLines;
                }

                mListener.onExpandUpdate(mExpand, mAllLines, mExpandableLines);
                return false;
            } finally {
                clearOnPreDrawListener();
            }
        }

    }

    public void toggle() {
        if (mAllLines == ALL_LINES_UNKNOWN) {
            reset(!mExpand);
        } else {
            mExpand = !mExpand;
            mListener.onExpandUpdate(mExpand, mAllLines, mExpandableLines);
        }
    }

    public void reset() {
        reset(mExpandableLines, mExpand);
    }

    public void reset(boolean expand) {
        reset(mExpandableLines, expand);
    }

    public void reset(int expandableLines, boolean expand) {
        clearOnPreDrawListener();

        mAllLines = ALL_LINES_UNKNOWN;
        mExpandableLines = expandableLines;
        mExpand = expand;

        mListener.onReset();

        resetOnPreDrawListener();
    }

    private void resetOnPreDrawListener() {
        clearOnPreDrawListener();

        mOnPreDraw = new OnPreDrawImpl();
        mItemView.getViewTreeObserver().addOnPreDrawListener(mOnPreDraw);
    }

    private void clearOnPreDrawListener() {
        OnPreDrawImpl old = mOnPreDraw;
        mOnPreDraw = null;
        if (old != null) {
            mItemView.getViewTreeObserver().removeOnPreDrawListener(old);
        }
    }

    public boolean isExpand() {
        return mExpand;
    }

    public int getExpandableLines() {
        return mExpandableLines;
    }

    public interface ExpandUpdateListener {
        int getCurrentLines();

        void onExpandUpdate(boolean expand, int allLines, int expandableLines);

        void onReset();
    }

}
