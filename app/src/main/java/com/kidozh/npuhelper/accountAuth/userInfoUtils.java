package com.kidozh.npuhelper.accountAuth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.kidozh.npuhelper.MainActivity;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import androidx.appcompat.app.AlertDialog;

public class userInfoUtils {
    private static String TAG = userInfoUtils.class.getSimpleName();

    private static String documentIDName = "documentID";

    public static SoapSerializationEnvelope getDocumentIdEnvelope(String username, String apptoken){
        
        return loginUtils.getDocumentIdEnvelope(username,apptoken);
    }

    public static SoapObject fetchResult(SoapSerializationEnvelope envelope, String url) {
        return loginUtils.fetchResult(envelope,url);
    }

    public static SoapObject getDocumentIdResult(String username, String apptoken){
        SoapSerializationEnvelope envelope = getDocumentIdEnvelope(username,apptoken);
        return fetchResult(envelope,loginUtils.wsdlURL);
    }

    public static SoapSerializationEnvelope getDocumentDetailEnvelope(String username, String apptoken,String documentID){
        String getDocumentIdMethodName = "personDocumentInformation";
        SoapObject request = new SoapObject(loginUtils.nameSpace,getDocumentIdMethodName);
        try{
            request.addProperty("userName",loginUtils.encryptWith3DES(username,apptoken));
            request.addProperty("informationName",loginUtils.encryptWith3DES(username,apptoken));
            request.addProperty("informationId",loginUtils.encryptWith3DES(documentID,apptoken));
            request.addProperty("strKey",loginUtils.encryptWith3DES(loginUtils.privateKey,apptoken));
            request.addProperty("apptoken",apptoken);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        SoapSerializationEnvelope envelope = loginUtils.getSoapSerializationEnvelope(request);
        return envelope;


    }

    public static SoapObject getDocumentDetailResult(String username, String apptoken, String documentID){
        SoapSerializationEnvelope envelope = getDocumentDetailEnvelope(username,apptoken,documentID);
        return fetchResult(envelope,loginUtils.wsdlURL);
    }

    public static accountInfoBean getTokenInfoFromLocal(Context context){
        return loginUtils.getTokenInfoToLocal(context);
    }

    public static String parseDocumentId(SoapObject soapObject){
        SoapPrimitive loginResponse = (SoapPrimitive) soapObject.getProperty(0);


        try {
            String given_xml = (String) loginResponse.getValue();
            Log.i(TAG,"Given XML : "+given_xml);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(given_xml)));
            NodeList portalInfoList = document.getElementsByTagName("module");
            Element infoElement = (Element) portalInfoList.item(0);
            Log.i(TAG,"Recv Document Id Info"+infoElement.toString());
            String informationId = getNodeValue("informationid",infoElement);
            return informationId;


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }



    public static Map<String,String> parseDocumentDetail(SoapObject soapObject){
        SoapPrimitive loginResponse = (SoapPrimitive) soapObject.getProperty(0);

        String given_xml = (String) loginResponse.getValue();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = null;

        Map<String, String> map = new HashMap<String, String>();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(given_xml)));
            NodeList itemInfoList = document.getElementsByTagName("data");
            // every data
            for(int i = 0; i<itemInfoList.getLength();i++){
                Node dataNode = itemInfoList.item(i);
                Element dataElement = (Element) dataNode;
                String name = dataElement.getElementsByTagName("name").item(0).getTextContent();
                String value = dataElement.getElementsByTagName("value").item(0).getTextContent();
                map.put(name,value);

            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



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

    public static boolean saveDocumentIDToLocal(Context context, String documentID){
        SharedPreferences sharedPreferences = context.getSharedPreferences(loginUtils.getAuthSharedPreferenceName(),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(documentIDName,documentID);
        editor.apply();
        return true;
    }

    public static String getDocumentIDFromLocal(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(loginUtils.getAuthSharedPreferenceName(),0);
        return sharedPreferences.getString(documentIDName,"");
    }
}
