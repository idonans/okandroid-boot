package com.okandroid.boot.app.ext.preload;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.rx.DisposableHolder;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.viewproxy.ViewProxy;

import java.io.IOException;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class PreloadViewProxy<T extends PreloadView> extends ViewProxy<T> {

    private final String CLASS_NAME = getClass().getSimpleName();
    // 前置数据是否已经准备好
    private boolean mPreDataPrepared;

    public PreloadViewProxy(T view) {
        super(view);
    }

    /**
     * 前置数据是否已经准备好
     *
     * @return
     */
    public final boolean isPreDataPrepared() {
        return mPreDataPrepared;
    }

    public final void setPreDataPrepared(boolean preDataPrepared) {
        mPreDataPrepared = preDataPrepared;
    }

    public final void startLoadPreData() {
        Threads.postBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    onPreDataLoadBackground();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                setPreDataPrepared(true);
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        T view = getView();
                        if (view == null) {
                            return;
                        }
                        view.notifyPreDataPrepared();
                    }
                });
            }
        });
    }

    /**
     * 后台处理需要异步加载的前置数据
     */
    protected abstract void onPreDataLoadBackground();

    private boolean mPrepared;

    public final boolean isPrepared() {
        return mPrepared;
    }

    /**
     * 实际内容 view 已经显示，需要的前置数据也已经加载
     */
    @CallSuper
    public void onPrepared() {
        if (!isPreDataPrepared()) {
            throw new IllegalAccessError("pre data not prepared");
        }
        if (isPrepared()) {
            new IllegalAccessError("already prepared").printStackTrace();
        }
        mPrepared = true;
    }

    private final DisposableHolder mDefaultRequestHolder = new DisposableHolder();

    public void replaceDefaultRequestHolder(Disposable disposable) {
        mDefaultRequestHolder.setDisposable(disposable);
    }

    public boolean requestBackPressed() {
        T view = getView();
        if (view != null) {
            return view.requestBackPressed();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        mDefaultRequestHolder.clear();
        super.close();
    }

    /**
     * 保存数据
     */
    @CallSuper
    protected void onSaveDataObject(@NonNull Map retainObject) {
        Log.v(CLASS_NAME, "onSaveDataObject");
    }

    /**
     * 恢复数据
     */
    @CallSuper
    protected void onRestoreDataObject(@NonNull Map retainObject) {
        Log.v(CLASS_NAME, "onRestoreDataObject");
    }

}
