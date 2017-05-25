package com.sample.boot.module.splash;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.util.IOUtil;
import com.sample.boot.R;
import com.sample.boot.app.BaseFragment;
import com.sample.boot.module.signin.SignInActivity;

/**
 * Created by idonans on 2017/2/3.
 */

public class SplashFragment extends BaseFragment implements SplashView {

    public static SplashFragment newInstance() {
        Bundle args = new Bundle();
        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public SplashViewProxy getDefaultViewProxy() {
        return (SplashViewProxy) super.getDefaultViewProxy();
    }

    @Override
    protected PreloadViewProxy newDefaultViewProxy() {
        return new SplashViewProxy(this);
    }

    @Override
    public boolean directToSignIn() {
        if (!isAppCompatResumed()) {
            return false;
        }

        FragmentActivity activity = getActivity();
        if (activity == null) {
            return false;
        }

        startActivity(SignInActivity.startIntent(activity));
        activity.overridePendingTransition(0, 0);
        activity.finish();
        return true;
    }

    @Override
    protected void showPreloadLoadingView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        // ignore
    }

    @Override
    protected void hidePreloadLoadingView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        // ignore
    }

    private Content mContent;

    @Override
    protected void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mContent);
        mContent = new Content(activity, inflater, contentView);
    }

    private class Content extends PreloadSubViewHelper {

        private Content(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
            super(activity, inflater, contentView, R.layout.sample_splash_view);
        }

    }

}
