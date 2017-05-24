package com.okandroid.boot.app.ext.preload;

/**
 * Created by idonans on 2017/2/15.
 */

public interface PreloadView {

    boolean requestBackPressed();

    boolean isLoadingViewShown();

    void showLoadingView();

    void hideLoadingView();

    void notifyPreDataPrepared();

}
