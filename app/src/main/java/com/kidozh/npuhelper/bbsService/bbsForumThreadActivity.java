package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.campusLibrary.librarySearchBookFragment;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsForumThreadActivity extends AppCompatActivity {
    private static final String TAG = bbsForumThreadActivity.class.getSimpleName();

    @BindView(R.id.bbs_forum_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bbs_forum_fab)
    FloatingActionButton fab;
    @BindView(R.id.bbs_forum_description_textview)
    TextView mForumDesciption;
    @BindView(R.id.bbs_forum_alert_textview)
    TextView mForumAlert;
    @BindView(R.id.bbs_forum_thread_number_textview)
    TextView mForumThreadNum;
    @BindView(R.id.bbs_forum_post_number_textview)
    TextView mForumPostNum;
    @BindView(R.id.bbs_forum_info_swipe_refreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.bbs_forum_thread_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.bbs_forum_toolbar_textView)
    TextView mToolbarTextview;
    @BindView(R.id.more_thread_btn)
    Button moreThreadBtn;
    private bbsUtils.forumInfo forum;
    private OkHttpClient client = new OkHttpClient();
    private bbsForumThreadAdapter adapter;
    boolean isTaskRunning;
    String fid;

    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_forum_thread);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        forum = intent.getParcelableExtra(bbsUtils.PASS_FORUM_THREAD_KEY);
        fid = String.valueOf(forum.fid);
        configureActionBar();
        configureFab();
        configureClient();
        configureForumInfo();
        configureRecyclerview();
        configureSwipeRefreshLayout();


        new getThreadInfoTask(this).execute();
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                new getThreadInfoTask(getApplicationContext()).execute();

            }
        });
    }

    private void configureClient(){
        client = getUnsafeOkHttpClient();
    }

    private void configureActionBar(){
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTextview.setText(forum.name);

    }

    private void configureForumInfo(){
        getSupportActionBar().setTitle(forum.name);
        SpannableString spannableString = new SpannableString(Html.fromHtml(forum.description));
        mForumDesciption.setText(spannableString, TextView.BufferType.SPANNABLE);
        mForumThreadNum.setText(forum.threads);
        mForumPostNum.setText(forum.totalPost);
    }

    private void configureRecyclerview(){
        mRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        adapter = new bbsForumThreadAdapter(this,"",fid);
        mRecyclerview.setAdapter(adapter);
        moreThreadBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new getThreadInfoTask(getApplicationContext()).execute();
            }
        });
    }

    private void configureFab(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private class getThreadInfoTask extends AsyncTask<Void,Void,String>{
        Request request;
        Context context;

        getThreadInfoTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
            request = new Request.Builder()
                    .url(bbsUtils.getForumUrlByFid(forum.fid,page))
                    .build();
            isTaskRunning = true;
            Log.d(TAG,"API ->"+bbsUtils.getForumUrlByFid(forum.fid,page));
        }

        @Override
        protected String doInBackground(Void... voids) {
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
            List<bbsUtils.threadInfo> threadInfoList;
            if (page == 1 || page == 0){
                threadInfoList = bbsUtils.parseThreadListInfo(s,true);
            }
            else {
                threadInfoList = bbsUtils.parseThreadListInfo(s,false);
            }

            if(threadInfoList!=null){
                if(adapter.threadInfoList == null || page == 1){
                    adapter.setThreadInfoList(threadInfoList,s);
                }
                else {
                    adapter.threadInfoList.addAll(threadInfoList);
                    adapter.jsonString = s;
                    adapter.notifyDataSetChanged();
                }

            }
            SpannableString spannableString = new SpannableString(Html.fromHtml(bbsUtils.getThreadRuleString(s)));
            mForumAlert.setText(spannableString, TextView.BufferType.SPANNABLE);
            page += 1;
            isTaskRunning = false;
        }
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
