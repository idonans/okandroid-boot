package com.okandroid.boot.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collection;

/**
 * 同一时间至多只有一页数据处于加载中。例如：当前正在加载第二页数据，又触发了下拉刷新开始加载第一页，那么正在加载的第二页的请求会被终止。
 * <p>
 * Created by idonans on 2017/4/20.
 */

public class PageDataAdapter extends RecyclerViewGroupAdapter {

    private static final String TAG = "PageDataAdapter";

    /**
     * 显示初始加载内容的分组区域, 如：全屏的加载中，网络错误，加载失败等. 该组中通常只有一个 item 项
     */
    public static final int GROUP_INIT = 100;

    /**
     * 显示分页数据内容
     */
    public static final int GROUP_PAGE_CONTENT_DEFAULT = 1000;

    /**
     * 当存在分页数据内容时，在数据底部显示加载中，网络错误或加载失败的样式. 该组中通常只有一个 item 项
     */
    public static final int GROUP_MORE = 2000;

    /**
     * 小样式的加载中, 加载错误.
     */
    public static final int VIEW_HOLDER_TYPE_LOADING_SMALL = 2000;
    /**
     * 大样式的加载中, 加载错误.
     */
    public static final int VIEW_HOLDER_TYPE_LOADING_LARGE = 2001;

    private PageLoadingStatusHandler mPageLoadingStatusHandler;

