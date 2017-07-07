package com.okandroid.boot.app.ext.dynamic;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.okandroid.boot.lang.ClassName;
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

public abstract class DynamicViewProxy<T extends DynamicView> extends ViewProxy<T> {

    private final String CLASS_NAME = ClassName.valueOf(this);

    // 是否已经初始化
    private boolean mInit;

    public DynamicViewProxy(T view) {
        super(view);
    }

    /**
     * 数据是否已经初始化
     *
     * @return
     */
    public final boolean isInit() {
        return mInit;
    }

    public final void setInit(boolean init) {
        mInit = init;
    }

    public final void startInit() {
        Threads.postBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v(CLASS_NAME, "call onInitBackground");
                    onInitBackground();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                setInit(true);
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        T view = getView();
                        if (view == null) {
                            return;
                        }

                        view.notifyInitComplete();
                    }
                });
            }
        });
    }

    /**
     * 子线程加载初始化数据, 可以加载网络数据, 在数据的加载过程中, 视图上正在显示一个 initContentView
     */
    protected abstract void onInitBackground();

    private boolean mPrepared;

    public final boolean isPrepared() {
        return mPrepared;
    }

    private void setPrepared() {
        if (isPrepared()) {
            new IllegalAccessError("already prepared").printStackTrace();
        }
        mPrepared = true;
    }

    /**
     * complete content view 已经创建好
     */
    @CallSuper
    public void onCompleteContentViewCreated() {
        Log.v(CLASS_NAME, "onCompleteContentViewCreated");
        if (!isInit()) {
            throw new IllegalAccessError("not init");
        }
        setPrepared();
        requestUpdateCompleteContentViewIfChanged();
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

    /**
     * 请求刷新 complete content view 内容
     */
    public void requestUpdateCompleteContentViewIfChanged() {
        T view = getView();
        if (view == null) {
            return;
        }

        if (!isInit()) {
            return;
        }

        if (!isPrepared()) {
            return;
        }

        if (!view.isReallyForeground()) {
            return;
        }

        if (!mCalledReady) {
            mCalledReady = true;
            Log.v(CLASS_NAME, "call onReady");
            onReady();
        }

        view.onUpdateCompleteContentViewIfChanged();
    }

    private boolean mCalledReady;

    /**
     * 初始化完成时调用, 先于页面上的 onUpdateCompleteContentViewIfChanged, 仅调用一次
     */
    public abstract void onReady();

}
