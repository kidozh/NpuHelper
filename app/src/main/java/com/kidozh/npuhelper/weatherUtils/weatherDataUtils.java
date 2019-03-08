package com.kidozh.npuhelper.weatherUtils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.kidozh.npuhelper.R;

import java.util.HashMap;
import java.util.Map;

public class weatherDataUtils {
    final static String TAG = weatherDataUtils.class.getSimpleName();
    public static int getDrawableWeatherByString(String skycon_string){
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("CLEAR_DAY", R.drawable.vector_drawable_weather_sunny);
        weather2drawable.put("CLEAR_NIGHT", R.drawable.vector_drawable_weather_night);
        weather2drawable.put("PARTLY_CLOUDY_DAY",R.drawable.vector_drawable_weather_partlycloudy);
        weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.drawable.vector_drawable_weather_partlycloudy);
        weather2drawable.put("CLOUDY",R.drawable.vector_drawable_weather_cloudy);

        weather2drawable.put("RAIN",R.drawable.vector_drawable_weather_rainy);
        weather2drawable.put("SNOW",R.drawable.vector_drawable_weather_snowy);
        weather2drawable.put("WIND",R.drawable.vector_drawable_weather_windy);
        weather2drawable.put("FOG",R.drawable.vector_drawable_weather_fog);
        weather2drawable.put("HAZE",R.drawable.vector_drawable_weather_fog);
        weather2drawable.put("LIGHT_HAZE",R.drawable.vector_drawable_weather_light_haze);
        weather2drawable.put("MODERATE_HAZE",R.drawable.vector_drawable_weather_moderate_haze);
        weather2drawable.put("HEAVY_HAZE",R.drawable.vector_drawable_weather_heavy_haze);
        weather2drawable.put("LIGHT_RAIN",R.drawable.vector_drawable_weather_light_rain);
        weather2drawable.put("MODERATE_RAIN",R.drawable.vector_drawable_weather_moderate_rain);
        weather2drawable.put("HEAVY_RAIN",R.drawable.vector_drawable_weather_heavy_rain);
        weather2drawable.put("LIGHT_SNOW",R.drawable.vector_drawable_weather_light_snow);
        weather2drawable.put("MODERATE_SNOW",R.drawable.vector_drawable_weather_moderate_snow);
        weather2drawable.put("HEAVY_SNOW",R.drawable.vector_drawable_weather_heavy_snow);
        weather2drawable.put("STORM_SNOW",R.drawable.vector_drawable_weather_heavy_snow);

        weather2drawable.put("DUST",R.drawable.vector_drawable_weather_dust);
        weather2drawable.put("SAND",R.drawable.vector_drawable_weather_sand);
        weather2drawable.put("THUNDER_SHOWER",R.drawable.vector_drawable_weather_thunder_shower);
        weather2drawable.put("ICE RAIN",R.drawable.vector_drawable_weather_ice_rain);
        weather2drawable.put("ICE_RAIN",R.drawable.vector_drawable_weather_ice_rain);
        weather2drawable.put("HAIL",R.drawable.vector_drawable_weather_hail);
        weather2drawable.put("SLEET",R.drawable.vector_drawable_weather_sleet);
        weather2drawable.put("TORNADO",R.drawable.vector_drawable_weather_tornado);


