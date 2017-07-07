package com.okandroid.boot.app.ext.dynamic;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by idonans on 2017/2/15.
 */

public interface DynamicView {

    /**
     * 一个用来辅助保存数据的对象, 以便于恢复上一次的状态
     */
    @NonNull
    Map getRetainDataObject();

    boolean requestBackPressed();

    boolean isLoadingViewShown();

    void showLoadingView();

    void hideLoadingView();

    /**
     * 初始化结束, 不能确保所有的初始数据都正确加载了. 例如: 一个依赖网络的数据可能会初始化失败.
     * 视图部分, 此时会切换到 content view 视图
     */
    void notifyInitComplete();

}
