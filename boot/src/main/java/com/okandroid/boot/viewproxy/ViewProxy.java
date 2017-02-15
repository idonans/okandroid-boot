package com.okandroid.boot.viewproxy;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.util.AvailableUtil;

import java.io.Closeable;
import java.io.IOException;

/**
 * <pre>
 *     ViewProxyImpl mViewProxy = new ViewProxyImpl(this);
 * </pre>
 * <p>
 * Created by idonans on 2016/11/18.
 */
public abstract class ViewProxy<VIEW> implements Available, Closeable {

    private VIEW mView;

    public ViewProxy(VIEW view) {
        mView = view;
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
