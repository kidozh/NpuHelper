package com.kidozh.npuhelper.utilities;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class okHttpUtils {

    public static OkHttpClient getUnsafeOkHttpClient(){
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        return mBuilder.build();
    }

    public static OkHttpClient getUnsafeOkHttpClientWithCookieJar(Context context){
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder().cookieJar(cookieJar);
        mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());


        return mBuilder.build();
    }

    private static class CookiesManager implements CookieJar {
        Context context;
        CookiesManager(Context context){
            this.context = context;
        }

        private final PersistentCookieStore cookieStore = new PersistentCookieStore(context);

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }

}
