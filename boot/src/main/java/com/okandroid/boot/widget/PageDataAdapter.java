package com.okandroid.boot.widget;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

/**
 * 同一时间至多只有一页数据处于加载中。例如：当前正在加载第二页数据，又触发了下拉刷新开始加载第一页，那么正在加载的第二页的请求会被终止。
 * <p>
 * Created by idonans on 2017/4/20.
 */

public class PageDataAdapter extends RecyclerViewGroupAdapter {

    /**
     * 显示初始加载内容的分组区域, 如：全屏的加载中，网络错误，加载失败等. 该组中通常只有一个 item 项
     */
    public static final int GROUP_INIT = 1;

    /**
     * 显示分页数据内容
     */
    public static final int GROUP_PAGE_CONTENT_DEFAULT = 100;

    /**
     * 当存在分页数据内容时，在数据底部显示加载中，网络错误或加载失败的样式. 该组中通常只有一个 item 项
     */
    public static final int GROUP_MORE = 1000;

    public PageDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public PageDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
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
                    // 使用 init 方式显示加载错误的内容, 此时是小样式(高度较小)
                    replaceAndNotifyInit(pageLoadingStatus.withStyle(true));
                }
            } else {
                // 正在加载第一页，但当前页面是空的
                // 隐藏可能的下拉刷新
                if (callback != null) {
                    callback.hideSwipeRefreshing();
                }

                // 使用 init 方式显示加载状态, 此时是全屏样式(高度较大)
                replaceAndNotifyInit(pageLoadingStatus.withStyle(false));
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
            replaceAndNotifyMore(pageLoadingStatus.withStyle(hasAnyPageContent));
        }
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
         * 是否处于加载中
         */
        public final boolean loading;
        /**
         * 是否网络错误
         */
        public final boolean networkError;
        /**
         * 是否服务器错误
         */
        public final boolean serverError;
        /**
         * 是否数据错误
         */
        public final boolean dataError;
        /**
         * 其他未知错误
         */
        public final boolean unknownError;

        /**
         * 是否使用小样式显示信息
         */
        public final boolean smallStyle;

        public PageLoadingStatus(boolean firstPage, boolean loading, boolean networkError,
                                 boolean serverError, boolean dataError, boolean unknownError) {
            this(firstPage, loading, networkError, serverError, dataError, unknownError, false);
        }

        private PageLoadingStatus(boolean firstPage, boolean loading, boolean networkError,
                                  boolean serverError, boolean dataError, boolean unknownError,
                                  boolean smallStyle) {
            this.firstPage = firstPage;
            this.loading = loading;
            this.networkError = networkError;
            this.serverError = serverError;
            this.dataError = dataError;
            this.unknownError = unknownError;
            this.smallStyle = smallStyle;
        }

        public PageLoadingStatus withStyle(boolean smallStyle) {
            return new PageLoadingStatus(this.firstPage, this.loading, this.networkError,
                    this.serverError, this.dataError, this.unknownError,
                    smallStyle);
        }

    }

    public interface ExtraPageLoadingStatusCallback {
        void showSwipeRefreshing();

        void hideSwipeRefreshing();

        void enableSwipeRefreshing();

        void disableSwipeRefreshing();
    }

}
