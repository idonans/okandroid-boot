package com.okandroid.boot.lang;

import com.okandroid.boot.util.AvailableUtil;

import java.lang.ref.WeakReference;

/**
 * 弱引用关系
 * Created by idonans on 16-4-13.
 */
public class WeakAvailable implements Available {

    private WeakReference<Object> mWeakReference = new WeakReference<>(null);

    public WeakAvailable(Object object) {
        mWeakReference = new WeakReference<>(object);
    }

    @Override
    public boolean isAvailable() {
        return AvailableUtil.isAvailable(mWeakReference.get());
    }

    public void setObject(Object object) {
        mWeakReference = new WeakReference<>(object);
    }

    public Object getObject() {
        return mWeakReference.get();
    }

}
