package com.sample.boot.module.splash;

import com.okandroid.boot.thread.Threads;
import com.sample.boot.app.BaseViewProxy;

/**
 * Created by idonans on 2017/2/3.
 */

public class SplashViewProxy extends BaseViewProxy<SplashView> {

    public SplashViewProxy(SplashView splashView) {
        super(splashView);
    }

    @Override
    protected void onInitBackground() {
        Threads.sleepQuietly(2000);
    }

    @Override
    public void onCompleteContentViewCreated() {
        super.onCompleteContentViewCreated();

        SplashView view = getView();
        if (view == null) {
            return;
        }

        view.directToSignIn();
    }

}
