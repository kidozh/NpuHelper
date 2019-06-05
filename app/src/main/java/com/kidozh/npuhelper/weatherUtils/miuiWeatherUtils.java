package com.kidozh.npuhelper.weatherUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.kidozh.npuhelper.R;

public class miuiWeatherUtils {
    private static final String TAG = caiyunWeatherUtils.class.getSimpleName();
    private static final String MIUI_HOST = "https://weatherapi.market.xiaomi.com/wtr-v3/weather/all";
    private static final String SIGN = "zUFJoAR2ZVrDy1vF3D07";

    public static String getLocationKeyInPreference(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String locationLabel = prefs.getString(mContext.getString(R.string.pref_key_location_selection),"y");
        if(locationLabel.equals("y")){
            // for debug
            //return "weathercn:101280209";
            return "weathercn:101110101";
        }
        else {
            // exact hu xian
            return "weathercn:101110106";
        }
    }
    public static Integer getLocationPreferenceTextResource(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String locationLabel = prefs.getString(mContext.getString(R.string.pref_key_location_selection),"y");
        if(locationLabel.equals("y")){
            return R.string.youyi_campus_name_full;
        }
        else {
            return R.string.changan_campus_name_full;
        }
    }

    public static String build_forecast_api_url(String locationKey){
        Uri build_uri = Uri.parse(MIUI_HOST).buildUpon()
                .appendQueryParameter("sign",SIGN)
                .appendQueryParameter("isGlobal","false")
                .appendQueryParameter("locale","zh_cn")
                .appendQueryParameter("longitude","0")
                .appendQueryParameter("latitude","0")
                .appendQueryParameter("locationKey",locationKey)
                .appendQueryParameter("appKey","weather20190426")
                .appendQueryParameter("days","15")
                .build();
        Log.v(TAG,"MIUI forecast URL : "+ build_uri.toString());
        return build_uri.toString();
    }

