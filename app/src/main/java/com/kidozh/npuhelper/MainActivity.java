package com.kidozh.npuhelper;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.aboutApp.aboutAppActivity;
import com.kidozh.npuhelper.accountAuth.LoginUniversityActivity;
import com.kidozh.npuhelper.accountAuth.accountInfoBean;
import com.kidozh.npuhelper.accountAuth.loginUtils;
import com.kidozh.npuhelper.accountAuth.personalInfoDisplayActivity;
import com.kidozh.npuhelper.bbsService.bbsShowPortalActivity;
import com.kidozh.npuhelper.campusAddressBook.campusAddressBookMainActivity;
import com.kidozh.npuhelper.campusLibrary.libraryPortalActivity;
import com.kidozh.npuhelper.campusTransaction.RecentTransactionFragment;
import com.kidozh.npuhelper.campusTransaction.TransactionHistoryActivity;
import com.kidozh.npuhelper.physicalExercise.displayStadiumActivity;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusNetworkUtils;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusUtils;
import com.kidozh.npuhelper.preference.SettingsActivity;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusListActivity;
import com.kidozh.npuhelper.schoolCalendar.schoolCalendarMainActivity;
import com.kidozh.npuhelper.scoreQuery.queryScoreMainActivity;
import com.kidozh.npuhelper.utilities.locationUtils;
import com.kidozh.npuhelper.weatherUtils.WeatherDetailActivity;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherDatabase;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherEntry;
import com.kidozh.npuhelper.campusBuildingLoc.campusBuildingPortalActivity;
import com.kidozh.npuhelper.weatherUtils.weatherDataUtils;
import com.kidozh.npuhelper.xianCityBus.cityBusPortalActivity;
import com.kidozh.npuhelper.weatherUtils.miuiWeatherUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
//import cz.msebera.android.httpclient.client.cache.Resource;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements RecentTransactionFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FORECAST_LOADER_ID = 0;

    @BindView(R.id.query_weather_progress)  ProgressBar mLoadingWeatherProgressBar;
    @BindView(R.id.location_name)  TextView mLocationName;
    @BindView(R.id.location_temperature)  TextView mLocationTemperature;
    @BindView(R.id.weather_icon)  ImageView mWeatherIcon;


    static private String celsius_temperature_unit_label = "°C";

    private LocationManager locationManager;
    private Context mContext;
    private String currentLocation;
    @BindView(R.id.weather)  CardView mWeatherCard;
    @BindView(R.id.drawer_layout)  DrawerLayout drawer;
    @BindView(R.id.shuttle_start_time)  TextView mShuttleStartTime;
    @BindView(R.id.shuttle_start_time1)  TextView mShuttleStartTime1;
    @BindView(R.id.left_time)  TextView mShuttleLeftTime;
    @BindView(R.id.left_time1) TextView mShuttleLeftTime1;



    private double locLatitude = 34.24626;
    private double locLongitude = 108.91148;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private caiyunWeatherDatabase mDb;

    @Nullable @BindView(R.id.stadium_toolbar) Toolbar toolbar;

    private String mRealTimeInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        // bind data
        ButterKnife.bind(this);

        mTitle = mDrawerTitle = getTitle();
        mContext = this;

        // get database connection
        mDb = caiyunWeatherDatabase.getsInstance(getApplicationContext());

        // setSupportActionBar(toolbar);

        try{
            // getActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.d(TAG,"Drawer object is "+drawer + " " + R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return MainActivity.this.onNavigationItemSelected(item);
            }
        });
        Button mAuthBtn = (Button) navigationView.getHeaderView(0).findViewById(R.id.auth_status_btn);

        mContext = getApplicationContext();
        new getCalenderFromApiTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new getWeatherInfoTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        displaySchoolBus(this);

        countDownTimer.start();
        Log.d(TAG,"Main thread finished."+mAuthBtn);
        configureStatusBar();
        configureToolbar();

        getWeatherFromDb();

        configureAuthBtn(mAuthBtn);


    }

    private void configureToolbar(){
        ColorDrawable drawable = new ColorDrawable(getColor(R.color.colorCloud));
        getSupportActionBar().setBackgroundDrawable(drawable);
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorStatusBarBg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void configureAuthBtn(Button mAuthBtn){
        accountInfoBean accountInfo = loginUtils.getTokenInfoToLocal(this);
        Log.i(TAG,"accountInfo" + accountInfo);
        if (accountInfo == null){
            mAuthBtn.setText(R.string.sign_in_text);
            mAuthBtn.setBackgroundColor(getColor(R.color.authStatusBg));
            mAuthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,LoginUniversityActivity.class);
                    startActivity(intent);
                }
            });
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            // change menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_menu_drawer);


        }
        else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            ImageView mImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.login_status_imageView);
