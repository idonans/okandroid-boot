package com.okandroid.boot.app.ext.page;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.dynamic.DynamicFragment;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.boot.widget.PageDataAdapter;
import com.okandroid.boot.widget.ptr.PtrLayout;

import java.util.Collection;
import java.util.Map;

/**
 * Created by idonans on 2017/4/20.
 */

public abstract class PageFragment extends DynamicFragment implements PageView {

    @Override
    protected abstract PageViewProxy newDefaultViewProxy();

    protected PageContentView mPageContentView;

    @Override
    protected void showCompleteContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mPageContentView);
        mPageContentView = createPageContentView(activity, inflater, contentView);
    }

    protected PageContentView createPageContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        return new PageContentView(activity, inflater, contentView, R.layout.okandroid_ext_page_view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IOUtil.closeQuietly(mPageContentView);
        mPageContentView = null;
    }

    @Override
    protected void onSaveDataObject(@NonNull Map retainObject) {
        super.onSaveDataObject(retainObject);

        if (mPageContentView != null) {
            mPageContentView.onSaveDataObject(retainObject);
        }
    }

    protected class PageContentView extends ContentViewHelper {

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

        private static final String SAVE_KEY_PAGE_RECYCLER_SCROLL = "okandroid.save.key.PAGE_RECYCLER_SCROLL";

        protected void init() {
            mPtrLayout = ViewUtil.findViewByID(mRootView, R.id.ptr_layout);
            mRecyclerView = ViewUtil.findViewByID(mRootView, R.id.recycler_view);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

            mPageDataAdapter = createPageDataAdapter(mRecyclerView);
            mPageDataAdapter.onRestoreDataObject(getRetainDataObject());

            mPageDataAdapter.setOnMoreLoadListener(new PageDataAdapter.OnMoreLoadListener() {
                @Override
                public void onLoadMore() {
                    loadNextPage();
                }
            });
            mRecyclerView.setAdapter(mPageDataAdapter);

            Object adapterPositionObject = getRetainDataObject().get(SAVE_KEY_PAGE_RECYCLER_SCROLL);
            if (adapterPositionObject != null) {
                mRecyclerView.scrollToPosition((Integer) adapterPositionObject);
            }

            mPtrLayout.setOnRefreshListener(new PtrLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadFirstPage();
                }
            });
        }

        /**
         * 保存数据
         */
        protected void onSaveDataObject(@NonNull Map retainObject) {
            if (mPageDataAdapter != null) {
                mPageDataAdapter.onSaveDataObject(retainObject);
            }

            if (mRecyclerView != null) {
                if (mRecyclerView.getChildCount() > 0) {
                    int adapterPosition = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(0));
                    retainObject.put(SAVE_KEY_PAGE_RECYCLER_SCROLL, adapterPosition);
                }
            }
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

    protected void loadFirstPage() {
        PageViewProxy proxy = getDefaultViewProxy();
        if (proxy != null) {
            proxy.loadFirstPage();
        }
    }

    protected void loadNextPage() {
        PageViewProxy proxy = getDefaultViewProxy();
        if (proxy != null) {
            proxy.loadNextPage();
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
    public PageViewProxy getDefaultViewProxy() {
        return (PageViewProxy) super.getDefaultViewProxy();
    }

}
