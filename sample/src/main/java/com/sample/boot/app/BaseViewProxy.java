package com.sample.boot.app;

import com.okandroid.boot.app.ext.dynamic.DynamicViewProxy;

/**
 * Created by idonans on 2017/2/15.
 */

public abstract class BaseViewProxy<T extends BaseView> extends DynamicViewProxy<T> {

    public BaseViewProxy(T view) {
        super(view);
    }

}
