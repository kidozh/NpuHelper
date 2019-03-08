package com.kidozh.npuhelper.weatherUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import com.kidozh.npuhelper.R;

public class miuiWeatherUtils {
    private static final String TAG = caiyunWeatherUtils.class.getSimpleName();
    private static final String MIUI_HOST = "https://weatherapi.market.xiaomi.com/wtr-v3/weather/all";
    private static final String SIGN = "zUFJoAR2ZVrDy1vF3D07";

    public static String getLocationKeyInPreference(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String locationLabel = prefs.getString(mContext.getString(R.string.pref_key_location_selection),"y");
        if(locationLabel.equals("y")){
            return "weathercn:101110101";
        }
        else {
            // exact hu xian
            return "weathercn:101110106";
        }
    }

    public static URL build_forecast_api_url(String locationKey){
        Uri build_uri = Uri.parse(MIUI_HOST).buildUpon()
                .appendQueryParameter("sign",SIGN)
                .appendQueryParameter("isGlobal","false")
                .appendQueryParameter("locale","zh_cn")
                .appendQueryParameter("longitude","0")
                .appendQueryParameter("latitude","0")
                .appendQueryParameter("locationKey",locationKey)
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

    public static String getWeatherApi(String longitude,String latitude){
        Uri build_uri = Uri.parse(MIUI_HOST).buildUpon()
                .appendQueryParameter("sign",SIGN)
                .appendQueryParameter("isGlobal","false")
                .appendQueryParameter("locale","zh_cn")
                .appendQueryParameter("longitude",longitude)
                .appendQueryParameter("latitude",latitude)
                .build();
        return build_uri.toString();
    }
}
