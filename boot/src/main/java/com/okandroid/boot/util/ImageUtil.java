package com.okandroid.boot.util;

import android.support.annotation.Nullable;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.okandroid.boot.data.FrescoManager;
import com.okandroid.boot.thread.ThreadPool;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Created by idonans on 2017/2/6.
 */

public class ImageUtil {

    private ImageUtil() {
    }

    public interface ImageFileFetchListener {
        void onFileFetched(@Nullable File file);
    }

    public static void cacheImageWithFresco(String imageUrl, final ImageFileFetchListener listener) {
        // init fresco is need.
        FrescoManager.getInstance();

        final ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
        DataSource<Void> dataSource = Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, null);
        dataSource.subscribe(new BaseDataSubscriber<Void>() {
            @Override
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                try {
                    dataSource.getResult();
                    CacheKey cacheKey = Fresco.getImagePipeline().getCacheKeyFactory().getEncodedCacheKey(imageRequest, null);
                    BinaryResource binaryResource = Fresco.getImagePipelineFactory().getMainFileCache().getResource(cacheKey);
                    File file = ((FileBinaryResource) binaryResource).getFile();
                    listener.onFileFetched(file);
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                listener.onFileFetched(null);
            }

            @Override
            protected void onFailureImpl(DataSource<Void> dataSource) {
                listener.onFileFetched(null);
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {
                ThreadPool.getInstance().post(command);
            }
        });
    }

}
