package com.kidozh.npuhelper.accountAuth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.kidozh.npuhelper.accountAuth.loginUtils.encryptWith3DES;
import static com.kidozh.npuhelper.accountAuth.loginUtils.getSoapSerializationEnvelope;
import static com.kidozh.npuhelper.accountAuth.loginUtils.nameSpace;
import static com.kidozh.npuhelper.accountAuth.loginUtils.privateKey;

public class schoolCardUtils {
    private static String TAG = schoolCardUtils.class.getSimpleName();

    private static String soapGetOCIDAction = "http://service.login.newmobile.com/getocbalance";
    private static String soapGetOCIDDetailAction = "http://service.login.newmobile.com/getocdetail";

    public static class schoolCardInfoBean{
        public String balance;
        public String cardNumber;
        schoolCardInfoBean(String cardNumber,String balance) {
            this.balance = balance;
            this.cardNumber = cardNumber;
        }
    }



    public static SoapSerializationEnvelope getCardInfoEnvelope(String username, String apptoken){
        String getocbalanceMethodName = "getocbalance";
        SoapObject request = new SoapObject(nameSpace,getocbalanceMethodName);
        Log.d(TAG,"SEND REQ: "+request.getName());
        try{
            request.addProperty("userName",encryptWith3DES(username,apptoken));
            request.addProperty("strKey",encryptWith3DES(privateKey,apptoken));
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
            HttpTransportSE httpTransportSE = new HttpTransportSE(url,5000);

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

    public static SoapObject getCardInfoResult(String username, String apptoken){
        SoapSerializationEnvelope envelope = getCardInfoEnvelope(username,apptoken);
        return fetchResult(envelope,loginUtils.wsdlURL,soapGetOCIDAction);
    }

    public static schoolCardInfoBean parseCardInfoRes(SoapObject soapObject){
        if (soapObject == null){
            return null;
        }
        SoapPrimitive loginResponse = (SoapPrimitive) soapObject.getProperty(0);

        String given_xml = (String) loginResponse.getValue();
        Log.i(TAG,"Given XML : "+given_xml);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(given_xml)));
            NodeList portalInfoList = document.getElementsByTagName("OcBalance");
            Element infoElement = (Element) portalInfoList.item(0);
            Log.i(TAG,"Recv Card Info"+infoElement.toString());
            String ocid = userInfoUtils.getNodeValue("ocid",infoElement);
            String balance = userInfoUtils.getNodeValue("balance",infoElement);
            return new schoolCardInfoBean(ocid,balance);


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static SoapSerializationEnvelope getBalanceEnvelope(String apptoken,
                                                               String ocid,
                                                               String index,
                                                               String pageSize,
                                                               Date startTime,
                                                               Date endTime){
        String getDocumentIdMethodName = "getocdetail";
        SoapObject request = new SoapObject(nameSpace,getDocumentIdMethodName);
        try{
            request.addProperty("detailtype", encryptWith3DES("1",apptoken));
            request.addProperty("ocid", encryptWith3DES(ocid,apptoken));
            request.addProperty("pageindex", encryptWith3DES(index,apptoken));
            request.addProperty("pagesize", encryptWith3DES(pageSize,apptoken));
            // deal with time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String startString = formatter.format(startTime);
            String endString  = formatter.format(endTime);
            request.addProperty("startdate", encryptWith3DES(startString,apptoken));
            request.addProperty("enddate", encryptWith3DES(endString,apptoken));
            request.addProperty("strkey", encryptWith3DES(privateKey,apptoken));
            request.addProperty("apptoken",apptoken);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        return envelope;


    }

    public static SoapSerializationEnvelope getChargeEnvelope(String apptoken,
                                                               String ocid,
                                                               String index,
                                                               String pageSize,
                                                               Date startTime,
                                                               Date endTime){
        String getDocumentIdMethodName = "getocdetail";
        SoapObject request = new SoapObject(nameSpace,getDocumentIdMethodName);
        try{
            request.addProperty("detailtype", encryptWith3DES("0",apptoken));
            request.addProperty("ocid", encryptWith3DES(ocid,apptoken));
            request.addProperty("pageindex", encryptWith3DES(index,apptoken));
            request.addProperty("pagesize", encryptWith3DES(pageSize,apptoken));
            // deal with time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String startString = formatter.format(startTime);
            String endString  = formatter.format(endTime);
            request.addProperty("startdate", encryptWith3DES(startString,apptoken));
            request.addProperty("enddate", encryptWith3DES(endString,apptoken));
            request.addProperty("strkey", encryptWith3DES(privateKey,apptoken));
            request.addProperty("apptoken",apptoken);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        return envelope;


    }


    public static SoapObject getBalanceDetailResult( String apptoken, String ocid,
                                                    String index,
                                                    String pageSize,
                                                    Date startTime,
                                                    Date endTime){
        SoapSerializationEnvelope envelope = getBalanceEnvelope(apptoken,ocid,index,pageSize,startTime,endTime);
        return fetchResult(envelope,loginUtils.wsdlURL,soapGetOCIDDetailAction);
    }

    public static SoapObject getChargeDetailResult( String apptoken, String ocid,
                                                     String index,
                                                     String pageSize,
                                                     Date startTime,
                                                     Date endTime){
        SoapSerializationEnvelope envelope = getChargeEnvelope(apptoken,ocid,index,pageSize,startTime,endTime);
        return fetchResult(envelope,loginUtils.wsdlURL,soapGetOCIDDetailAction);
    }

    public static accountInfoBean getTokenInfoFromLocal(Context context){
        return loginUtils.getTokenInfoToLocal(context);
    }



    private static String ocidName = "ocid";

    public static boolean saveOCIDToLocal(Context context, String ocid){
        SharedPreferences sharedPreferences = context.getSharedPreferences(loginUtils.getAuthSharedPreferenceName(),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ocidName,ocid);
        editor.apply();
        return true;
    }

    public static String getOCIDFromLocal(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(loginUtils.getAuthSharedPreferenceName(),0);
        return sharedPreferences.getString(ocidName,"");
    }


}
