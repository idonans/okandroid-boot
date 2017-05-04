package com.okandroid.boot.widget;

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

    private static final int ALL_LINES_UNKNOWN = -1;

    private final MaxLineView mMaxLineView;

    private int mAllLines = ALL_LINES_UNKNOWN;
    private boolean mExpand;
    private final ExpandUpdateListener mListener;

    public MaxLineViewHelper(MaxLineView maxLineView, ExpandUpdateListener listener) {
        mMaxLineView = maxLineView;
        mListener = listener;

        mMaxLineView.setOnViewMeasureListener(new OnViewMeasureListener() {
            @Override
            public void onViewMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (mOnViewMeasureListenerInternal != null) {
                    mOnViewMeasureListenerInternal.onViewMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        });
    }

    private OnViewMeasureListenerImpl mOnViewMeasureListenerInternal;

    private class OnViewMeasureListenerImpl implements OnViewMeasureListener {

        @Override
        public void onViewMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mOnViewMeasureListenerInternal != this) {
                return;
            }

            int currentLines = mListener.getCurrentLines();
            if (mAllLines == ALL_LINES_UNKNOWN) {
                mAllLines = currentLines;

                mListener.onExpandUpdate(mExpand, mAllLines);
                mMaxLineView.callOnViewMeasureSuper(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public void toggle() {
        if (mAllLines == ALL_LINES_UNKNOWN) {
            update(!mExpand);
        } else {
            mExpand = !mExpand;
            mListener.onExpandUpdate(mExpand, mAllLines);
        }
    }

    public void update() {
        update(mExpand);
    }

    public void update(boolean expand) {
        mOnViewMeasureListenerInternal = null;

        mAllLines = ALL_LINES_UNKNOWN;
        mExpand = expand;

        mListener.onReset();

        mOnViewMeasureListenerInternal = new OnViewMeasureListenerImpl();
    }

    public boolean isExpand() {
        return mExpand;
    }

    public interface ExpandUpdateListener {
        int getCurrentLines();

        void onExpandUpdate(boolean expand, int allLines);

        void onReset();
    }

    public interface MaxLineView {
        void setOnViewMeasureListener(OnViewMeasureListener listener);

        void callOnViewMeasureSuper(int widthMeasureSpec, int heightMeasureSpec);
    }

    public interface OnViewMeasureListener {
        void onViewMeasure(int widthMeasureSpec, int heightMeasureSpec);
    }

}
