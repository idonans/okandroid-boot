package com.sample.boot.module.splash;

import com.okandroid.boot.app.ext.dynamic.DynamicViewData;
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
    protected DynamicViewData onInitBackground() {
        Threads.sleepQuietly(2000);
        return new DynamicViewData() {
        };
    }

    @Override
    public void onReady() {
        SplashView view = getView();
        if (view == null) {
            return;
        }

        view.directToSignIn();
    }

}
