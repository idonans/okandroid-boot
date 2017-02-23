package com.okandroid.boot.app.ext.preload;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.backpressed.BackPressedFragment;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.SystemUtil;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class PreloadBaseFragment extends BackPressedFragment implements PreloadView {

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

        Activity activity = SystemUtil.getActivityFromFragment(this);
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

        Activity activity = SystemUtil.getActivityFromFragment(this);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDefaultViewProxy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDefaultViewProxy();
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

    @Override
    public boolean isViewAvailable() {
        return isAvailable() && getView() != null;
    }

    @Override
    public boolean isViewResumed() {
        return isViewAvailable() && isAppCompatResumed();
    }

    private PreloadViewProxy mDefaultViewProxy;

    @Override
    public boolean onBackPressed() {
        if (isLoadingViewShown()) {
            hideLoadingView();
            return true;
        }

        return super.onBackPressed();
    }

    private AlertDialog mLoadingDialog;

    private boolean isLoadingDialogShown() {
        return mLoadingDialog != null;
    }

    private void showLoadingDialog() {
        hideLoadingDialog();

        Activity activity = getActivity();

        if (!AvailableUtil.isAvailable(activity)) {
            return;
        }

        mLoadingDialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setMessage("加载中...")
                .show();
    }

    private void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public boolean isLoadingViewShown() {
        return isLoadingDialogShown();
    }

    @Override
    public void showLoadingView() {
        showLoadingDialog();
    }

    @Override
    public void hideLoadingView() {
        hideLoadingDialog();
    }

}