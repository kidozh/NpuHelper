package com.kidozh.npuhelper.physicalExercise;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class displayStadiumActivity extends AppCompatActivity {
    private static String TAG = displayStadiumActivity.class.getSimpleName();

    @BindView(R.id.register_stadium_fab)
    FloatingActionButton registerFab;
    @BindView(R.id.stadium_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stadium_recyclerview)
    RecyclerView mStadiumRecyclerview;
    @BindView(R.id.marked_stadium_recyclerview)
    RecyclerView mMarkedStadiumRecyclerview;
    @BindView(R.id.mark_stadium_textview)
    TextView markStadiumTextview;
    @BindView(R.id.stadium_swipeRefreshLayout)
    SwipeRefreshLayout stadiumRefreshLayout;
    private final OkHttpClient client = new OkHttpClient();
    displayStadiumAdapter markAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stadium);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        configureRecyclerView();

        configureStatusBar();
        configureToolbar();
        renderStadiumList();

        new getMarkedStadiumInfo(this).execute();

//        registerFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Context mContext =this;

        stadiumRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMarkedStadiumInfo task= new getMarkedStadiumInfo(mContext);
                task.execute();

            }
        });
    }

    private void configureStatusBar(){
//        getWindow().setStatusBarColor(getColor(R.color.colorBelizahole));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    void configureToolbar(){

        toolbar.setTitle(R.string.physical_exercise_department);
//        toolbar.setTitleTextColor(getColor(R.color.colorPureWhite));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(R.string.physical_exercise_department);


    }

    private void configureRecyclerView(){
        mStadiumRecyclerview.setHasFixedSize(true);
        mStadiumRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mMarkedStadiumRecyclerview.setHasFixedSize(true);
        mMarkedStadiumRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        markAdapter = new displayStadiumAdapter(this,true);
        mMarkedStadiumRecyclerview.setAdapter(markAdapter);

        mStadiumRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this, mStadiumRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanList = stadiumInfoUtils.getMarkedStadiumIDJsonlist(getApplicationContext());
                List<stadiumInfoUtils.stadiumInfoBean> allStadiumInfoBeanList = stadiumInfoUtils.getAllAccessibleStadium();
                stadiumInfoUtils.stadiumInfoBean selectedStadiumInfo= allStadiumInfoBeanList.get(position);
                Boolean isNotRepetitive = true;
                if(stadiumInfoBeanList == null){
                    stadiumInfoBeanList = new ArrayList<>();
                }
                for(int i=0;i<stadiumInfoBeanList.size();i++){
                    stadiumInfoUtils.stadiumInfoBean currentInfo = stadiumInfoBeanList.get(i);
                    if(currentInfo.areaID.equals(selectedStadiumInfo.areaID) ){
                        isNotRepetitive = false;
                    }
                }
                if(isNotRepetitive){
                    stadiumInfoBeanList.add(allStadiumInfoBeanList.get(position));
                }

                stadiumInfoUtils.setMarkedStadiumIDJsonlist(getApplicationContext(),stadiumInfoBeanList);
                new getMarkedStadiumInfo(getApplicationContext()).execute();
                Log.d(TAG,"YOU LONG NORMAL MARK "+position);
            }
        }));

        mMarkedStadiumRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(this, mMarkedStadiumRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanList = stadiumInfoUtils.getMarkedStadiumIDJsonlist(getApplicationContext());
                List<stadiumInfoUtils.stadiumInfoBean> allStadiumInfoBeanList = stadiumInfoUtils.getAllAccessibleStadium();
                Log.d(TAG,"YOU LONG CLICK MARK "+position+" "+allStadiumInfoBeanList.get(position).areaID+" "+stadiumInfoBeanList.size());

                stadiumInfoBeanList.remove(position);

                stadiumInfoUtils.setMarkedStadiumIDJsonlist(getApplicationContext(),stadiumInfoBeanList);
                new getMarkedStadiumInfo(getApplicationContext()).execute();

            }
        }));

    }



    private void renderStadiumList(){
        GestureDetector mGestureDetector;
        List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanList = stadiumInfoUtils.getAllAccessibleStadium();
        displayStadiumAdapter adapter = new displayStadiumAdapter(this, false);
        adapter.stadiumInfoBeanList = stadiumInfoBeanList;
        mStadiumRecyclerview.setAdapter(adapter);



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

    public class getMarkedStadiumInfo extends AsyncTask<Void,Void,List<stadiumInfoUtils.stadiumInfoBean>>{
        Context mContext;
        List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanList;
        getMarkedStadiumInfo(Context context){
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get marked info first
            stadiumInfoBeanList = stadiumInfoUtils.getMarkedStadiumIDJsonlist(mContext);
            markStadiumTextview.setText(R.string.loading);
        }

        @Override
        protected List<stadiumInfoUtils.stadiumInfoBean> doInBackground(Void... voids) {
            List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanInfo = new ArrayList<>();
            if(stadiumInfoBeanList == null){
                return null;
            }
            else {

                // send them one by one
                for(int i=0;i<stadiumInfoBeanList.size();i++){
                    stadiumInfoUtils.stadiumInfoBean info = stadiumInfoBeanList.get(i);
                    Request request = stadiumInfoUtils.buildQueryStadiumInfoRequest(info.areaID);
                    try{
                        Response resp = client.newCall(request).execute();
                        if(resp.isSuccessful()){
                            String avalibilityString = resp.body().string();
                            Map<String,Integer> accessibleMap = stadiumInfoUtils.parseAvalibilityJSON(avalibilityString);
                            info.available_facilities_num = accessibleMap.get("access").toString();
                            info.all_facilities_num = accessibleMap.get("all").toString();
                            stadiumInfoBeanInfo.add(info);

                        }
                    }
                    catch (Exception e){
                        return null;
                    }

                }
                return stadiumInfoBeanInfo;

            }

        }

        @Override
        protected void onPostExecute(List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeans) {
            super.onPostExecute(stadiumInfoBeans);


            markAdapter.stadiumInfoBeanList = stadiumInfoBeans;

            markAdapter.notifyDataSetChanged();
            markStadiumTextview.setText(R.string.marked_stadium);
            if(stadiumRefreshLayout.isRefreshing()){
                stadiumRefreshLayout.setRefreshing(false);
            }
        }
    }

}
