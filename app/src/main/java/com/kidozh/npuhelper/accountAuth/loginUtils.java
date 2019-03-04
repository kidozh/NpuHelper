package com.kidozh.npuhelper.accountAuth;

import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class loginUtils {
    private static String TAG = loginUtils.class.getSimpleName();
    private static String privateKey = "WYNn2rNOtkuMGGlPrFSaMB0rQoBUmssS";

    private static String wsdlURL = "http://m-ecampus.nwpu.edu.cn/zftal-mobile/webservice/newmobile/MobileLoginXMLService";

    private static String nameSpace = "http://service.login.newmobile.com/";

    private static String recommendMethodName = "getMhRecommendPage";

    private static String soapLoginAction = "http://service.login.newmobile.com/Login";

    private static String loginMethodName = "Login";


    public String getPrivateKey() {
        return privateKey;
    }

    public String getLoginMethodName() {
        return loginMethodName;
    }

    public String getRecommendMethodName() {
        return recommendMethodName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getWsdlURL() {
        return wsdlURL;
    }

    public static SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request){
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
        return envelope;
    }

    public static void addPropertyInfo(SoapObject request, String name, String value){
        PropertyInfo PI = new PropertyInfo();
        PI.setNamespace(nameSpace);
        PI.setName(name);
        PI.setType(PropertyInfo.STRING_CLASS);
        PI.setValue(value);
        request.addProperty(PI);
    }

    public static SoapSerializationEnvelope getLoginSerializationEnvelope(String username, String password){
        SoapObject request = new SoapObject(nameSpace,loginMethodName);
        addPropertyInfo(request,"userName",username);
        addPropertyInfo(request,"strKey",privateKey);
        addPropertyInfo(request,"passWord",password);
//        request.addProperty("userName",username);
//        request.addProperty("passWord",password);
//        request.addProperty("strKey",privateKey);
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        return envelope;
    }

    public static String loginToGetToken(String username, String password) {
        SoapSerializationEnvelope envelope = getLoginSerializationEnvelope(username,password);


        HttpTransportSE httpTransportSE = new HttpTransportSE(wsdlURL);
        httpTransportSE.debug = true;
        Log.d(TAG,httpTransportSE.requestDump+" \n"+envelope.toString());

        try {
            httpTransportSE.call(soapLoginAction, envelope);//调用
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        SoapPrimitive response = null;//获得返回对象
        try {
             response = (SoapPrimitive) envelope.getResponse();
             Log.i(TAG,response.toString());
            // response = (SoapObject) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if(response!=null){
            System.out.println(response);
            return response.toString();
        }
        return "";
    }
}