    public static int getDrawableWeatherByString(String miuiWeather){
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("0", R.drawable.vector_drawable_weather_sunny);
        //weather2drawable.put("CLEAR_NIGHT", R.drawable.vector_drawable_weather_night);
        weather2drawable.put("1",R.drawable.vector_drawable_weather_partly_cloudy);
        //weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.drawable.vector_drawable_weather_partlycloudy);
        weather2drawable.put("2",R.drawable.vector_drawable_weather_cloudy);

        weather2drawable.put("3",R.drawable.vector_drawable_weather_rainy);
        weather2drawable.put("13",R.drawable.vector_drawable_weather_snowy);
        weather2drawable.put("WIND",R.drawable.vector_drawable_weather_windy);
        weather2drawable.put("18",R.drawable.vector_drawable_weather_fog);
        weather2drawable.put("53",R.drawable.vector_drawable_weather_fog);
        weather2drawable.put("35",R.drawable.vector_drawable_weather_light_haze);
        weather2drawable.put("MODERATE_HAZE",R.drawable.vector_drawable_weather_moderate_haze);
        weather2drawable.put("HEAVY_HAZE",R.drawable.vector_drawable_weather_heavy_haze);
        weather2drawable.put("30",R.drawable.vector_drawable_weather_heavy_haze);
        weather2drawable.put("31",R.drawable.vector_drawable_weather_moderate_haze);
        // rain
        weather2drawable.put("7",R.drawable.vector_drawable_weather_light_rain);
        weather2drawable.put("8",R.drawable.vector_drawable_weather_moderate_rain);
        weather2drawable.put("9",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("10",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("11",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("12",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("21",R.drawable.vector_drawable_weather_light_rain);
        weather2drawable.put("22",R.drawable.vector_drawable_weather_moderate_rain);
        weather2drawable.put("23",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("24",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("25",R.drawable.vector_drawable_weather_heavy_rain);
        //snow
        weather2drawable.put("14",R.drawable.vector_drawable_weather_light_snow);
        weather2drawable.put("15",R.drawable.vector_drawable_weather_moderate_snow);
        weather2drawable.put("16",R.drawable.vector_drawable_weather_heavy_snow);
        weather2drawable.put("17",R.drawable.vector_drawable_weather_heavy_snow);
        weather2drawable.put("26",R.drawable.vector_drawable_weather_light_snow);
        weather2drawable.put("27",R.drawable.vector_drawable_weather_moderate_snow);
        weather2drawable.put("28",R.drawable.vector_drawable_weather_heavy_snow);
        weather2drawable.put("34",R.drawable.vector_drawable_weather_heavy_snow);

        weather2drawable.put("29",R.drawable.vector_drawable_weather_dust);
        weather2drawable.put("20",R.drawable.vector_drawable_weather_sand);
        weather2drawable.put("4",R.drawable.vector_drawable_weather_thunder_shower);
        weather2drawable.put("5",R.drawable.vector_drawable_weather_thunder_shower);
        weather2drawable.put("19",R.drawable.vector_drawable_weather_ice_rain);
        weather2drawable.put("HAIL",R.drawable.vector_drawable_weather_hail);
        weather2drawable.put("6",R.drawable.vector_drawable_weather_sleet);
        weather2drawable.put("32",R.drawable.vector_drawable_weather_tornado);
        weather2drawable.put("33",R.drawable.vector_drawable_weather_tornado);
        weather2drawable.put("99",R.drawable.vector_drawable_weather_night);

        int drawable_id = (int) weather2drawable.get(miuiWeather);
        return drawable_id;
    }

    public static int getWeatherTextByString(String skycon_string){
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("0",R.string.CLEAR_DAY);
        weather2drawable.put("CLEAR_NIGHT", R.string.CLEAR_NIGHT);
        weather2drawable.put("1",R.string.PARTLY_CLOUDY_DAY);
        weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.string.PARTLY_CLOUDY_NIGHT);
        weather2drawable.put("2",R.string.CLOUDY);
        weather2drawable.put("3",R.string.RAIN);
        weather2drawable.put("13",R.string.SNOW);
        weather2drawable.put("WIND",R.string.WIND);
        weather2drawable.put("18",R.string.FOG);
        weather2drawable.put("53",R.string.HAZE);

        weather2drawable.put("35",R.string.LIGHT_HAZE);
        weather2drawable.put("MODERATE_HAZE",R.string.MODERATE_HAZE);
        weather2drawable.put("HEAVY_HAZE",R.string.HEAVY_HAZE);
        weather2drawable.put("7",R.string.LIGHT_RAIN);
        weather2drawable.put("8",R.string.MODERATE_RAIN);
        weather2drawable.put("9",R.string.HEAVY_RAIN);
        weather2drawable.put("10",R.string.HEAVY_RAIN);
        weather2drawable.put("11",R.string.HEAVY_RAIN);
        weather2drawable.put("12",R.string.HEAVY_RAIN);

        weather2drawable.put("14",R.string.LIGHT_SNOW);
        weather2drawable.put("15",R.string.MODERATE_SNOW);
        weather2drawable.put("16",R.string.HEAVY_SNOW);
        weather2drawable.put("17",R.string.STORM_SNOW);
        weather2drawable.put("34",R.string.STORM_SNOW);
        weather2drawable.put("29",R.string.DUST);
        weather2drawable.put("30",R.string.SAND);
        weather2drawable.put("31",R.string.SAND);

        weather2drawable.put("4",R.string.THUNDER_SHOWER);
        weather2drawable.put("5",R.string.THUNDER_SHOWER);
        weather2drawable.put("ICE RAIN",R.string.ICE_RAIN);
        weather2drawable.put("6",R.string.ICE_RAIN);
        weather2drawable.put("HAIL",R.string.HAIL);
        weather2drawable.put("19",R.string.SLEET);
        weather2drawable.put("33",R.string.TORNADO);
        weather2drawable.put("32",R.string.TORNADO);
        weather2drawable.put("99",R.string.unknown);

        return (int) weather2drawable.get(skycon_string);
    }

}