//            mImage.setBackgroundColor(getColor(R.color.colorCloud));
//            mImage.setImageDrawable(getDrawable(R.drawable.vector_drawable_npu_badge));
            mImage.setBackgroundResource(R.mipmap.nwpu_library);
            mImage.setImageResource(R.mipmap.nwpu_library);
            mAuthBtn.setText(accountInfo.name);
            mAuthBtn.setBackgroundColor(getColor(R.color.colorPeterRiver));
            mAuthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,personalInfoDisplayActivity.class);
                    startActivity(intent);
                }
            });
            // change menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_auth_main_menu_drawer);
            addRecentTransactionFragment();


        }

    }

    private void addRecentTransactionFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.recent_transaction_fragment,RecentTransactionFragment.newInstance("",""));
        transaction.commit();
    }


    @Override
    protected void onResume() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Button mAuthBtn = (Button) navigationView.getHeaderView(0).findViewById(R.id.auth_status_btn);
        countDownTimer.start();
        configureAuthBtn(mAuthBtn);
        super.onResume();

    }

    public void getWeatherFromDb(){
        // get only the toppest one
        final LiveData<caiyunWeatherEntry> weatherDataTask = mDb.caiyunWeatherDao().getLastWeatherById();

        weatherDataTask.observe(this, new Observer<caiyunWeatherEntry>() {
            @Override
            public void onChanged(@Nullable caiyunWeatherEntry caiyunWeatherEntries) {

                try {
                    populateWeatherUI(caiyunWeatherEntries);
                }
                catch (JSONException e){
                    e.printStackTrace();
                    Log.d(TAG,"Error when get data from weather database");

                }
            }
        });
    }

    class getWeatherInfoTask extends AsyncTask<Void,Void,String>{



        private final OkHttpClient client = new OkHttpClient();
        Request request;
        Context mContext;

        getWeatherInfoTask(Context mContext){
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String miuiLocationKey = miuiWeatherUtils.getLocationKeyInPreference(mContext);

            String api_url = miuiWeatherUtils.build_forecast_api_url(miuiLocationKey);
            request = new Request.Builder()
                    .url(api_url)
                    .build();
            mLocationTemperature.setText("...");

            Log.d(TAG,"START QUERYING "+api_url);

        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonResponse = "";
            try{
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    jsonResponse = response.body().string();
                } else {
                    //throw new IOException("Unexpected code " + response);
                    return null;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                jsonResponse = "";
            } catch (NullPointerException e){
                jsonResponse = "";
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String data) {
            mLoadingWeatherProgressBar.setVisibility(View.INVISIBLE);
            mWeatherCard.setVisibility(View.VISIBLE);
            Log.d(TAG,"On query load finished, response is "+data);
            // set Text
            int locationTextResource = miuiWeatherUtils.getLocationPreferenceTextResource(mContext);
            mLocationName.setText(locationTextResource);
            if(data == null){
                mLocationTemperature.setText(getString(R.string.unknown_temperature));
                Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
            }
            mRealTimeInfo = data;
            JSONObject jsonData ;
            try {
                Log.d(TAG,"Recv weather info "+data);
                jsonData = new JSONObject(data);
                // Geometry decoder
                JSONObject weatherRawResult = jsonData;
                JSONObject weatherResult = weatherRawResult.getJSONObject("current");
                String localTemperature = weatherResult.getJSONObject("temperature").getString("value");

                // save it to database
                final caiyunWeatherEntry weatherEntry = new caiyunWeatherEntry(
                        localTemperature,
                        String.format("%s,%s",locLatitude,locLongitude),
                        data,
                        new Date()
                );

                new insertDataTask(weatherEntry).execute();
                populateWeatherUI(weatherEntry);





            } catch (JSONException e) {
                e.printStackTrace();
                Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
            }

            Log.d(TAG,"Weather condition finished..");
            super.onPostExecute(data);
        }
    }


    public void populateWeatherUI(caiyunWeatherEntry caiyunWeather) throws JSONException {
        if(caiyunWeather == null){
            Log.d(TAG,"Weather Obj is null ....");
            return;

        }

        String jsonString = caiyunWeather.getJson_string();
        Log.d(TAG,"Populate by "+jsonString);
        JSONObject jsonData = new JSONObject(jsonString);
        JSONObject weatherRawResult = jsonData;
        JSONObject weatherResult = weatherRawResult.getJSONObject("current");

        String localTemperature = weatherResult.getJSONObject("temperature").getString("value");
        String weatherCondition = weatherResult.getString("weather");

        String aqiVal = weatherRawResult.getJSONObject("aqi").getString("aqi");
        int primaryColor = weatherDataUtils.getAQIColorResource((int) Float.parseFloat(aqiVal));
        mWeatherCard.setBackgroundColor(getColor(primaryColor));
        // transfer weather condition

        // get Drawable icon
        Log.d(TAG,"Recv Weather is "+weatherCondition);
        Drawable weatherIcon = getDrawable((miuiWeatherUtils.getDrawableWeatherByString(weatherCondition)));
        mWeatherIcon.setImageDrawable(weatherIcon);
        mWeatherIcon.setColorFilter(getColor(R.color.colorPureWhite));
        mLocationTemperature.setText(String.format("%s %s",localTemperature, celsius_temperature_unit_label));
        Log.d(TAG,"Ended Rendering Weather Condition");
        mWeatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,WeatherDetailActivity.class);
                intent.putExtra("REALTIME_WEATHER",mRealTimeInfo);
                intent.putExtra("CUR_LOC",mLocationName.getText());
                startActivity(intent);
            }
        });
        mWeatherCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new getWeatherInfoTask(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            }
        });
    }



    public void displaySchoolBus(final Context context){
        //Log.d(TAG, "isFestival "+schoolBusUtils.isFestivalHoliday+" isWorkday "+schoolBusUtils.isFestivalWorkDay);
        int youyi2changanLeftMinutes = schoolBusUtils.getBusLeftMinutesToChangan();
        int changan2youyiLeftMinutes = schoolBusUtils.getBusLeftMinutesToYouyi();
        if(youyi2changanLeftMinutes == -1){
            mShuttleLeftTime.setText(" ");
            mShuttleStartTime.setText(getString(R.string.no_left_shuttle_today));
        }
        else {
            int nextBusArrivalTime = schoolBusUtils.getNearestBusTimeToChangan();
            int nextBusMinute = nextBusArrivalTime % 100;
            int nextBusHour = nextBusArrivalTime / 100;
            String extraZero = "";
            if(nextBusMinute == 0){
                extraZero = "0";
            }
            mShuttleStartTime.setText(String.format("%s:%s%s",nextBusHour,nextBusMinute,extraZero));
            int leftMinutes = youyi2changanLeftMinutes % 60;
            int leftHours = youyi2changanLeftMinutes / 60;
            if (leftHours == 0){
                mShuttleLeftTime.setText(String.format("%s%s", youyi2changanLeftMinutes, getString(R.string.minute_tag)));
            }
            else {
                mShuttleLeftTime.setText(String.format("%s%s %s%s",leftHours,getString(R.string.hour_tag),leftMinutes,getString(R.string.minute_tag)));
            }

        }

        if (changan2youyiLeftMinutes == -1){
            mShuttleLeftTime1.setText("");
            mShuttleStartTime1.setText(getString(R.string.no_left_shuttle_today));
        }
        else {
            int nextBusArrivalTime = schoolBusUtils.getNearestBusTimeToYouyi();
            int nextBusMinute = nextBusArrivalTime % 100;
            int nextBusHour = nextBusArrivalTime / 100;
            String extraZero = "";
            if(nextBusMinute == 0){
                extraZero = "0";
            }
            mShuttleStartTime1.setText(String.format("%s:%s%s",nextBusHour,nextBusMinute,extraZero));
            int leftMinutes = changan2youyiLeftMinutes % 60;
            int leftHours = changan2youyiLeftMinutes / 60;
            if(leftHours == 0){
                mShuttleLeftTime1.setText(String.format("%s %s", changan2youyiLeftMinutes, getString(R.string.minute_tag)));
            }
            else {
                mShuttleLeftTime1.setText(String.format("%s%s %s%s",leftHours,getString(R.string.hour_tag),leftMinutes,getString(R.string.minute_tag)));
            }

        }
        // Log.v(TAG,"school shuttle rendering finished");
        CardView mCardView = (CardView) findViewById(R.id.arrival_card);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,schoolBusListActivity.class);
                intent.putExtra("DEPARTURE_CAMPUS","YOUYI");
                startActivity(intent);
            }
        });

        CardView mCardView1 = (CardView) findViewById(R.id.arrival_card1);
        mCardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,schoolBusListActivity.class);
                intent.putExtra("DEPARTURE_CAMPUS","CHANGAN");
                startActivity(intent);
            }
        });

    }
    // stay as long as possible
    private CountDownTimer countDownTimer = new CountDownTimer(1000000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            displaySchoolBus(MainActivity.this);
        }

        @Override
        public void onFinish() {

        }
    };

    // library_buttom_navigation added
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //getMenuInflater().inflate(R.menu.activity_main_menu_drawer,menu);
        return true;
    }

    @Override public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == android.R.id.home){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
                //super.onBackPressed();
            }
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle library_buttom_navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ipv6_free_tv) {
            // Handle the camera action
            Intent intent = new Intent(this,schoolCalendarMainActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_position_label) {
            Intent intent = new Intent(this,campusBuildingPortalActivity.class);
            startActivity(intent);
            return false;

        } else if (id == R.id.nav_address_book) {
            Intent intent = new Intent(this,campusAddressBookMainActivity.class);
            startActivity(intent);
            return false;

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return false;
        } else if (id == R.id.nav_bus_services) {
            Intent intent = new Intent(this,cityBusPortalActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_transaction) {
            Intent intent = new Intent(this, TransactionHistoryActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.nav_score) {
            Intent intent = new Intent(this, queryScoreMainActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.nav_library) {
            Intent intent = new Intent(this, libraryPortalActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.nav_pe_department) {
            Intent intent = new Intent(this, displayStadiumActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.nav_npu_bbs) {
            Intent intent = new Intent(this, bbsShowPortalActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.nav_about_app) {
            Intent intent = new Intent(this, aboutAppActivity.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);

    }

    @SuppressLint("StaticFieldLeak")
    private class insertDataTask extends AsyncTask<Void,Void,Void>{
        caiyunWeatherEntry mWeatherEntry;

        public insertDataTask(caiyunWeatherEntry mWeatherEntry){
            this.mWeatherEntry = mWeatherEntry;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDb.caiyunWeatherDao().insertCaiyunWeatherRecord(mWeatherEntry);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG,"Save it to database "+mWeatherEntry);

        }
    }

    public class getCalenderFromApiTask extends AsyncTask<Void,Void,String>{
        URL mApiUrl;
        Context mContext;
        private Request request;
        private final OkHttpClient client = new OkHttpClient();

        getCalenderFromApiTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mApiUrl = schoolBusNetworkUtils.build_school_calendar_url(mContext);
            request = new Request.Builder()
                    .url(mApiUrl)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonResponse = "";
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
            Calendar calendar = Calendar.getInstance();
            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH)+1;
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("DefaultLocale")
            String curTimeString = String.format("%s%02d%02d",curYear,curMonth,curDay);
            Log.d(TAG,"Current Time Tag is "+curTimeString);
            if(!s.contains(curTimeString)){
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                String festival = schoolBusUtils.handleApiJson(jsonObject);
                Log.d(TAG,"Detect festival : "+festival+" isFestival "+schoolBusUtils.isFestivalHoliday+" isWorkday "+schoolBusUtils.isFestivalWorkDay);

                if(festival.length()!=0){
                    displaySchoolBus(mContext);
                }

            } catch (JSONException e) {
                Log.d(TAG,"Wrong JSON : ");
                e.printStackTrace();
            }
        }
    }


    private void showRequestLocationPermissionDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle(getString(R.string.request_location_dialog_title));
        normalDialog.setMessage(getString(R.string.portal_request_location_message));
        normalDialog.setPositiveButton(getString(R.string.request_location_dialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},8);
                        getCurrentLocation();
                    }
                });
        normalDialog.setNegativeButton(getString(R.string.request_location_dialog_refuse),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private Location getCurrentLocation(){
        Location location = locationUtils.getLastKnownLocation(mContext);
        if(location != null){
            currentLocation = location.getLongitude()+","+location.getLatitude();
            locLatitude = location.getLatitude();
            locLongitude = location.getLongitude();
        }

        return location;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

}
