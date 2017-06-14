package com.okandroid.boot.app.ext.preload;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.backpressed.BackPressedFragment;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.widget.ContentLoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class PreloadBaseFragment extends BackPressedFragment implements PreloadView {

    private final String CLASS_NAME = getClass().getSimpleName();
    // 用于在 fragment 前后台切换时保存数据, 此过程 ui 部分会销毁和重建, 因此数据中不能包含 ui 的引用
    private final Map mRetainDataObject = new HashMap();

    @NonNull
    @Override
    public Map getRetainDataObject() {
        return mRetainDataObject;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            new IllegalAccessError("container is null").printStackTrace();
            return null;
        }

        if (inflater == null) {
            new IllegalAccessError("inflater is null").printStackTrace();
            return null;
        }

        Activity activity = getActivity();
        if (activity == null) {
            new IllegalAccessError("activity is null").printStackTrace();
            return null;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            new IllegalAccessError("activity is not available").printStackTrace();
            return null;
        }

        return onCreateViewSafety(activity, inflater, container);
    }

    protected abstract View onCreateViewSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

        createDefaultViewProxy();
        onViewCreatedSafety(activity, inflater, view);
    }

    private void createDefaultViewProxy() {
        closeDefaultViewProxy();
        mDefaultViewProxy = newDefaultViewProxy();
        if (mDefaultViewProxy != null) {
            mDefaultViewProxy.onRestoreDataObject(getRetainDataObject());
        } else {
            Log.v(CLASS_NAME, "not create default view proxy");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 在视图销毁时保存数据
        // 保存数据之前, 清空可能的旧的数据
        getRetainDataObject().clear();
        onSaveDataObject(getRetainDataObject());
        PreloadViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy != null) {
            viewProxy.onSaveDataObject(getRetainDataObject());
        }

        closeDefaultViewProxy();
        hideLoadingView();
    }

    /**
     * 保存数据
     */
    @CallSuper
    protected void onSaveDataObject(@NonNull Map retainObject) {
        Log.v(CLASS_NAME, "onSaveDataObject");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDefaultViewProxy();
        hideLoadingView();

        // clear retain data object
        getRetainDataObject().clear();
    }

    @Nullable
    public PreloadViewProxy getDefaultViewProxy() {
        return mDefaultViewProxy;
    }

    protected abstract PreloadViewProxy newDefaultViewProxy();

    private void closeDefaultViewProxy() {
        IOUtil.closeQuietly(mDefaultViewProxy);
        mDefaultViewProxy = null;
    }

    protected abstract void onViewCreatedSafety(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull View view);

    private PreloadViewProxy mDefaultViewProxy;

    @Override
    public boolean onBackPressed() {
        if (isLoadingViewShown()) {
            hideLoadingView();
            return true;
        }

        return super.onBackPressed();
    }

    private ContentLoadingDialog mContentLoadingDialog;

    @Override
    public boolean isLoadingViewShown() {
        return mContentLoadingDialog != null && mContentLoadingDialog.isShowing();
    }

    @Override
    public void showLoadingView() {
        if (!isAvailable()) {
            return;
        }
        Activity activity = getActivity();
        if (!AvailableUtil.isAvailable(activity)) {
            return;
        }

        if (mContentLoadingDialog == null) {
            mContentLoadingDialog = new ContentLoadingDialog(activity);
        }
        mContentLoadingDialog.show();
    }

    @Override
    public void hideLoadingView() {
        if (mContentLoadingDialog != null) {
            mContentLoadingDialog.dismiss();
        }
    }

}
