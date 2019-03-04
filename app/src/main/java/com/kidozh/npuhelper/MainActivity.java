package com.kidozh.npuhelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.DPoint;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Tip;
import com.kidozh.npuhelper.accountAuth.LoginUniversityActivity;
import com.kidozh.npuhelper.campusAddressBook.campusAddressBookMainActivity;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusNetworkUtils;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusUtils;
import com.kidozh.npuhelper.preference.SettingsActivity;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusListActivity;
import com.kidozh.npuhelper.schoolCalendar.schoolCalendarMainActivity;
import com.kidozh.npuhelper.utilities.locationUtils;
import com.kidozh.npuhelper.weatherUtils.WeatherDetailActivity;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherDatabase;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherEntry;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherUtils;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherViewModel;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherViewModelFactory;
import com.kidozh.npuhelper.weatherUtils.addCaiyunWeatherViewModel;
import com.kidozh.npuhelper.campusBuildingLoc.campusBuildingPortalActivity;
import com.kidozh.npuhelper.xianCityBus.cityBusPortalActivity;
import com.kidozh.npuhelper.xianCityBus.suggestCityLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.tools.ant.Main;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.client.cache.Resource;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

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

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;

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

        // check permission
        suggestCityLocation suggestionCityLocation = getCurrentSuggestCityLocation();
        LatLonPoint latLonPoint = suggestionCityLocation.locationTip.getPoint();
        currentLocation = String.format("%s,%s",latLonPoint.getLongitude(),latLonPoint.getLatitude());
        locLatitude = latLonPoint.getLatitude();
        locLongitude = latLonPoint.getLongitude();

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
        new getCalenderFromApiTask(mContext).execute();
        new getWeatherInfoTask().execute();

        displaySchoolBus(this);

        countDownTimer.start();
        Log.d(TAG,"Main thread finished."+mAuthBtn);
        renderGeoLocationByAmap();
        // renderGeoLocation();
        getWeatherFromDb();

        mAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginUniversityActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String location = null;
            if(currentLocation != null){
                location = currentLocation;
            }
            else {
                location = caiyunWeatherUtils.get_GEO_LOCATION();
            }




            try{
                URL caiyunWeatherApiUrl = caiyunWeatherUtils.build_realtime_api_url(location);
                Log.d(TAG, "START QUERYING " + caiyunWeatherApiUrl);
                String jsonResponse = caiyunWeatherUtils.getResponseFromHttpUrl(caiyunWeatherApiUrl);
                return jsonResponse;
            }
            catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            mLoadingWeatherProgressBar.setVisibility(View.INVISIBLE);
            mWeatherCard.setVisibility(View.VISIBLE);
            Log.d(TAG,"On query load finished, response is "+data);
            // set Text
            if(data == null){
                mLocationName.setText(getString(R.string.geo_parse_failed));
                mLocationTemperature.setText(getString(R.string.unknown_temperature));
                Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
                return;
            }
            mRealTimeInfo = data;
            JSONObject jsonData ;
            try {
                jsonData = new JSONObject(data);
                String status = (String) jsonData.get("status");
                Log.i(TAG,"status " + status);
                if(!status.equals("ok")){
                    mLocationTemperature.setText(getString(R.string.unknown_temperature));
                    Toasty.error(mContext,(String) jsonData.get("error"),Toast.LENGTH_LONG,true).show();
                }
                else {
                    // Geometry decoder
                    JSONObject weatherResult = jsonData.getJSONObject("result");
                    String localTemperature = weatherResult.getString("temperature");
                    String weatherCondition = weatherResult.getString("skycon");

                    // save it to database
                    final caiyunWeatherEntry weatherEntry = new caiyunWeatherEntry(
                            localTemperature,
                            String.format("%s,%s",locLatitude,locLongitude),
                            data,
                            new Date()
                    );

                    new insertDataTask(weatherEntry).execute();
                    populateWeatherUI(weatherEntry);



                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
            }

            Log.d(TAG,"Weather condition finished..");
            super.onPostExecute(data);
        }
    }

    public void renderGeoLocationByAmap(){
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if(i == 1000){
                    RegeocodeAddress geoCodeAddr = regeocodeResult.getRegeocodeAddress();
                    String locationName = geoCodeAddr.getTownship();
                    Log.d(TAG,"Get Neighbor : "+locationName);
                    mLocationName.setText(locationName);

                }
                else {
                    Log.d(TAG,"Get Result Code "+i);
                    Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        LatLonPoint latLonPoint = new LatLonPoint(locLatitude,locLongitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.GPS);

        geocoderSearch.getFromLocationAsyn(query);
    }

    public void renderGeoLocation(){
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        if(Geocoder.isPresent()){
            List<Address> locationList = null;
            try {
                // there is backend service
                locationList = gc.getFromLocation(locLatitude,locLongitude,1);
                Address address = locationList.get(0);

                String countryName = address.getCountryName();
                String locality = address.getLocality();
                String adminArea = address.getAdminArea();
                String feature = address.getFeatureName();

                String locationFullName = adminArea + " " + locality + " " + feature;
                Log.d(TAG,"get location name " +  locationFullName);

                Log.d(TAG,address.toString());

                mLocationName.setText(feature);


            }
            catch (Exception e){
                mLocationName.setText(getString(R.string.geo_parse_failed));
                Toasty.error(this, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
                e.printStackTrace();
            }
            //assert locationList != null;
            Log.d(TAG,"Ended Loading Weather condition");
            //finish();
        }
        else {
            GeocodeSearch geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if(i == 1000){
                        RegeocodeAddress geoCodeAddr = regeocodeResult.getRegeocodeAddress();
                        String neighborhood = geoCodeAddr.getNeighborhood();
                        mLocationName.setText(neighborhood);

                    }
                    else {
                        Toasty.error(mContext, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
            LatLonPoint latLonPoint = new LatLonPoint(locLatitude,locLongitude);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.GPS);

            geocoderSearch.getFromLocationAsyn(query);
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
        JSONObject weatherResult = jsonData.getJSONObject("result");
        String localTemperature = weatherResult.getString("temperature");
        String weatherCondition = weatherResult.getString("skycon");
        // transfer weather condition
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("CLEAR_DAY",R.drawable.vector_drawable_weather_sunny);
        weather2drawable.put("CLEAR_NIGHT", R.drawable.vector_drawable_weather_night);
        weather2drawable.put("PARTLY_CLOUDY_DAY",R.drawable.vector_drawable_weather_partlycloudy);
        weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.drawable.vector_drawable_weather_partlycloudy);
        weather2drawable.put("CLOUDY",R.drawable.vector_drawable_weather_cloudy);
        weather2drawable.put("RAIN",R.drawable.vector_drawable_weather_rainy);
        weather2drawable.put("SNOW",R.drawable.vector_drawable_weather_snowy);
        weather2drawable.put("WIND",R.drawable.vector_drawable_weather_windy);
        weather2drawable.put("FOG",R.drawable.vector_drawable_weather_fog);
        weather2drawable.put("HAZE",R.drawable.vector_drawable_weather_fog);

        // get Drawable icon
        Drawable weatherIcon = getDrawable((Integer) weather2drawable.get(weatherCondition));
        mWeatherIcon.setImageDrawable(weatherIcon);
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
    }



    public void displaySchoolBus(final Context context){
        Log.d(TAG, "isFestival "+schoolBusUtils.isFestivalHoliday+" isWorkday "+schoolBusUtils.isFestivalWorkDay);
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

    // navigation added
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
        // Handle navigation view item clicks here.
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

        } else if (id == R.id.nav_send) {

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

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;
        for (String provider : providers) {

            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
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

    public suggestCityLocation getCurrentSuggestCityLocation(){
        String currentLocationName = mContext.getString(R.string.bus_my_location_label);
        String currentLocation = "";
        Tip currentTip = new Tip();
        currentTip.setName(currentLocationName);
        String GEO_LOCATION = "108.91148,34.24626";

        // check permission
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toasty.info(mContext,mContext.getString(R.string.location_denied_notice),Toast.LENGTH_SHORT,true).show();
                // use youyi campus
                String youyiCampusName = mContext.getString(R.string.youyi_campus_name);
                double locLatitude = 34.24626;
                double locLongitude = 108.91148;

                LatLonPoint campuslatLonPoint = new LatLonPoint(locLatitude,locLongitude);
                currentTip.setPostion(campuslatLonPoint);
                return new suggestCityLocation(youyiCampusName,youyiCampusName,currentTip);


            }
            else {
                currentLocation = GEO_LOCATION;
                showRequestLocationPermissionDialog();
                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},8);
            }

        }
        Log.d(TAG,"USER permits location request");
        Location location = locationUtils.getLastKnownLocation(mContext);
        if(location != null){
            currentLocation = location.getLongitude()+","+location.getLatitude();
            locLatitude = location.getLatitude();
            locLongitude = location.getLongitude();
        }
        else {
            // use default value
            currentLocation = GEO_LOCATION;

        }


        Log.d(TAG,"Loc "+currentLocation +" Manager "+ location);
        currentLocationName = mContext.getString(R.string.bus_my_location_label);
        LatLonPoint campuslatLonPoint = new LatLonPoint(locLatitude,locLongitude);
        currentTip.setPostion(campuslatLonPoint);
        return new suggestCityLocation(currentLocationName,currentLocationName,currentTip);
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

}
