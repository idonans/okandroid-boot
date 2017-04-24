package com.sample.boot.module.datalist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okandroid.boot.app.ext.pageloading.PageLoadingFragment;
import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.widget.PageDataAdapter;
import com.okandroid.boot.widget.RecyclerViewGroupAdapter;
import com.sample.boot.R;

/**
 * Created by idonans on 2017/4/21.
 */

public class DataListFragment extends PageLoadingFragment implements DataListView {

    private static final String TAG = "DataListFragment";

    @Override
    protected PageLoadingViewProxy newDefaultViewProxy() {
        return new DataListViewProxy(this);
    }

    @Override
    protected PageDataAdapter createPageDataAdapter(RecyclerView recyclerView) {
        return new DataListAdapter(recyclerView);
    }

    @Override
    protected PageContentView createPageContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        return new PageContentView(activity, inflater, contentView, R.layout.sample_data_list_view);
    }

    private class DataListAdapter extends PageDataAdapter {

        public DataListAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater(null);
            switch (viewType) {
                case VIEW_HOLDER_TYPE_LOADING_SMALL:
                    return new ViewHolderLoading(this, inflater, parent, R.layout.sample_data_list_view_item_loading_small);
                case VIEW_HOLDER_TYPE_LOADING_LARGE:
                    return new ViewHolderLoading(this, inflater, parent, R.layout.sample_data_list_view_item_loading_large);
                case GROUP_PAGE_CONTENT_DEFAULT:
                    return new ViewHolderItemData(this, inflater, parent);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void checkAndNotifyLoadMore(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG + " checkAndNotifyLoadMore position:" + position + ", adapter position:" + holder.getAdapterPosition() + ", layout position:" + holder.getLayoutPosition());
            super.checkAndNotifyLoadMore(holder, position);
        }
    }

    private class ViewHolderLoading extends RecyclerViewGroupAdapter.RecyclerViewGroupHolder {

        private final TextView mTextView;

        public ViewHolderLoading(RecyclerViewGroupAdapter groupAdapter, LayoutInflater inflater, ViewGroup parent, int layout) {
            super(groupAdapter, inflater, parent, layout);
            mTextView = findViewByID(R.id.text);
        }

        @Override
        protected void update(@NonNull Object object, int position) {
            // clear old click ref
            itemView.setOnClickListener(null);

            if (!(object instanceof PageDataAdapter.PageLoadingStatus)) {
                mTextView.setText(null);
                return;
            }

            PageDataAdapter.PageLoadingStatus pageLoadingStatus = (PageDataAdapter.PageLoadingStatus) object;
            PageLoadingViewProxy.ExtraPageMessage extraPageMessage = null;
            if (pageLoadingStatus.extraMessage instanceof PageLoadingViewProxy.ExtraPageMessage) {
                extraPageMessage = (PageLoadingViewProxy.ExtraPageMessage) pageLoadingStatus.extraMessage;
            }

            if (pageLoadingStatus.loading) {
                mTextView.setText("加载中...");

                if (extraPageMessage != null && extraPageMessage.detailMessage != null) {
                    mTextView.append(" " + extraPageMessage.detailMessage);
                }
            } else if (pageLoadingStatus.loadSuccess) {
                boolean lastPage = false;
                if (extraPageMessage != null) {
                    lastPage = extraPageMessage.isLastPage();
                }

                if (!pageLoadingStatus.smallStyle
                        && pageLoadingStatus.firstPage
                        && pageLoadingStatus.isPageLoadingContentEmpty()) {
                    mTextView.setText("暂无数据，点击重试");
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadFirstPage();
                        }
                    });
                } else if (lastPage) {
                    mTextView.setText("就这么多了");
                } else {
                    mTextView.setText("加载完成");
                }

                if (extraPageMessage != null && extraPageMessage.detailMessage != null) {
                    mTextView.append(" " + extraPageMessage.detailMessage);
                }
            } else if (pageLoadingStatus.loadFail) {
                mTextView.setText("加载失败, 点击重试");

                if (extraPageMessage != null) {
                    if (extraPageMessage.serverError) {
                        mTextView.append(" " + "服务器忙");
                    } else if (extraPageMessage.networkError) {
                        mTextView.append(" " + "网络不给力");
                    }
                }

                if (extraPageMessage != null && extraPageMessage.detailMessage != null) {
                    mTextView.append(" " + extraPageMessage.detailMessage);
                }

                if (pageLoadingStatus.firstPage) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadFirstPage();
                        }
                    });
                } else {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadNextPage();
                        }
                    });
                }
            } else {
                mTextView.setText(null);
                if (extraPageMessage != null && extraPageMessage.detailMessage != null) {
                    mTextView.append(" " + extraPageMessage.detailMessage);
                }
            }
        }
    }

    private class ViewHolderItemData extends RecyclerViewGroupAdapter.RecyclerViewGroupHolder {

        private final TextView mTextView;

        public ViewHolderItemData(RecyclerViewGroupAdapter groupAdapter, LayoutInflater inflater, ViewGroup parent) {
            super(groupAdapter, inflater, parent, R.layout.sample_data_list_view_item_data);
            mTextView = findViewByID(R.id.text);
        }

        @Override
        protected void update(@NonNull Object object, int position) {
            mTextView.setText(String.valueOf(object));
        }
    }

}
