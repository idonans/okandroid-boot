package com.sample.boot.module.signin;

import com.okandroid.boot.util.ImageUtil;
import com.okandroid.boot.viewproxy.ViewProxy;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInViewProxy extends ViewProxy<SignInView> {

    public SignInViewProxy(SignInView signInView) {
        super(signInView);
    }

    @Override
    protected void onLoading() {

    }

    @Override
    protected void onStart() {

    }

    public void prefetchImage() {
        runAfterInit(true, new Runnable() {
            @Override
            public void run() {
                SignInView view = getView();
                if (view == null) {
                    return;
                }

                ImageUtil.cacheImageWithFresco("https://avatars3.githubusercontent.com/u/4043830?v=3&s=460");
            }
        });
    }

}
