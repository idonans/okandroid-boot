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

    boolean isLoadingDialogShown();

    void showLoadingDialog();

    void hideLoadingDialog();

    /**
     * 初始化中
     */
    void notifyInitLoading();

    /**
     * 初始化成功
     */
    void notifyInitSuccess();

    /**
     * 初始化失败
     */
    void notifyInitFail();

    /**
     * 判断当前视图是否正在前台显示.
     * 例如: 如果当前视图处于 view pager 中的不可见页, 则不认为是在前台显示.
     */
    boolean isReallyForeground();

    /**
     * 此方法可能被重复调用, 注意执行性能, 过滤不必要的更新.
     * 在页面重回前台(打开, 从其他页面返回, view pager 切换至等), 并且准备好基础数据时, 会调用此方法.
     * 在更新数据时, 应当相对于当前页面上的数据做对比, 如果有变更, 才更新到新内容.
     * 当前页面的状态可能处于初始化中, 初始化失败, 或者初始化成功
     * <p>
     * 行为上可以理解为 Activity 层面的 onResume
     */
    void onUpdateContentViewIfChanged();

}
