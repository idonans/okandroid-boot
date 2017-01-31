package com.okandroid.boot.lang;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.SystemUtil;

/**
 * 辅助观察软键盘的行为
 * Created by idonans on 16-4-19.
 */
public class SoftKeyboardObserver implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "SoftKeyboardObserver";
    private final SoftKeyboardListener mListener;

    private final int METHOD_NONE = 0;
    private final int METHOD_OPEN = 1;
    private final int METHOD_CLOSE = 2;
    // 避免同一个回调被连续调用两次，比如避免连续调用两次软键盘打开。
    private int mLastCallMethod = METHOD_NONE;

    private Host mHost;

    /**
     * call constructor with param post true.
     *
     * @see #SoftKeyboardObserver(boolean, SoftKeyboardListener)
     */
    public SoftKeyboardObserver(@Nullable SoftKeyboardListener listener) {
        this(true, listener);
    }

    /**
     * @param post 指定在键盘状态发生变更时，是否将事件 post 到下一个 ui 循环中回调
     */
    public SoftKeyboardObserver(boolean post, @Nullable SoftKeyboardListener listener) {
        if (post) {
            mListener = new SoftKeyboardListenerPoster(listener);
        } else {
            mListener = new SoftKeyboardListenerDirect(listener);
        }
    }

    public void register(@NonNull Activity activity) {
        if (mHost != null) {
            throw new IllegalAccessError("already register");
        }

        checkWindowSoftInputMode(activity);

        mHost = new Host(activity);

        if (isHostAvailable()) {
            mHost.mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    public void register(@NonNull Fragment fragment) {
        Activity activity = SystemUtil.getActivityFromFragment(fragment);
        if (activity == null) {
            Log.e(TAG + " register fragment error. activity not found from fragment " + fragment.getClass().getName() + "@" + fragment.hashCode());
            return;
        }

        register(activity);
    }

    public void unregister() {
        if (mHost == null) {
            Log.e(TAG + " not register or already unregister");
            return;
        }

        if (isHostAvailable()) {
            if (Build.VERSION.SDK_INT >= 16) {
                mHost.mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                mHost.mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
        mHost = null;
    }

    private boolean isHostAvailable() {
        return mHost != null && mHost.mContentView != null;
    }

    private void onSoftKeyboardOpen() {
        if (mLastCallMethod == METHOD_OPEN) {
            return;
        }
        mLastCallMethod = METHOD_OPEN;
        mListener.onSoftKeyboardOpen();
    }

    private void onSoftKeyboardClose() {
        if (mLastCallMethod == METHOD_CLOSE) {
            return;
        }
        mLastCallMethod = METHOD_CLOSE;
        mListener.onSoftKeyboardClose();
    }

    @Override
    public void onGlobalLayout() {
        Log.d(TAG + " onGlobalLayout");

        if (isHostAvailable()) {
            if (mHost.isSoftKeyboardShown()) {
                onSoftKeyboardOpen();
            } else {
                onSoftKeyboardClose();
            }
        }
    }

    private void checkWindowSoftInputMode(@NonNull Activity activity) {
        if (activity.getWindow() != null) {
            if ((activity.getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
                throw new IllegalArgumentException("softInputMode is not adjustResize " + activity.getClass().getName() + "@" + activity.hashCode());
            }
        }
    }

    private class Host {
        @Nullable
        private final View mContentView;

        private Host(@Nullable Activity activity) {
            if (activity != null) {
                mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            } else {
                mContentView = null;
            }
        }

        private boolean isSoftKeyboardShown() {
            return SystemUtil.isSoftKeyboardShown(mContentView);
        }
    }

    public interface SoftKeyboardListener {
        void onSoftKeyboardOpen();

        void onSoftKeyboardClose();
    }

    private class SoftKeyboardListenerPoster implements SoftKeyboardListener {
        private final SoftKeyboardListener mListener;

        private SoftKeyboardListenerPoster(SoftKeyboardListener listener) {
            mListener = listener;
        }

        @Override
        public void onSoftKeyboardOpen() {
            if (mListener != null) {
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        if (isHostAvailable()) {
                            mListener.onSoftKeyboardOpen();
                        }
                    }
                });
            }
        }

        @Override
        public void onSoftKeyboardClose() {
            if (mListener != null) {
                Threads.postUi(new Runnable() {
                    @Override
                    public void run() {
                        if (isHostAvailable()) {
                            mListener.onSoftKeyboardClose();
                        }
                    }
                });
            }
        }

    }

    private class SoftKeyboardListenerDirect implements SoftKeyboardListener {
        private final SoftKeyboardListener mListener;

        private SoftKeyboardListenerDirect(SoftKeyboardListener listener) {
            mListener = listener;
        }

        @Override
        public void onSoftKeyboardOpen() {
            if (mListener != null) {
                Threads.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        if (isHostAvailable()) {
                            mListener.onSoftKeyboardOpen();
                        }
                    }
                });
            }
        }

        @Override
        public void onSoftKeyboardClose() {
            if (mListener != null) {
                Threads.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        if (isHostAvailable()) {
                            mListener.onSoftKeyboardClose();
                        }
                    }
                });
            }
        }

    }

}
