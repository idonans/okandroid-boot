package com.okandroid.boot.app.ext.preload;

import com.okandroid.boot.rx.SubscriptionHolder;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.viewproxy.ViewProxy;

import java.io.IOException;

import rx.Subscription;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class PreloadViewProxy<T extends PreloadView> extends ViewProxy<T> {

    private boolean mPrepared;

    public PreloadViewProxy(T view) {
        super(view);
    }

    public boolean isPrepared() {
        return mPrepared;
    }

    public void setPrepared(boolean prepared) {
        mPrepared = prepared;
    }

    public void startPrepare() {
        Threads.postBackground(new Runnable() {
            @Override
            public void run() {
                prepareBackground();
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPrepared()) {
                            throw new IllegalStateException("not prepared, need setPrepared(true) on prepareBackground()");
                        }

                        T view = getView();
                        if (view == null) {
                            return;
                        }
                        view.notifyViewProxyPrepared();
                    }
                });
            }
        });
    }

    protected abstract void prepareBackground();

    public void onPrepared() {
        if (!isPrepared()) {
            throw new IllegalStateException("not prepared");
        }
    }

    public T getResumedView() {
        T view = getView();
        if (view == null) {
            return null;
        }
        if (view.isViewResumed()) {
            return view;
        }
        return null;
    }

    private final SubscriptionHolder mDefaultSubscriptionHolder = new SubscriptionHolder();

    public void replaceDefaultSubscription(Subscription subscription) {
        mDefaultSubscriptionHolder.setSubscription(subscription);
    }

    public boolean onBackClick() {
        T view = getResumedView();
        if (view != null) {
            return view.callActivityBackPressed();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        mDefaultSubscriptionHolder.clear();
        super.close();
    }

}
