package com.sample.boot.module.signin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.viewproxy.ViewProxy;
import com.sample.boot.R;
import com.sample.boot.app.viewproxy.BaseViewFragment;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInFragment extends BaseViewFragment implements SignInView {

    public static SignInFragment newInstance() {
        Bundle args = new Bundle();
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            throw new IllegalStateException("container is null");
        }

        return inflater.inflate(R.layout.sample_sign_in_view, container, false);
    }

    @Override
    protected ViewProxy newDefaultViewProxy() {
        return new SignInViewProxy(this);
    }

    @Nullable
    @Override
    public SignInViewProxy getDefaultViewProxy() {
        return (SignInViewProxy) super.getDefaultViewProxy();
    }

}
