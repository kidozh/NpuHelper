package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.provider.SearchRecentSuggestions;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.Manifest;
import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class campusBuildingSearchResultActivity extends AppCompatActivity {
    private static final String TAG = campusBuildingPortalActivity.class.getSimpleName();

    private campusBuildingInfoDatabase mDb;
    @BindView(R.id.location_search_result_recycler_view)
    RecyclerView mLocationSearchResultRecyclerView;

    @BindView(R.id.location_search_result_progressBar)
    ProgressBar mLocationSearchResultProgressBar;

    campusBuildingSearchResultListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_building_search_result);
        ButterKnife.bind(this);
        mDb = campusBuildingInfoDatabase.getsInstance(getApplicationContext());
        String search_location_name;

        // set adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        mLocationSearchResultRecyclerView.setLayoutManager(layoutManager);

        // COMPLETED (42) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mLocationSearchResultRecyclerView.setHasFixedSize(true);

        mAdapter = new campusBuildingSearchResultListAdapter(this);


        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG,"Search location : "+ query);
            saveRecentLocation(intent);
            search_location_name = query;
        }
        else {
            search_location_name = intent.getStringExtra("SEARCH_LOCATION_NAME");

        }

        // handle query
        new searchLocationTask(search_location_name).execute();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},8);
        setActionBar();
        configureStatusBar();
    }

    private void configureStatusBar(){
//        getWindow().setStatusBarColor(getColor(R.color.colorStatusBarBg));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
//        ColorDrawable drawable = new ColorDrawable(getColor(R.color.colorCloud));
//        getSupportActionBar().setBackgroundDrawable(drawable);
    }

    @SuppressLint("StaticFieldLeak")
    private class searchLocationTask extends AsyncTask<Void,Void,Void>{
        String queryText;
        List<campusBuildingInfoEntity> campusBuildingInfoEntityList;
        searchLocationTask(String queryText){
            this.queryText = queryText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLocationSearchResultProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(queryText.equals("")){
                campusBuildingInfoEntityList = mDb.campusBuildingInfoDao().getAll();
            }
            else {
                campusBuildingInfoEntityList = mDb.campusBuildingInfoDao().getRelatedCampusBuildingInfo(queryText);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLocationSearchResultProgressBar.setVisibility(View.GONE);
            mAdapter.setCampusBuildingInfoEntityList(campusBuildingInfoEntityList);
            mLocationSearchResultRecyclerView.setAdapter(mAdapter);

        }


    }

    private void saveRecentLocation(Intent intent){
        String query = intent.getStringExtra(SearchManager.QUERY);

        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                campusBuildingSearchSuggestionProvider.AUTHORITY,
                campusBuildingSearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);

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
