package com.kidozh.npuhelper.schoolCalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kidozh.npuhelper.R;

import java.net.MalformedURLException;
import java.net.URL;

public class schoolCalendarUtils {
    private static final String TAG = schoolCalendarUtils.class.getSimpleName();

    final static private String GITHUB_CALENDAR_API_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/calendar.json";
    final static private String GITEE_CALENDAR_API_URL  = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/calendar.json";

    public static URL build_school_calendar_url(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"gitee");
        if (api_string.equals("gitee")){
            api_string = GITEE_CALENDAR_API_URL;
        }
        else{
            api_string = GITHUB_CALENDAR_API_URL;
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

    public static URL build_school_news_url(Context mContext){
        String api_string = "http://news.nwpu.edu.cn/system/resource/newsdata/getData.jsp";
        Log.d(TAG,"NEWS api source : "+api_string);

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

    public static URL build_hot_news_url(Context mContext){
        String api_string = "http://news.nwpu.edu.cn/system/resource/js/news/hotdynpullnews.jsp";
        Log.d(TAG,"Hot NEWS api source : "+api_string);

        Uri buildUri = Uri.parse(api_string).buildUpon()
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Hot news api source RAW URI"+url);
        return url;
    }



}
