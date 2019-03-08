package com.kidozh.npuhelper.campusAddressBook;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kidozh.npuhelper.R;

import java.net.MalformedURLException;
import java.net.URL;

public class campusAddressBookInfoUtils {
    private static final String TAG = campusAddressBookInfoUtils.class.getSimpleName();

    final static private String GITHUB_API_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/department_phone.json";
    final static private String GITEE_API_URL = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/department_phone.json";

    final static private String GITHUB_DETAILED_API_URL = "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/phone_detail_minify.json";
    final static private String GITEE_DETAILED_API_URL = "https://gitee.com/kidozh/nwpu_info_api/raw/master/v1/phone_detail_minify.json";


    public static URL build_url(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"gitee");
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

    public static URL build_detailed_url(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String api_string = prefs.getString(mContext.getString(R.string.pref_location_api_source),"gitee");
        if (api_string.equals("gitee")){
            api_string = GITEE_DETAILED_API_URL;
        }
        else{
            api_string = GITHUB_DETAILED_API_URL;
        }
        // Log.d(TAG,"api source : "+api_string);

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
}
