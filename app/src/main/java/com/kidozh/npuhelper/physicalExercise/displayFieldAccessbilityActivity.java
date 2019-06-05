package com.kidozh.npuhelper.physicalExercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class displayFieldAccessbilityActivity extends AppCompatActivity {
    public static String FIELD_PASS_KEY = "FIELD_PASS_KEY";
    public static String TAG = displayFieldAccessbilityActivity.class.getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    @BindView(R.id.stadium_detail_area_info)
    TextView mStadiumDetailAreaInfo;
    @BindView(R.id.stadium_area_info_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.stadium_detail_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    stadiumInfoUtils.stadiumInfoBean queryField;
    @BindView(R.id.stadium_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stadium_info_update_time)
    TextView mStadiumUpdateTime;
    @BindView(R.id.stadium_detail_title)
    TextView mStadiumDetailTitle;

    private displayFieldAccessAdapter adapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_field_accessbility);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        queryField = intent.getParcelableExtra(FIELD_PASS_KEY);
        configureToolbar();
        configureStatusBar();

        renderRecyclerview();


        configureSwipeRefreshLayout();

        new getStadiumInfo(this).execute();
        mStadiumDetailAreaInfo.setText(String.format("%s",queryField.areaName));



    }

    void configureToolbar(){
        Log.d(TAG,"Enter "+ queryField.stadiumName);
        //toolbar.setTitle(queryField.stadiumName);
        toolbar.setBackgroundColor(getColor(R.color.colorPureWhite));
        toolbar.setTitleTextColor(getColor(R.color.colorMidnightblue));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(queryField.stadiumName);
        displayFieldAccessbilityActivity.this.setTitle(queryField.stadiumName);
        mStadiumDetailTitle.setText(queryField.stadiumName);

    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorPureWhite));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getStadiumInfo(getApplicationContext()).execute();

            }
        });
    }


    private void renderRecyclerview(){
        mRecyclerview.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new displayFieldAccessAdapter(this);
        mRecyclerview.setAdapter(adapter);
    }

    private class getStadiumInfo extends AsyncTask<Void,Void, String> {
        Context mContext;

        getStadiumInfo(Context context){
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get marked info first
            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Request request = stadiumInfoUtils.buildQueryStadiumInfoRequest(queryField.areaID);
                Response resp = client.newCall(request).execute();
                if(resp.isSuccessful() && resp.body()!=null){
                    return resp.body().string();


                }
                else {
                    return null;
                }
            }

            catch (Exception e){
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                List<stadiumInfoUtils.stadiumAvaliabilityInfo> avaliabilityInfos = stadiumInfoUtils.parseAvalibilityInfo(s);
                adapter.mList = avaliabilityInfos;
                adapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                Date date = new Date();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
                mStadiumUpdateTime.setText(df.format(date));
            }
        }
    }

    @Override
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
