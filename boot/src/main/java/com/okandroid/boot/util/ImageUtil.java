package com.okandroid.boot.util;

import com.facebook.cache.common.CacheKey;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.okandroid.boot.data.FrescoManager;
import com.okandroid.boot.thread.ThreadPool;

import java.util.concurrent.Executor;

/**
 * Created by idonans on 2017/2/6.
 */

public class ImageUtil {

    private ImageUtil() {
    }

    public static void cacheImageWithFresco(String imageUrl) {
        // init fresco is need.
        FrescoManager.getInstance();

        final ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
        DataSource<Void> dataSource = Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, null);
        dataSource.subscribe(new BaseDataSubscriber<Void>() {
            @Override
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                CacheKey cacheKey = Fresco.getImagePipeline().getCacheKeyFactory().getEncodedCacheKey(imageRequest, null);
            }

            @Override
            protected void onFailureImpl(DataSource<Void> dataSource) {
                // TODO
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {
                ThreadPool.getInstance().post(command);
            }
        });
    }

}
