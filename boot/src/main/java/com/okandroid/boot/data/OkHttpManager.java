package com.okandroid.boot.data;

import android.text.TextUtils;

import com.okandroid.boot.App;
import com.okandroid.boot.lang.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * okhttp3
 */
public class OkHttpManager {

    private static class InstanceHolder {

        private static final OkHttpManager sInstance = new OkHttpManager();

    }

    private static boolean sInit;

    public static OkHttpManager getInstance() {
        OkHttpManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private static final String TAG = "OkHttpManager";
    private final OkHttpClient mOkHttpClient;

    private OkHttpManager() {
        Interceptor defaultUserAgentInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (chain.request().header("User-Agent") != null) {
                    return chain.proceed(chain.request());
                }

                String defaultUserAgent = App.getDefaultUserAgent();
                if (TextUtils.isEmpty(defaultUserAgent)) {
                    return chain.proceed(chain.request());
                }

                return chain.proceed(
                        chain.request().newBuilder()
                                .header("User-Agent", defaultUserAgent)
                                .build());
            }
        };

        if (App.getBuildConfigAdapter().isDebug()) {
            Log.d(TAG + " in debug mode, config OkHttpClient.");

            Interceptor contentEncodingInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Log.d(TAG + " contentEncodingInterceptor intercept");
                    Request request = chain.request().newBuilder()
                            .header("Accept-Encoding", "identity")
                            .build();
                    return chain.proceed(request);
                }
            };

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(defaultUserAgentInterceptor)
                    .addInterceptor(contentEncodingInterceptor)
                    .addInterceptor(mExtInterceptorAdapter)
                    .addInterceptor(httpLoggingInterceptor)
                    .cookieJar(CookiesManager.getInstance().getOkHttp3CookieJar())
                    .build();
        } else {
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(defaultUserAgentInterceptor)
                    .addInterceptor(mExtInterceptorAdapter)
                    .cookieJar(CookiesManager.getInstance().getOkHttp3CookieJar())
                    .build();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private final Interceptor mExtInterceptorAdapter = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (mExtInterceptor != null) {
                return mExtInterceptor.intercept(chain);
            }
            return chain.proceed(chain.request());
        }
    };

    private Interceptor mExtInterceptor;

    public void setExtInterceptor(Interceptor mExtInterceptor) {
        this.mExtInterceptor = mExtInterceptor;
    }

    public Interceptor getExtInterceptor() {
        return mExtInterceptor;
    }

}
