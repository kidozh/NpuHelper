package com.kidozh.npuhelper.bbsService;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.FactoryConfigurationError;

public class bbsUtils {
    private static String TAG = bbsUtils.class.getSimpleName();
    public static String BASE_URL = "https://bbs.nwpu.edu.cn";
    public static String PASS_FORUM_THREAD_KEY = "PASS_FORUM_THREAD_KEY";

    public static String getBaseUrl(){
        return BASE_URL;
    }

    public static String getBBSForumInfoApi(){
        return BASE_URL + "/api/mobile/index.php?version=4&module=forumindex";
    }

    public static String getBBSForumIconURLByFid(int fid){
        return BASE_URL+String.format("/data/attachment/common/1f/common_%s_icon.png",fid);
    }

    public static String getForumUrlByFid(int fid,int page){
        return BASE_URL+String.format("/api/mobile/index.php?version=4&module=forumdisplay&fid=%s&page=%s&ppp=15",fid,page);
    }

    public static String getThreadCommentUrlByFid(int tid,int page){
        return BASE_URL+String.format("/api/mobile/index.php?version=4&module=viewthread&tid=%s&page=%s&ppp=15",tid,page);
    }

    public static String getSmallAvatarUrlByUid(String uid){
        return BASE_URL+String.format("/uc_server/avatar.php?uid=%s&size=small",uid);
    }

    public static String getLoginUrl(){
        return "https://bbs.nwpu.edu.cn/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
        //return "https://bbs.nwpu.edu.cn/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
    }

    public static String getUserProfileUrl(){
        return BASE_URL + "/api/mobile/index.php?version=4&module=profile";
    }

    public static String getUserThreadUrl(int page){
        return BASE_URL + String.format("/api/mobile/index.php?version=4&module=mythread&page=%s",page);
    }



    public static String getPostsUrl(int fid, int page, boolean isInner) {
        if (isInner) {
            return "forum.php?mod=forumdisplay&fid=" + fid + "&page=" + page;
        } else {
            return "forum.php?mod=forumdisplay&fid=" + fid + "&page=" + page + "&mobile=2";
        }
    }

    public static String getArticleListApiUrl(int fid, int page) {
        return "api/mobile/index.php?version=4&module=forumdisplay&fid=" + fid + "&page=" + page;
    }

    public static String getArticleApiUrl(String tid, int page, int pageSize) {
        return "api/mobile/index.php?version=4&module=viewthread&tid=" + tid + "&page=" + page + "&ppp=" + pageSize;
    }

    public static String getAttachmentImageUrl(String s){
        return "https://bbs.nwpu.edu.cn/data/attachment/forum/"+s;
    }

    public static String ajaxGetReplyPostParametersUrl(String tid, String pid){
        return "https://bbs.nwpu.edu.cn"+String.format("/forum.php?mod=post&action=reply&tid=%s&repquote=%s&extra=&page=1&infloat=yes&handlekey=reply&inajax=1&ajaxtarget=fwin_content_reply",tid,pid);
    }

    public static class categorySectionFid{
        public String name;
        public int fid;
        public List<Integer> forumFidList;

        categorySectionFid(String name, int fid, List<Integer> forumFidList){
            this.name = name;
            this.fid = fid;
            this.forumFidList = forumFidList;
        }
    }

