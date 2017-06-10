package com.okandroid.boot.data;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.okandroid.boot.db.SimpleDB;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.ThreadPool;

/**
 * 数据存储服务
 * <br>
 * 不同进程间的数据不同，不能使用此存储实现进程间共享数据。
 * 此处提供了 setting 和 cache 两个不同的存储空间，各自独立运行。
 * <br>
 * Created by idonans on 16-4-13.
 */
public class StorageManager {

    private static class InstanceHolder {

        private static final StorageManager sInstance = new StorageManager();

    }

    private static boolean sInit;

    public static StorageManager getInstance() {
        StorageManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private static final String TAG = "StorageManager";
    private final SimpleDB mDBSetting;
    private final SimpleDB mDBCache;

    private StorageManager() {
        mDBSetting = new SimpleDB("setting");
        mDBCache = new SimpleDB("cache");

        // 数据库启动时, 做一次 trim 操作.
        int settingTrimSize = mDBSetting.trim(5000);
        Log.d(TAG, "setting trim size:", settingTrimSize);
        int cacheTrimSize = mDBCache.trim(5000);
        Log.d(TAG, "cache trim size:" + cacheTrimSize);
    }

    public void setSetting(@Nullable String key, @Nullable String value) {
        mDBSetting.set(key, value);
    }

    @CheckResult
    public String getSetting(@Nullable final String key) {
        String value = mDBSetting.get(key);
        if (!TextUtils.isEmpty(value)) {
            // 异步处理
            ThreadPool.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mDBSetting.touch(key);
                }
            });
        }
        return value;
    }

    public void setCache(@Nullable String key, @Nullable String value) {
        mDBCache.set(key, value);
    }

    @CheckResult
    public String getCache(@Nullable final String key) {
        String value = mDBCache.get(key);
        if (!TextUtils.isEmpty(value)) {
            // 异步处理
            ThreadPool.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mDBCache.touch(key);
                }
            });
        }
        return value;
    }

    /**
     * 打印所有 Cache 内容， 协助调试使用
     */
    public void printCacheContent() {
        mDBCache.printAllRows();
    }

    /**
     * 打印所有 Setting 内容， 协助调试使用
     */
    public void printSettingContent() {
        mDBSetting.printAllRows();
    }

}

