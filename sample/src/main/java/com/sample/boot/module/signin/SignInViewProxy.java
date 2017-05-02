package com.sample.boot.module.signin;

import android.support.annotation.Nullable;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.HumanUtil;
import com.okandroid.boot.util.ImageUtil;
import com.sample.boot.app.BaseViewProxy;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

        replaceDefaultRequestHolder(Single.just("1")
                .delay(3000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoadingView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }
                        e.printStackTrace();
                        view.hideLoadingView();
                    }
                }));
    }

}
