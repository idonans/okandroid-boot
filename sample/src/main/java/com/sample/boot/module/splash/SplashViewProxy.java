package com.sample.boot.module.splash;

import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.viewproxy.ViewProxy;

/**
 * Created by idonans on 2017/2/3.
 */

public class SplashViewProxy extends ViewProxy<SplashView> {

    public SplashViewProxy(SplashView splashView) {
        super(splashView);
    }

    @Override
    protected void onInitBackground() {
        super.onInitBackground();

        Threads.sleepQuietly(2000);
    }

    @Override
    protected void onLoading() {
    }

    @Override
    protected void onStart() {
        SplashView view = getView();
        if (view == null) {
            return;
        }

        if (view.isViewResumed()) {
            view.directToSignIn();
        }
    }

}
