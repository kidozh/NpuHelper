package com.kidozh.npuhelper.schoolBusUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kidozh.npuhelper.R;

import java.net.MalformedURLException;
import java.net.URL;


public class schoolBusNetworkUtils {
    private static final String TAG = schoolBusNetworkUtils.class.getSimpleName();

    private final static String GITHUB_API_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/calendar.json";
    private final static String GITEE_API_URL = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/calendar.json";

    public static URL build_school_calendar_url(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"github");
        if (api_string.equals("gitee")){
            api_string = GITEE_API_URL;
        }
        else{
            api_string = GITHUB_API_URL;
        }
        Log.d(TAG,"calendar api source : "+api_string);

        Uri buildUri = Uri.parse(api_string).buildUpon()
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
}
