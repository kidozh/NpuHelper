package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kidozh.npuhelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class campusBuildingPortalActivity extends AppCompatActivity {
    private static final String TAG = campusBuildingPortalActivity.class.getSimpleName();
    private String apiJsonString;
    private JSONObject apiJsonObj;
    Context mContext;

    @BindView(R.id.location_horizonal_progressBar)
    ProgressBar mLocationHorizonalProgressBar;

    @BindView(R.id.location_search_div)
    SearchView mLocationSearchDiv;

    @BindView(R.id.location_search_btn)
    Button searchBtn;

    @BindView(R.id.random_choice_btn)
    Button randomChoiceBtn;

    private campusBuildingInfoDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_building_portal);
        mContext = this;
        ButterKnife.bind(this);
        // ActionBar
        setActionBar();
        curlJsonFromData();
        //init Search bar
        initSearchBar(mLocationSearchDiv);
        configureStatusBar();

        mDb = campusBuildingInfoDatabase.getsInstance(getApplicationContext());


        // asyncTask
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG,"Search location : "+ query);
            saveRecentLocation(intent);
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get Query String
                CharSequence queryCharSeq = mLocationSearchDiv.getQuery();
                String queryText = queryCharSeq.toString();
                Intent intent;
                intent = new Intent(campusBuildingPortalActivity.this,campusBuildingSearchResultActivity.class);
                intent.putExtra("SEARCH_LOCATION_NAME",queryText);
                startActivity(intent);
            }
        });

        randomChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new randomGetLocationTask().execute();
            }
        });
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorStatusBarBg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.location_search_banner_text);
        ColorDrawable drawable = new ColorDrawable(getColor(R.color.colorCloud));
        getSupportActionBar().setBackgroundDrawable(drawable);
    }

    private void saveRecentLocation(Intent intent){
        String query = intent.getStringExtra(SearchManager.QUERY);

        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                campusBuildingSearchSuggestionProvider.AUTHORITY,
                campusBuildingSearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);

    }

    private void saveRecentLocation(String recentLocation){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                campusBuildingSearchSuggestionProvider.AUTHORITY,
                campusBuildingSearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(recentLocation, null);
    }

    @SuppressLint("StaticFieldLeak")
    class randomGetLocationTask extends AsyncTask<Void,Void,Void>{
        campusBuildingInfoEntity randomCampusBuildingInfoEntity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            randomCampusBuildingInfoEntity = mDb.campusBuildingInfoDao().getCampusBuildingInfoRandomly();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(randomCampusBuildingInfoEntity == null){
                return;
            }
            String[] locArray = randomCampusBuildingInfoEntity.location.split(",");
            double[] gaodeLoc =  GPSUtil.bd09_To_gps84(Double.parseDouble(locArray[1]),Double.parseDouble(locArray[0]));
            String locString = String.format("%s,%s",gaodeLoc[0],gaodeLoc[1]);
            Intent intent = new Intent(campusBuildingPortalActivity.this,campusBuildingDetailActivity.class);
            intent.putExtra("BUILDING_NAME",randomCampusBuildingInfoEntity.name);
            intent.putExtra("BUILDING_LOCATION",locString);
            intent.putExtra("BUILDING_DESCRIPTION",randomCampusBuildingInfoEntity.description);
            intent.putExtra("BUILDING_PICTURE_PATH",randomCampusBuildingInfoEntity.imgUrl);
            startActivity(intent);

        }
    }


    private void curlJsonFromData(){
        @SuppressLint("StaticFieldLeak")
        class curlApiTask extends AsyncTask<Void,Void,String>{

            private Request request;
            private final OkHttpClient client = new OkHttpClient();
            private String jsonResponse;

            @Override
            protected void onPreExecute() {
                mLocationHorizonalProgressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
                URL apiURL = campusBuildingUtils.build_url(mContext);
                request = new Request.Builder()
                        .url(apiURL)
                        .build();


            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        jsonResponse = response.body().string();
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    jsonResponse = "";
                }

                return jsonResponse;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mLocationHorizonalProgressBar.setVisibility(View.INVISIBLE);
                apiJsonString = jsonResponse;
                Log.d(TAG,"Read info from api "+apiJsonString);
                try {
                    apiJsonObj = campusBuildingUtils.load_json(apiJsonString);
                    // save it to Database
                    saveCampusBuildingData(apiJsonObj.getJSONArray("place"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(campusBuildingPortalActivity.this,getString(R.string.failed_to_parse_json),Toast.LENGTH_LONG).show();
                }
            }
        }
        new curlApiTask().execute();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_location, menu);
        final MenuItem searchItem = menu.findItem(R.id.location_app_bar_search);

//        final SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        initSearchBar(searchView);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            String mSearchTerm;
//            Boolean mSearchQueryChanged;
//
//            @Override
//            public boolean onQueryTextSubmit(String queryText) {
//                saveRecentLocation(queryText);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
//                if (mSearchTerm == null && newFilter == null) {
//                    return true;
//                }
//                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
//                    return true;
//                }
//                mSearchTerm = newFilter;
//                mSearchQueryChanged = true;
//                searchLocationByName(newText); //handle this
//                return true;
//            }
//        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);

        return true;
    }

    private void initSearchBar(SearchView searchView){
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            String mSearchTerm;
            Boolean mSearchQueryChanged;

            @Override
            public boolean onQueryTextSubmit(String queryText) {
                saveRecentLocation(queryText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                if (mSearchTerm == null && newFilter == null) {
                    return true;
                }
                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                    return true;
                }
                mSearchTerm = newFilter;
                mSearchQueryChanged = true;
                searchLocationByName(newText); //handle this
                return true;
            }
        });
    }

    private void searchLocationByName(String searchText){
        if (apiJsonObj == null){
            Toasty.error(mContext,getString(R.string.location_search_not_ready),Toast.LENGTH_SHORT).show();
        }
        else {
            try{
                JSONArray locationArray = apiJsonObj.getJSONArray("place");
                for (int i = 0; i < locationArray.length(); i++) {
                    JSONObject object = (JSONObject) locationArray.get(i);
                    JSONObject nameObj = object.getJSONObject("name");
                    // search reference
                    String nameZh = nameObj.getString("zh");
                    String descriptionLoc = object.getString("description");
                    if(nameZh.contains(searchText) || descriptionLoc.contains(searchText)){
                        //append that
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveCampusBuildingData(JSONArray jsonArray) {
        List<campusBuildingInfoEntity> campusBuildingInfoEntityList = new ArrayList<>();
        for(int i =0;i<jsonArray.length();i++){
            try {
                JSONObject campusLoc = (JSONObject) jsonArray.get(i);
                // parse JSON obj
                JSONObject nameObj = campusLoc.getJSONObject("name");
                String zhName = nameObj.getString("zh");
                String imgUrl = campusLoc.optString("img_url","");
                String description = campusLoc.optString("description","");
                String location = campusLoc.getString("location");
                String campus = campusLoc.getString("campus");
                campusBuildingInfoEntity campusBuildingInfo = new  campusBuildingInfoEntity(zhName,imgUrl,description,location,campus);
                campusBuildingInfoEntityList.add(campusBuildingInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(campusBuildingInfoEntityList.size() != 0){
                // Insert to db
                try{
                    new insertBuildingInfoTask(campusBuildingInfoEntityList).execute();
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }



        }


    }

    @SuppressLint("StaticFieldLeak")
    public class insertBuildingInfoTask extends AsyncTask<Void,Void,Void>{
        List<campusBuildingInfoEntity> campusBuildingInfoEntityList = new ArrayList<>();
        public insertBuildingInfoTask(List<campusBuildingInfoEntity> campusBuildingInfoEntityList){
            this.campusBuildingInfoEntityList = campusBuildingInfoEntityList;
        }

        campusBuildingInfoEntity getCampusBuildingInfoEntityByName(String name){
            for(int i = 0;i<campusBuildingInfoEntityList.size();i++){
                if(campusBuildingInfoEntityList.get(i).name.equals(name)){
                    return campusBuildingInfoEntityList.get(i);
                }
            }
            return null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // sync data
            // remove that to speed up
//            List<campusBuildingInfoEntity> allCampusLocation = mDb.campusBuildingInfoDao().getAll();
//            for (int i=0;i<allCampusLocation.size();i++){
//                campusBuildingInfoEntity campusBuildingInfo = allCampusLocation.get(i);
//                String campusBuildingName = campusBuildingInfo.name;
//                campusBuildingInfoEntity deleteCampusBuildingInfoEntity = getCampusBuildingInfoEntityByName(campusBuildingName);
//                if(deleteCampusBuildingInfoEntity != null){
//                    mDb.campusBuildingInfoDao().deleteInfo(deleteCampusBuildingInfoEntity);
//                }
//
//            }
            // then insert
            try{
                mDb.campusBuildingInfoDao().insertInfos(campusBuildingInfoEntityList);
            }
            catch (Exception e){
                Log.d(TAG,"Error "+e.toString());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG,"Insert to database " + campusBuildingInfoEntityList.size());
        }
    }






}
