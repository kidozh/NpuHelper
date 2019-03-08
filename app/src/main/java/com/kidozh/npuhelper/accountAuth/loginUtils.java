package com.kidozh.npuhelper.accountAuth;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.util.regex.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kobjects.base64.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class loginUtils {
    private static String TAG = loginUtils.class.getSimpleName();
    public static String privateKey = "WYNn2rNOtkuMGGlPrFSaMB0rQoBUmssS";

    public static String wsdlURL = "http://m-ecampus.nwpu.edu.cn/zftal-mobile/webservice/newmobile/MobileLoginXMLService";

    public static String nameSpace = "http://service.login.newmobile.com/";

    private static String recommendMethodName = "getMhRecommendPage";

    private static String soapLoginAction = "http://service.login.newmobile.com/Login";

    private static String loginMethodName = "Login";

    private static String authSharedPreferenceName = "authPortalToken";

    public static String getAuthSharedPreferenceName() {
        return authSharedPreferenceName;
    }


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
        envelope.dotNet = false;//由于不是.net开发的webservice，所以这里要设置为false
        return envelope;
    }

    public static SoapSerializationEnvelope getLoginSerializationEnvelope(String username, String password){
        SoapObject request = new SoapObject(nameSpace,loginMethodName);

        request.addProperty("userName",username);
        request.addProperty("passWord",password);
        request.addProperty("strKey",privateKey);
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        return envelope;
    }

    public static SoapSerializationEnvelope getDocumentIdEnvelope(String username, String apptoken){
        String getDocumentIdMethodName = "personDocumentInformationList";
        SoapObject request = new SoapObject(nameSpace,getDocumentIdMethodName);
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

    public static SoapObject fetchResult(SoapSerializationEnvelope envelope,String url) {

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
        Log.i(TAG,response.toString()+response.getName());
        return response;
    }



    public static SoapObject loginToGetToken(String username, String password) {
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

        SoapObject response = null;//获得返回对象
        response = (SoapObject) envelope.bodyIn;
        Log.i(TAG,response.toString()+response.getName());
        return response;
    }

    public static boolean isUserSignedSuccessfully(SoapObject s){
        // Name : LoginResponse
        for(int i = 0;i < s.getAttributeCount(); i++){
            Log.i(TAG,"Returned Key : "+s.getAttributeAsString(i));
        }
        SoapPrimitive loginResponse = (SoapPrimitive) s.getProperty(0);
        Log.i(TAG,loginResponse.getName()+"~~~"+loginResponse.getValue()+"###"+"!!!!"+loginResponse.getAttributeCount());
        String returned_value = (String) loginResponse.getValue();

        if (Pattern.matches(".*?<code>404</code>.*?",returned_value)){
            return false;
        }
        else {
            accountInfoBean accountInfo = parseSuccessfulXMLText(s);
            return true;
        }
    }

    public static accountInfoBean parseSuccessfulXMLText(SoapObject s){
        SoapPrimitive loginResponse = (SoapPrimitive) s.getProperty(0);

        String given_xml = (String) loginResponse.getValue();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        accountInfoBean accountInfo = new accountInfoBean();
        try{
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(new InputSource(new StringReader(given_xml)));
            NodeList portalInfoList = document.getElementsByTagName("mh");
            Element portalInfoElement = (Element) portalInfoList.item(0);
            Log.i(TAG,"Recv Portal Info"+portalInfoElement.toString());

            accountInfo.name = getNodeValue("xm",portalInfoElement);
            accountInfo.role = getNodeValue("role",portalInfoElement);
            accountInfo.bm = getNodeValue("bm",portalInfoElement);
            accountInfo.accountNumber = getNodeValue("yhm",portalInfoElement);
            accountInfo.appName = getNodeValue("appname",portalInfoElement);
            accountInfo.college = getNodeValue("xy",portalInfoElement);
            accountInfo.majorNumber = getNodeValue("zydm",portalInfoElement);
            accountInfo.majorName = getNodeValue("zymc",portalInfoElement);
            accountInfo.classNumber = getNodeValue("bj",portalInfoElement);
            accountInfo.grade = getNodeValue("nj",portalInfoElement);
            accountInfo.dqxnxq = getNodeValue("dqxnxq",portalInfoElement);
            accountInfo.appToken = getNodeValue("app_token",portalInfoElement);
            accountInfo.TGTticket = getNodeValue("TGTticket",portalInfoElement);
            accountInfo.STticket = getNodeValue("STticket",portalInfoElement);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return null;
        }
        return accountInfo;

    }

    protected static String getNodeValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        Node node = nodeList.item(0);
        if(node!=null){
            if(node.hasChildNodes()){
                Node child = node.getFirstChild();
                while (child!=null){
                    if(child.getNodeType() == Node.TEXT_NODE){
                        return  child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static accountInfoBean saveTokenInfoToLocal(Context context,accountInfoBean accountInfo){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(getAuthSharedPreferenceName(),0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Field[] fields = accountInfo.getClass().getDeclaredFields();
            for (Field f:fields){
                f.setAccessible(true);
                String field = f.toString().substring(f.toString().lastIndexOf(".")+1);
                String value = (String) f.get(accountInfo);
                Log.i(TAG," saved : "+field+"   "+value);
                editor.putString(field,value);
            }
            editor.apply();
            return accountInfo;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String PASSWORD_KEY_IN_STORAGE = "KEY_STORAGE";

    // in order to use uis.nwpu.edu.cn service...
    // although it's strongly not recommended to save token in local...
    public static boolean savePasswordInfoToLocal(Context context,String password){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(getAuthSharedPreferenceName(),0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PASSWORD_KEY_IN_STORAGE,password);
            editor.apply();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String getPasswordInfoFromLocal(Context context){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(getAuthSharedPreferenceName(),0);
            return sharedPreferences.getString(PASSWORD_KEY_IN_STORAGE,"");

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static void clearAccountInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(getAuthSharedPreferenceName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public static accountInfoBean getTokenInfoToLocal(Context context){
        accountInfoBean accountInfo = new accountInfoBean();
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(getAuthSharedPreferenceName(),0);
            Field[] fields = accountInfo.getClass().getDeclaredFields();
            boolean allNull = true;
            for (Field f:fields){
                f.setAccessible(true);
                String field = f.toString().substring(f.toString().lastIndexOf(".")+1);
                String value = sharedPreferences.getString(field,"");
                //Log.i(TAG,field+"   "+value);
                if(value.length() != 0){
                    allNull = false;
                }
                f.set(accountInfo,value);
            }
            if (allNull){
                return null;
            }
            else {
                return accountInfo;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static String encryptWith3DES(String encryptKey,String encryptValue) throws Exception {
        // Referenced Code...
//    public static String a(String paramString1, String paramString2)
//            throws Exception
//    {
//        DESedeKeySpec paramString = new DESedeKeySpec(paramString2.getBytes());
//        paramString = SecretKeyFactory.getInstance("desede").generateSecret(paramString);
//        Cipher localCipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
//        localCipher.init(1, paramString2, new IvParameterSpec("76543210".getBytes()));
//        return Base64.encode(localCipher.doFinal(paramString1.getBytes("utf-8")));
//    }
        DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(encryptValue.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
        SecretKey secretKey = keyFactory.generateSecret(deSedeKeySpec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        cipher.init(1,secretKey,new IvParameterSpec("76543210".getBytes()));
        return Base64.encode(cipher.doFinal(encryptKey.getBytes("utf-8")));
    }





}
