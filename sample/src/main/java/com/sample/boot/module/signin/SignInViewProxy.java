package com.sample.boot.module.signin;

import android.support.annotation.Nullable;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;
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

        ImageUtil.cacheImageWithFresco("https://avatars3.githubusercontent.com/u/4043830?v=3&s=460", new ImageUtil.ImageFileFetchListener() {
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
