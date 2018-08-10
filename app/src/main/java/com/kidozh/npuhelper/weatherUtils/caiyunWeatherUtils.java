package com.kidozh.npuhelper.weatherUtils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class caiyunWeatherUtils {
    private static final String TAG = caiyunWeatherUtils.class.getSimpleName();

    final static private String CAIYUN_HOST = "https://api.caiyunapp.com/v2";
    // TODO : APPLY FOR A REAL VALUE
    final static private String API_KEY = "XVhE=bHyaIzLeaym";
    final static private String RESOPNSE_RES = "forecast.json";
    final static private String REALTIME_RESPONSE_RES = "realtime.json";
    final static private String RAIN_UNIT_KEY = "unit";
    final static private String RAIN_UNIT_VALUE = "metric:v2";

    public static String GEO_LOCATION = "108.913676,34.2434835";

    public static URL build_forecast_api_url(String geo_location){
        final String api_string = CAIYUN_HOST + "/" + API_KEY + "/" + geo_location + "/" + RESOPNSE_RES;
        Uri build_uri = Uri.parse(api_string).buildUpon()
                .appendQueryParameter(RAIN_UNIT_KEY,RAIN_UNIT_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(build_uri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Build URI"+url);
        return url;
    }

    public static URL build_realtime_api_url(String geo_location){
        final String api_string = CAIYUN_HOST + "/" + API_KEY + "/" + geo_location + "/" + REALTIME_RESPONSE_RES;
        Uri build_uri = Uri.parse(api_string).buildUpon()
                .appendQueryParameter(RAIN_UNIT_KEY,RAIN_UNIT_VALUE)
                .build();
        URL url = null;
        try{
            url = new URL(build_uri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Build URI"+url);
        return url;
    }

    public static String get_GEO_LOCATION() {
        return GEO_LOCATION;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
