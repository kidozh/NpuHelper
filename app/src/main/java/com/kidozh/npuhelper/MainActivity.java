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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.schoolBusUtils.schoolBusUtils;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherDatabase;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherEntry;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherUtils;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherViewModel;
import com.kidozh.npuhelper.weatherUtils.caiyunWeatherViewModelFactory;
import com.kidozh.npuhelper.weatherUtils.addCaiyunWeatherViewModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

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

    private double locLatitude = 34.244065;
    private double locLongitude = 108.915874;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private caiyunWeatherDatabase mDb;

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        // bind data
        ButterKnife.bind(this);

        mTitle = mDrawerTitle = getTitle();

        // get database connection
        mDb = caiyunWeatherDatabase.getsInstance(getApplicationContext());

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG,"GET LOCATION PERMIT"+locationManager);

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toasty.info(this,getString(R.string.caiyun_support_notice),Toast.LENGTH_SHORT,true).show();
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},8);
            }
            else {
                // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},8);
                // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},8);
                Toasty.error(this, getString(R.string.location_fail_notice), Toast.LENGTH_LONG, true).show();


            }



        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},8);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                currentLocation = location.getLongitude()+","+location.getLatitude();
                locLatitude = location.getLatitude();
                locLongitude = location.getLongitude();
            }
            else {
                // use default value
                currentLocation = caiyunWeatherUtils.GEO_LOCATION;
            }


            Log.d(TAG,"Loc"+currentLocation);


        }

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
                return false;
            }
        });


        LoaderManager.LoaderCallbacks<String> callback = MainActivity.this;
        int loaderId = FORECAST_LOADER_ID;
        mContext = getApplicationContext();

        displaySchoolBus(this);

        countDownTimer.start();
        // weather query
        //weather data observe
        //getSupportLoaderManager().initLoader(loaderId,bundleForLoader,callback);
        getLoaderManager().initLoader(loaderId,null,callback);
        Log.d(TAG,"Main thread finished.");
        renderGeoLocation();
        getWeatherFromDb();


    }

    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.LoaderCallbacks<String> callback = MainActivity.this;
        getLoaderManager().restartLoader(FORECAST_LOADER_ID,null,callback);
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

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            String mWeatherData = null;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "ON START RENDERING CACHE" + mWeatherData);
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    Log.d(TAG,"Weather Data is " + mWeatherData);
                    mWeatherCard.setVisibility(View.INVISIBLE);
                    mLoadingWeatherProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }


                super.onStartLoading();
            }

            @Override
            public String loadInBackground() {
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

            public void deliverResult(String data) {
                mWeatherData = data;
                Log.d(TAG,"Deliver data "+data);
                if(data != null){

                    super.deliverResult(data);
                }
                else {
                    super.deliverResult(null);
                }

            }


        };
    }



    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mLoadingWeatherProgressBar.setVisibility(View.INVISIBLE);
        mWeatherCard.setVisibility(View.VISIBLE);
        Log.d(TAG,"On query load finished, response is "+data);
        // set Text
        if(data == null){
            mLocationName.setText(getString(R.string.geo_parse_failed));
            mLocationTemperature.setText(getString(R.string.unknown_temperature));
            Toasty.error(this, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
            return;
        }
        JSONObject jsonData ;
        try {
            jsonData = new JSONObject(data);
            String status = (String) jsonData.get("status");
            Log.i(TAG,"status " + status);
            if(!status.equals("ok")){
                mLocationTemperature.setText(getString(R.string.unknown_temperature));
                Toasty.error(this,(String) jsonData.get("error"),Toast.LENGTH_LONG,true).show();
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


                //mDb.caiyunWeatherDao().insertCaiyunWeatherRecord(weatherEntry);

                caiyunWeatherViewModelFactory factory = new caiyunWeatherViewModelFactory(mDb);
                final addCaiyunWeatherViewModel viewModel = ViewModelProviders.of(this,factory).get(addCaiyunWeatherViewModel.class);
                viewModel.getCaiyunWeatherEntryLiveData().observe(this, new Observer<caiyunWeatherEntry>() {
                    @Override
                    public void onChanged(@Nullable caiyunWeatherEntry caiyunWeatherEntry) {
                        viewModel.getCaiyunWeatherEntryLiveData().removeObserver(this);
                        try {
                            populateWeatherUI(weatherEntry);
                        } catch (JSONException e) {
                            Log.d(TAG,"JSON error when configured with weather");
                            e.printStackTrace();
                        }
                    }
                });
                //finish();
                populateWeatherUI(weatherEntry);



            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toasty.error(this, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
        }

        Log.d(TAG,"Weather condition finished..");



    }

    public void renderGeoLocation(){
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
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
        catch (IOException e){
            mLocationName.setText(getString(R.string.geo_parse_failed));
            Toasty.error(this, getString(R.string.connection_error_notice), Toast.LENGTH_SHORT, true).show();
            // e.printStackTrace();
        }
        //assert locationList != null;
        Log.d(TAG,"Ended Loading Weather condition");
        //finish();
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

        // get Drawable icon
        Drawable weatherIcon = getDrawable((Integer) weather2drawable.get(weatherCondition));
        mWeatherIcon.setImageDrawable(weatherIcon);
        mLocationTemperature.setText(String.format("%s %s",localTemperature, celsius_temperature_unit_label));
        Log.d(TAG,"Ended Rendering Weather Condition");
    }

    public void displaySchoolBus(Context context){
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
        Log.v(TAG,"school shuttle rendering finished");

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
        Log.d(TAG,"Pressed id "+ id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

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



}
