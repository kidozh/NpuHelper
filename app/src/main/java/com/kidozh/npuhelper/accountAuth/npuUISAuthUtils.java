package com.kidozh.npuhelper.accountAuth;

import android.content.Context;

import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class npuUISAuthUtils {
    public static OkHttpClient buildCookiePersistantOkHttpClient(){
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
                cookieStore.put(httpUrl.host(), cookies);
            }
            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                // only in dev mode remember to roll back!
                .sslSocketFactory(TrustAllCerts.createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();
        return okHttpClient;
    }

    final static String npuUISAuthHost = "https://uis.nwpu.edu.cn/cas/v1/tickets";

    public static Request buildUISAuthRequest(String username,String password){
        FormBody authBody = new FormBody
                .Builder()
                .add("username",username)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(npuUISAuthHost)
                .post(authBody)
                .build();
        return request;
    }

    public static Request buildUISAuthRequest(Context mContext){
        accountInfoBean accountBeam = loginUtils.getTokenInfoToLocal(mContext);
        String password = loginUtils.getPasswordInfoFromLocal(mContext);
        return buildUISAuthRequest(accountBeam.accountNumber,password);

    }

    final static String npuUISRedirectAuthFormat = "https://uis.nwpu.edu.cn/cas/v1/tickets/%s";
    public static Request buildUISRedirectRequest(String tgtTicket,String service){
        FormBody authBody = new FormBody
                .Builder()
                .add("service",service)
                .build();
        String NPUUISRedirectURL = String.format(npuUISRedirectAuthFormat,tgtTicket);

        Request request = new Request.Builder()
                .url(NPUUISRedirectURL)
                .post(authBody)
                .build();
        return request;
    }

    public static Request buildUISRedirectRequestByURL(String URL,String service){
        FormBody authBody = new FormBody
                .Builder()
                .add("service",service)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .post(authBody)
                .build();
        return request;
    }
}
