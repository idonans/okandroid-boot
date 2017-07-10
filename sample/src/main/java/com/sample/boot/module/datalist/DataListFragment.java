package com.sample.boot.module.datalist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okandroid.boot.app.ext.dynamic.DynamicViewData;
import com.okandroid.boot.app.ext.page.PageFragment;
import com.okandroid.boot.app.ext.page.PageViewProxy;
import com.okandroid.boot.app.ext.page.adapter.PageStatusDataAdapter;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.widget.MaxLineViewFrameLayout;
import com.okandroid.boot.widget.MaxLineViewHelper;
import com.okandroid.boot.widget.RecyclerViewGroupAdapter;
import com.sample.boot.R;

/**
 * Created by idonans on 2017/4/21.
 */

public class DataListFragment extends PageFragment implements DataListView {

    private static final String TAG = "DataListFragment";

    @Override
    protected PageViewProxy newDefaultViewProxy() {
        return new DataListViewProxy(this);
    }

    @Override
    protected com.okandroid.boot.widget.PageDataAdapter createPageDataAdapter(RecyclerView recyclerView) {
        return new DataListAdapter(recyclerView);
    }

    @Override
    protected PageContentView createPageContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView, @NonNull DynamicViewData dynamicViewData) {
        return new PageContentView(activity, inflater, contentView, R.layout.sample_data_list_view);
    }

    private class DataListAdapter extends PageStatusDataAdapter {

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
        private final TextView mExpandControl;
        private final MaxLineViewFrameLayout mMaxLineView;
        private final MaxLineViewHelper mMaxLineViewHelper;

        private DataListViewProxy.Item mItem;

        public ViewHolderItemData(RecyclerViewGroupAdapter groupAdapter, LayoutInflater inflater, ViewGroup parent) {
            super(groupAdapter, inflater, parent, R.layout.sample_data_list_view_item_data);
            mMaxLineView = findViewByID(R.id.max_line_view);
            mTextView = findViewByID(R.id.text);
            mExpandControl = findViewByID(R.id.expand_control);

            mMaxLineViewHelper = new MaxLineViewHelper(mMaxLineView, new MaxLineViewHelper.ExpandUpdateListener() {
                @Override
                public int getCurrentLines() {
                    int lineCount = mTextView.getLineCount();
                    Log.d(TAG + " ViewHolderItemData getCurrentLines: " + lineCount);
                    // return mTextView.getLineCount();
                    return lineCount;
                }

                @Override
                public void onExpandUpdate(boolean expand, int allLines) {
                    Log.d(TAG + " ViewHolderItemData onExpandUpdate expand: " + expand + ", allLines: " + allLines);
                    if (mItem != null) {
                        mItem._expand = expand;
                    }

                    final int limitLine = 5;

                    if (allLines <= limitLine) {
                        // 总行数不足, 隐藏 展开/关闭 按钮
                        mTextView.setMaxLines(Integer.MAX_VALUE);
                        mExpandControl.setVisibility(View.GONE);
                    } else {
                        if (expand) {
                            // 展开状态
                            mTextView.setMaxLines(Integer.MAX_VALUE);
                            mExpandControl.setVisibility(View.VISIBLE);
                            mExpandControl.setText("收起");
                        } else {
                            // 收起状态
                            mTextView.setMaxLines(limitLine);
                            mExpandControl.setVisibility(View.VISIBLE);
                            mExpandControl.setText("展开");
                        }
                    }
                }

                @Override
                public void onReset() {
                    Log.d(TAG + " ViewHolderItemData onReset");
                    mTextView.setMaxLines(Integer.MAX_VALUE);
                    mExpandControl.setVisibility(View.GONE);
                }
            });
            mExpandControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaxLineViewHelper.toggle();
                }
            });
        }

        @Override
        protected void update(@NonNull Object object, int position) {
            Log.d(TAG + " ViewHolderItemData update position: " + position);

            mItem = (DataListViewProxy.Item) object;

            mTextView.setText(mItem.content);
            mMaxLineViewHelper.update(mItem._expand);
        }
    }

}
