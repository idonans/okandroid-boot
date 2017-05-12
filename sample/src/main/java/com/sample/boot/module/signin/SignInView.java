package com.sample.boot.module.signin;

import com.sample.boot.app.BaseView;

/**
 * Created by idonans on 2017/2/3.
 */

public interface SignInView extends BaseView {
    void hideDownloadAndInstallApkDialog();

    void showDownloadAndInstallApkDialog(SignInViewProxy.ApkInstallInfo apkInstallInfo);

    void updateDownloadAndInstallApkDialog(SignInViewProxy.ApkInstallInfo apkInstallInfo);

    void showDownloadAndInstallApkFail();

}
