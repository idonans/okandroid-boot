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
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.ThreadPool;
import com.okandroid.boot.thread.Threads;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Created by idonans on 2017/2/6.
 */

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    private ImageUtil() {
    }

    public interface ImageFileFetchListener {
        void onFileFetched(@Nullable File file);
    }

    public static void cacheImageWithFresco(String imageUrl, final ImageFileFetchListener listener) {
        // init fresco if need.
        FrescoManager.getInstance();

        final ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
        DataSource<Void> dataSource = Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, null);
        dataSource.subscribe(new BaseDataSubscriber<Void>() {
            @Override
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                tryFindLocalCacheFile(imageRequest, listener, true);
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

    private static void tryFindLocalCacheFile(final ImageRequest imageRequest, final ImageFileFetchListener listener, final boolean first) {
        try {
            CacheKey cacheKey = Fresco.getImagePipeline().getCacheKeyFactory().getEncodedCacheKey(imageRequest, null);
            BinaryResource binaryResource = Fresco.getImagePipelineFactory().getMainFileCache().getResource(cacheKey);
            if (binaryResource == null && first) {
                // 第一次加载本地缓存文件失败，可能是因为 libimagepipeline.so 未加载, 此处做一个稍微的延时后重试一次
                Log.d(TAG + " tryFindLocalCacheFile BinaryResource is null, will try again with small delay.");
                Threads.postBackground(new Runnable() {
                    @Override
                    public void run() {
                        Threads.sleepQuietly(100);
                        tryFindLocalCacheFile(imageRequest, listener, false);
                    }
                });
                return;
            } else {
                File file = ((FileBinaryResource) binaryResource).getFile();
                listener.onFileFetched(file);
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        listener.onFileFetched(null);
    }

}
