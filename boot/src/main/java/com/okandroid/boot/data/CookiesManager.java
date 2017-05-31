package com.okandroid.boot.data;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.okandroid.boot.AppContext;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 共享 cookie 管理, 链接 webview cookie, okhttp3 cookie, etc.
 * Created by idonans on 17-5-31.
 */
public class CookiesManager {

    private static class InstanceHolder {

        private static final CookiesManager sInstance = new CookiesManager();

    }

    private static boolean sInit;

    public static CookiesManager getInstance() {
        CookiesManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private static final String TAG = "CookiesManager";

    private final CookieManager mCookieManager;
    private final OkHttp3CookieJar mOkHttp3CookieJar;

    private CookiesManager() {
        CookieSyncManager.createInstance(AppContext.getContext());
        mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);
        mCookieManager.removeExpiredCookie();
        CookieSyncManager.getInstance().sync();

        mOkHttp3CookieJar = new OkHttp3CookieJar();
    }

    public void clearAll() {
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    public void enableCookie(WebView webView) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
    }

    public OkHttp3CookieJar getOkHttp3CookieJar() {
        return mOkHttp3CookieJar;
    }

    private class OkHttp3CookieJar implements CookieJar {

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (url == null) {
                return;
            }

            if (cookies != null && !cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    if (cookie != null) {
                        mCookieManager.setCookie(url.toString(), cookie.toString());
                    }
                }
                CookieSyncManager.getInstance().sync();
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookieList = new ArrayList<>();

            if (url == null) {
                return cookieList;
            }

            String cookieString = mCookieManager.getCookie(url.toString());
            if (TextUtils.isEmpty(cookieString)) {
                return cookieList;
            }

            try {
                String[] cookies = cookieString.split(";");
                for (String cookie : cookies) {
                    cookieList.add(Cookie.parse(url, cookie));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cookieList;
        }
    }

}
