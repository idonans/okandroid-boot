package com.okandroid.boot.app.ext.pageloading;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.okandroid.boot.util.IOUtil;

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

    protected abstract PageContentView createPageContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView);

    protected class PageContentView extends PreloadSubViewHelper {

        public PageContentView(Activity activity, LayoutInflater inflater, ViewGroup parentView, View rootView) {
            super(activity, inflater, parentView, rootView);
        }

        public PageContentView(Activity activity, LayoutInflater inflater, ViewGroup parentView, int layout) {
            super(activity, inflater, parentView, layout);
        }

    }

}
