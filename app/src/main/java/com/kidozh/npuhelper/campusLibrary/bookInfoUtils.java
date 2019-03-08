package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;
import com.kidozh.npuhelper.accountAuth.loginUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.kidozh.npuhelper.accountAuth.loginUtils.encryptWith3DES;
import static com.kidozh.npuhelper.accountAuth.loginUtils.getSoapSerializationEnvelope;
import static com.kidozh.npuhelper.accountAuth.loginUtils.privateKey;

public class bookInfoUtils {
    private final static String TAG = bookInfoUtils.class.getSimpleName();
    public static class bookBeam implements Parcelable {
        public String title;
        public String author;
        public String callNumber;
        public String docType;
        public String isbnNumber;
        public String publisher;
        public String marcRecNumber;
        public String publishTime;
        public String imgUrl;
        public int totalNumber = -1;
        public int accessNumber = -1;
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(author);
            dest.writeString(callNumber);
            dest.writeString(docType);
            dest.writeString(isbnNumber);
            dest.writeString(publisher);
            dest.writeString(marcRecNumber);
            dest.writeString(publishTime);
            dest.writeString(imgUrl);
            dest.writeInt(totalNumber);
            dest.writeInt(accessNumber);

        }

        public static final Parcelable.Creator<bookBeam> CREATOR = new Creator<bookBeam>() {
            @Override
            public bookBeam createFromParcel(Parcel source) {
                bookBeam bookEntity = new bookBeam();
                bookEntity.title = source.readString();
                bookEntity.author = source.readString();
                bookEntity.callNumber = source.readString();
                bookEntity.docType = source.readString();
                bookEntity.isbnNumber = source.readString();
                bookEntity.publisher = source.readString();
                bookEntity.marcRecNumber = source.readString();
                bookEntity.publishTime = source.readString();
                bookEntity.imgUrl = source.readString();
                bookEntity.totalNumber = source.readInt();
                bookEntity.accessNumber = source.readInt();


                return bookEntity;
            }

            @Override
            public bookBeam[] newArray(int size) {
                return new bookBeam[size];
            }
        };

    }

    public static String queryLibraryApi = "http://202.117.255.187:8080/opac/ajax_search_adv.php";
    public static String queryDeatilBookhost = "http://202.117.255.187:8080/opac/ajax_isbn_marc_no.php";

    public static String buildDetailBookApi(String marcNo, String isbn, String rdm){
        Uri build_uri = Uri.parse(queryDeatilBookhost).buildUpon()
                .appendQueryParameter("marc_no",marcNo)
                .appendQueryParameter("isbn",isbn)
                .appendQueryParameter("rdm",rdm)
                .build();
        return build_uri.toString();
    }



    public static String buildSimpleQueryJSON(String title,String baseField,int pageCnt, int pageSize,String sortField,String sortType){
        try{
            JSONObject rootInfoObj = new JSONObject();
            rootInfoObj.put("locale","");
            //pageCount: 1
            //pageSize: 20
            rootInfoObj.put("pageCount",pageCnt);
            rootInfoObj.put("pageSize",pageSize);
            //sortField: "publisher"
            //sortType: "desc"
            rootInfoObj.put("sortField",sortField);
            rootInfoObj.put("sortType",sortType);
            // two empty
            rootInfoObj.put("filters",new JSONArray());
            rootInfoObj.put("limiter",new JSONArray());

            JSONObject queryInfo = new JSONObject();
            //fieldCode: ""
            //fieldValue: "雅思"
            queryInfo.put("fieldCode",baseField);
            queryInfo.put("fieldValue",title);
            JSONArray queryChainList = new JSONArray();
            queryChainList.put(queryInfo);
            JSONObject searchObj = new JSONObject();
            searchObj.put("fieldList",new JSONArray().put(queryInfo));
            rootInfoObj.put("searchWords",new JSONArray().put(searchObj));
            return rootInfoObj.toString();

        }
        catch (JSONException e){
            e.printStackTrace();
            return "";
        }

    }

    public static List<bookBeam> parseJson(String jsonString){
        List<bookBeam> bookBeamList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray bookArray = jsonObject.getJSONArray("content");
            for(int i=0;i<bookArray.length();i++){
                JSONObject bookInfo = (JSONObject) bookArray.get(i);
                bookBeam bookEntity = new bookBeam();
                bookEntity.author =  bookInfo.getString("author");
                bookEntity.callNumber =  bookInfo.getString("callNo");
                bookEntity.docType =  bookInfo.getString("docTypeName");
                bookEntity.isbnNumber =  bookInfo.getString("isbn");
                bookEntity.marcRecNumber =  bookInfo.getString("marcRecNo");
                bookEntity.publishTime =  bookInfo.getString("pubYear");
                bookEntity.publisher =  bookInfo.getString("publisher");
                bookEntity.title =  bookInfo.getString("title");
                bookBeamList.add(bookEntity);
            }
            return bookBeamList;
        }
        catch (JSONException e){
            e.printStackTrace();
            return bookBeamList;
        }
    }

    public static String bookStatusApiHost = "http://202.117.255.187:8080/opac/ajax_item.php";

    public static String buildBookDetailStatusApi(String marcNo){
        Uri build_uri = Uri.parse(bookStatusApiHost).buildUpon()
                .appendQueryParameter("marc_no",marcNo)
                .build();
        return build_uri.toString();
    }

    public static class bookBorrowStatus{
        public String callNumber;
        public String barCode;
        public String year;
        public String location;
        public Boolean isAccessible;
        public String status;
        public Date DueDate;
    }

    public static List<bookBorrowStatus> parseBookBorrowInfo(String htmlString){
        Document doc = Jsoup.parse(htmlString);
        Elements tables = doc.select(".whitetext");
        List<bookBorrowStatus> bookBorrowStatusList = new ArrayList<>();
        for(int i=0;i<tables.size();i++){
            try{
                Element table = tables.get(i);
                Elements ths = table.select("td");
                bookBorrowStatus bookBorrowEntity = new bookBorrowStatus();
                bookBorrowEntity.callNumber = ths.get(0).text();
                bookBorrowEntity.barCode = ths.get(1).text();
                bookBorrowEntity.year = ths.get(2).text();
                bookBorrowEntity.location = ths.get(3).text();
                Element borrowableElemnt = ths.get(4);
                if(borrowableElemnt.getElementsByTag("font").size()!=0){
                    bookBorrowEntity.isAccessible = true;
                    bookBorrowEntity.status = "";
                }
                else {
                    bookBorrowEntity.isAccessible = false;
                    String borrowableText = borrowableElemnt.text();
                    if(borrowableText.contains("：")){
                        bookBorrowEntity.status = borrowableText.split("：")[0];
                        String dueDateString = borrowableText.split("：")[1];
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        bookBorrowEntity.DueDate = sdf.parse(dueDateString);
                    }
                    else {
                        bookBorrowEntity.status = borrowableText;

                    }
                }
                bookBorrowStatusList.add(bookBorrowEntity);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        return bookBorrowStatusList;
    }

    public static String bookDetailApiHost = "http://202.117.255.187:8080/opac/item.php";

    public static String buildBookDetailInfoApi(String marcNo){
        Uri build_uri = Uri.parse(bookDetailApiHost).buildUpon()
                .appendQueryParameter("marc_no",marcNo)
                .build();
        return build_uri.toString();
    }

    public static class bookInfoItem{
        public String key;
        public String value;
        bookInfoItem(String key,String value){
            this.key = key;
            this.value = value;
        }
    }

    public static List<bookInfoItem> parseBookDetailInfo(String htmlString){
        Document doc = Jsoup.parse(htmlString);
        Elements tables = doc.select(".booklist");
        List<bookInfoItem> bookInfoItemList = new ArrayList<>();
        for(int i=0;i<tables.size();i++){
            try{
                Element table = tables.get(i);
                String key = table.select("dt").get(0).text();
                String value = table.select("dd").get(0).text();
                bookInfoItemList.add(new bookInfoItem(key,value));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return bookInfoItemList;

    }

    public static String bookDoubanCommentApiHost = "http://202.117.255.187:8080/opac/ajax_douban.php";

    public static String buildBookCommentApi(String isbn){
        Uri build_uri = Uri.parse(bookDoubanCommentApiHost).buildUpon()
                .appendQueryParameter("isbn",isbn)
                .build();
        return build_uri.toString();
    }

    public static List<bookInfoItem> parseBookCommentInfo(Context context, String jsonString){
        List<bookInfoItem> bookInfoItemList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String AuthorIntro = jsonObject.getString("author_intro");
            String doubanLink = jsonObject.getString("link");
            String doubanSummary = jsonObject.getString("summary");
            if(doubanSummary.length()!=0){
                bookInfoItemList.add(new bookInfoItem(context.getString(R.string.douban_summary),doubanSummary));
            }
            if(AuthorIntro.length()!=0){
                bookInfoItemList.add(new bookInfoItem(context.getString(R.string.douban_author_intro),AuthorIntro));
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return bookInfoItemList;

    }

    public static String libraryAuthApiHost = "http://202.117.255.187:8080/reader/hwthau.php";

    public static String buildLibraryAuthApi(String STticket){
        Uri build_uri = Uri.parse(libraryAuthApiHost).buildUpon()
                .appendQueryParameter("ticket",STticket)
                .build();
        return build_uri.toString();
    }

    public static Request buildLibraryAuthApiRequest(String STticket){
        Uri build_uri = Uri.parse(libraryAuthApiHost).buildUpon()
                .appendQueryParameter("ticket",STticket)
                .build();
        String url = build_uri.toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        return request;
    }

    public static Map<String,String> parseUserInfo(String htmlString){
        Map<String,String> infoMap = new HashMap<>();
        try{
            Document doc = Jsoup.parse(htmlString);
            Elements spans = doc.select(".profile-info-value > span");
            // start or end string
            String startTime = spans.get(0).text();
            String endTime = spans.get(1).text();
            infoMap.put("valid_time_start",startTime);
            infoMap.put("valid_time_end",endTime);

            Elements numbers = doc.select(".bigger-170");
            infoMap.put("max_borrow",numbers.get(0).text());
            infoMap.put("max_reserve",numbers.get(1).text());
            infoMap.put("max_commission",numbers.get(2).text());
        }
        catch (Exception e){
            return null;
        }




        return infoMap;
    }

    public static String[] popularBookURLArray = {
            "http://202.117.255.187:8080/top/top_lend.php",
            "http://202.117.255.187:8080/top/top_score.php",
            "http://202.117.255.187:8080/top/top_shelf.php",
            "http://202.117.255.187:8080/top/top_book.php"
    };

    public static class bookBoard{
        public String title;
        public String marcNo;
        public String author;
        public String publisher;
        public String callNo;
        public String extraInfo;

    }

    public static int getPopularBookExtraKey(int index){
        switch (index){
            case 0: return R.string.book_lend_time;
            case 1: return R.string.book_remark;
            case 2: return R.string.book_collect_times;
            case 3: return R.string.book_browse;
        }
        return R.string.unknown;
    }

    public static List<bookBoard> parsePopularBoard(int index,String htmlString){
        List<bookBoard> bookBoardList = new ArrayList<>();
        try{
            Document doc = Jsoup.parse(htmlString);
            Elements trs = doc.select("tr");
            // start is head,just ignore it
            for(int i=1;i<trs.size();i++){
                bookBoard bookInfo = new bookBoard();
                Element tr = trs.get(i);
                Elements tds = tr.getElementsByTag("td");
                bookInfo.title = tds.get(1).text();
                bookInfo.author = tds.get(2).text();
                bookInfo.publisher = tds.get(3).text();
                bookInfo.callNo = tds.get(4).text();
                // handle marcNo
                String href = tds.get(1).getElementsByTag("a").attr("href");
                Log.d(TAG,"Get href"+href);
                String marcNo = "";
                try{
                    marcNo = href.split("=")[1];
                }
                catch (Exception e){

                    e.printStackTrace();

                    //continue;
                }
                Log.d(TAG,"Get marc"+marcNo);

                bookInfo.marcNo = marcNo;
                if(index == 0){
                    bookInfo.extraInfo = tds.get(6).text();
                }
                else if(index == 1){
                    Element td = tds.get(5);
                    Element imageElement = td.getElementsByTag("img").get(0);
                    String srcString = imageElement.attr("src");
                    Pattern compile = Pattern.compile("(\\d)");
                    Matcher matcher = compile.matcher(srcString);
                    if(matcher.find()){
                        String ratingString = matcher.group();
                        Log.d(TAG,"Can find it "+ratingString);
                        bookInfo.extraInfo = ratingString;
                    }
                    else{
                        Log.d(TAG,"Cannot find it "+srcString);
                        bookInfo.extraInfo = "4";
                    }


                }
                else if(index == 2){
                    bookInfo.extraInfo = tds.get(5).text();
                }
                else if(index == 3){
                    bookInfo.extraInfo = tds.get(5).text();
                }
                bookBoardList.add(bookInfo);

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bookBoardList;
    }

    private static String nameSpace = "http://service.LibService.newmobile.com/";
    private static String libraryWSDLUrl = "http://m-ecampus.nwpu.edu.cn/zftal-mobile/webservice/newmobile/MobileHWWebService";

    // soap func...
    public static SoapSerializationEnvelope getBorrowInfoEnvelope(String username, String apptoken){
        String getocbalanceMethodName = "getCircs";
        SoapObject request = new SoapObject(nameSpace,getocbalanceMethodName);
        Log.d(TAG,"SEND REQ: "+request.getName());
        try{
            request.addProperty("userName",encryptWith3DES(username,apptoken));
            request.addProperty("certId",encryptWith3DES(username,apptoken));
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

    public static SoapObject getBorrowBookResult(String username, String apptoken){
        SoapSerializationEnvelope envelope = getBorrowInfoEnvelope(username,apptoken);
        String soapGetCirsAction = "http://service.LibService.newmobile.com/getCircs";
        return fetchResult(envelope, libraryWSDLUrl,soapGetCirsAction);
    }

    public static class borrowBook{
        String title,author,avatarUrl,barCode,propNo;
        Date lendDate, dueDate;
    }


    public static List<borrowBook> parseLendBookRes(SoapObject soapObject){
        List<borrowBook> bookBeamList = new ArrayList<>();

        SoapPrimitive loginResponse = (SoapPrimitive) soapObject.getProperty(0);

        String given_json = (String) loginResponse.getValue();
        Log.i(TAG,"Given Book Json : "+given_json);
        try {

            JSONObject bookJson = new JSONObject(given_json);
            JSONArray lendBookList = bookJson.getJSONArray("value");
            for(int i=0;i<lendBookList.length();i++){
                JSONObject borrowBookObj = (JSONObject) lendBookList.get(i);
                borrowBook book = new borrowBook();
                book.title = borrowBookObj.getString("title");
                Log.d(TAG,"Get Book title "+book.title);
                book.author = borrowBookObj.getString("author");
                book.propNo = borrowBookObj.getString("propNo");
                book.avatarUrl = borrowBookObj.optString("imageURL","");
                book.barCode = borrowBookObj.getString("barcode");
                String lendDateStr = borrowBookObj.getString("lendDate");
                String dueDateStr = borrowBookObj.getString("dueDay");

                SimpleDateFormat dueSdf = new SimpleDateFormat("yyyy-MM-dd        ", Locale.US);
                SimpleDateFormat lendSdf = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss",Locale.getDefault());
                book.dueDate = dueSdf.parse(dueDateStr);
                book.lendDate = lendSdf.parse(lendDateStr);
                bookBeamList.add(book);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return bookBeamList;

    }

    public static String[] libraryCategories = {null,"A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "X",
            "Z"};

}
