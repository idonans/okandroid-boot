package com.sample.boot.module.signin;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.okandroid.boot.data.OkHttpManager;
import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.lang.Progress;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.FileUtil;
import com.okandroid.boot.util.HumanUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ImageCacheUtil;
import com.okandroid.boot.util.SystemUtil;
import com.sample.boot.app.BaseViewProxy;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInViewProxy extends BaseViewProxy<SignInView> {

    private static final String TAG = "SignInViewProxy";

    public SignInViewProxy(SignInView signInView) {
        super(signInView);
    }

    @Override
    protected void onInitBackground() {
        Threads.sleepQuietly(5000);
    }

    @Override
    public void onReady() {
    }

    public void prefetchImage() {
        if (!isPrepared()) {
            return;
        }

        SignInView view = getView();
        if (view == null) {
            return;
        }

        ImageCacheUtil.cacheImageThumb(
                "http://img.zcool.cn/community/016c5258d0d7e8a801219c77d69fa1.jpg",
                1024,
                1024,
                32 * HumanUtil.KB,
                new ImageCacheUtil.ImageCacheListener() {
                    @Override
                    public void onImageCached(@Nullable File file) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(TAG + " cacheImageThumb onImageCached ");
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

        ImageCacheUtil.cacheImage(
                "http://img.zcool.cn/community/016c5258d0d7e8a801219c77d69fa1.jpg",
                new ImageCacheUtil.ImageCacheListener() {
                    @Override
                    public void onImageCached(@Nullable File file) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(TAG + " cacheImage onImageCached ");
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

    public void startDownloadAndInstallApk(final String apkUrl) {
        // clear old apk downloader
        mApkDownloader = null;

        if (!isPrepared()) {
            return;
        }

        SignInView view = getView();
        if (view == null) {
            return;
        }

        view.hideLoadingView();
        view.hideDownloadAndInstallApkDialog();

        final ApkInstallInfo info = new ApkInstallInfo();
        info.apkUrl = apkUrl;

        replaceDefaultRequestHolder(Single
                .fromCallable(new Callable<ApkDownloader>() {
                    @Override
                    public ApkDownloader call() throws Exception {
                        mApkDownloader = new ApkDownloader(info);
                        mApkDownloader.start();
                        return mApkDownloader;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApkDownloader>() {
                    @Override
                    public void accept(@NonNull ApkDownloader apkDownloader) throws Exception {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }

                        view.showDownloadAndInstallApkDialog(apkDownloader.mApkInstallInfo.copy());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        SignInView view = getView();
                        if (view == null) {
                            return;
                        }

                        view.hideDownloadAndInstallApkDialog();
                        view.showDownloadAndInstallApkFail();

                        throwable.printStackTrace();
                    }
                }));
    }

    public static class ApkInstallInfo {
        public String apkUrl;
        public String localApkFile;
        /**
         * [0, 100]
         */
        public int downloadProgress;

        public ApkInstallInfo copy() {
            ApkInstallInfo copy = new ApkInstallInfo();
            copy.apkUrl = this.apkUrl;
            copy.localApkFile = this.localApkFile;
            copy.downloadProgress = this.downloadProgress;
            return copy;
        }
    }

    private ApkDownloader mApkDownloader;

    public class ApkDownloader implements Runnable, Available {

        private static final String TAG = "ApkDownloader";
        private final ApkInstallInfo mApkInstallInfo;
        private boolean mStart;

        private ApkDownloader(ApkInstallInfo apkInstallInfo) {
            mApkInstallInfo = apkInstallInfo;
        }

        public synchronized void start() {
            if (mStart) {
                Log.d(TAG + " already start");
                return;
            }
            mStart = true;
            Threads.postBackground(this);
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            Closeable closeable = null;
            File errorApkFile = null;
            try {
                // create load file
                File dir = FileUtil.getExternalCacheDir();
                if (dir == null) {
                    throw new Exception("dir create fail");
                }

                dir = new File(dir, "apk");
                FileUtil.createDir(dir);

                File apkFile = FileUtil.createNewTmpFileQuietly("okboot", ".apk", dir);

                errorApkFile = apkFile;

                fos = new FileOutputStream(apkFile);

                OkHttpClient okHttpClient = OkHttpManager.getInstance().getOkHttpClient();
                Request request = new Request.Builder()
                        .url(mApkInstallInfo.apkUrl)
                        .get()
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                closeable = response;

                long contentLength = response.body().contentLength();

                IOUtil.copy(
                        response.body().byteStream(),
                        fos,
                        ApkDownloader.this,
                        new Progress(contentLength, 0) {
                            @Override
                            protected void onUpdate() {
                                super.onUpdate();

                                // for test, let download speed slow
                                Threads.sleepQuietly(20);

                                int newPercent = getPercent();
                                if (mApkInstallInfo.downloadProgress == newPercent) {
                                    return;
                                }

                                mApkInstallInfo.downloadProgress = newPercent;
                                Log.d(TAG + " download progress: " + mApkInstallInfo.downloadProgress);
                                Threads.postUi(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApkDownloader.this.notifyUpdate();
                                    }
                                });
                            }
                        }
                );

                errorApkFile = null;
                mApkInstallInfo.localApkFile = apkFile.getAbsolutePath();

                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        ApkDownloader.this.notifyFinish();
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        ApkDownloader.this.notifyError();
                    }
                });
            } finally {
                IOUtil.closeQuietly(closeable);
                IOUtil.closeQuietly(fos);
                FileUtil.deleteFileQuietly(errorApkFile);
            }
        }

        @UiThread
        private void notifyUpdate() {
            SignInView view = getView();
            if (view == null) {
                return;
            }

            if (!isAvailable()) {
                return;
            }

            view.updateDownloadAndInstallApkDialog(mApkInstallInfo.copy());
        }

        @UiThread
        private void notifyError() {
            SignInView view = getView();
            if (view == null) {
                return;
            }

            if (!isAvailable()) {
                return;
            }

            view.hideDownloadAndInstallApkDialog();
            view.showDownloadAndInstallApkFail();
        }

        @UiThread
        private void notifyFinish() {
            SignInView view = getView();
            if (view == null) {
                return;
            }

            if (!isAvailable()) {
                return;
            }

            view.updateDownloadAndInstallApkDialog(mApkInstallInfo.copy());
            // install apk
            SystemUtil.installApk(new File(mApkInstallInfo.localApkFile));
        }

        @Override
        public boolean isAvailable() {
            SignInView view = getView();
            if (view == null) {
                return false;
            }
            return mApkDownloader == this;
        }
    }

}
