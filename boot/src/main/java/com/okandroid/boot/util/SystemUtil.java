package com.okandroid.boot.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.okandroid.boot.AppContext;
import com.okandroid.boot.R;
import com.okandroid.boot.data.AppIDManager;
import com.okandroid.boot.lang.Log;

import java.io.File;
import java.util.Locale;

/**
 * 一些系统相关辅助类
 * Created by idonans on 16-4-14.
 */
public class SystemUtil {

    private static final String TAG = "SystemUtil";

    /**
     * @see AppIDManager
     */
    public static String getAppID() {
        return AppIDManager.getInstance().getAppID();
    }

    /**
     * 成功打开软件商店(会尝试定位到指定软件)返回true, 如果没有安装任何软件商店, 返回false.
     */
    public static boolean openMarket(String packageName) {
        String url = "market://details?id=" + packageName;
        return openView(url);
    }

    public static boolean openView(String url) {
        return openView(url, null);
    }

    /**
     * 使用 chooser 方式打开指定 url, 处理成功返回true, 否则返回false.
     */
    public static boolean openView(String url, CharSequence chooserTitle) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(AppContext.getContext().getPackageManager()) != null) {
                if (TextUtils.isEmpty(chooserTitle)) {
                    chooserTitle = " ";
                }
                Intent chooser = Intent.createChooser(intent, chooserTitle);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppContext.getContext().startActivity(chooser);
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前进程允许的最大 heap size(字节数). 仅 Java 部分的内容受此系统设置限制， native 层的内容消耗受手机内存容量限制.
     * 容量超过此值会出现 OOM 错误.
     * 如 手机 CHM-UL00 的 heap size 是 268435456 byte (256M), 该手机的配置是 2G 内存，16G 存储空间, 1280x720 分辨率, Android 4.4.2 系统
     */
    public static long getMaxHeapSize() {
        ActivityManager am = (ActivityManager) AppContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return am.getMemoryClass() * 1024L * 1024L;
    }

    public static boolean showSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) AppContext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.showSoftInput(editText, 0);
    }

    public static boolean hideSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) AppContext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * @see #isSoftKeyboardShown(View)
     */
    public static boolean isSoftKeyboardShown(Activity activity) {
        View view = null;
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                view = window.getDecorView();
            }
        }
        return isSoftKeyboardShown(view);
    }

    /**
     * @see #isSoftKeyboardShown(View)
     */
    public static boolean isSoftKeyboardShown(Fragment fragment) {
        return isSoftKeyboardShown(fragment.getActivity());
    }

    /**
     * 判断当前软键盘是否处于打开状态 (非全屏并且 windowSoftInputMode 为 adjustResize 时有效)
     *
     * @return return false if view is null or not in view tree.
     */
    public static boolean isSoftKeyboardShown(@Nullable View view) {
        if (view == null) {
            return false;
        }

        View rootView = view.getRootView();
        if (rootView == null) {
            return false;
        }

        View contentView = rootView.findViewById(Window.ID_ANDROID_CONTENT);
        if (contentView == null) {
            return false;
        }

        View okandroidContent = contentView.findViewById(R.id.okandroid_content);
        if (okandroidContent != null) {
            contentView = okandroidContent;
        }

        int softKeyboardHeight = DimenUtil.dp2px(100);

        // print log
        Log.v(TAG, "isSoftKeyboardShown"
                , "\nrootView " + rootView.getClass().getName() + ", bottom:" + rootView.getBottom() + ", padding:" + getPadding(rootView)
                , "\ncontentView " + contentView.getClass().getName() + ", bottom:" + contentView.getBottom() + ", padding:" + getPadding(contentView)
                , "\nsoftKeyboardHeight:" + softKeyboardHeight);

        if (contentView.getPaddingBottom() > softKeyboardHeight) {
            return true;
        }

        return rootView.getBottom() - contentView.getBottom() > softKeyboardHeight;
    }

    private static String getPadding(View view) {
        return String.format(Locale.getDefault(), "[%s, %s, %s, %s]",
                view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * 注意：全屏模式下，软键盘监听将失效
     * <p>
     * 设置全屏
     */
    public static void setFullscreenWithSystemUi(View view) {
        view.getRootView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * 取消全屏设置
     */
    public static void unsetFullscreenWithSystemUi(View view) {
        view.getRootView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setStatusBarTransparent(Window window) {
        if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            // 发现：华为手机在 5.1 上的透明状态栏仍然使用的是 4.4 的效果，此处设置完全透明状态栏的 API 调整到最低 6.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        unsetFullscreenWithSystemUi(window.getDecorView());
    }

    /**
     * 将指定文件添加到系统媒体库，如将一张图片添加到系统媒体库，使得在 Gallery 中能够显示.
     */
    public static void addToMediaStore(File file) {
        try {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            AppContext.getContext().sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统安装程序安装指定 apk, 调用成功返回 true, 否则返回 false.
     */
    public static boolean installApk(File apkFile) {
        try {
            Uri uri = FileUtil.getFileUri(apkFile);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            FileUtil.addGrantUriPermission(intent);

            if (intent.resolveActivity(AppContext.getContext().getPackageManager()) != null) {
                Intent chooser = Intent.createChooser(intent, null);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppContext.getContext().startActivity(chooser);
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取系统默认 user-agent
     */
    public static String getSystemUserAgent() {
        return System.getProperty("http.agent");
    }

    /**
     * 获取系统 webview 默认 user-agent
     */
    public static String getSystemWebViewUserAgent() {
        if (Build.VERSION.SDK_INT >= 17) {
            return WebSettings.getDefaultUserAgent(AppContext.getContext());
        } else {
            return new WebView(AppContext.getContext()).getSettings().getUserAgentString();
        }
    }

}
