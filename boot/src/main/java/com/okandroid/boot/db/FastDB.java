package com.okandroid.boot.db;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.okandroid.boot.util.SystemUtil;

/**
 * base on SimpleDB but contain one memory cache layer.
 * <p>
 * Created by idonans on 2016/12/7.
 */
public class FastDB {

    private final SimpleDB mSimpleDB;
    private final long mMaxMemoryCacheSize;
    private final MemoryCache mMemoryCache;

    public FastDB(@NonNull String databaseName, boolean large) {
        mSimpleDB = new SimpleDB(databaseName);

        if (large) {
            mMaxMemoryCacheSize = SystemUtil.getMaxHeapSize() / 32;
        } else {
            mMaxMemoryCacheSize = SystemUtil.getMaxHeapSize() / 128;
        }
        mMemoryCache = new MemoryCache((int) mMaxMemoryCacheSize);
    }

    public long getMaxMemoryCacheSize() {
        return mMaxMemoryCacheSize;
    }

    public MemoryCache getMemoryCache() {
        return mMemoryCache;
    }

    public SimpleDB getSimpleDB() {
        return mSimpleDB;
    }

    @CheckResult
    public String get(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        String value = mMemoryCache.get(key);
        if (value != null) {
            return value;
        }

        value = mSimpleDB.get(key);
        if (!TextUtils.isEmpty(value)) {
            mMemoryCache.put(key, value);
        }
        return value;
    }

    public void set(@Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            remove(key);
            return;
        }

        mMemoryCache.put(key, value);
        mSimpleDB.set(key, value);
    }

    public void remove(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        mMemoryCache.remove(key);
        mSimpleDB.remove(key);
    }

    public void touch(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        mSimpleDB.touch(key);
    }

    public int trim(int maxRows) {
        if (maxRows < 1) {
            return -1;
        }

        return mSimpleDB.trim(maxRows);
    }

    public int clear() {
        mMemoryCache.trimToSize(0);
        return mSimpleDB.clear();
    }

    public int count() {
        return mSimpleDB.count();
    }

    public void printAllRows() {
        mSimpleDB.printAllRows();
    }

    private class MemoryCache extends LruCache<String, String> {

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public MemoryCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, String value) {
            if (value == null) {
                return 0;
            }
            return value.length() * 3;
        }

    }

}