        int drawable_id = (int) weather2drawable.get(skycon_string);
        return drawable_id;
    }

    public static int getWeatherTextByString(String skycon_string){
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("CLEAR_DAY",R.string.CLEAR_DAY);
        weather2drawable.put("CLEAR_NIGHT", R.string.CLEAR_NIGHT);
        weather2drawable.put("PARTLY_CLOUDY_DAY",R.string.PARTLY_CLOUDY_DAY);
        weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.string.PARTLY_CLOUDY_NIGHT);
        weather2drawable.put("CLOUDY",R.string.CLOUDY);
        weather2drawable.put("RAIN",R.string.RAIN);
        weather2drawable.put("SNOW",R.string.SNOW);
        weather2drawable.put("WIND",R.string.WIND);
        weather2drawable.put("FOG",R.string.FOG);
        weather2drawable.put("HAZE",R.string.HAZE);

        weather2drawable.put("LIGHT_HAZE",R.string.LIGHT_HAZE);
        weather2drawable.put("MODERATE_HAZE",R.string.MODERATE_HAZE);
        weather2drawable.put("HEAVY_HAZE",R.string.HEAVY_HAZE);
        weather2drawable.put("LIGHT_RAIN",R.string.LIGHT_RAIN);
        weather2drawable.put("MODERATE_RAIN",R.string.MODERATE_RAIN);
        weather2drawable.put("HEAVY_RAIN",R.string.HEAVY_RAIN);
        weather2drawable.put("LIGHT_SNOW",R.string.LIGHT_SNOW);
        weather2drawable.put("MODERATE_SNOW",R.string.MODERATE_SNOW);
        weather2drawable.put("HEAVY_SNOW",R.string.HEAVY_SNOW);
        weather2drawable.put("STORM_SNOW",R.string.STORM_SNOW);

        weather2drawable.put("DUST",R.string.DUST);
        weather2drawable.put("SAND",R.string.SAND);
        weather2drawable.put("THUNDER_SHOWER",R.string.THUNDER_SHOWER);
        weather2drawable.put("ICE RAIN",R.string.ICE_RAIN);
        weather2drawable.put("ICE_RAIN",R.string.ICE_RAIN);
        weather2drawable.put("HAIL",R.string.HAIL);
        weather2drawable.put("SLEET",R.string.SLEET);
        weather2drawable.put("TORNADO",R.string.TORNADO);

        return (int) weather2drawable.get(skycon_string);
    }

    private boolean isBetween(double number,double a, double b){
        if(number >= a && number < b){
            return true;
        }
        else {
            return false;
        }
    }

    public static int getWindScaleNumber(double speed){
        // @speed : km/h
        if(speed < 2) return 0;
        else if(speed < 6) return 1;
        else if(speed < 12) return 2;
        else if(speed < 20) return 3;
        else if(speed < 29) return 4;
        else if(speed < 39) return 5;
        else if(speed < 50) return 6;
        else if(speed < 62) return 7;
        else if(speed < 75) return 8;
        else if(speed < 89) return 9;
        else if(speed < 103) return 10;
        else if(speed < 118) return 11;
        else if(speed < 134) return 12;
        else if(speed < 150) return 13;
        else if(speed < 167) return 14;
        else if(speed < 184) return 15;
        else if(speed < 202) return 16;
        else if(speed < 221) return 17;
        else if(speed >= 221) return 18;
        return 0;

    }

    public static int getWindScaleNameTextResource(Context context,double speed){
        int windScale = getWindScaleNumber(speed);
        Resources res = context.getResources();
        int textResource = res.getIdentifier(String.format("wind_scale_%s_txt",windScale),
                "string",
                context.getPackageName());
        return textResource;
    }

    public static int getWindScaleOceanTextResource(Context context,double speed){
        int windScale = getWindScaleNumber(speed);
        Resources res = context.getResources();
        int textResource = res.getIdentifier(String.format("wind_scale_ocean_describe_%s_txt",windScale),
                "string",
                context.getPackageName());
        return textResource;
    }

    public static int getWindScaleLandTextResource(Context context,double speed){
        int windScale = getWindScaleNumber(speed);
        Resources res = context.getResources();
        int textResource = res.getIdentifier(String.format("wind_scale_land_describe_%s_txt",windScale),
                "string",
                context.getPackageName());
        return textResource;
    }

    public static int getAQITextResource(int aqi){
        if (aqi >0 && aqi<50) return R.string.air_quality_good;
        else if (aqi<100) return R.string.air_quality_moderate;
        else if (aqi<150) return R.string.air_quality_unhealthy_for_sensitive_group;
        else if (aqi<200) return R.string.air_quality_unhealthy;
        else if (aqi<300) return R.string.air_quality_very_unhealthy;
        else if (aqi>=300) return R.string.air_quality_hazardous;
        else return R.string.unknown;
    }

    public static int getAQIColorResource(int aqi){
        if (aqi >0 && aqi<50) return R.color.air_quality_good;
        else if (aqi<100) return R.color.air_quality_moderate;
        else if (aqi<150) return R.color.air_quality_unhealthy_for_sensitive_group;
        else if (aqi<200) return R.color.air_quality_unhealthy;
        else if (aqi<300) return R.color.air_quality_very_unhealthy;
        else if (aqi>=300) return R.color.air_quality_hazardous;
        else return R.color.air_quality_unknown;
    }

    public static int getRainTextResource(float intensity){
        if(intensity < 0.03) return R.string.little_or_no_rain;
        else if(intensity < 0.35) return R.string.moderate_rain_or_snow;
        else if(intensity < 0.48) return R.string.heavy_rain_or_snow;
        else return R.string.storm_rain_or_snow;

    }

    public static int getWindDirectionTextResource(float direction){
        int[] windDirection = {R.string.north_wind,R.string.northeast_wind,R.string.east_wind,
                R.string.southeast_wind,R.string.south_wind,R.string.southwest_wind,
                R.string.west_wind,R.string.northwest_wind
        };
        int index = (int) ((direction + 22.5)%360) /45;
        Log.d(TAG,"get index "+index+" "+direction);
        if(index < windDirection.length){
            return windDirection[index];
        }
        else {
            return R.string.unknown;
        }

    }

    public static int getVisibilityTextResource(float visibility){
        if(visibility > 25) return R.string.very_good;
        else if( visibility > 15) return R.string.good;
        else if( visibility > 10) return R.string.general;
        else if( visibility > 5) return R.string.rather_poor;
        else if( visibility > 1) return R.string.poor;
        else if( visibility > 0.3) return R.string.very_poor;
        else if( visibility > 0.1) return R.string.nearly_invisible;
        else if( visibility <= 0.1) return R.string.none;
        else return R.string.unknown;
    }

    public static int getCloudRateDescriptionTextResource(float rate){
        if(rate < 1.0/9) return R.string.blue_sky;
        else if(rate < 2.0/9) return R.string.clear;
        else if(rate < 3.0/9) return R.string.partial_time_with_sun;
        else if(rate < 4.0/9) return R.string.sparse_cloud;
        else if(rate < 5.0/9) return R.string.partly_cloudy;
        else if(rate < 6.0/9) return R.string.crack_cloud;
        else if(rate < 7.0/9) return R.string.dense_cloud;
        else if(rate < 8.0/9) return R.string.partial_time_with_sun;
        else return R.string.CLOUDY;
    }


}
