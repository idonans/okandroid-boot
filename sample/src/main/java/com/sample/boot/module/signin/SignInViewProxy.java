package com.sample.boot.module.signin;

import android.support.annotation.Nullable;

import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.HumanUtil;
import com.okandroid.boot.util.ImageUtil;
import com.okandroid.boot.viewproxy.ViewProxy;

import java.io.File;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInViewProxy extends ViewProxy<SignInView> {

    private static final String TAG = "SignInViewProxy";

    public SignInViewProxy(SignInView signInView) {
        super(signInView);
    }

    @Override
    protected void onLoading() {

    }

    @Override
    protected void onStart() {

    }

    public void prefetchImage() {
        runAfterInit(true, new Runnable() {
            @Override
            public void run() {
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
        });
    }

}
