package com.sample.boot.app.viewproxy;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;

import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.viewproxy.ViewProxy;
import com.sample.boot.app.BaseFragment;

/**
 * Created by idonans on 2017/2/3.
 */

public abstract class ViewProxyBaseFragment extends BaseFragment implements ViewProxyBaseFragmentView {

    @Override
    public boolean isViewAvailable() {
        return isAvailable() && getView() != null;
    }

    @Override
    public boolean isViewResumed() {
        return isViewAvailable() && isAppCompatResumed();
    }

    private ViewProxy mDefaultViewProxy;

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            createDefaultViewProxy();
        }
    }

    private void createDefaultViewProxy() {
        closeDefaultViewProxy();
        mDefaultViewProxy = newDefaultViewProxy();
        mDefaultViewProxy.start();
    }

    @Nullable
    public ViewProxy getDefaultViewProxy() {
        return mDefaultViewProxy;
    }

    protected abstract ViewProxy newDefaultViewProxy();

    private void closeDefaultViewProxy() {
        IOUtil.closeQuietly(mDefaultViewProxy);
        mDefaultViewProxy = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDefaultViewProxy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDefaultViewProxy();
    }

}
