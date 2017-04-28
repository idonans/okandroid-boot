package com.okandroid.boot.app.ext.pageloading.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.widget.PageDataAdapter;
import com.okandroid.boot.widget.RecyclerViewGroupAdapter;

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

    private final LayoutInflater mLayoutInflater;

    public PageLoadingDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    public PageLoadingDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_TYPE_EXTRA_LOADING_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_loading_small);
            case VIEW_HOLDER_TYPE_EXTRA_LOADING_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_loading_large);
            case VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_empty_data_small);
            case VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_empty_data_large);
            case VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_no_more_data_small);
            case VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_no_more_data_large);
            case VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_load_success_small);
            case VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_load_success_large);
            case VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_server_error_small);
            case VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_server_error_large);
            case VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_network_error_small);
            case VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_network_error_large);
            case VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_SMALL:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_unknown_error_small);
            case VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_LARGE:
                return new RetryViewHolder(this, mLayoutInflater, parent, R.layout.okandroid_ext_pageloading_view_holder_extra_unknown_error_large);
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
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

    private class RetryViewHolder extends RecyclerViewGroupHolder {

        private final View mRetry;
        private final TextView mExtraMsg;

        public RetryViewHolder(RecyclerViewGroupAdapter groupAdapter, LayoutInflater inflater, ViewGroup parent, int layout) {
            super(groupAdapter, inflater, parent, layout);
            mRetry = findViewByID(R.id.retry);
            mExtraMsg = findViewByID(R.id.extra_msg);
        }

        @Override
        public void onHolderUpdate(@Nullable Object object, int position) {
            if (mRetry != null) {
                mRetry.setOnClickListener(null);
            }
            super.onHolderUpdate(object, position);
        }

        @Override
        protected void update(@NonNull Object object, int position) {
            super.update(object, position);

            PageLoadingStatus pageLoadingStatus = null;
            PageLoadingViewProxy.ExtraPageMessage extraPageMessage = null;

            if (object instanceof PageLoadingStatus) {
                pageLoadingStatus = (PageLoadingStatus) object;

                if (pageLoadingStatus.extraMessage instanceof PageLoadingViewProxy.ExtraPageMessage) {
                    extraPageMessage = (PageLoadingViewProxy.ExtraPageMessage) pageLoadingStatus.extraMessage;
                }
            }

            if (mRetry != null) {
                final int pageNo;

                if (pageLoadingStatus != null && pageLoadingStatus.firstPage) {
                    pageNo = 0;
                } else if (extraPageMessage != null) {
                    pageNo = extraPageMessage.pageNo;
                } else {
                    pageNo = -1;
                }

                if (pageNo >= 0) {
                    mRetry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reloadPageData(pageNo);
                        }
                    });
                }
            }

            CharSequence extraMsg = null;
            if (extraPageMessage != null) {
                extraMsg = extraPageMessage.detailMessage;
            }
            setExtraMsg(extraMsg);
        }

        private void setExtraMsg(CharSequence extraMsg) {
            if (mExtraMsg != null) {
                mExtraMsg.setText(extraMsg);
                if (TextUtils.isEmpty(extraMsg)) {
                    mExtraMsg.setVisibility(View.GONE);
                } else {
                    mExtraMsg.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * 重新加载指定页数据
     *
     * @param pageNo
     */
    protected void reloadPageData(int pageNo) {
    }

}
