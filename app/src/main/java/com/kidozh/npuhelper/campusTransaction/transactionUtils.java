package com.kidozh.npuhelper.campusTransaction;


import android.util.Log;

import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class transactionUtils{
    final static String TAG = transactionUtils.class.getSimpleName();

    public static boolean isAppTokenError(SoapObject result){
        try{
            SoapPrimitive loginResponse = (SoapPrimitive) result.getProperty(0);

            String given_xml = (String) loginResponse.getValue();
            if(given_xml.contains("app_token error")){
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }

    public static List<expensesRecordEntity> parseExpensesInfo(SoapObject result){
        SoapPrimitive loginResponse = (SoapPrimitive) result.getProperty(0);

        String given_xml = (String) loginResponse.getValue();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = null;
        List<expensesRecordEntity> expensesRecordEntities = new ArrayList<>();

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(given_xml)));
            NodeList itemInfoList = document.getElementsByTagName("Item");
            // every data
            for(int i = 0; i<itemInfoList.getLength();i++){
                Node dataNode = itemInfoList.item(i);
                Element dataElement = (Element) dataNode;
                String location = dataElement.getElementsByTagName("consumeaspect").item(0).getTextContent();
                String timeString = dataElement.getElementsByTagName("consumetime").item(0).getTextContent();
                String payment = dataElement.getElementsByTagName("outlay").item(0).getTextContent();
                String balance = dataElement.getElementsByTagName("balance").item(0).getTextContent();
                SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date payDate = sdf.parse(timeString);
                expensesRecordEntity entity = new expensesRecordEntity(location,
                        Float.parseFloat(payment),
                        Float.parseFloat(balance),
                        payDate);
                expensesRecordEntities.add(entity);


            }
            // need to filter
            for(int i=0;i<expensesRecordEntities.size();i++){
                if(expensesRecordEntities.size() <= 1 ){
                    // no need to filter
                    break;
                }
                expensesRecordEntity curExpensesRecord = expensesRecordEntities.get(i);
                Log.d(TAG,"BEFORE SCANING: "+i+" "+curExpensesRecord.balance+" "+ curExpensesRecord.amount);
                if(curExpensesRecord.amount < 0 ){
                    break;
                }
                if(curExpensesRecord == null || curExpensesRecord.balance < curExpensesRecord.amount ){
                    continue;
                }
                Log.d(TAG,"AFTER SCANING: "+i+" "+curExpensesRecord.balance+" "+ curExpensesRecord.amount);


                if(i == 0){
                    if(isMoneyEqual(curExpensesRecord.balance - curExpensesRecord.amount, expensesRecordEntities.get(1).balance) ){
                        expensesRecordEntities.set(i,null);
                    }
                }
                else if(i == expensesRecordEntities.size() -1 && expensesRecordEntities.get(i-1) != null){
                    if(isMoneyEqual(curExpensesRecord.balance - curExpensesRecord.amount, expensesRecordEntities.get(i-1).balance) ){
                        expensesRecordEntities.set(i,null);
                    }
                }
                else{
                    if( (expensesRecordEntities.get(i+1) != null &&
                            isMoneyEqual(curExpensesRecord.balance - curExpensesRecord.amount, expensesRecordEntities.get(i+1).balance)

                            ||
                            (expensesRecordEntities.get(i-1) != null && isMoneyEqual(curExpensesRecord.balance - curExpensesRecord.amount, expensesRecordEntities.get(i-1).balance))

                    )){
                        expensesRecordEntities.set(i,null);
                    }
                }

            }

            // remove all null objects
            Iterator<expensesRecordEntity> entityIterator = expensesRecordEntities.iterator();
            while(entityIterator.hasNext()){
                expensesRecordEntity entity = entityIterator.next();
                if(entity == null){
                    entityIterator.remove();
                }
            }

            return expensesRecordEntities;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public static boolean isMoneyEqual(float a, float b){
        Log.d(TAG,"Compare "+a+" "+b+" "+ Math.abs(a-b));
        if(Math.abs(a-b) <= 0.01){
            return true;
        }
        else {
            return false;
        }
    }


}
