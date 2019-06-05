package com.kidozh.npuhelper.physicalExercise;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kidozh.npuhelper.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Response;

public class stadiumInfoUtils {
    private static String TAG = stadiumInfoUtils.class.getSimpleName();

    public static class stadiumAvaliabilityInfo{
        public String areaID;
        public String personNum;

        public stadiumAvaliabilityInfo(String areaID,String personNum){
            this.areaID = areaID;
            this.personNum = personNum;
        }
    }

    public static class stadiumInfoBean implements Parcelable {
        public String stadiumID;
        public String areaID;
        public String stadiumName;
        public String areaName;
        public String stadiumPurpose;
        public String areaType;
        public String available_facilities_num;
        public String all_facilities_num;

        stadiumInfoBean(){}

        stadiumInfoBean(String stadiumID,String areaID,String stadiumName,String areaName,String stadiumPurpose,String areaType){
            this.stadiumID = stadiumID;
            this.areaID = areaID;
            this.stadiumName = stadiumName;
            this.areaName = areaName;
            this.stadiumPurpose = stadiumPurpose;
            this.areaType = areaType;
        }

        stadiumInfoBean(String stadiumID,String areaID,String stadiumName,String areaName,String stadiumPurpose,String areaType,String available_num, String all_num){
            this.stadiumID = stadiumID;
            this.areaID = areaID;
            this.stadiumName = stadiumName;
            this.areaName = areaName;
            this.stadiumPurpose = stadiumPurpose;
            this.areaType = areaType;
            this.available_facilities_num = available_num;
            this.all_facilities_num = all_num;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(stadiumID);
            dest.writeString(areaID);
            dest.writeString(stadiumName);
            dest.writeString(areaName);
            dest.writeString(stadiumPurpose);
            dest.writeString(areaType);
            dest.writeString(available_facilities_num);
            dest.writeString(all_facilities_num);
        }

        public static final Creator<stadiumInfoBean> CREATOR = new Creator<stadiumInfoBean>() {
            @Override
            public stadiumInfoBean createFromParcel(Parcel source) {
                stadiumInfoBean stadiumInfo = new stadiumInfoBean();
                stadiumInfo.stadiumID = source.readString();
                stadiumInfo.areaID = source.readString();
                stadiumInfo.stadiumName = source.readString();
                stadiumInfo.areaName = source.readString();
                stadiumInfo.stadiumPurpose = source.readString();
                stadiumInfo.areaType = source.readString();
                stadiumInfo.available_facilities_num = source.readString();
                stadiumInfo.all_facilities_num = source.readString();
                return stadiumInfo;
            }

            @Override
            public stadiumInfoBean[] newArray(int size) {
                return new stadiumInfoBean[size];
            }
        };
    }

    public static List<stadiumInfoBean> getAllAccessibleStadium(){
        List<stadiumInfoBean> stadiumInfoBeanList = new ArrayList<>();
        stadiumInfoBeanList.add(new stadiumInfoBean("293800000006",
                "311900000007","翱翔训练馆","羽毛球场","羽毛球","普通场"));

        stadiumInfoBeanList.add(new stadiumInfoBean("293800000001",
                "311900000002","综合训练馆","羽毛球场","羽毛球","普通场"));

        stadiumInfoBeanList.add(new stadiumInfoBean("293800000009",
                "311900000008","西苑训练馆","羽毛球场","羽毛球","普通场"));

        stadiumInfoBeanList.add(new stadiumInfoBean("293800000006",
                "311900000005","翱翔训练馆","乒乓球场","乒乓球","普通场"));

        stadiumInfoBeanList.add(new stadiumInfoBean("293800000009",
                "311900000009","西苑训练馆","乒乓球场","乒乓球","普通场"));

        stadiumInfoBeanList.add(new stadiumInfoBean("293800000001",
                "311900000006","综合训练馆","网球场","网球","普通场"));

        return stadiumInfoBeanList;
    }

