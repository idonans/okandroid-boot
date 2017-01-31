package com.okandroid.boot.viewproxy;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.thread.TaskQueue;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.AvailableUtil;

import java.io.Closeable;
import java.io.IOException;

/**
 * <pre>
 *     ViewProxyImpl mViewProxy = new ViewProxyImpl(this);
 *     mViewProxy.start();
 * </pre>
 * <p>
 * Created by idonans on 2016/11/18.
 */
public abstract class ViewProxy<VIEW> implements Available, Closeable {

    private final TaskQueue mQueue = new TaskQueue(1);
    private VIEW mView;
    private boolean mInit;

    public ViewProxy(VIEW view) {
        mView = view;
        enqueueRunnable(new Runnable() {
            @Override
            public void run() {
                onInitBackground();
                mInit = true;
            }
        });
    }

    public void start() {
        onLoading();
        runAfterInit(true, new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
    }

    @UiThread
    protected abstract void onLoading();

    @UiThread
    protected abstract void onStart();

    /**
     * 如果当前 presenter 未完成初始化，则会在初始化完成之后执行
     */
    public void runAfterInit(final boolean runOnUi, final Runnable runnable) {
        if (!isAvailable() || !AvailableUtil.isAvailable(runnable)) {
            return;
        }

        if (mInit) {
            if (runOnUi) {
                Threads.runOnUi(runnable);
            } else {
                enqueueRunnable(runnable);
            }
        } else {
            enqueueRunnable(new Runnable() {
                @Override
                public void run() {
                    // assert mInit;
                    if (!AvailableUtil.isAvailable(runnable)) {
                        return;
                    }

                    if (runOnUi) {
                        Threads.runOnUi(runnable);
                    } else {
                        runnable.run();
                    }
                }
            });
        }
    }

    private void enqueueRunnable(final Runnable runnable) {
        mQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                if (ViewProxy.this.isAvailable() && AvailableUtil.isAvailable(runnable)) {
                    runnable.run();
                }
            }
        });
    }

    @WorkerThread
    @CallSuper
    protected void onInitBackground() {
    }

    /**
     * if is not available, will return null.
     */
    public VIEW getView() {
        return isAvailable() ? mView : null;
    }

    @Override
    public boolean isAvailable() {
        return AvailableUtil.isAvailable(mView);
    }

    @Override
    public void close() throws IOException {
        mView = null;
    }

}
