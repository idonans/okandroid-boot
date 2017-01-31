package com.okandroid.boot;

import android.content.Context;
import android.text.TextUtils;

import com.okandroid.boot.data.FrescoManager;
import com.okandroid.boot.lang.Log;

/**
 * 在 App 启动时初始化, 通常在 Application#onCreate, ContentProvider#onCreate
 * Created by idonans on 16-4-18.
 */
public class App {

    private static boolean sInitCalled;
    private static BuildConfigAdapter sBuildConfigAdapter;

    private static String sDefaultUserAgent;
    private static boolean sUse565Config;

    private App() {
    }

    private static void init(Config config) {
        if (sInitCalled) {
            return;
        }
        synchronized (App.class) {
            if (sInitCalled) {
                return;
            }
            internalInit(config);
            sInitCalled = true;
        }
    }

    private static void internalInit(Config config) {
        AppContext.setContext(config.mContext);
        sBuildConfigAdapter = config.mBuildConfigAdapter;
        sDefaultUserAgent = config.mDefaultUserAgent;
        sUse565Config = config.mUse565Config;

        Log.setLogLevel(sBuildConfigAdapter.getLogLevel());
        Log.setLogTag(sBuildConfigAdapter.getLogTag());

        // 配置 fresco
        if (config.mUseFresco) {
            FrescoManager.getInstance();
        }
    }

    public static boolean isUse565Config() {
        return sUse565Config;
    }

    public static String getDefaultUserAgent() {
        return sDefaultUserAgent;
    }

    public static BuildConfigAdapter getBuildConfigAdapter() {
        return sBuildConfigAdapter;
    }

    /**
     * 引用当前 application 所在的 BuildConfig 中的值。 如 apply plugin: 'com.android.application' 所在
     * module 中的 BuildConfig
     */
    public interface BuildConfigAdapter {
        int getVersionCode();

        String getVersionName();

        String getLogTag();

        String getPublicSubDirName();

        String getChannel();

        int getLogLevel();

        boolean isDebug();
    }

    public static class Config {

        private Context mContext;
        private BuildConfigAdapter mBuildConfigAdapter;
        private boolean mUseFresco;
        private boolean mUse565Config;
        private String mDefaultUserAgent;

        private Config() {
        }

        public void init() {
            App.init(this);
        }

        public static class Builder {

            private Context mContext;
            private BuildConfigAdapter mBuildConfigAdapter;
            private boolean mUseFresco = true;
            private boolean mUse565Config;
            private String mDefaultUserAgent;

            public Builder setContext(Context context) {
                mContext = context.getApplicationContext();
                return this;
            }

            public Builder setBuildConfigAdapter(BuildConfigAdapter buildConfigAdapter) {
                mBuildConfigAdapter = buildConfigAdapter;
                return this;
            }

            public Builder setDefaultUserAgent(String defaultUserAgent) {
                mDefaultUserAgent = defaultUserAgent;
                return this;
            }

            public Builder setUseFresco(boolean useFresco) {
                mUseFresco = useFresco;
                return this;
            }

            public Builder setUse565Config(boolean use565Config) {
                mUse565Config = use565Config;
                return this;
            }

            public Config build() {
                if (mContext == null) {
                    throw new IllegalArgumentException("context not set");
                }

                if (mBuildConfigAdapter == null) {
                    throw new IllegalArgumentException("build config adapter not set");
                }

                Config config = new Config();
                config.mContext = this.mContext;
                config.mBuildConfigAdapter = this.mBuildConfigAdapter;
                config.mUseFresco = this.mUseFresco;
                config.mUse565Config = this.mUse565Config;

                if (!TextUtils.isEmpty(this.mDefaultUserAgent)) {
                    config.mDefaultUserAgent = this.mDefaultUserAgent;
                }

                return config;
            }
        }
    }

}
