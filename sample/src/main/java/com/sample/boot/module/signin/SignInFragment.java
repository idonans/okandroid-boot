package com.sample.boot.module.signin;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.okandroid.boot.viewproxy.ViewProxy;
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
