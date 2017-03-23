package com.sample.boot.module.signin;

import android.support.annotation.Nullable;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.FileUtil;
import com.okandroid.boot.util.HumanUtil;
import com.okandroid.boot.util.ImageUtil;
import com.sample.boot.app.BaseViewProxy;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInViewProxy extends BaseViewProxy<SignInView> {

    private static final String TAG = "SignInViewProxy";

    public SignInViewProxy(SignInView signInView) {
        super(signInView);
    }

    @Override
    protected void onPreDataLoadBackground() {
        Threads.sleepQuietly(5000);
    }

    public void prefetchImage() {
        if (!isPrepared()) {
            return;
        }

        SignInView view = getView();
        if (view == null) {
            return;
        }

        File localFile = FileUtil.createNewTmpFileQuietly("share", ".tmp", FileUtil.getExternalCacheDir());
        if (localFile == null) {
            Log.d(TAG + " fail to create tmp file for share");
            return;
        }

        ImageUtil.cacheImageWithFresco(
                "http://img.zcool.cn/community/016c5258d0d7e8a801219c77d69fa1.jpg",
                200,
                200,
                new ImageUtil.ImageFileFetchListener() {
                    @Override
                    public void onFileFetched(@Nullable File file) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(TAG + " cacheImageWithFresco for thumb onFileFetched ");
                        if (file == null) {
                            builder.append("file is null");
                        } else {
                            builder.append("file path: " + file.getAbsolutePath());
                            if (!file.exists()) {
                                builder.append(" not exists");
                            } else {
                                builder.append(" length: " + HumanUtil.getHumanSizeFromByte(file.length()));
                            }
                        }
                        Log.d(builder);
                    }
                });

        ImageUtil.cacheImageWithFresco(
                "http://img.zcool.cn/community/016c5258d0d7e8a801219c77d69fa1.jpg",
                new ImageUtil.ImageFileFetchListener() {
                    @Override
                    public void onFileFetched(@Nullable File file) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(TAG + " cacheImageWithFresco onFileFetched ");
                        if (file == null) {
                            builder.append("file is null");
                        } else {
                            builder.append("file path: " + file.getAbsolutePath());
                            if (!file.exists()) {
                                builder.append(" not exists");
                            } else {
                                builder.append(" length: " + HumanUtil.getHumanSizeFromByte(file.length()));
                            }
                        }
                        Log.d(builder);
                    }
                });
    }

    public void testLoading() {
        if (!isPrepared()) {
            return;
        }

        SignInView view = getView();
        if (view == null) {
            return;
        }

        view.showLoadingView();

        replaceDefaultSubscription(Observable.just("1")
                .delay(3000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoadingView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }
                        e.printStackTrace();
                        view.hideLoadingView();
                    }

                    @Override
                    public void onNext(String s) {
                    }
                }));
    }

}
