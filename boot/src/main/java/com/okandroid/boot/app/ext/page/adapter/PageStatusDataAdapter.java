package com.okandroid.boot.app.ext.page.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.page.PageViewProxy;
import com.okandroid.boot.ext.loadingstatus.LoadingStatus;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusEmptyDataLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusEmptyDataSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusLoadSuccessLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusLoadSuccessSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusLoadingLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusLoadingSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusNetworkErrorLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusNetworkErrorSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusNoMoreDataLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusNoMoreDataSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusServerErrorLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusServerErrorSmall;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusUnknownErrorLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusUnknownErrorSmall;
import com.okandroid.boot.widget.RecyclerViewGroupAdapter;

/**
 * Created by idonans on 2017/4/27.
 */

public class PageStatusDataAdapter extends com.okandroid.boot.widget.PageDataAdapter {

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

    public PageStatusDataAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    public PageStatusDataAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        super(data, recyclerView);
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_TYPE_EXTRA_LOADING_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusLoadingSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_LOADING_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusLoadingLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusEmptyDataSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_EMPTY_DATA_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusEmptyDataLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusNoMoreDataSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_NO_MORE_DATA_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusNoMoreDataLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusLoadSuccessSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_DATA_LOAD_SUCCESS_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusLoadSuccessLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusServerErrorSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_SERVER_ERROR_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusServerErrorLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusNetworkErrorSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_NET_ERROR_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusNetworkErrorLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_SMALL:
                return new LoadingStatusViewHolder(this, new LoadingStatusUnknownErrorSmall(getRecyclerView().getContext(), mLayoutInflater, parent));
            case VIEW_HOLDER_TYPE_EXTRA_UNKNOWN_ERROR_LARGE:
                return new LoadingStatusViewHolder(this, new LoadingStatusUnknownErrorLarge(getRecyclerView().getContext(), mLayoutInflater, parent));
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
                PageViewProxy.ExtraPageMessage extraPageMessage = null;
                if (pageLoadingStatus.extraMessage instanceof PageViewProxy.ExtraPageMessage) {
                    extraPageMessage = (PageViewProxy.ExtraPageMessage) pageLoadingStatus.extraMessage;
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

    public class LoadingStatusViewHolder extends RecyclerViewGroupHolder {

        protected LoadingStatus mLoadingStatus;

        public LoadingStatusViewHolder(RecyclerViewGroupAdapter groupAdapter, LoadingStatus loadingStatus) {
            super(groupAdapter, loadingStatus.view);
            mLoadingStatus = loadingStatus;
        }

        @Override
        public void onHolderUpdate(@Nullable Object object, int position) {
            if (mLoadingStatus.itemRetry != null) {
                mLoadingStatus.itemRetry.setOnClickListener(null);
            }
            super.onHolderUpdate(object, position);
        }

        @Override
        protected void update(@NonNull Object object, int position) {
            super.update(object, position);

            PageLoadingStatus pageLoadingStatus = null;
            PageViewProxy.ExtraPageMessage extraPageMessage = null;

            if (object instanceof PageLoadingStatus) {
                pageLoadingStatus = (PageLoadingStatus) object;

                if (pageLoadingStatus.extraMessage instanceof PageViewProxy.ExtraPageMessage) {
                    extraPageMessage = (PageViewProxy.ExtraPageMessage) pageLoadingStatus.extraMessage;
                }
            }

            if (mLoadingStatus.itemRetry != null) {
                final int pageNo;

                if (pageLoadingStatus != null && pageLoadingStatus.firstPage) {
                    pageNo = 0;
                } else if (extraPageMessage != null) {
                    pageNo = extraPageMessage.pageNo;
                } else {
                    pageNo = -1;
                }

                if (pageNo >= 0) {
                    mLoadingStatus.itemRetry.setOnClickListener(new View.OnClickListener() {
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
            if (mLoadingStatus.itemMessage != null) {
                mLoadingStatus.itemMessage.setText(extraMsg);
                if (TextUtils.isEmpty(extraMsg)) {
                    mLoadingStatus.itemMessage.setVisibility(View.GONE);
                } else {
                    mLoadingStatus.itemMessage.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * 重新加载指定页数据
     *
     * @param pageNo base on page 0
     */
    protected void reloadPageData(int pageNo) {
    }

}
