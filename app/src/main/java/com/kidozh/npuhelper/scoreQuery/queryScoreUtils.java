package com.kidozh.npuhelper.scoreQuery;

import android.util.Log;

import com.kidozh.npuhelper.accountAuth.loginUtils;
import com.kidozh.npuhelper.accountAuth.schoolCardUtils;
import com.kidozh.npuhelper.accountAuth.userInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.kidozh.npuhelper.accountAuth.loginUtils.encryptWith3DES;
import static com.kidozh.npuhelper.accountAuth.loginUtils.getSoapSerializationEnvelope;
import static com.kidozh.npuhelper.accountAuth.loginUtils.nameSpace;
import static com.kidozh.npuhelper.accountAuth.loginUtils.privateKey;

public class queryScoreUtils {
    final static String TAG = queryScoreUtils.class.getSimpleName();
    private static String soapGetOCIDAction = "http://service.login.newmobile.com/getAllScores";

    public static class scoreBeam{
        public String name;
        public String scoreNumber;
        public String scoreWeight;
        public Date updateAt;
        scoreBeam(String name,String scoreNumber,String scoreWeight, Date updateAt){
            this.name = name;
            this.scoreNumber  =scoreNumber;
            this.scoreWeight = scoreWeight;
            this.updateAt = updateAt;
        }
    }

    public static SoapSerializationEnvelope getScoreInfoEnvelope(String username, String apptoken){
        String getocbalanceMethodName = "getAllScores";
        SoapObject request = new SoapObject(nameSpace,getocbalanceMethodName);
        Log.d(TAG,"SEND REQ: "+request.getName());
        try{
            request.addProperty("username",encryptWith3DES(username,apptoken));
            //request.addProperty("strKey",encryptWith3DES(privateKey,apptoken));
            request.addProperty("apptoken",apptoken);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        return envelope;
    }

    public static SoapObject fetchResult(SoapSerializationEnvelope envelope, String url, String soapLoginAction) {
        HttpTransportSE httpTransportSE = new HttpTransportSE(url);

        try {
            httpTransportSE.call(soapLoginAction, envelope);//调用
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        SoapObject response = null;//获得返回对象
        response = (SoapObject) envelope.bodyIn;
        //Log.i(TAG,response.getName());
        return response;
    }

    public static SoapObject getScoreInfoResult(String username, String apptoken){
        SoapSerializationEnvelope envelope = getScoreInfoEnvelope(username,apptoken);
        return fetchResult(envelope, loginUtils.wsdlURL,soapGetOCIDAction);
    }

    public static List<scoreBeam> parseCardInfoRes(SoapObject soapObject){

        SoapPrimitive loginResponse = (SoapPrimitive) soapObject.getProperty(0);

        String given_string = (String) loginResponse.getValue();
        Log.i(TAG,"Given XML : "+given_string);
        List<scoreBeam> scoreBeamList = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(given_string);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject score = (JSONObject) jsonArray.get(i);
                String scoreName = score.optString("kcmc","");
                Boolean isRepeated = false;
                for (int j=0 ;j<scoreBeamList.size();j++){
                    scoreBeam scoreInfo = scoreBeamList.get(j);
                    if(scoreName.equals(scoreInfo.name)){
                        isRepeated = true;
                        break;
                    }
                }
                if(isRepeated) continue;
                String scoreWeight = score.optString("xf","");
                String scoreValue = score.optString("cj","");
                String updateTimeString = score.optString("createtime","");
                Date date;
                if(scoreValue.equals("") || scoreName.equals("")){
                    break;
                }
                try{
                    DateFormat fmt =new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S", Locale.getDefault());
                     date = fmt.parse(updateTimeString);
                }
                catch (ParseException e){
                    e.printStackTrace();
                    date = null;
                }
                scoreBeamList.add(new scoreBeam(scoreName,scoreValue,scoreWeight,date));


            }
            return scoreBeamList;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }


    }

    public static float getWeightsAveragePoints(List<scoreBeam> scoreBeamList){
        float totalWeight = 0, totalScore = 0;
        for(int i=0; i< scoreBeamList.size(); i++){
            scoreBeam score = scoreBeamList.get(i);
            if(score.scoreWeight.equals("")){
                return 0;
            }
            else {
                totalWeight += Float.parseFloat(score.scoreWeight);
                totalScore += Float.parseFloat(score.scoreWeight) * Float.parseFloat(score.scoreNumber);
            }
        }
        return totalScore / totalWeight;
    }

    public static double getUSPoint(float score){
        if(score >= 90) return 4.0;
        else if(score >= 80) return 3.0;
        else if(score >= 70) return 2.0;
        else if(score >= 60) return 1.0;
        return 0;
    }

    public static float getUSGPAPoints(List<scoreBeam> scoreBeamList){
        float totalWeight = 0, totalScore = 0;
        for(int i=0; i< scoreBeamList.size(); i++){
            scoreBeam score = scoreBeamList.get(i);
            if(score.scoreWeight.equals("")){
                return 0;
            }
            else {
                totalWeight += Float.parseFloat(score.scoreWeight);
                totalScore += Float.parseFloat(score.scoreWeight) * getUSPoint(Float.parseFloat(score.scoreNumber));
            }
        }
        return totalScore / totalWeight;
    }

    public static String getNPUPointText(float score){
        String[] labelList = {"A+","A","A-","B+","B","B-","C+","C","C-","D","F"};
        if(score>=70){
            int intscore = (int) score;
            int index = (int) Math.floor(7.9 - (intscore-60) / 5);
            if(index<0) index=0;
            return labelList[index];
        }
        else if(score >= 67) return "C+";
        else if(score >= 65) return  "C";
        else if(score >= 61) return "C-";
        else if(score>=60){
            return "D";
        }
        else {
            return "F";
        }


    }

    public static double getNPUPoint(float score){
        if(score >= 60){
            return 5 - (100 - score)/10.0;
        }
        else return 0;

    }

    public static float getNPUGPAPoints(List<scoreBeam> scoreBeamList){
        float totalWeight = 0, totalScore = 0;
        for(int i=0; i< scoreBeamList.size(); i++){
            scoreBeam score = scoreBeamList.get(i);
            if(score.scoreWeight.equals("")){
                return 0;
            }
            else {
                totalWeight += Float.parseFloat(score.scoreWeight);
                totalScore += Float.parseFloat(score.scoreWeight) * getNPUPoint(Float.parseFloat(score.scoreNumber));
            }
        }
        return totalScore / totalWeight;
    }
}
