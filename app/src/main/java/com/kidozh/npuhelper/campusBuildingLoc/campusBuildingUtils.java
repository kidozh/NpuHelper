package com.kidozh.npuhelper.campusBuildingLoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.util.JsonUtils;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class campusBuildingUtils {
    private static final String TAG = caiyunWeatherUtils.class.getSimpleName();

    final static private String GITHUB_API_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/building_location.json";
    final static private String GITEE_API_URL = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/building_location.json";

    final static private String GITHUB_DETAIL_API_TEMPLATE_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/building_detail/%s.md";
    final static private String GITEE_DETAIL_API_TEMPLATE_URL  = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/building_detail/%s.md";

    public static URL build_url(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"github");
        if (api_string.equals("gitee")){
            api_string = GITEE_API_URL;
        }
        else{
            api_string = GITHUB_API_URL;
        }
        Log.d(TAG,"api source : "+api_string);

        Uri buildUri = Uri.parse(api_string).buildUpon()
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Build Building RAW URI"+url);
        return url;
    }

    public static JSONObject load_json(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }

    public static URL build_location_detail_url(Context mContext,String building_name){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"github");
        String api_string_template = "";
        if (api_string.equals("gitee")){
            api_string_template = GITEE_DETAIL_API_TEMPLATE_URL;
        }
        else{
            api_string_template = GITHUB_DETAIL_API_TEMPLATE_URL;
        }
        Log.d(TAG,"api source : "+api_string);

        api_string = String.format(api_string_template,building_name);

        Uri buildUri = Uri.parse(api_string).buildUpon()
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Build Building RAW LOCATION URI"+url);
        return url;
    }
}