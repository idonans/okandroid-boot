package com.okandroid.boot.rx;

import android.support.annotation.Nullable;

import io.reactivex.disposables.Disposable;

/**
 * 辅助控制 Disposable 的释放
 * Created by idonans on 17-04-26.
 */
public class DisposableHolder {

    private Disposable mDisposable;

    /**
     * close last disposable if found and hold new one.
     *
     * @param disposable
     */
    public void setDisposable(@Nullable Disposable disposable) {
        if (mDisposable == disposable) {
            return;
        }
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }
        mDisposable = disposable;
    }

    /**
     * close last disposable if found.
     */
    public void clear() {
        setDisposable(null);
    }

}
