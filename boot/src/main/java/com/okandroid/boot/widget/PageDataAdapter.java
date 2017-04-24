package com.okandroid.boot.widget;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.okandroid.boot.lang.Log;

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
                    safetyCallback = new ExtraPageLoadingStatusCallback() {
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
                    };
                }
                ensurePageLoadingStatusHandler();
                mPageLoadingStatusHandler.showPageLoadingStatus(PageDataAdapter.this, pageLoadingStatus, safetyCallback);
            }
        });
    }

    public void autoDismissInitDelayIfMatch(final PageLoadingStatus pageLoadingStatus, final long delay) {
        getRecyclerView().postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Object item = getGroupItem(GROUP_INIT, 0);
                if (item == pageLoadingStatus) {
                    int[] positionAndSize = removeGroupItem(GROUP_INIT, 0);
                    if (positionAndSize != null) {
                        notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
                    }
                }
            }
        }, delay);
    }

    public void autoDismissMoreDelayIfMatch(final PageLoadingStatus pageLoadingStatus, final long delay) {
        getRecyclerView().postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Object item = getGroupItem(GROUP_MORE, 0);
                if (item == pageLoadingStatus) {
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
    public void removeAndNotifyInit() {
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
    public void removeAndNotifyMore() {
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
        public void showPageLoadingStatus(@NonNull PageDataAdapter pageDataAdapter,
                                          @NonNull PageLoadingStatus pageLoadingStatus,
                                          @NonNull ExtraPageLoadingStatusCallback callback) {
            final long delay = 2200L;
            final boolean hasAnyPageContent = pageDataAdapter.hasAnyPageContent();

            if (pageLoadingStatus.firstPage) {
                pageDataAdapter.getRecyclerView().scrollToPosition(0);
            }

            if (pageLoadingStatus.firstPage
                    && (pageLoadingStatus.loading || pageLoadingStatus.loadFail)
                    && !hasAnyPageContent) {
                // 正在加载第一页或者第一页加载失败，并且当前页面是空的, 此时需要禁用下拉刷新
                callback.disableSwipeRefreshing();
            } else {
                // 其他情况，启用下拉刷新
                callback.enableSwipeRefreshing();
            }

            if (pageLoadingStatus.loading) {
                // 加载中
                if (pageLoadingStatus.firstPage) {
                    // 正在加载第一页

                    // 清除加载其它页的状态
                    pageDataAdapter.removeAndNotifyMore();

                    // 如果当前没有显示分页数据内容，则使用全屏加载样式，否则使用下拉加载样式
                    if (!hasAnyPageContent) {
                        // 全屏加载中样式
                        callback.hideSwipeRefreshing();
                        pageLoadingStatus = pageLoadingStatus.newBuilder()
                                .setSmallStyle(false)
                                .build();
                        pageDataAdapter.replaceAndNotifyInit(pageLoadingStatus);
                    } else {
                        // 下拉加载中样式
                        pageDataAdapter.removeAndNotifyInit();
                        callback.showSwipeRefreshing();
                    }
                } else {
                    // 正在加载其它页

                    // 清除下拉刷新
                    callback.hideSwipeRefreshing();
                    // 清除加载第一页的状态
                    pageDataAdapter.removeAndNotifyInit();

                    pageLoadingStatus = pageLoadingStatus.newBuilder()
                            .setSmallStyle(hasAnyPageContent)
                            .build();
                    pageDataAdapter.replaceAndNotifyMore(pageLoadingStatus);
                }
            } else if (pageLoadingStatus.loadSuccess) {
                // 加载成功

                // 清除下拉刷新
                callback.hideSwipeRefreshing();

                if (pageLoadingStatus.firstPage) {
                    // 第一页加载成功

                    // 清除加载其它页的状态
                    pageDataAdapter.removeAndNotifyMore();

                    // 小样式，并且稍后清除
                    pageLoadingStatus = pageLoadingStatus.newBuilder()
                            .setSmallStyle(true)
                            .build();
                    pageDataAdapter.replaceAndNotifyInit(pageLoadingStatus);
                    pageDataAdapter.autoDismissInitDelayIfMatch(pageLoadingStatus, delay);
                } else {
                    // 其它页加载成功

                    // 清除加载第一页的状态
                    pageDataAdapter.removeAndNotifyInit();

                    // 小样式，并且稍后清除
                    pageLoadingStatus = pageLoadingStatus.newBuilder()
                            .setSmallStyle(true)
                            .build();
                    pageDataAdapter.replaceAndNotifyMore(pageLoadingStatus);
                    pageDataAdapter.autoDismissMoreDelayIfMatch(pageLoadingStatus, delay);
                }
            } else if (pageLoadingStatus.loadFail) {
                // 加载失败

                // 清除下拉刷新
                callback.hideSwipeRefreshing();

                if (pageLoadingStatus.firstPage) {
                    // 第一页加载失败

                    // 清除加载其它页的状态
                    pageDataAdapter.removeAndNotifyMore();

                    if (!hasAnyPageContent) {
                        // 当前没有显示分页数据内容，则使用全屏展示加载失败样式
                        pageLoadingStatus = pageLoadingStatus.newBuilder()
                                .setSmallStyle(false)
                                .build();
                        pageDataAdapter.replaceAndNotifyInit(pageLoadingStatus);
                    } else {
                        // 当前有显示分页数据内容，使用小样式展示加载失败，并且稍后清除
                        pageLoadingStatus = pageLoadingStatus.newBuilder()
                                .setSmallStyle(true)
                                .build();
                        pageDataAdapter.replaceAndNotifyInit(pageLoadingStatus);
                        pageDataAdapter.autoDismissInitDelayIfMatch(pageLoadingStatus, delay);
                    }
                } else {
                    // 其它页加载失败

                    // 清除加载第一页的状态
                    pageDataAdapter.removeAndNotifyInit();

                    if (!hasAnyPageContent) {
                        // 当前没有显示分页数据内容，则使用全屏展示加载失败样式
                        pageLoadingStatus = pageLoadingStatus.newBuilder()
                                .setSmallStyle(false)
                                .build();
                        pageDataAdapter.replaceAndNotifyMore(pageLoadingStatus);
                    } else {
                        // 当前有显示分页数据内容，使用小样式展示加载失败
                        pageLoadingStatus = pageLoadingStatus.newBuilder()
                                .setSmallStyle(true)
                                .build();
                        pageDataAdapter.replaceAndNotifyMore(pageLoadingStatus);
                    }
                }
            } else {
                Log.e(TAG + " unknown page loading status");
            }
        }
    }

    public interface ExtraPageLoadingStatusCallback {
        void showSwipeRefreshing();

        void hideSwipeRefreshing();

        void enableSwipeRefreshing();

        void disableSwipeRefreshing();
    }

}
