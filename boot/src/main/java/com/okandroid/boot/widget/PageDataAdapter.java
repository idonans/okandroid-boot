package com.okandroid.boot.widget;

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

    public PageDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public PageDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
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

    public void showPageData(boolean firstPage, Collection data) {
        if (firstPage) {
            replaceAndNotifyPageContent(data);
        } else {
            appendAndNotifyPageContent(data);
        }
    }

    public void replaceAndNotifyPageContent(Collection data) {
        if (data == null || data.isEmpty()) {
            int[] positionAndSize = clearGroupItems(GROUP_PAGE_CONTENT_DEFAULT);
            if (positionAndSize != null) {
                notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
            }
            return;
        }

        int newSize = data.size();
        int oldSize = getGroupItemCount(GROUP_PAGE_CONTENT_DEFAULT);

        if (oldSize == 0) {
            // append
            int[] positionAndSize = appendGroupItems(GROUP_PAGE_CONTENT_DEFAULT, data);
            if (positionAndSize != null) {
                notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
            }
        } else if (newSize == oldSize) {
            // replace
            clearGroupItems(GROUP_PAGE_CONTENT_DEFAULT);
            int[] positionAndSize = appendGroupItems(GROUP_PAGE_CONTENT_DEFAULT, data);
            if (positionAndSize != null) {
                notifyItemRangeChanged(positionAndSize[0], positionAndSize[1]);
            }
        } else {
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
    }

    public void appendAndNotifyPageContent(Collection data) {
        int[] positionAndSize = appendGroupItems(GROUP_PAGE_CONTENT_DEFAULT, data);
        if (positionAndSize != null) {
            notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
        }
    }


    public void showPageLoadingStatus(PageLoadingStatus pageLoadingStatus, ExtraPageLoadingStatusCallback callback) {
        boolean hasAnyPageContent = hasAnyPageContent();

        if (pageLoadingStatus.firstPage && !hasAnyPageContent) {
            // 正在加载第一页，并且当前页面是空的, 此时需要禁用下拉刷新
            if (callback != null) {
                callback.disableSwipeRefreshing();
            }
        } else {
            // 其他情况，启用下拉刷新
            if (callback != null) {
                callback.enableSwipeRefreshing();
            }
        }

        if (pageLoadingStatus.firstPage) {
            // 正在加载第一页

            // 清除可能存在的加载其它页的状态
            removeAndNotifyMore();

            if (hasAnyPageContent) {
                // 当前已经有分页内容在显示(通常至少有第一页的数据)
                if (pageLoadingStatus.loading) {
                    // 使用下拉刷新的方式显示加载中的状态
                    if (callback != null) {
                        callback.showSwipeRefreshing();
                    }
                    // 清除 init 加载状态
                    removeAndNotifyInit();
                } else {
                    // 隐藏可能的下拉刷新
                    if (callback != null) {
                        callback.hideSwipeRefreshing();
                    }
                    // 使用 init 方式显示加载错误和加载成功的内容, 此时是小样式，并且一段时间后要自动清除
                    pageLoadingStatus = pageLoadingStatus.newBuilder().setSmallStyle(true).setAutoDismiss(true).build();
                    replaceAndNotifyInit(pageLoadingStatus);
                    autoDismissInitDelayIfMatch(pageLoadingStatus);
                }
            } else {
                // 正在加载第一页，但当前页面是空的
                // 隐藏可能的下拉刷新
                if (callback != null) {
                    callback.hideSwipeRefreshing();
                }

                // 使用 init 方式显示加载状态, 此时是全屏样式(高度较大)
                replaceAndNotifyInit(pageLoadingStatus.newBuilder()
                        .setSmallStyle(false)
                        .setAutoDismiss(false)
                        .build());
            }
        } else {
            // 正在加载其它页

            // 隐藏可能的下拉刷新
            if (callback != null) {
                callback.hideSwipeRefreshing();
            }

            // 清除可能存在的 init 状态
            removeAndNotifyInit();

            // 使用 more 方式显示加载状态，如果有分页内容，使用小样式
            replaceAndNotifyMore(pageLoadingStatus.newBuilder()
                    .setSmallStyle(hasAnyPageContent)
                    .setAutoDismiss(false)
                    .build());
        }
    }

    public void autoDismissInitDelayIfMatch(final PageLoadingStatus pageLoadingStatus) {
        getRecyclerView().postDelayed(new Runnable() {
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
        }, 2000);
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
        int count = getGroupItemCount(GROUP_INIT);
        if (count == 0) {
            // append
            int[] positionAndSize = appendGroupItems(GROUP_INIT, Arrays.asList(pageLoadingStatus));
            if (positionAndSize != null) {
                notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
            }
        } else if (count == 1) {
            // replace
            clearGroupItems(GROUP_INIT);
            int[] positionAndSize = appendGroupItems(GROUP_INIT, Arrays.asList(pageLoadingStatus));
            if (positionAndSize != null) {
                notifyItemRangeChanged(positionAndSize[0], positionAndSize[1]);
            }
        } else {
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
        int count = getGroupItemCount(GROUP_MORE);
        if (count == 0) {
            // append
            int[] positionAndSize = appendGroupItems(GROUP_MORE, Arrays.asList(pageLoadingStatus));
            if (positionAndSize != null) {
                notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
            }
        } else if (count == 1) {
            // replace
            clearGroupItems(GROUP_MORE);
            int[] positionAndSize = appendGroupItems(GROUP_MORE, Arrays.asList(pageLoadingStatus));
            if (positionAndSize != null) {
                notifyItemRangeChanged(positionAndSize[0], positionAndSize[1]);
            }
        } else {
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

        /**
         * 是否自动关闭(一段时间之后隐藏该显示状态)
         */
        public final boolean autoDismiss;

        private PageLoadingStatus(boolean firstPage, boolean loading, boolean loadFail,
                                  boolean loadSuccess, Object extraMessage,
                                  boolean smallStyle, boolean autoDismiss) {
            this.firstPage = firstPage;
            this.loading = loading;
            this.loadFail = loadFail;
            this.loadSuccess = loadSuccess;
            this.extraMessage = extraMessage;
            this.smallStyle = smallStyle;
            this.autoDismiss = autoDismiss;
        }

        public Builder newBuilder() {
            return new Builder(this);
        }

        public static class Builder {

            private boolean mFirstPage, mLoading, mLoadFail, mLoadSuccess, mSmallStyle, mAutoDismiss;
            private Object mExtraMessage;

            public Builder() {
            }

            public Builder(PageLoadingStatus pageLoadingStatus) {
                this.mFirstPage = pageLoadingStatus.firstPage;
                this.mLoading = pageLoadingStatus.loading;
                this.mLoadFail = pageLoadingStatus.loadFail;
                this.mLoadSuccess = pageLoadingStatus.loadSuccess;
                this.mSmallStyle = pageLoadingStatus.smallStyle;
                this.mAutoDismiss = pageLoadingStatus.autoDismiss;
                this.mExtraMessage = pageLoadingStatus.extraMessage;
            }

            public PageLoadingStatus build() {
                return new PageLoadingStatus(this.mFirstPage, this.mLoading, this.mLoadFail,
                        this.mLoadSuccess, this.mExtraMessage, this.mSmallStyle, this.mAutoDismiss);
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

            public Builder setAutoDismiss(boolean autoDismiss) {
                mAutoDismiss = autoDismiss;
                return this;
            }

            public Builder setExtraMessage(Object extraMessage) {
                mExtraMessage = extraMessage;
                return this;
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
