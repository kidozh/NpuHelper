package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.campusLibrary.librarySearchBookFragment;
import com.kidozh.npuhelper.physicalExercise.RecyclerItemClickListener;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;
import com.kidozh.npuhelper.utilities.okHttpUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class bbsShowThreadActivity extends AppCompatActivity {
    private final static String TAG = bbsShowThreadActivity.class.getSimpleName();
    @BindView(R.id.bbs_thread_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bbs_thread_detail_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.bbs_thread_fab)
    FloatingActionButton fab;
    @BindView(R.id.bbs_thread_detail_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.bbs_thread_toolbar_title)
    TextView mThreadToolbarTitle;
    @BindView(R.id.bbs_thread_detail_more_btn)
    Button mMoreDetailBtn;
    @BindView(R.id.bbs_thread_detail_comment_editText)
    EditText mCommentEditText;
    @BindView(R.id.bbs_thread_detail_comment_button)
    Button mCommentBtn;
    @BindView(R.id.bbs_comment_constraintLayout)
    ConstraintLayout mCommentConstraintLayout;
    @BindView(R.id.bbs_thread_detail_reply_chip)
    Chip mThreadReplyBadge;
    @BindView(R.id.bbs_thread_detail_reply_content)
    TextView mThreadReplyContent;
    @BindView(R.id.error_thread_cardview)
    CardView errorThreadCardview;
    int page = 1;
    public String tid,subject,fid;
    private OkHttpClient client = new OkHttpClient();
    private bbsForumThreadCommentAdapter adapter;
    boolean isTaskRunning,hasLoadAll=false;
    String formHash = null;

    private bbsUtils.threadCommentInfo selectedThreadComment =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_show_thread);
        ButterKnife.bind(this);
        mCommentConstraintLayout.setVisibility(View.GONE);
        configureClient();
        Intent intent = getIntent();
        tid = intent.getStringExtra("TID");
        fid = intent.getStringExtra("FID");
        subject = intent.getStringExtra("SUBJECT");

        configureToolbar();
        configureRecyclerview();

        new getThreadCommentTask(this).execute();
        configureSwipeRefreshLayout();
        configureCommentBtn();

        // use webview...


    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                new getThreadCommentTask(getApplicationContext()).execute();

            }
        });
        mMoreDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getThreadCommentTask(getApplicationContext()).execute();
            }
        });
    }

    private void configureClient(){

        client = okHttpUtils.getUnsafeOkHttpClientWithCookieJar(this);
    }

    private void configureCommentBtn(){
        mCommentBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String commentMessage = mCommentEditText.getText().toString();
                if(commentMessage.length() == 0){
                    Toasty.info(getApplicationContext(),getString(R.string.bbs_require_comment),Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG,"SELECTED THREAD COMMENT "+selectedThreadComment);
                    if(selectedThreadComment == null){
                        // directly comment thread
                        new sendCommentToThreadTask(getApplicationContext(),commentMessage).execute();
                    }
                    else {
                        String pid = selectedThreadComment.pid;
                        Log.d(TAG,"Send Reply to "+pid);
                        new sendReplyToThreadTask(getApplicationContext(),pid,commentMessage,selectedThreadComment.message).execute();
                    }

                }
            }
        });
    }

    private void configureRecyclerview(){
        mRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        adapter = new bbsForumThreadCommentAdapter(this);
        adapter.subject =subject;
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isScrollAtEnd()){
                    new getThreadCommentTask(getApplicationContext()).execute();
                }
            }

            public boolean isScrollAtEnd(){

                if (mRecyclerview.computeVerticalScrollExtent() + mRecyclerview.computeVerticalScrollOffset()
                        >= mRecyclerview.computeVerticalScrollRange()){
                    return true;
                }
                else {
                    return false;
                }

            }
        });

        mRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this,mRecyclerview,new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
                bbsUtils.threadCommentInfo threadCommentInfo = adapter.threadInfoList.get(position);
                selectedThreadComment = threadCommentInfo;
                mThreadReplyBadge.setText(threadCommentInfo.author);
                mThreadReplyBadge.setVisibility(View.VISIBLE);
                mCommentEditText.setHint(String.format("@%s",threadCommentInfo.author));
                String decodeString = threadCommentInfo.message;
                decodeString =decodeString
                        .replace("&amp;","&")
                        .replace("&lt;","<")
                        .replace("&gt;",">")
                        .replace("&quot;","“");

                Spanned sp = Html.fromHtml(decodeString);

                mThreadReplyContent.setText(sp, TextView.BufferType.SPANNABLE);
                mThreadReplyContent.setVisibility(View.VISIBLE);
                mThreadReplyBadge.setOnCloseIconClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        mThreadReplyBadge.setVisibility(View.GONE);
                        mThreadReplyContent.setVisibility(View.GONE);
                        mCommentEditText.setHint(R.string.bbs_thread_say_something);
                        selectedThreadComment = null;
                    }
                });

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        
    }


    private class getThreadCommentTask extends AsyncTask<Void,Void,String> {
        Request request;
        Context context;

        getThreadCommentTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
            String apiStr = bbsUtils.getThreadCommentUrlByFid(Integer.parseInt(tid),page);
            request = new Request.Builder()
                    .url(apiStr)
                    .build();
            isTaskRunning = true;
            Log.d(TAG,"API ->"+apiStr);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if(hasLoadAll){
                return null;
            }
            try{
                Response resp = client.newCall(request).execute();
                if(resp.isSuccessful() && resp.body()!=null){
                    return resp.body().string();
                }
                else {
                    return null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                //Log.e(TAG,e.printStackTrace());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
            if(s == null){
                return;
            }
            else{
                Log.d(TAG,"Get Thread "+s);
            }

            String curFormHash = bbsUtils.parseFormHash(s);
            bbsUtils.bbsPersonInfo bbsPersonInfo = bbsUtils.parsePersonInfo(s);
            if(bbsPersonInfo == null || bbsPersonInfo.auth == null || bbsPersonInfo.auth.equals("null")){
                //mCommentConstraintLayout.setVisibility(View.INVISIBLE);
                mCommentBtn.setText(R.string.bbs_require_login);
                mCommentBtn.setEnabled(false);
                mCommentEditText.setEnabled(false);
            }
            if(curFormHash != null){
                formHash = curFormHash;
                mCommentConstraintLayout.setVisibility(View.VISIBLE);
            }
            else {

            }
            if(s!=null){
                Log.d(TAG,s);

            }
            else {
                hasLoadAll = true;
                Log.d(TAG,"Getting Null value");
                return;
            }
            List<bbsUtils.threadCommentInfo> threadInfoList;
            threadInfoList = bbsUtils.parseThreadCommentInfo(s);


            if(threadInfoList!=null&& threadInfoList.size() !=0){
                Log.d(TAG,"Getting threadList size "+threadInfoList.size());
                if(adapter.threadInfoList == null || page == 1 || page == 0){
                    adapter.setThreadInfoList(threadInfoList);
                }
                else {
                    adapter.threadInfoList.addAll(threadInfoList);
                    adapter.notifyDataSetChanged();
                }
                if(threadInfoList.size()<15){
                    hasLoadAll = true;
                }

            }

            else {
                Log.d(TAG,"Getting threadList is null get page"+page);
                if(page == 1){
                    errorThreadCardview.setVisibility(View.VISIBLE);
                    hasLoadAll = true;
                }
                else {
                    hasLoadAll = true;
                }

            }

            page += 1;
            isTaskRunning = false;
        }
    }


    private class sendCommentToThreadTask extends AsyncTask<Void,Void,String>{

        Request request;
        Context mContext;
        String message;

        sendCommentToThreadTask(Context context,String message){
            this.mContext = context;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Date timeGetTime = new Date();
            FormBody formBody = new FormBody.Builder()
                    .add("message", message)
                    .add("subject", "")
                    .add("usesig", "1")
                    .add("posttime",String.valueOf(timeGetTime.getTime() / 1000 - 30))
                    .add("formhash",formHash)
                    .build();
            Log.d(TAG,"get Form "+message+" hash "
                    +formHash+" fid "+fid+" tid "+tid
                    + " API ->"+bbsUtils.getReplyThreadUrl(fid,tid)+" postTime "+String.valueOf(timeGetTime.getTime() / 1000 - 30));
            request = new Request.Builder()
                    .url(bbsUtils.getReplyThreadUrl(fid,tid))
                    .post(formBody)
                    .build();
            mCommentBtn.setText(R.string.bbs_commentting);
            mCommentBtn.setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful() && response.body()!=null){
                    String content = response.body().string();
                    Log.d(TAG,"get content "+content);
                    if(content.contains("回复发布成功")){
                        return content;
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Toasty.error(getApplicationContext(),getString(R.string.bbs_comment_failed),Toast.LENGTH_LONG).show();
            }
            else {
                Toasty.success(getApplicationContext(),getString(R.string.bbs_comment_successfully),Toast.LENGTH_LONG).show();
                // clear it..
                mCommentEditText.setText("");
                page = 1;
                hasLoadAll = false;
                new getThreadCommentTask(mContext).execute();

            }
            mCommentBtn.setText(R.string.bbs_thread_comment);
            mCommentBtn.setEnabled(true);
        }
    }


    private class sendReplyToThreadTask extends AsyncTask<Void,Void,String>{

        Request request,ajaxGetReplyUrl;
        Context mContext;
        String message,noticeAuthorMsg;
        String replyPid;

        sendReplyToThreadTask(Context context,String replyPid,String message,String noticeAuthorMsg){
            this.mContext = context;
            this.message = message;
            this.replyPid = replyPid;
            this.noticeAuthorMsg = noticeAuthorMsg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FormBody formBody = new FormBody.Builder()
                    .add("formhash",formHash)
                    .add("handlekey","reply")
                    .add("noticeauthormsg","")
                    .add("usesig", "1")
                    .add("reppid", replyPid)
                    .add("reppost", replyPid)
                    .add("message", message)
                    //.add("subject", message)
                    .build();
            Log.d(TAG,"get Reply Form "+message+" hash "
                    +formHash+" reppid "+replyPid+" tid "+tid
                    + " API ->"+bbsUtils.getReplyThreadUrl(fid,tid));
            request = new Request.Builder()
                    .url(bbsUtils.getReplyToSomeoneThreadUrl(fid,tid))
                    .post(formBody)
                    .build();
            mCommentBtn.setText(R.string.bbs_commentting);
            mCommentBtn.setEnabled(false);
            String pid = selectedThreadComment.pid;
            ajaxGetReplyUrl = new Request.Builder()
                    .url(bbsUtils.ajaxGetReplyPostParametersUrl(tid,pid))
                    .build();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String formHash,noticetrimstr,noticeauthor,noticeauthormsg;

            try{
                // get ajax reply first
                Response ajaxResponse = client.newCall(ajaxGetReplyUrl).execute();




                if(ajaxResponse.isSuccessful() && ajaxResponse.body()!=null){
                    String formContentXml = ajaxResponse.body().string();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    //获得Document对象
                    InputStream is = new ByteArrayInputStream(formContentXml.getBytes());
                    String formContentHtml = builder.parse(is).getElementsByTagName("root").item(0).getTextContent();
                    //获得student的List


                    Log.d(TAG,"Form Content "+formContentHtml);
                    Document formDoc = Jsoup.parse(formContentHtml);
                    //Element formhashElement = formDoc.getElementById("formhash");
                    formHash = formDoc.select("input[name=formhash]").val();
                    noticetrimstr = formDoc.select("input[name=noticetrimstr]").val();
                    noticeauthor = formDoc.select("input[name=noticeauthor]").val();
                    noticeauthormsg = formDoc.select("input[name=noticeauthormsg]").val();
                    Log.d(TAG,"Get raw mstr"+noticetrimstr+" author "+noticeauthor+" msg "+noticeauthormsg);
                }
                else {
                    return null;
                }
                if(noticeauthor==null || noticeauthor.length() == 0){
                    return null;
                }
                Log.d(TAG,"Get mstr"+noticetrimstr+" author "+noticeauthor+" msg "+noticeauthormsg);

                FormBody formBody = new FormBody.Builder()
                        .add("formhash",formHash)
                        .add("handlekey","reply")
                        .add("noticeauthormsg",noticeauthormsg)
                        .add("usesig", "1")
                        .add("reppid", replyPid)
                        .add("reppost", replyPid)
                        .add("message", message)
                        .add("noticetrimstr",noticetrimstr)
                        .add("noticeauthor",noticeauthor)
                        //.add("subject", message)
                        .build();
                request = new Request.Builder()
                        .url(bbsUtils.getReplyToSomeoneThreadUrl(fid,tid))
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                if(response.isSuccessful() && response.body()!=null){
                    String content = response.body().string();
                    Log.d(TAG,"get content "+content);
                    if(content.contains("回复发布成功")){
                        return content;
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if(s==null){
                Toasty.error(getApplicationContext(),getString(R.string.bbs_comment_failed),Toast.LENGTH_LONG).show();
            }
            else {
                Toasty.success(getApplicationContext(),getString(R.string.bbs_comment_successfully),Toast.LENGTH_LONG).show();
                // clear it..
                mCommentEditText.setText("");
                page = 1;
                hasLoadAll = false;
                new getThreadCommentTask(mContext).execute();

            }
            mCommentBtn.setText(R.string.bbs_thread_comment);
            mCommentBtn.setEnabled(true);
        }
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(subject);
        mThreadToolbarTitle.setText(subject);
    }






    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
