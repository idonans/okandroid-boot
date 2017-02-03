package com.sample.boot.module.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.viewproxy.ViewProxy;
import com.sample.boot.R;
import com.sample.boot.app.viewproxy.BaseViewFragment;
import com.sample.boot.module.signin.SignInActivity;

/**
 * Created by idonans on 2017/2/3.
 */

public class SplashFragment extends BaseViewFragment implements SplashView {

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
        activity.finish();
        activity.overridePendingTransition(0, 0);
        return true;
    }

}
