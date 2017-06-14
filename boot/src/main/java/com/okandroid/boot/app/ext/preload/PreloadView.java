package com.okandroid.boot.app.ext.preload;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by idonans on 2017/2/15.
 */

public interface PreloadView {

    /**
     * 一个用来辅助保存数据的对象, 以便于恢复上一次的状态
     */
    @NonNull
    Map getRetainDataObject();

    boolean requestBackPressed();

    boolean isLoadingViewShown();

    void showLoadingView();

    void hideLoadingView();

    void notifyPreDataPrepared();

}