    private static String markedStadiumKey = "MARKED_STADIUM_KEY";

    public static List<stadiumInfoBean> getMarkedStadiumIDJsonlist(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String markedStadiumJsonstring = prefs.getString(markedStadiumKey,"");
        if(markedStadiumJsonstring == null || markedStadiumJsonstring.equals("")){
            return null;
        }
        else {
            // exact hu xian
            List<stadiumInfoBean> allAccessibleStadium = getAllAccessibleStadium();
            List<stadiumInfoBean> markedAccessibleStadium = new ArrayList<>();
            try{

                JSONArray areaIDList = new JSONArray(markedStadiumJsonstring);
                for(int i=0;i<areaIDList.length();i++){
                    String currentAreaID = (String) areaIDList.get(i);
                    for(int j=0;j<allAccessibleStadium.size();j++){
                        if(allAccessibleStadium.get(j).areaID.equals(currentAreaID)){
                            markedAccessibleStadium.add(allAccessibleStadium.get(j));
                        }
                    }
                }
                return markedAccessibleStadium;
            }
            catch (Exception e){
                return null;
            }
        }
    }

    public static void setMarkedStadiumIDJsonlist(Context context, List<stadiumInfoBean> stadiumInfoBeanList){
        List<String> idArray = new ArrayList<>();
        for(int i=0;i<stadiumInfoBeanList.size();i++){
            idArray.add(stadiumInfoBeanList.get(i).areaID);
        }
        JSONArray idJsonArray = new JSONArray(idArray);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(markedStadiumKey,idJsonArray.toString());
        editor.apply();
    }

    public static Request buildQueryStadiumInfoRequest(String areaID){
        String host = "http://222.24.192.216:9000/NEW/PublicRequest";
        String jsonString = "";
        Map<String,String> sendDataMap = new HashMap<>();
        sendDataMap.put("AreaID",areaID);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String t=format.format(new Date());
        sendDataMap.put("OrderDate",t);
        JSONObject sendDataJSON = new JSONObject(sendDataMap);
        Uri build_uri = Uri.parse(host).buildUpon()
                .appendQueryParameter("RequestName","QueryFieldInfo")
                .appendQueryParameter("RequestData",sendDataJSON.toString())
                .build();
        Log.d(TAG,"send : "+sendDataJSON.toString());
        String url = build_uri.toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        return request;
    }

    public static Map<String,Integer> parseAvalibilityJSON(String jsonString){

        try {
            Map<String,Integer> avalibilityMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.getBoolean("isError")){
                return null;
            }
            else {
                JSONArray resultArray = jsonObject.getJSONArray("Result");
                int allAvaliable= 0;
                for(int i=0;i<resultArray.length();i++){
                    JSONObject admissionObj = (JSONObject) resultArray.get(i);
                    if(admissionObj.getString("AdmissionPeopleCount").equals("0")){
                        allAvaliable +=1;
                    }
                }
                avalibilityMap.put("all", resultArray.length());
                avalibilityMap.put("access",allAvaliable);
                return avalibilityMap;

            }
        }
        catch (Exception e){
            return null;
        }
    }

    public static List<stadiumInfoUtils.stadiumAvaliabilityInfo> parseAvalibilityInfo(String jsonString){

        try {
            List<stadiumInfoUtils.stadiumAvaliabilityInfo> stadiumAvaliabilityInfos = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.getBoolean("isError")){
                return null;
            }
            else {
                JSONArray resultArray = jsonObject.getJSONArray("Result");
                for(int i=0;i<resultArray.length();i++){
                    JSONObject admissionObj = (JSONObject) resultArray.get(i);

                    stadiumAvaliabilityInfos.add(
                            new stadiumAvaliabilityInfo(admissionObj.getString("FieldNo"),
                                    admissionObj.getString("AdmissionPeopleCount"))
                    );
                }

                return stadiumAvaliabilityInfos;

            }
        }
        catch (Exception e){
            return null;
        }
    }
}
