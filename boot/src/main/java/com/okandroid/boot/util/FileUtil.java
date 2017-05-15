package com.okandroid.boot.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.CheckResult;
import android.text.TextUtils;

import com.okandroid.boot.App;
import com.okandroid.boot.AppContext;
import com.okandroid.boot.data.ProcessManager;
import com.okandroid.boot.lang.BootFileProvider;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 文件操作相关辅助类
 * Created by idonans on 16-4-15.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    private FileUtil() {
    }

    /**
     * 获得一个进程安全的缓存目录(在 private cache目录下的一个与进程名相关的目录, 如果目录不存在，会尝试创建)
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getCacheDir() {
        File cacheDir = AppContext.getContext().getCacheDir();
        if (cacheDir == null) {
            return null;
        }
        File processCacheDir = new File(cacheDir, ProcessManager.getInstance().getProcessTag());
        if (createDir(processCacheDir)) {
            return processCacheDir;
        }
        return null;
    }

    /**
     * 获得一个进程安全的外部缓存目录(在扩展卡的当前 app cache目录下的一个与进程名相关的目录, 如果目录不存在，会尝试创建)，
     * 该目录可能会被用户卸载。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getExternalCacheDir() {
        File cacheDir = AppContext.getContext().getExternalCacheDir();
        if (cacheDir == null) {
            return null;
        }
        File processCacheDir = new File(cacheDir, ProcessManager.getInstance().getProcessTag());
        if (createDir(processCacheDir)) {
            return processCacheDir;
        }
        return null;
    }

    /**
     * 判断指定的文件是否存在并且是一个文件(不是文件夹)
     */
    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 判断指定的文件是否存在并且是一个文件夹
     */
    public static boolean isDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 得到一个在系统拍照目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicDCIMDir() {
        return getPublicDir(Environment.DIRECTORY_DCIM);
    }

    /**
     * 得到一个在系统图片目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicPictureDir() {
        return getPublicDir(Environment.DIRECTORY_PICTURES);
    }

    /**
     * 得到一个在系统下载目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicDownloadDir() {
        return getPublicDir(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * 得到一个在系统视频目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicMovieDir() {
        return getPublicDir(Environment.DIRECTORY_MOVIES);
    }

    /**
     * 得到一个在系统音乐目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicMusicDir() {
        return getPublicDir(Environment.DIRECTORY_MUSIC);
    }

    /**
     * 得到一个在指定系统目录下，以当前应用标识为子文件夹名的目录(如果目录不存在则尝试创建)。
     * <br>
     * 不能获得这样一个目录，返回 null.
     */
    @CheckResult
    public static File getPublicDir(String environmentDirectory) {
        File envDir = Environment.getExternalStoragePublicDirectory(environmentDirectory);
        if (envDir == null) {
            return null;
        }

        File appEnvDir = new File(envDir, App.getBuildConfigAdapter().getPublicSubDirName());
        if (createDir(appEnvDir)) {
            return appEnvDir;
        }
        return null;
    }

    /**
     * 创建目录，如果创建成功，或者目录已经存在，返回true. 否则返回false.
     */
    public static boolean createDir(File file) {
        if (file == null) {
            return false;
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.isDirectory() || !file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 文件(文件夹)已删除或者删除成功返回true, 否则返回false
     */
    public static boolean deleteFileQuietly(File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        if (file.isFile()) {
            file.delete();
            return !file.exists();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!deleteFileQuietly(f)) {
                        return false;
                    }
                }
            }
            file.delete();
            return !file.exists();
        }
        return false;
    }

    /**
     * 路径无效或者文件(文件夹)已删除或者删除成功返回true, 否则返回false
     */
    public static boolean deleteFileQuietly(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        return deleteFileQuietly(new File(path));
    }

    /**
     * 如果文件不存在并且此次创建成功，返回true. 否则返回false.
     */
    public static boolean createNewFileQuietly(File file) {
        if (file == null) {
            return false;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!createDir(parent)) {
                return false;
            }
        }

        if (file.exists()) {
            return false;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.exists() && file.isFile();
    }

    /**
     * 从指定 url 获取扩展名, 不包含扩展名分隔符<code>.</code>，如果获取失败，返回 null.
     */
    @CheckResult
    public static String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return null;
    }

    /**
     * 从指定 url 获取文件名，包含扩展名, 如果获取失败，返回 null.
     */
    @CheckResult
    public static String getFilenameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;
            return filename;
        }

        return null;
    }

    /**
     * 以指定的文件名前缀和后缀在指定文件夹创建一个临时文件，如果创建失败，返回 null.
     */
    @CheckResult
    public static File createNewTmpFileQuietly(String prefix, String suffix, File dir) {
        try {
            if (createDir(dir)) {
                if (prefix == null) {
                    prefix = "";
                }
                if (suffix == null) {
                    suffix = ".tmp";
                }

                String filename = String.valueOf(System.currentTimeMillis());
                File tmpFile = new File(dir, prefix + filename + suffix);

                if (tmpFile.createNewFile()) {
                    return tmpFile;
                }

                for (int i = 1; i < 20; i++) {
                    tmpFile = new File(dir, prefix + filename + "(" + i + ")" + suffix);
                    if (tmpFile.createNewFile()) {
                        return tmpFile;
                    }
                }

                throw new RuntimeException("相似文件太多 " + tmpFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个新文件，如果指定的文件已经存在，则尝试创建一个相似的文件，返回创建成功的文件路径，如果创建失败，返回 null.
     *
     * @param path
     * @return
     */
    @CheckResult
    public static String createSimilarFileQuietly(String path) {
        try {
            File f = new File(path);
            File parent = f.getParentFile();
            String filename = f.getName();

            String extension = FileUtil.getFileExtensionFromUrl(filename);
            if (!TextUtils.isEmpty(extension)) {
                filename = filename.substring(0, filename.length() - extension.length() - 1);
                extension = "." + extension;
            } else {
                extension = "";
            }

            if (FileUtil.createDir(parent)) {
                File tmpFile = new File(parent, filename + extension);

                if (tmpFile.createNewFile()) {
                    return tmpFile.getAbsolutePath();
                }

                for (int i = 1; i < 20; i++) {
                    tmpFile = new File(parent, filename + "(" + i + ")" + extension);
                    if (tmpFile.createNewFile()) {
                        return tmpFile.getAbsolutePath();
                    }
                }

                throw new RuntimeException("相似文件太多 " + tmpFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri getFileUri(File file) {
        Uri targetUri;
        if (Build.VERSION.SDK_INT >= 24) {
            targetUri = BootFileProvider.getUriForFile(file);
        } else {
            targetUri = Uri.fromFile(file);
        }
        return targetUri;
    }

    /**
     * use file content uri for Intent data, need set grant uri permission
     */
    public static void addGrantUriPermission(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

}
