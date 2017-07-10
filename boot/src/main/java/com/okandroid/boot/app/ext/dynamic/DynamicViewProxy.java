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

    // 初始化数据
    protected DynamicViewData mDynamicViewData;

    public DynamicViewProxy(T view) {
        super(view);
    }

    /**
     * 数据是否已经初始化
     *
     * @return
     */
    public final boolean isInit() {
        return mDynamicViewData != null;
    }

    /**
     * @return true if changed.
     */
    public final boolean setInit(DynamicViewData dynamicViewData) {
        if (mDynamicViewData != null) {
            Log.e(CLASS_NAME, "already init");
            return false;
        }
        mDynamicViewData = dynamicViewData;
        return true;
    }

    public DynamicViewData getDynamicViewData() {
        return mDynamicViewData;
    }

    public final void startInit() {
        replaceDefaultRequestHolder(null);

        DynamicView view = getView();
        if (view == null) {
            return;
        }

        if (!isInit()) {
            Log.e(CLASS_NAME, "already init");
            return;
        }

        view.notifyInitLoading();

        Threads.postBackground(new Runnable() {
            @Override
            public void run() {
                DynamicViewData dynamicViewData = null;
                try {
                    Log.v(CLASS_NAME, "call onInitBackground");
                    dynamicViewData = onInitBackground();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (!setInit(dynamicViewData)) {
                    return;
                }
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        T view = getView();
                        if (view == null) {
                            return;
                        }

                        if (isInit()) {
                            view.notifyInitSuccess();
                        } else {
                            view.notifyInitFail();
                        }
                    }
                });
            }
        });
    }

    /**
     * 子线程加载初始化数据, 可以加载网络数据, 在数据的加载过程中, 视图上正在显示一个 initContentView. 如果返回空, 或者抛出异常, 则视为初始化失败.
     */
    protected abstract DynamicViewData onInitBackground();

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
    public void onInitSuccessContentViewCreated() {
        Log.v(CLASS_NAME, "onInitSuccessContentViewCreated");
        if (!isInit()) {
            throw new IllegalAccessError("not init");
        }
        setPrepared();
        requestUpdateContentViewIfChanged();
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
    public void requestUpdateContentViewIfChanged() {
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

        view.onUpdateContentViewIfChanged();
    }

    private boolean mCalledReady;

    /**
     * 初始化完成时调用, 先于页面上的 onUpdateContentViewIfChanged, 仅调用一次
     */
    public abstract void onReady();

}
