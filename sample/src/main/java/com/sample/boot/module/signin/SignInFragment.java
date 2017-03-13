package com.sample.boot.module.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.SystemUtil;
import com.okandroid.boot.util.ViewUtil;
import com.sample.boot.R;
import com.sample.boot.app.BaseFragment;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInFragment extends BaseFragment implements SignInView {

    public static SignInFragment newInstance() {
        Bundle args = new Bundle();
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public SignInViewProxy getDefaultViewProxy() {
        return (SignInViewProxy) super.getDefaultViewProxy();
    }

    @Override
    protected PreloadViewProxy newDefaultViewProxy() {
        return new SignInViewProxy(this);
    }

    private Content mContent;

    @Override
    protected void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mContent);
        mContent = new Content(activity, inflater, contentView);
    }

    private class Content extends PreloadSubViewHelper {

        private final View mPrefetchImage;
        private final View mTestLoading;
        private final View mTestOpenUrl;

        private Content(Activity activity, LayoutInflater inflater, ViewGroup contentView) {
            super(activity, inflater, contentView, R.layout.sample_sign_in_view);
            mPrefetchImage = ViewUtil.findViewByID(mRootView, R.id.prefetch_image);
            mPrefetchImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }
                    viewProxy.prefetchImage();
                }
            });

            mTestLoading = ViewUtil.findViewByID(mRootView, R.id.test_loading);
            mTestLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }
                    viewProxy.testLoading();
                }
            });

            mTestOpenUrl = ViewUtil.findViewByID(mRootView, R.id.test_open_url);
            mTestOpenUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }

                    String url = "https://www.baidu.com";
                    url = "market://details?id=com.zcool.community";
                    SystemUtil.openView(url);
                }
            });
        }

    }

}
