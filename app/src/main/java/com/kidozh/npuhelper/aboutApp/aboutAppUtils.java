package com.kidozh.npuhelper.aboutApp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;

public class aboutAppUtils {

    public static String getAppVersionUrlString(){
        return "https://raw.githubusercontent.com/kidozh/nwpu_info_api/master/v1/app_version.json";
    }

    public static String getAppProjectUrlString(){
        return "https://github.com/kidozh/NpuHelper";
    }

    public static String getAppDeveloperUrlString(){
        return "https://github.com/kidozh/NpuHelper";
    }

    public static List<appInfo> getAppInfoList(Context context){
        List<appInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        String appVersion;
        try{
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            appVersion = packInfo.versionName;
        }
        catch (Exception e){
            appVersion = context.getString(R.string.unknown);
        }


        appInfoList.add(new appInfo(R.drawable.vector_drawable_app_version_notice,R.string.about_app_version,appVersion));
        appInfoList.add(new appInfo(R.drawable.vector_drawable_update,R.string.about_app_latest_version,""));
        appInfoList.add(new appInfo(R.drawable.vector_drawable_people,R.string.about_app_developer,"kidozh"));
        appInfoList.add(new appInfo(R.drawable.vector_drawable_github,R.string.about_app_project_on_github,"NpuHelper","https://github.com/kidozh/NpuHelper"));

        return appInfoList;
    }

    public static class appInfo{
        public int imageResource;
        public int infoKeyResource;
        public String infoValue;
        public String externalUrl;

        appInfo(int imageResource,int infoKeyResource,String infoValue){
            this.imageResource = imageResource;
            this.infoKeyResource = infoKeyResource;
            this.infoValue = infoValue;
        }
        appInfo(int imageResource,int infoKeyResource,String infoValue,String externalUrl){
            this.imageResource = imageResource;
            this.infoKeyResource = infoKeyResource;
            this.infoValue = infoValue;
            this.externalUrl = externalUrl;
        }
    }
}