    public static List<categorySectionFid> parseCategoryFids(String s){
        try{
            List<categorySectionFid> categorySectionFidList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONArray catagoryList = variables.getJSONArray("catlist");
            for(int i=0;i<catagoryList.length();i++){
                JSONObject catagory = catagoryList.getJSONObject(i);
                String cataName = catagory.getString("name");
                String fid = catagory.getString("fid");
                JSONArray forumFidList = catagory.getJSONArray("forums");
                List<Integer> fidList = new ArrayList<>();
                for(int j=0;j<forumFidList.length();j++){
                    String fidString = forumFidList.getString(j);
                    fidList.add(Integer.parseInt(fidString));
                }

                categorySectionFid categorySectionFid = new categorySectionFid(cataName,Integer.parseInt(fid),fidList);
                categorySectionFidList.add(categorySectionFid);
            }
            return categorySectionFidList;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class forumInfo implements Parcelable {
        public String name;
        public int fid;
        public String iconURL;
        public String description;
        public String todayPost,totalPost,threads;
        forumInfo(){}
        forumInfo(String name, int fid,String iconURL, String description, String todayPost,String totalPost, String threads){
            this.name = name;
            this.fid = fid;
            this.iconURL = iconURL;
            this.description =description;
            this.todayPost = todayPost;
            this.totalPost = totalPost;
            this.threads = threads;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeInt(this.fid);
            dest.writeString(this.iconURL);
            dest.writeString(this.description);
            dest.writeString(this.todayPost);
            dest.writeString(this.totalPost);
            dest.writeString(this.threads);
        }

        public static final Creator<forumInfo> CREATOR = new Creator<forumInfo>() {
            @Override
            public forumInfo createFromParcel(Parcel source) {
                forumInfo forumInfo = new forumInfo();
                forumInfo.name = source.readString();
                forumInfo.fid = source.readInt();
                forumInfo.iconURL = source.readString();
                forumInfo.description = source.readString();
                forumInfo.todayPost = source.readString();
                forumInfo.totalPost = source.readString();
                forumInfo.threads = source.readString();
                return forumInfo;
            }

            @Override
            public forumInfo[] newArray(int size) {
                return new forumInfo[size];
            }
        };
    }


    public static forumInfo getForumInfoByFid(String s,int fid){
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONArray catagoryList = variables.getJSONArray("forumlist");
            for(int i=0;i<catagoryList.length();i++){
                JSONObject catagory = catagoryList.getJSONObject(i);
                String fidString = catagory.getString("fid");
                if (! fidString.equals(String.valueOf(fid))){
                    continue;
                }
                String forumName = catagory.getString("name");
                String allPost = catagory.getString("posts");
                String thread = catagory.getString("threads");
                String todayPosts = catagory.getString("todayposts");
                String iconURLString = catagory.optString("icon","");
                String description = catagory.optString("description","");

                return new forumInfo(forumName,fid,iconURLString,description,todayPosts,allPost,thread);

            }


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static class threadInfo{
        public String tid,fid;
        public String typeid;
        public String readperm;
        public String author,authorId;
        public String subject;
        public Date publishAt,UpdateAt;
        public String lastUpdator;
        public String viewNum,repliesNum;
        public Boolean isTop = false;
        public List<shortReplyInfo> shortReplyInfoList;
        threadInfo(){}

    }

    public static class shortReplyInfo{
        public String pid;
        public String author,authorId;
        public String message;
    }

    public static String getThreadRuleString(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONObject forumInfo = variables.getJSONObject("forum");

            return forumInfo.optString("rules","");

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<threadInfo> parseThreadListInfo(String s,Boolean isFirst){
        try{
            List<threadInfo> threadInfoList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONArray threadList = variables.getJSONArray("forum_threadlist");
            for(int i=0;i<threadList.length();i++){
                JSONObject threadObj = threadList.getJSONObject(i);
                String tid = threadObj.getString("tid");
                String typeid = threadObj.getString("typeid");
                String readPerm = threadObj.getString("readperm");
                String author = threadObj.getString("author");
                String authorId = threadObj.getString("authorid");
                String subject = threadObj.getString("subject");
                String updateAtString = threadObj.getString("lastpost");
                String publishAtStringTimestamp = threadObj.getString("dbdateline");
                String lastUpdator = threadObj.getString("lastposter");
                String viewNum = threadObj.getString("views");
                String replyNum = threadObj.getString("replies");
                String displayOrder = threadObj.getString("displayorder");

                threadInfo thread = new threadInfo();
                thread.tid = tid;
                thread.typeid = typeid;
                thread.readperm = readPerm;
                thread.author = author;
                thread.authorId = authorId;
                thread.subject = subject;
                thread.lastUpdator = lastUpdator;
                thread.viewNum = viewNum;
                thread.repliesNum = replyNum;
                thread.publishAt = new Timestamp(Long.parseLong(publishAtStringTimestamp)*1000);
                if(!displayOrder.equals("0")){
                    thread.isTop = true;
                }
                if(isFirst==false&& thread.isTop == true){
                    continue;
                }
                threadInfoList.add(thread);
            }
            return threadInfoList;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String,String> parseThreadType(String s){
        try{
            Map<String,String> threadTypeMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONObject threadTypes = variables.getJSONObject("threadtypes");
            JSONObject types = threadTypes.getJSONObject("types");
            Iterator<String> stringIterator = types.keys();
            while (stringIterator.hasNext()){
                String key = stringIterator.next();
                String value = types.getString(key);
                threadTypeMap.put(key,value);
            }
            return threadTypeMap;


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class threadCommentInfo{
        public String tid,pid;
        public Boolean first = false;
        public String author,authorId;
        public String message;
        public Date publishAt;
        public List<attachmentInfo> attachmentInfoList;

        threadCommentInfo(String tid,String pid,String author,String authorId, String message,Date publishAt){
            this.tid = tid;
            this.pid = pid;
            this.author = author;
            this.authorId = authorId;
            this.message = message;
            this.publishAt = publishAt;
        }

    }

    public static String parseFormHash(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            return variables.getString("formhash");
        }
        catch (Exception e){
            return null;
        }
    }

    public static List<threadCommentInfo> parseThreadCommentInfo(String s){
        try{
            List<threadCommentInfo> threadInfoList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONArray threadList = variables.getJSONArray("postlist");
            for(int i=0;i<threadList.length();i++){
                JSONObject threadObj = threadList.getJSONObject(i);
                String tid = threadObj.getString("tid");
                String pid = threadObj.getString("pid");
                String firstString = threadObj.getString("first");
                String author = threadObj.getString("author");
                String authorId = threadObj.getString("authorid");
                String message = threadObj.getString("message");
                String publishAtStringTimestamp = threadObj.getString("dbdateline");
                Date publishAt = new Timestamp(Long.parseLong(publishAtStringTimestamp)*1000);
                threadCommentInfo threadComment = new threadCommentInfo(tid,pid,author,authorId,message,publishAt);
                if(firstString.equals("1")){
                    threadComment.first = true;
                }
                else {
                    threadComment.first = false;
                }
                // attachment
                if(threadObj.has("attachments")){
                    Log.d(TAG,"Find attachment!!!");
                    try{
                        JSONObject attachmentObj = threadObj.getJSONObject("attachments");
                        List<attachmentInfo> attachmentInfoList = getAttachmentInfo(attachmentObj);
                        Log.d(TAG,"get attachmentInfo"+attachmentInfoList.size());
                        threadComment.attachmentInfoList = attachmentInfoList;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }

                threadInfoList.add(threadComment);


            }
            return threadInfoList;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String parseErrorInfo(String s){
        try{
            //Log.d(TAG,"JSON"+s);
            JSONObject jsonObject = new JSONObject(s);
            return (String) jsonObject.getString("error");
        }
        catch (Exception e){

            e.printStackTrace();
            return null;
        }

    }

    public static class attachmentInfo{
        public String aid,tid,pid,uid;
        public Date publishAt;
        public String filename,relativeUrl;

        attachmentInfo(String aid, String tid,String pid,String uid,String relativeUrl,String filename, Date publishAt){
            this.aid = aid;
            this.tid = tid;
            this.pid = pid;
            this.uid = uid;
            this.filename = filename;
            this.publishAt = publishAt;
            this.relativeUrl = relativeUrl;
        }

    }

    public static List<attachmentInfo> getAttachmentInfo(JSONObject jsonObject){
        try{
            Log.d(TAG,"Get ->"+jsonObject.toString());
            List<attachmentInfo> attachmentInfoList = new ArrayList<>();
            Iterator<String> stringIterator = jsonObject.keys();
            while (stringIterator.hasNext()){
                String key = stringIterator.next();
                JSONObject attachmentObj = jsonObject.getJSONObject(key);
                String aid = attachmentObj.getString("aid");
                String tid = attachmentObj.getString("tid");
                String pid = attachmentObj.getString("pid");
                String uid = attachmentObj.getString("uid");
                String filename = attachmentObj.getString("filename");
                if (!(filename.endsWith("png")||filename.endsWith("jpg")||filename.endsWith("gif"))){
                    continue;
                }
                String relativeUrl = attachmentObj.getString("attachment");
                String publishAtStr = attachmentObj.getString("dbdateline");
                Date publishAt = new Timestamp(Long.parseLong(publishAtStr)*1000);

                attachmentInfoList.add(new attachmentInfo(aid,tid,pid,uid,relativeUrl,filename,publishAt));
            }
            return attachmentInfoList;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class bbsNotification{
        public String notificationNum,notificationType,jsonName;
        public bbsNotification(String notificationNum,String notificationType,String jsonName){
            this.notificationNum = notificationNum;
            this.notificationType = notificationType;
            this.jsonName = jsonName;
        }

    }

    public static class bbsPersonInfo{
        public String auth,saltkey,uid,username,readPerm;
        public bbsPersonInfo(String auth, String saltkey,String uid,String username,String readPerm){
            this.auth = auth;
            this.saltkey = saltkey;
            this.uid = uid;
            this.username = username;
            this.readPerm = readPerm;
        }
    }

    public static bbsPersonInfo parsePersonInfo(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            Log.d(TAG,"Get ->"+jsonObject.toString());
            List<bbsNotification> notifications = new ArrayList<>();
            JSONObject variables = jsonObject.getJSONObject("Variables");
            return new bbsPersonInfo(
                    variables.getString("auth"),
                    variables.getString("saltkey"),
                    variables.getString("member_uid"),
                    variables.getString("member_username"),
                    variables.getString("readaccess")
                    );


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<bbsNotification> parseNotificationInfo(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            Log.d(TAG,"Get ->"+jsonObject.toString());
            List<bbsNotification> notifications = new ArrayList<>();
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONObject notice = variables.getJSONObject("notice");

            String[] keyList = {"newpush","newpm","newprompt","newmypost"};
            for(int i=0;i<keyList.length;i++){
                String key = keyList[i];
                notifications.add(new bbsNotification(notice.getString(key),key,key));
            }
            return notifications;


        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<threadInfo> parsePersonalThreadListInfo(String s){
        try{
            List<threadInfo> threadInfoList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(s);
            JSONObject variables = jsonObject.getJSONObject("Variables");
            JSONArray threadList = variables.getJSONArray("data");
            for(int i=0;i<threadList.length();i++){
                JSONObject threadObj = threadList.getJSONObject(i);
                String tid = threadObj.getString("tid");
                String typeid = threadObj.getString("typeid");
                String readPerm = threadObj.getString("readperm");
                String author = threadObj.getString("author");
                String authorId = threadObj.getString("authorid");
                String subject = threadObj.getString("subject");
                String updateAtString = threadObj.getString("lastpost");
                String publishAtStringTimestamp = threadObj.getString("dbdateline");
                String lastUpdator = threadObj.getString("lastposter");
                String viewNum = threadObj.getString("views");
                String replyNum = threadObj.getString("replies");
                String displayOrder = threadObj.getString("displayorder");
                String fid = threadObj.getString("fid");

                threadInfo thread = new threadInfo();
                thread.tid = tid;
                thread.typeid = typeid;
                thread.readperm = readPerm;
                thread.author = author;
                thread.authorId = authorId;
                thread.subject = subject;
                thread.lastUpdator = lastUpdator;
                thread.viewNum = viewNum;
                thread.repliesNum = replyNum;
                thread.publishAt = new Timestamp(Long.parseLong(publishAtStringTimestamp)*1000);
                thread.fid = fid;
                if(!displayOrder.equals("0")){
                    thread.isTop = true;
                }
                threadInfoList.add(thread);
            }
            return threadInfoList;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getReplyThreadUrl(String fid,String tid){
        Uri uri = Uri.parse(BASE_URL+"/forum.php").buildUpon()
                .appendQueryParameter("mod","post")
                .appendQueryParameter("action","reply")
                .appendQueryParameter("fid",fid)
                .appendQueryParameter("tid",tid)
                .appendQueryParameter("inajax","1")
                .appendQueryParameter("replysubmit","yes")
                .appendQueryParameter("infloat","yes")
                .appendQueryParameter("handlekey","fastpost")
                .appendQueryParameter("extra","")
                .build();
        return uri.toString();
    }

    public static String getReplyToSomeoneThreadUrl(String fid,String tid){
        Uri uri = Uri.parse(BASE_URL+"/forum.php").buildUpon()
                .appendQueryParameter("mod","post")
                .appendQueryParameter("action","reply")
                .appendQueryParameter("fid",fid)
                .appendQueryParameter("tid",tid)
                .appendQueryParameter("inajax","1")
                .appendQueryParameter("replysubmit","yes")
                .appendQueryParameter("infloat","yes")
                .appendQueryParameter("extra","")
                .build();
        return uri.toString();
    }



}
