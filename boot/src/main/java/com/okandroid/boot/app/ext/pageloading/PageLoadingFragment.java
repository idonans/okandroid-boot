package com.okandroid.boot.app.ext.pageloading;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.boot.widget.PageDataAdapter;
import com.okandroid.boot.widget.ptr.PtrLayout;

import java.util.Collection;

/**
 * Created by idonans on 2017/4/20.
 */

public abstract class PageLoadingFragment extends PreloadFragment implements PageLoadingView {

    @Override
    protected abstract PageLoadingViewProxy newDefaultViewProxy();

    protected PageContentView mPageContentView;

    @Override
    protected void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mPageContentView);
        mPageContentView = createPageContentView(activity, inflater, contentView);
    }

    protected PageContentView createPageContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        return new PageContentView(activity, inflater, contentView, R.layout.okandroid_ext_pageloading_view);
    }

    protected class PageContentView extends PreloadSubViewHelper {

        public PageContentView(Activity activity, LayoutInflater inflater, ViewGroup parentView, View rootView) {
            super(activity, inflater, parentView, rootView);
            init();
        }

        public PageContentView(Activity activity, LayoutInflater inflater, ViewGroup parentView, int layout) {
            super(activity, inflater, parentView, layout);
            init();
        }

        protected PtrLayout mPtrLayout;
        protected RecyclerView mRecyclerView;

        protected PageDataAdapter mPageDataAdapter;

        protected void init() {
            mPtrLayout = ViewUtil.findViewByID(mRootView, R.id.ptr_layout);
            mRecyclerView = ViewUtil.findViewByID(mRootView, R.id.recycler_view);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

            mPageDataAdapter = createPageDataAdapter(mRecyclerView);
            mPageDataAdapter.setOnMoreLoadListener(new PageDataAdapter.OnMoreLoadListener() {
                @Override
                public void onLoadMore() {
                    PageLoadingViewProxy proxy = getDefaultViewProxy();
                    if (proxy != null) {
                        proxy.loadNextPage();
                    }
                }
            });
            mRecyclerView.setAdapter(mPageDataAdapter);

            mPtrLayout.setOnRefreshListener(new PtrLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    PageLoadingViewProxy proxy = getDefaultViewProxy();
                    if (proxy != null) {
                        proxy.loadFirstPage();
                    }
                }
            });
        }

        protected void showPageLoadingStatus(PageDataAdapter.PageLoadingStatus pageLoadingStatus) {
            mPageDataAdapter.showPageLoadingStatus(pageLoadingStatus, new PageDataAdapter.ExtraPageLoadingStatusCallback() {
                @Override
                public void showSwipeRefreshing() {
                    mPtrLayout.setRefreshing(true);
                }

                @Override
                public void hideSwipeRefreshing() {
                    mPtrLayout.setRefreshing(false);
                }

                @Override
                public void enableSwipeRefreshing() {
                    mPtrLayout.setEnabled(true);
                }

                @Override
                public void disableSwipeRefreshing() {
                    mPtrLayout.setEnabled(false);
                }
            });
        }

        protected void showPageContent(boolean firstPage, Collection data) {
            mPageDataAdapter.showPageData(firstPage, data);
        }
    }

    @Override
    public void showPageLoadingStatus(PageDataAdapter.PageLoadingStatus pageLoadingStatus) {
        if (AvailableUtil.isAvailable(mPageContentView)) {
            mPageContentView.showPageLoadingStatus(pageLoadingStatus);
        }
    }

    @Override
    public void showPageContent(boolean firstPage, Collection data) {
        if (AvailableUtil.isAvailable(mPageContentView)) {
            mPageContentView.showPageContent(firstPage, data);
        }
    }

    protected abstract PageDataAdapter createPageDataAdapter(RecyclerView recyclerView);

    @Nullable
    @Override
    public PageLoadingViewProxy getDefaultViewProxy() {
        return (PageLoadingViewProxy) super.getDefaultViewProxy();
    }

}
