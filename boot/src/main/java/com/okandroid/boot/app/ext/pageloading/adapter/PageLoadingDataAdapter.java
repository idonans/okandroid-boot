package com.okandroid.boot.app.ext.pageloading.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.widget.PageDataAdapter;

/**
 * Created by idonans on 2017/4/27.
 */

public class PageLoadingDataAdapter extends PageDataAdapter {

    /**
     * large loading style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_LOADING_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 10;
    /**
     * small loading style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_LOADING_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 11;
    /**
     * large empty data style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 12;
    /**
     * small empty data style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 13;
    /**
     * large no more data style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 14;
    /**
     * small no more data style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 15;
    /**
     * large data load success style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 16;
    /**
     * small data load success style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 17;
    /**
     * large server error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 18;
    /**
     * small server error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 19;
    /**
     * large net error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 20;
    /**
     * small net error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 21;
    /**
     * large unknown error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_LARGE = VIEW_HOLDER_TYPE_EXTRA_BASE + 22;
    /**
     * small unknown error style
     */
    public static final int VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_SMALL = VIEW_HOLDER_TYPE_EXTRA_BASE + 23;

    public PageLoadingDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public PageLoadingDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getGroupItemViewType(int position, int group, int positionInGroup) {
        switch (group) {
            case GROUP_INIT:
            case GROUP_MORE:
                PageLoadingStatus pageLoadingStatus = (PageLoadingStatus) getGroupItem(group, positionInGroup);
                PageLoadingViewProxy.ExtraPageMessage extraPageMessage = null;
                if (pageLoadingStatus.extraMessage instanceof PageLoadingViewProxy.ExtraPageMessage) {
                    extraPageMessage = (PageLoadingViewProxy.ExtraPageMessage) pageLoadingStatus.extraMessage;
                }

                if (pageLoadingStatus.loading) {
                    return pageLoadingStatus.smallStyle ?
                            VIEW_HOLDER_TYPE_EXTRA_LOADING_SMALL :
                            VIEW_HOLDER_TYPE_EXTRA_LOADING_LARGE;
                } else if (pageLoadingStatus.loadSuccess) {
                    if (pageLoadingStatus.firstPage && pageLoadingStatus.isPageLoadingContentEmpty()) {
                        // empty data
                        return pageLoadingStatus.smallStyle ?
                                VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_SMALL :
                                VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_LARGE;
                    } else if (extraPageMessage != null && extraPageMessage.isLastPage()) {
                        // no more data (last page or after last page)
                        return pageLoadingStatus.smallStyle ?
                                VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_SMALL :
                                VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_LARGE;
                    } else {
                        // normal load success
                        return pageLoadingStatus.smallStyle ?
                                VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_SMALL :
                                VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_LARGE;
                    }
                } else if (pageLoadingStatus.loadFail) {
                    if (extraPageMessage != null) {
                        if (extraPageMessage.serverError) {
                            return pageLoadingStatus.smallStyle ?
                                    VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_SMALL :
                                    VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_LARGE;
                        } else if (extraPageMessage.networkError) {
                            return pageLoadingStatus.smallStyle ?
                                    VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_SMALL :
                                    VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_LARGE;
                        }
                    }
                }

                return pageLoadingStatus.smallStyle ?
                        VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_SMALL :
                        VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_LARGE;
        }

        return super.getGroupItemViewType(position, group, positionInGroup);
    }

}
