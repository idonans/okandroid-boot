package com.okandroid.boot.app.ext.preload;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.okandroid.boot.R;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.SystemUtil;
import com.okandroid.boot.util.ViewUtil;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class PreloadFragment extends PreloadBaseFragment {

    private static final String TAG = "PreloadFragment";
    protected static final int PRELOAD_CONTENT_VIEW_ID = R.id.okandroid_preload_content;

    @Override
    protected View onCreateViewSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        FrameLayout content = new FrameLayout(activity);
        content.setId(PRELOAD_CONTENT_VIEW_ID);
        ViewGroup.LayoutParams contentParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        content.setLayoutParams(contentParams);
        return content;
    }

    private ViewGroup mContentView;

    @Override
    protected void onViewCreatedSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull View view) {
        PreloadViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            Log.e(TAG + " view proxy is null");
            return;
        }

        mContentView = ViewUtil.findViewByID(view, PRELOAD_CONTENT_VIEW_ID);
        if (mContentView == null) {
            Log.e(TAG + " content view not found");
            return;
        }

        if (viewProxy.isPrepared()) {
            notifyViewProxyPrepared();
        } else {
            showPreloadLoadingView(activity, inflater, mContentView);
            viewProxy.startPrepare();
        }
    }

    @Override
    public void notifyViewProxyPrepared() {
        View view = getView();
        if (view == null) {
            new IllegalAccessError("view is null").printStackTrace();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return;
        }

        Activity activity = SystemUtil.getActivityFromFragment(this);
        if (activity == null) {
            new IllegalAccessError("activity is null").printStackTrace();
            return;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            new IllegalAccessError("activity is not available").printStackTrace();
            return;
        }

        if (mContentView == null) {
            Log.e(TAG + " content view not found");
            return;
        }

        hidePreloadLoadingView(activity, inflater, mContentView);
        showPreloadContentView(activity, inflater, mContentView);
    }

    protected abstract void hidePreloadLoadingView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView);

    protected abstract void showPreloadLoadingView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView);

    protected abstract void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView);

}
