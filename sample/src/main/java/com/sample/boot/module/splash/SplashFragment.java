package com.sample.boot.module.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.viewproxy.ViewProxy;
import com.sample.boot.R;
import com.sample.boot.app.viewproxy.BaseViewProxyFragment;

/**
 * Created by idonans on 2017/2/3.
 */

public class SplashFragment extends BaseViewProxyFragment implements SplashView {

    public static SplashFragment newInstance() {
        Bundle args = new Bundle();
        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            throw new IllegalStateException("container is null");
        }
        return inflater.inflate(R.layout.sample_splash_view, container, false);
    }

    @Override
    protected ViewProxy newDefaultViewProxy() {
        return new SplashViewProxy(this);
    }

    @Nullable
    @Override
    public SplashViewProxy getDefaultViewProxy() {
        return (SplashViewProxy) super.getDefaultViewProxy();
    }

}