    public PageDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public PageDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
    }

    public void setPageLoadingStatusHandler(PageLoadingStatusHandler pageLoadingStatusHandler) {
        mPageLoadingStatusHandler = pageLoadingStatusHandler;
    }

    private void ensurePageLoadingStatusHandler() {
        if (mPageLoadingStatusHandler == null) {
            mPageLoadingStatusHandler = new PageLoadingStatusHandler();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getGroupItemViewType(int position, int group, int positionInGroup) {
        switch (group) {
            case GROUP_INIT:
            case GROUP_MORE:
                PageLoadingStatus pageLoadingStatus = (PageLoadingStatus) getGroupItem(group, positionInGroup);
                if (pageLoadingStatus.smallStyle) {
                    return VIEW_HOLDER_TYPE_LOADING_SMALL;
                } else {
                    return VIEW_HOLDER_TYPE_LOADING_LARGE;
                }
        }

        return super.getGroupItemViewType(position, group, positionInGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        checkAndNotifyLoadMore(holder, position);
    }

    public void checkAndNotifyLoadMore(RecyclerView.ViewHolder holder, int position) {
        int[] groupAndPosition = getGroupAndPosition(position);
        if (groupAndPosition != null) {
            if (groupAndPosition[0] == GROUP_PAGE_CONTENT_DEFAULT) {
                int count = getGroupItemCount(GROUP_PAGE_CONTENT_DEFAULT);
                if (groupAndPosition[1] + 2 >= count) {
                    notifyLoadMore();
                }
            }
        }
    }

    public interface OnMoreLoadListener {
        void onLoadMore();
    }

    private OnMoreLoadListener mOnMoreLoadListener;

    public void setOnMoreLoadListener(OnMoreLoadListener onMoreLoadListener) {
        mOnMoreLoadListener = onMoreLoadListener;
    }

    public void notifyLoadMore() {
        if (mOnMoreLoadListener != null) {
            mOnMoreLoadListener.onLoadMore();
        }
    }

    public void showPageData(final boolean firstPage, final Collection data) {
        getRecyclerView().postOnAnimation(new Runnable() {
            @Override
            public void run() {
                showPageDataInternal(firstPage, data);
            }
        });
    }

    private void showPageDataInternal(boolean firstPage, Collection data) {
        if (firstPage) {
            replaceAndNotifyPageContent(data);
        } else {
            appendAndNotifyPageContent(data);
        }
    }

    public void replaceAndNotifyPageContent(Collection data) {
        // remove and add
        int[] positionAndSize = clearGroupItems(GROUP_PAGE_CONTENT_DEFAULT);
        if (positionAndSize != null) {
            notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
        }
        positionAndSize = appendGroupItems(GROUP_PAGE_CONTENT_DEFAULT, data);
        if (positionAndSize != null) {
            notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
        }
    }

    public void appendAndNotifyPageContent(Collection data) {
        int[] positionAndSize = appendGroupItems(GROUP_PAGE_CONTENT_DEFAULT, data);
        if (positionAndSize != null) {
            notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
        }
    }

    public void showPageLoadingStatus(final PageLoadingStatus pageLoadingStatus, final ExtraPageLoadingStatusCallback callback) {
        getRecyclerView().postOnAnimation(new Runnable() {
            @Override
            public void run() {
                ExtraPageLoadingStatusCallback safetyCallback = callback;
                if (safetyCallback == null) {
                    safetyCallback = new SimpleExtraPageLoadingStatusCallback();
                }
                ensurePageLoadingStatusHandler();
                mPageLoadingStatusHandler.showPageLoadingStatus(PageDataAdapter.this, pageLoadingStatus, safetyCallback);
            }
        });
    }

    private PageLoadingStatus mPendingAutoDismissPageLoadingStatusInit;

    public void autoDismissInitDelayIfMatch(final PageLoadingStatus pageLoadingStatus, final long delay) {
        mPendingAutoDismissPageLoadingStatusInit = pageLoadingStatus;
        getRecyclerView().postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Object item = getGroupItem(GROUP_INIT, 0);
                if (item == pageLoadingStatus) {
                    if (mPendingAutoDismissPageLoadingStatusInit == item) {
                        mPendingAutoDismissPageLoadingStatusInit = null;
                    }
                    int[] positionAndSize = removeGroupItem(GROUP_INIT, 0);
                    if (positionAndSize != null) {
                        notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
                    }
                }
            }
        }, delay);
    }

    private PageLoadingStatus mPendingAutoDismissPageLoadingStatusMore;

    public void autoDismissMoreDelayIfMatch(final PageLoadingStatus pageLoadingStatus, final long delay) {
        mPendingAutoDismissPageLoadingStatusMore = pageLoadingStatus;
        getRecyclerView().postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Object item = getGroupItem(GROUP_MORE, 0);
                if (item == pageLoadingStatus) {
                    if (mPendingAutoDismissPageLoadingStatusMore == item) {
                        mPendingAutoDismissPageLoadingStatusMore = null;
                    }
                    int[] positionAndSize = removeGroupItem(GROUP_MORE, 0);
                    if (positionAndSize != null) {
                        notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
                    }
                }
            }
        }, delay);
    }

    /**
     * 动态删除 GROUP_INIT 中的数据
     */
    public void removeAndNotifyInit(boolean skipAutoDismiss) {
        if (skipAutoDismiss && mPendingAutoDismissPageLoadingStatusInit != null) {
            int count = getGroupItemCount(GROUP_INIT);
            if (count == 1) {
                Object item = getGroupItem(GROUP_INIT, 0);
                if (mPendingAutoDismissPageLoadingStatusInit == item) {
                    return;
                }
            }
        }

        int[] positionAndSize = clearGroupItems(GROUP_INIT);
        if (positionAndSize != null) {
            notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
        }
    }

    /**
     * 动态替换 GROUP_INIT 中的数据
     */
    public void replaceAndNotifyInit(PageLoadingStatus pageLoadingStatus) {
        // remove and add
        int[] positionAndSize = clearGroupItems(GROUP_INIT);
        if (positionAndSize != null) {
            notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
        }
        positionAndSize = appendGroupItems(GROUP_INIT, Arrays.asList(pageLoadingStatus));
        if (positionAndSize != null) {
            notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
        }
    }

    /**
     * 动态删除 GROUP_MORE 中的数据
     */
    public void removeAndNotifyMore(boolean skipAutoDismiss) {
        if (skipAutoDismiss && mPendingAutoDismissPageLoadingStatusMore != null) {
            int count = getGroupItemCount(GROUP_MORE);
            if (count == 1) {
                Object item = getGroupItem(GROUP_MORE, 0);
                if (mPendingAutoDismissPageLoadingStatusMore == item) {
                    return;
                }
            }
        }

        int[] positionAndSize = clearGroupItems(GROUP_MORE);
        if (positionAndSize != null) {
            notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
        }
    }

    /**
     * 动态替换 GROUP_MORE 中的数据
     */
    public void replaceAndNotifyMore(PageLoadingStatus pageLoadingStatus) {
        // remove and add
        int[] positionAndSize = clearGroupItems(GROUP_MORE);
        if (positionAndSize != null) {
            notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
        }
        positionAndSize = appendGroupItems(GROUP_MORE, Arrays.asList(pageLoadingStatus));
        if (positionAndSize != null) {
            notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
        }
    }

    public boolean hasAnyPageContent() {
        return getGroupItemCount(GROUP_PAGE_CONTENT_DEFAULT) > 0;
    }

    public static class PageLoadingStatus {

        public interface PageLoadingContentValidator {
            boolean isPageLoadingContentEmpty();
        }

        /**
         * 是否是第一页
         */
        public final boolean firstPage;

        /**
         * 是否加载中
         */
        public final boolean loading;

        /**
         * 是否加载失败
         */
        public final boolean loadFail;

        /**
         * 是否加载成功
         */
        public final boolean loadSuccess;

        /**
         * 额外的辅助信息
         */
        public final Object extraMessage;

        /**
         * 是否使用小样式显示信息
         */
        public final boolean smallStyle;

        private PageLoadingStatus(boolean firstPage, boolean loading, boolean loadFail,
                                  boolean loadSuccess, Object extraMessage, boolean smallStyle) {
            this.firstPage = firstPage;
            this.loading = loading;
            this.loadFail = loadFail;
            this.loadSuccess = loadSuccess;
            this.extraMessage = extraMessage;
            this.smallStyle = smallStyle;
        }

        public final boolean isPageLoadingContentEmpty() {
            if (extraMessage instanceof PageLoadingContentValidator) {
                return ((PageLoadingContentValidator) extraMessage).isPageLoadingContentEmpty();
            }
            return false;
        }

        public Builder newBuilder() {
            return new Builder(this);
        }

        public static class Builder {

            private boolean mFirstPage, mLoading, mLoadFail,
                    mLoadSuccess, mSmallStyle;
            private Object mExtraMessage;

            public Builder() {
            }

            public Builder(PageLoadingStatus pageLoadingStatus) {
                this.mFirstPage = pageLoadingStatus.firstPage;
                this.mLoading = pageLoadingStatus.loading;
                this.mLoadFail = pageLoadingStatus.loadFail;
                this.mLoadSuccess = pageLoadingStatus.loadSuccess;
                this.mSmallStyle = pageLoadingStatus.smallStyle;
                this.mExtraMessage = pageLoadingStatus.extraMessage;
            }

            public PageLoadingStatus build() {
                return new PageLoadingStatus(this.mFirstPage, this.mLoading, this.mLoadFail,
                        this.mLoadSuccess, this.mExtraMessage, this.mSmallStyle);
            }

            public Builder setFirstPage(boolean firstPage) {
                mFirstPage = firstPage;
                return this;
            }

            public Builder setLoading(boolean loading) {
                mLoading = loading;
                return this;
            }

            public Builder setLoadFail(boolean loadFail) {
                mLoadFail = loadFail;
                return this;
            }

            public Builder setLoadSuccess(boolean loadSuccess) {
                mLoadSuccess = loadSuccess;
                return this;
            }

            public Builder setSmallStyle(boolean smallStyle) {
                mSmallStyle = smallStyle;
                return this;
            }

            public Builder setExtraMessage(Object extraMessage) {
                mExtraMessage = extraMessage;
                return this;
            }

        }

    }

    public static class PageLoadingStatusHandler {

        /**
         * 根据当前请求状态调整 recycler view scroll 位置
         */
        protected void adjustRecyclerViewScroll(@NonNull RecyclerView recyclerView,
                                                boolean hasAnyPageContent,
                                                @NonNull PageDataAdapter pageDataAdapter,
                                                @NonNull PageLoadingStatus pageLoadingStatus,
                                                @NonNull ExtraPageLoadingStatusCallback callback) {
            if (pageLoadingStatus.firstPage) {
                int childCount = recyclerView.getChildCount();
                if (childCount > 0) {
                    int adapterPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
                    if (adapterPosition == 0) {
                        // 如果页面没有向下滚动，尽量使得第一页的加载状态可以显示出来
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
        }

        /**
         * 根据当前请求状态调整下拉刷新的可用性
         */
        protected boolean isSwipeRefreshingEnable(
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback) {

            // 计算当前分页数据显示之后页面上是否是空数据
            boolean mergePageContentEmpty = !hasAnyPageContent;
            if (pageLoadingStatus.loadSuccess) {
                if (pageLoadingStatus.firstPage) {
                    mergePageContentEmpty = pageLoadingStatus.isPageLoadingContentEmpty();
                } else {
                    mergePageContentEmpty &= pageLoadingStatus.isPageLoadingContentEmpty();
                }
            }

            // 如果加载显示之后，页面仍然是空的，需要禁用下拉刷新。

            return !mergePageContentEmpty;
        }

        /**
         * 是否跳过关闭之前的 auto dismiss PageLoadingStatus for init
         */
        protected boolean canSkipAutoDismissInit(
                boolean forceShowSwipeRefreshing,
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback,
                @Nullable PageLoadingStatus pendingPageLoadingStatusAutoDismissInit) {
            if (forceShowSwipeRefreshing) {
                return false;
            }
            return true;
        }

        /**
         * 是否跳过关闭之前的 auto dismiss PageLoadingStatus for more
         */
        protected boolean canSkipAutoDismissMore(
                boolean forceShowSwipeRefreshing,
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback,
                @Nullable PageLoadingStatus pendingPageLoadingStatusAutoDismissMore) {
            if (forceShowSwipeRefreshing) {
                return false;
            }
            return true;
        }

        /**
         * 是否要显示下拉刷新
         */
        protected boolean forceShowSwipeRefreshing(
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback) {
            // 仅正在加载非空的第一页时，显示下拉刷新
            return hasAnyPageContent && pageLoadingStatus.firstPage && pageLoadingStatus.loading;
        }

        /**
         * 是否使用 group init 显示当前的加载状态
         */
        protected boolean useInitGroupForShowStatus(
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback) {
            return pageLoadingStatus.firstPage;
        }

        /**
         * 是否使用小样式显示当前的加载状态
         */
        protected boolean useSmallStyleForShowStatus(
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback) {

            // 计算当前分页数据显示之后页面上是否是空数据
            boolean mergePageContentEmpty = !hasAnyPageContent;
            if (pageLoadingStatus.loadSuccess) {
                if (pageLoadingStatus.firstPage) {
                    mergePageContentEmpty = pageLoadingStatus.isPageLoadingContentEmpty();
                } else {
                    mergePageContentEmpty &= pageLoadingStatus.isPageLoadingContentEmpty();
                }
            }

            // 如果加载显示之后，页面仍然是空的，使用全屏显示。

            return !mergePageContentEmpty;
        }

        /**
         * 是否在稍后自动关闭该显示状态
         */
        protected boolean needAutoDismissStatus(
                boolean hasAnyPageContent,
                @NonNull PageDataAdapter pageDataAdapter,
                @NonNull PageLoadingStatus pageLoadingStatus,
                @NonNull ExtraPageLoadingStatusCallback callback) {
            if (!pageLoadingStatus.smallStyle) {
                // 全屏样式不自动关闭
                return false;
            }

            if (pageLoadingStatus.loading) {
                // 加载中状态不自动关闭
                return false;
            }

            if (pageLoadingStatus.firstPage) {
                // 第一页的加载失败和加载完成可以自动关闭
                return true;
            }

            // 其它页的加载成功可以自动关闭
            return pageLoadingStatus.loadSuccess;
        }

        public void showPageLoadingStatus(@NonNull PageDataAdapter pageDataAdapter,
                                          @NonNull PageLoadingStatus pageLoadingStatus,
                                          @NonNull ExtraPageLoadingStatusCallback callback) {
            final long delay = 2200L;
            final boolean hasAnyPageContent = pageDataAdapter.hasAnyPageContent();

            adjustRecyclerViewScroll(pageDataAdapter.getRecyclerView(),
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback);

            final boolean swipeRefreshingEnable;
            if (isSwipeRefreshingEnable(hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback)) {
                swipeRefreshingEnable = true;
                callback.enableSwipeRefreshing();
            } else {
                swipeRefreshingEnable = false;
                callback.hideSwipeRefreshing();
                callback.disableSwipeRefreshing();
            }

            final boolean forceShowSwipeRefreshing;

            if (swipeRefreshingEnable) {
                forceShowSwipeRefreshing = forceShowSwipeRefreshing(
                        hasAnyPageContent,
                        pageDataAdapter,
                        pageLoadingStatus,
                        callback
                );
                if (forceShowSwipeRefreshing) {
                    callback.showSwipeRefreshing();
                } else {
                    callback.hideSwipeRefreshing();
                }
            } else {
                forceShowSwipeRefreshing = false;
            }

            final boolean skipAutoDismissInit = canSkipAutoDismissInit(
                    forceShowSwipeRefreshing,
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback,
                    pageDataAdapter.mPendingAutoDismissPageLoadingStatusInit
            );

            final boolean skipAutoDismissMore = canSkipAutoDismissMore(
                    forceShowSwipeRefreshing,
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback,
                    pageDataAdapter.mPendingAutoDismissPageLoadingStatusMore
            );

            if (forceShowSwipeRefreshing) {
                // 显示下拉刷新时，关闭其它状态的显示
                pageDataAdapter.removeAndNotifyInit(skipAutoDismissInit);
                pageDataAdapter.removeAndNotifyMore(skipAutoDismissMore);
                return;
            }

            final boolean useGroupInit = useInitGroupForShowStatus(
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback
            );

            final boolean useSmallStyle = useSmallStyleForShowStatus(
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback
            );

            pageLoadingStatus = pageLoadingStatus.newBuilder()
                    .setSmallStyle(useSmallStyle)
                    .build();

            final boolean needAutoDismiss = needAutoDismissStatus(
                    hasAnyPageContent,
                    pageDataAdapter,
                    pageLoadingStatus,
                    callback
            );


            if (useGroupInit) {
                pageDataAdapter.removeAndNotifyMore(skipAutoDismissMore);
                pageDataAdapter.replaceAndNotifyInit(pageLoadingStatus);
                if (needAutoDismiss) {
                    pageDataAdapter.autoDismissInitDelayIfMatch(pageLoadingStatus, delay);
                }
            } else {
                // use group more
                pageDataAdapter.removeAndNotifyInit(skipAutoDismissInit);
                pageDataAdapter.replaceAndNotifyMore(pageLoadingStatus);
                if (needAutoDismiss) {
                    pageDataAdapter.autoDismissMoreDelayIfMatch(pageLoadingStatus, delay);
                }
            }
        }
    }

    public interface ExtraPageLoadingStatusCallback {
        void showSwipeRefreshing();

        void hideSwipeRefreshing();

        void enableSwipeRefreshing();

        void disableSwipeRefreshing();
    }

    public static class SimpleExtraPageLoadingStatusCallback implements ExtraPageLoadingStatusCallback {

        @Override
        public void showSwipeRefreshing() {
            // ignore
        }

        @Override
        public void hideSwipeRefreshing() {
            // ignore
        }

        @Override
        public void enableSwipeRefreshing() {
            // ignore
        }

        @Override
        public void disableSwipeRefreshing() {
            // ignore
        }
    }

}
