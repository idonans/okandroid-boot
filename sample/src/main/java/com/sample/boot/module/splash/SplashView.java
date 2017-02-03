package com.sample.boot.module.splash;

import com.sample.boot.app.viewproxy.BaseView;

/**
 * Created by idonans on 2017/2/3.
 */

public interface SplashView extends BaseView {

    /**
     * @return direct success return true, otherwise return false.
     */
    boolean directToSignIn();

}
