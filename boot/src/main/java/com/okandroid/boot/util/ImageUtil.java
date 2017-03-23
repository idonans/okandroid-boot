package com.okandroid.boot.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.memory.PooledByteBufferInputStream;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.okandroid.boot.data.FrescoManager;
import com.okandroid.boot.thread.Threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * Created by idonans on 2017/2/6.
 */

public class ImageUtil {

    private static final String TAG = "ImageUtil";
    private static final String CACHE_FILE_PREFIX = "boot_image_cache";
    private static final Executor DEFAULT_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            Threads.postBackground(command);
        }
    };

    private ImageUtil() {
    }

    public interface ImageFileFetchListener {
        void onFileFetched(@Nullable File file);
    }

    private static class OnceImageFileFetchListener implements ImageFileFetchListener {

        private ImageFileFetchListener mOutListener;

        private OnceImageFileFetchListener(ImageFileFetchListener listener) {
            mOutListener = listener;
        }

        @Override
        public void onFileFetched(@Nullable File file) {
            if (mOutListener != null) {
                mOutListener.onFileFetched(file);
            }
            mOutListener = null;
        }

    }

    /**
     * 载入指定图片到本地磁盘
     */
    public static void cacheImageWithFresco(final String imageUrl, ImageFileFetchListener listener) {
        // init fresco if need.
        FrescoManager.getInstance();

        final OnceImageFileFetchListener onceListener = new OnceImageFileFetchListener(listener);
        if (TextUtils.isEmpty(imageUrl)) {
            onceListener.onFileFetched(null);
            return;
        }

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl));
        builder.setRotationOptions(RotationOptions.autoRotate());

        final ImageRequest imageRequest = builder.build();

        Fresco.getImagePipeline().fetchEncodedImage(imageRequest, null)
                .subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
                    @Override
                    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        FileOutputStream fos = null;
                        InputStream is = null;
                        File errFile = null;
                        CloseableReference<PooledByteBuffer> buffer = null;
                        try {
                            String extension = FileUtil.getFileExtensionFromUrl(imageUrl);
                            if (extension != null) {
                                extension = "." + extension;
                            }
                            File targetFile = FileUtil.createNewTmpFileQuietly(
                                    CACHE_FILE_PREFIX,
                                    extension,
                                    FileUtil.getExternalCacheDir());
                            if (targetFile == null) {
                                return;
                            }

                            errFile = targetFile;
                            FileUtil.createNewFileQuietly(targetFile);

                            fos = new FileOutputStream(targetFile);

                            buffer = dataSource.getResult();
                            is = new PooledByteBufferInputStream(buffer.get());

                            long copy = IOUtil.copy(is, fos, AvailableUtil.always(), null);
                            if (copy > 0) {
                                errFile = null;
                                onceListener.onFileFetched(targetFile);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            IOUtil.closeQuietly(buffer);
                            IOUtil.closeQuietly(is);
                            IOUtil.closeQuietly(fos);
                            FileUtil.deleteFileQuietly(errFile);
                            onceListener.onFileFetched(null);
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        onceListener.onFileFetched(null);
                    }
                }, DEFAULT_EXECUTOR);
    }

    /**
     * 载入指定图片的缩略图到本地磁盘
     */
    public static void cacheImageWithFresco(String imageUrl, int width, int height, ImageFileFetchListener listener) {
        // init fresco if need.
        FrescoManager.getInstance();

        final OnceImageFileFetchListener onceListener = new OnceImageFileFetchListener(listener);
        if (TextUtils.isEmpty(imageUrl)) {
            onceListener.onFileFetched(null);
            return;
        }

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl));
        builder.setRotationOptions(RotationOptions.autoRotate());
        if (width > 0 && height > 0) {
            builder.setResizeOptions(ResizeOptions.forDimensions(width, height));
        }

        final ImageRequest imageRequest = builder.build();

        Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null)
                .subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    protected void onNewResultImpl(Bitmap bitmap) {
                        FileOutputStream fos = null;
                        File errFile = null;
                        try {
                            File targetFile = FileUtil.createNewTmpFileQuietly(
                                    CACHE_FILE_PREFIX,
                                    ".jpg",
                                    FileUtil.getExternalCacheDir());
                            if (targetFile == null) {
                                return;
                            }

                            errFile = targetFile;
                            FileUtil.createNewFileQuietly(targetFile);

                            fos = new FileOutputStream(targetFile);
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)) {
                                errFile = null;
                                onceListener.onFileFetched(targetFile);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            IOUtil.closeQuietly(fos);
                            FileUtil.deleteFileQuietly(errFile);
                            onceListener.onFileFetched(null);
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        onceListener.onFileFetched(null);
                    }
                }, DEFAULT_EXECUTOR);
    }

}
