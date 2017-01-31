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
 * Created by idonans on 16-6-12.
 */
public class OkHttpManager {

    private static class InstanceHolder {

        private static final OkHttpManager sInstance = new OkHttpManager();

    }

    public static OkHttpManager getInstance() {
        return InstanceHolder.sInstance;
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
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
        } else {
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(defaultUserAgentInterceptor)
                    .build();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

}
