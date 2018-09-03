package com.kidozh.npuhelper.weatherUtils;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class miuiWeatherUtils {
    private static final String TAG = caiyunWeatherUtils.class.getSimpleName();
    private static final String MIUI_HOST = "https://weatherapi.market.xiaomi.com/wtr-v3/weather/all";
    private static final String SIGN = "zUFJoAR2ZVrDy1vF3D07";

    public static URL build_forecast_api_url(String longitude,String latitude){
        Uri build_uri = Uri.parse(MIUI_HOST).buildUpon()
                .appendQueryParameter("sign",SIGN)
                .appendQueryParameter("isGlobal","false")
                .appendQueryParameter("locale","zh_cn")
                .appendQueryParameter("longitude",longitude)
                .appendQueryParameter("latitude",latitude)
                .build();
        URL url = null;
        try{
            url = new URL(build_uri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"MIUI forecast URL : "+ url);
        return url;
    }
}
