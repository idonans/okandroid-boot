package com.sample.boot.module.datalist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okandroid.boot.app.ext.pageloading.PageLoadingFragment;
import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.app.ext.pageloading.adapter.PageLoadingDataAdapter;
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

    private class DataListAdapter extends PageLoadingDataAdapter {

        private final LayoutInflater mLayoutInflater;

        public DataListAdapter(RecyclerView recyclerView) {
            super(recyclerView);
            mLayoutInflater = getLayoutInflater(null);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case GROUP_PAGE_CONTENT_DEFAULT:
                    return new ViewHolderItemData(this, mLayoutInflater, parent);
                default:
                    return super.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void checkAndNotifyLoadMore(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG + " checkAndNotifyLoadMore position:" + position + ", adapter position:" + holder.getAdapterPosition() + ", layout position:" + holder.getLayoutPosition());
            super.checkAndNotifyLoadMore(holder, position);
        }

        @Override
        protected void reloadPageData(int pageNo) {
            if (pageNo == 0) {
                loadFirstPage();
            } else {
                loadNextPage();
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
