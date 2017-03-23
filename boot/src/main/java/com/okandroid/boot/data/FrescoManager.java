package com.okandroid.boot.data;

import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.logging.FLogDefaultLoggingDelegate;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
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

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (App.isUse565Config()) {
            config = Bitmap.Config.RGB_565;
        }

        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(AppContext.getContext())
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
                .setBitmapsConfig(config)
                .build();

        FLogDefaultLoggingDelegate fLogDefaultLoggingDelegate = FLogDefaultLoggingDelegate.getInstance();
        fLogDefaultLoggingDelegate.setApplicationTag(App.getBuildConfigAdapter().getLogTag());
        if (App.getBuildConfigAdapter().isDebug()) {
            fLogDefaultLoggingDelegate.setMinimumLoggingLevel(FLog.DEBUG);
        }
        Fresco.initialize(AppContext.getContext(), imagePipelineConfig);
    }

}
