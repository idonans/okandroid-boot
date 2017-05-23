package com.okandroid.boot.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.okandroid.boot.data.FrescoManager;
import com.okandroid.boot.data.TmpFileManager;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * 使用临时文件加载缓存图 {@link com.okandroid.boot.data.TmpFileManager}
 * Created by idonans on 2017/2/6.
 */

public class ImageCacheUtil {

    private static final String TAG = "ImageCacheUtil";
    private static final String CACHE_IMAGE_PREFIX = "image_cache";
    private static final String CACHE_IMAGE_THUMB_PREFIX = "image_cache_thumb";
    private static final Executor DEFAULT_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            Threads.postBackground(command);
        }
    };

    private ImageCacheUtil() {
    }

    /**
     * 缓存指定图片到本地磁盘
     */
    public static void cacheImage(final String imageUrl, ImageCacheListener listener) {
        // init fresco if need.
        FrescoManager.getInstance();

        final OnceImageCacheListener onceListener = new OnceImageCacheListener(listener);
        if (TextUtils.isEmpty(imageUrl)) {
            onceListener.onImageCached(null);
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
                            File targetFile = TmpFileManager.getInstance().createNewTmpFileQuietly(
                                    CACHE_IMAGE_PREFIX,
                                    extension);
                            if (targetFile == null) {
                                throw new IllegalArgumentException("fail to create target file");
                            }

                            errFile = targetFile;
                            FileUtil.createNewFileQuietly(targetFile);

                            fos = new FileOutputStream(targetFile);

                            buffer = dataSource.getResult();
                            is = new PooledByteBufferInputStream(buffer.get());

                            long copy = IOUtil.copy(is, fos, AvailableUtil.always(), null);
                            if (copy > 0) {
                                errFile = null;
                                onceListener.onImageCached(targetFile);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            IOUtil.closeQuietly(buffer);
                            IOUtil.closeQuietly(is);
                            IOUtil.closeQuietly(fos);
                            FileUtil.deleteFileQuietly(errFile);
                            onceListener.onImageCached(null);
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        Log.v(TAG, "cacheImage onFailureImpl");
                        onceListener.onImageCached(null);
                    }
                }, DEFAULT_EXECUTOR);
    }

    /**
     * 载入指定图片的缩略图到本地磁盘, 如果缩略图尺寸不合适, 会自动调整到合适尺寸
     *
     * @param maxFileSize 传递 -1 表示不限制图片文件尺寸
     */
    public static void cacheImageThumb(final String imageUrl, final int width, final int height, final long maxFileSize, final ImageCacheListener listener) {
        // init fresco if need.
        FrescoManager.getInstance();

        final OnceImageCacheListener onceListener = new OnceImageCacheListener(listener);
        if (TextUtils.isEmpty(imageUrl)) {
            onceListener.onImageCached(null);
            return;
        }

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl));
        builder.setRotationOptions(RotationOptions.autoRotate());

        final int resizeWidth;
        final int resizeHeight;
        if (width <= 0 || width > 1024) {
            resizeWidth = 1024;
        } else {
            resizeWidth = width;
        }
        if (height <= 0 || height > 1024) {
            resizeHeight = 1024;
        } else {
            resizeHeight = height;
        }

        builder.setResizeOptions(ResizeOptions.forDimensions(resizeWidth, resizeHeight));
        builder.setImageDecodeOptions(
                ImageDecodeOptions.newBuilder()
                        .setForceStaticImage(true)
                        .setBitmapConfig(Bitmap.Config.RGB_565)
                        .build());

        final ImageRequest imageRequest = builder.build();

        Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null)
                .subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    protected void onNewResultImpl(Bitmap bitmap) {
                        FileOutputStream fos = null;
                        File errFile = null;
                        try {
                            File targetFile = TmpFileManager.getInstance().createNewTmpFileQuietly(
                                    CACHE_IMAGE_THUMB_PREFIX,
                                    ".jpg");
                            if (targetFile == null) {
                                throw new IllegalArgumentException("fail to create target file");
                            }

                            errFile = targetFile;
                            FileUtil.createNewFileQuietly(targetFile);

                            fos = new FileOutputStream(targetFile);
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)) {

                                fos.close();

                                // 排除过小的文件尺寸
                                final long minFileLength = 8 * HumanUtil.KB;
                                final long fileLength = targetFile.length();

                                if (fileLength <= 0) {
                                    // error
                                    throw new IllegalAccessException("invalid file length " + fileLength);
                                } else if (maxFileSize <= 0 || fileLength <= minFileLength || fileLength <= maxFileSize) {
                                    // success
                                    errFile = null;
                                    onceListener.onImageCached(targetFile);
                                } else {
                                    // file length too large, scale down file size
                                    onceListener.clear();
                                    // guess scale size
                                    float scaleSize = (Math.max(minFileLength, maxFileSize)) * 1f / fileLength * 1.5f;
                                    scaleSize = Math.max(0.5f, scaleSize);
                                    scaleSize = Math.min(0.8f, scaleSize);
                                    Log.v(TAG, "file size too large, scale size and try again",
                                            "fileLength:", HumanUtil.getHumanSizeFromByte(fileLength),
                                            "maxFileSize:", HumanUtil.getHumanSizeFromByte(maxFileSize),
                                            "minFileLength:", HumanUtil.getHumanSizeFromByte(minFileLength),
                                            "width:", width, "height:", height, "scaleSize:", scaleSize);
                                    cacheImageThumb(imageUrl, (int) (width * scaleSize), (int) (height * scaleSize), maxFileSize, listener);
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            IOUtil.closeQuietly(fos);
                            FileUtil.deleteFileQuietly(errFile);
                            onceListener.onImageCached(null);
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        Log.v(TAG, "cacheImageThumb onFailureImpl");
                        onceListener.onImageCached(null);
                    }
                }, DEFAULT_EXECUTOR);
    }

    public interface ImageCacheListener {
        void onImageCached(@Nullable File file);
    }

    private static class OnceImageCacheListener implements ImageCacheListener {

        private ImageCacheListener mOutListener;

        private OnceImageCacheListener(ImageCacheListener listener) {
            mOutListener = listener;
        }

        private void clear() {
            mOutListener = null;
        }

        @Override
        public void onImageCached(@Nullable File file) {
            if (mOutListener != null) {
                mOutListener.onImageCached(file);
            }
            mOutListener = null;
        }

    }

}
