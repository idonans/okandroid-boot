package com.okandroid.boot.app.ext.dynamic;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.okandroid.boot.R;
import com.okandroid.boot.ext.loadingstatus.LoadingStatus;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusNetworkErrorLarge;
import com.okandroid.boot.ext.loadingstatus.LoadingStatusUnknownErrorLarge;
import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.lang.ResumedViewClickListener;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.NetUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.boot.widget.ContentLoadingView;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class DynamicFragment extends DynamicBaseFragment {

    private static final String TAG = "DynamicFragment";
    protected static final int DYNAMIC_CONTENT_VIEW_ID = R.id.okandroid_dynamic_content;

    @Override
    protected View onCreateViewSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        FrameLayout content = new FrameLayout(activity);
        content.setId(DYNAMIC_CONTENT_VIEW_ID);
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
        DynamicViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            Log.e(TAG + " view proxy is null");
            return;
        }

        mContentView = ViewUtil.findViewByID(view, DYNAMIC_CONTENT_VIEW_ID);
        if (mContentView == null) {
            Log.e(TAG + " content view not found");
            return;
        }

        if (viewProxy.isInit()) {
            notifyInitSuccess();
        } else {
            viewProxy.startInit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentView = null;
    }

    @Override
    public void notifyInitFail() {
        View view = getView();
        if (view == null) {
            new IllegalAccessError("view is null").printStackTrace();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return;
        }

        Activity activity = getActivity();
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

        DynamicViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            Log.e(TAG + " view proxy is null");
            return;
        }

        if (viewProxy.isInit()) {
            Log.e(TAG, " view proxy already init");
            return;
        }

        showInitFailContentView(activity, inflater, mContentView);

        // 动态添加 view 后, 如果添加的 view 中有 fitSystemWindow 的内容, 需要刷新 window insets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mContentView.requestApplyInsets();
        } else {
            mContentView.requestFitSystemWindows();
        }
    }

    @Override
    public void notifyInitLoading() {
        View view = getView();
        if (view == null) {
            new IllegalAccessError("view is null").printStackTrace();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return;
        }

        Activity activity = getActivity();
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

        DynamicViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            Log.e(TAG + " view proxy is null");
            return;
        }

        if (viewProxy.isInit()) {
            Log.e(TAG, " view proxy already init");
            return;
        }

        showInitLoadingContentView(activity, inflater, mContentView);

        // 动态添加 view 后, 如果添加的 view 中有 fitSystemWindow 的内容, 需要刷新 window insets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mContentView.requestApplyInsets();
        } else {
            mContentView.requestFitSystemWindows();
        }
    }

    @Override
    public void notifyInitSuccess() {
        View view = getView();
        if (view == null) {
            new IllegalAccessError("view is null").printStackTrace();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return;
        }

        Activity activity = getActivity();
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

        DynamicViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            Log.e(TAG + " view proxy is null");
            return;
        }

        if (!viewProxy.isInit()) {
            Log.e(TAG, "view proxy not init");
            return;
        }

        DynamicViewData dynamicViewData = viewProxy.getDynamicViewData();
        if (dynamicViewData == null) {
            Log.e(TAG, "dynamic view data is null");
            return;
        }

        showInitSuccessContentView(activity, inflater, mContentView, dynamicViewData);

        // 动态添加 view 后, 如果添加的 view 中有 fitSystemWindow 的内容, 需要刷新 window insets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mContentView.requestApplyInsets();
        } else {
            mContentView.requestFitSystemWindows();
        }

        viewProxy.onInitSuccessContentViewCreated();

        // clear saved retain object
        getRetainDataObject().clear();
    }

    /**
     * proxy 在初始化数据过程中显示的 content view
     */
    protected void showInitLoadingContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        ContentLoadingView loadingView = new ContentLoadingView(activity);
        loadingView.showLoading();
        new ContentViewHelper(activity, inflater, contentView, loadingView);
    }

    protected void showInitFailContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        LoadingStatus loadingStatus;
        if (!NetUtil.hasActiveNetwork()) {
            // 网络错误
            loadingStatus = new LoadingStatusNetworkErrorLarge(activity, inflater, contentView);
        } else {
            // 未知错误
            loadingStatus = new LoadingStatusUnknownErrorLarge(activity, inflater, contentView);
        }

        if (loadingStatus.itemRetry != null) {
            loadingStatus.itemRetry.setOnClickListener(new ResumedViewClickListener(DynamicFragment.this) {
                @Override
                public void onClick(View v, ResumedViewClickListener listener) {
                    DynamicViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy != null) {
                        viewProxy.startInit();
                    }
                }
            });
        }
        new ContentViewHelper(activity, inflater, contentView, loadingStatus.view);
    }

    /**
     * proxy 初始化数据完成时显示的 content view
     */
    protected abstract void showInitSuccessContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView, @NonNull DynamicViewData dynamicViewData);

    /**
     * 每一个 content view 是一个独立的部分, 后创建的 content view 会覆盖之前创建的 content view 内容(前面的 content view 会从 view tree 中删除)
     */
    protected class ContentViewHelper implements Closeable, Available {

        public final Activity mActivity;
        public final LayoutInflater mInflater;
        public final ViewGroup mParentView;
        public final View mRootView;

        public ContentViewHelper(Activity activity, LayoutInflater inflater, ViewGroup parentView, View rootView) {
            parentView.removeAllViews();

            mActivity = activity;
            mInflater = inflater;
            mParentView = parentView;
            mRootView = rootView;
            mParentView.addView(mRootView);
        }

        public ContentViewHelper(Activity activity, LayoutInflater inflater, ViewGroup parentView, int layout) {
            this(activity, inflater, parentView, inflater.inflate(layout, parentView, false));
        }

        @Override
        public void close() throws IOException {
            mParentView.removeView(mRootView);
        }

        @Override
        public boolean isAvailable() {
            return DynamicFragment.this.isAvailable() && mRootView.getParent() != null;
        }

    }

    @Override
    public boolean isReallyForeground() {
        return isAppCompatResumed()
                && !isHidden()
                && getUserVisibleHint();
    }

    @Override
    public void onResume() {
        super.onResume();

        requestProxyUpdateCompleteContentViewIfChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            requestProxyUpdateCompleteContentViewIfChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            requestProxyUpdateCompleteContentViewIfChanged();
        }
    }

    public void requestProxyUpdateCompleteContentViewIfChanged() {
        DynamicViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy != null) {
            viewProxy.requestUpdateContentViewIfChanged();
        }
    }

}
