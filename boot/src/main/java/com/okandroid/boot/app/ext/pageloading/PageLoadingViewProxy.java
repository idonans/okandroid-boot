package com.okandroid.boot.app.ext.pageloading;

import com.okandroid.boot.app.ext.preload.PreloadViewProxy;

/**
 * Created by idonans on 2017/4/20.
 */

public class PageLoadingViewProxy<T extends PageLoadingView> extends PreloadViewProxy<T> {

    public PageLoadingViewProxy(T view) {
        super(view);
    }

    @Override
    protected void onPreDataLoadBackground() {

    }

    @Override
    public void onPrepared() {
        super.onPrepared();
    }

}
