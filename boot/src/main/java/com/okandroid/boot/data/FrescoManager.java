package com.okandroid.boot.data;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.logging.FLogDefaultLoggingDelegate;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.okandroid.boot.App;
import com.okandroid.boot.AppContext;
import com.okandroid.boot.util.FileUtil;

import java.io.File;

/**
 * fresco 图片加载. 如果有扩展卡，则将图片换存在扩展卡上，否则缓存在内置空间上。
 * Created by idonans on 16-5-3.
 */
public class FrescoManager {

    private static class InstanceHolder {

        private static final FrescoManager sInstance = new FrescoManager();

    }

    private static boolean sInit;

    public static FrescoManager getInstance() {
        FrescoManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private FrescoManager() {
        File frescoCacheBaseDir = FileUtil.getExternalCacheDir();
        if (frescoCacheBaseDir == null) {
            frescoCacheBaseDir = FileUtil.getCacheDir();
        }

        FLogDefaultLoggingDelegate fLogDefaultLoggingDelegate = FLogDefaultLoggingDelegate.getInstance();
        fLogDefaultLoggingDelegate.setApplicationTag(App.getBuildConfigAdapter().getLogTag());
        if (App.getBuildConfigAdapter().isDebug()) {
            fLogDefaultLoggingDelegate.setMinimumLoggingLevel(FLog.DEBUG);
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (App.isUse565Config()) {
            config = Bitmap.Config.RGB_565;
        }

        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(AppContext.getContext())
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(AppContext.getContext())
                        .setBaseDirectoryPath(frescoCacheBaseDir)
                        .setBaseDirectoryName("fresco_main_disk_" + ProcessManager.getInstance().getProcessTag())
                        .build())
                .setSmallImageDiskCacheConfig(DiskCacheConfig.newBuilder(AppContext.getContext())
                        .setBaseDirectoryPath(frescoCacheBaseDir)
                        .setBaseDirectoryName("fresco_small_disk_" + ProcessManager.getInstance().getProcessTag())
                        .build())
                .setNetworkFetcher(new OkHttpNetworkFetcher(OkHttpManager.getInstance().getOkHttpClient()))
                .setDownsampleEnabled(true)
                .setBitmapsConfig(config);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(new OKAndroidBitmapMemoryCacheParamsSupplier());
        }

        Fresco.initialize(AppContext.getContext(), imagePipelineConfigBuilder.build());
    }

    private static class OKAndroidBitmapMemoryCacheParamsSupplier implements Supplier<MemoryCacheParams> {
        private static final int MAX_CACHE_ENTRIES = 256;
        private static final int MAX_EVICTION_QUEUE_SIZE = 1;
        private static final int MAX_EVICTION_QUEUE_ENTRIES = 1;
        private static final int MAX_CACHE_ENTRY_SIZE = Integer.MAX_VALUE;

        private final ActivityManager mActivityManager;

        public OKAndroidBitmapMemoryCacheParamsSupplier() {
            mActivityManager = (ActivityManager) AppContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        }

        @Override
        public MemoryCacheParams get() {
            return new MemoryCacheParams(
                    getMaxCacheSize(),
                    MAX_CACHE_ENTRIES,
                    MAX_EVICTION_QUEUE_SIZE,
                    MAX_EVICTION_QUEUE_ENTRIES,
                    MAX_CACHE_ENTRY_SIZE);
        }

        private int getMaxCacheSize() {
            final int maxMemory =
                    Math.min(mActivityManager.getMemoryClass() * ByteConstants.MB, Integer.MAX_VALUE);
            return maxMemory / 6;
        }
    }

}
