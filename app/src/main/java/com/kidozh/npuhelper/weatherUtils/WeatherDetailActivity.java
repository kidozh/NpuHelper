package com.kidozh.npuhelper.weatherUtils;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cjh.weatherviewlibarary.WeatherView;

public class WeatherDetailActivity extends AppCompatActivity {
    final static private String TAG = WeatherDetailActivity.class.getSimpleName();
    @BindView(R.id.weather_app_bar)
    AppBarLayout mWeatherAppBarLayout;

    @BindView(R.id.detailed_weather_temperature_number)
    TextView mDetailedWeatherTempNum;

    @BindView(R.id.detailed_weather_icon)
    ImageView mDetailedWeatherIcon;

    @BindView(R.id.detailed_weather_description)
    TextView mDetailedWeatherDescription;

    @BindView(R.id.detailed_weather_wind_strength)
    TextView mDetailedWeatherWindSpeed;
    @BindView(R.id.detailed_weather_wind_direction)
    TextView mDetailedWeatherWindDirection;
//    @BindView(R.id.weather_recyler_view)
//    RecyclerView mRecyclerview;
    @BindView(R.id.weather_detail_total_cardView)
    CardView mWeatherCardview;
    @BindView(R.id.miuiWeatherRecyclerView)
    RecyclerView miuiDailyForecastRecyclerView;
    @BindView(R.id.current_weather_recyclerview)
    RecyclerView currentWeatherRecylerview;
    private WeatherView<miuiWeatherData> miuiWeatherView;



    public int primaryColor = R.color.colorPeterRiver;

    DisplayMetrics dm;
    public static WeatherDetailActivity instance;

    public int dip2px(float dip) {
        return (int) (dip * dm.density + 0.5);
    }

    public int sp2px(float spValue) {
        return (int) (spValue * dm.scaledDensity + 0.5f);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather_detail);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailed_weather_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String cur_location = intent.getStringExtra("CUR_LOC");
        String realtime_json = intent.getStringExtra("REALTIME_WEATHER");
        getSupportActionBar().setTitle(cur_location);
        setActionBar();
        Log.d(TAG,"get realtime weather: "+realtime_json);
        populateWeatherCondition(realtime_json);
        //populateAirRecyclerView(realtime_json);
        //toolbar.setBackgroundColor(getBaseContext().getColor(R.color.colorGreensea));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "We are developing this function later", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void setPrimaryBackground(int colorResource){
        mWeatherCardview.setBackgroundColor(getColor(colorResource));
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setContentScrimColor(getColor(colorResource));
        ColorDrawable colorDrawable = new ColorDrawable(getColor(colorResource));
        toolbarLayout.setStatusBarScrim(colorDrawable);

    }

    public void populateWeatherCondition(String realtimeString){
        //background setting
        int pic_id = getResources().getIdentifier("dark_background","mipmap",getPackageName());
        mWeatherAppBarLayout.setBackgroundResource(pic_id);

        try{
            JSONObject jsonData = new JSONObject(realtimeString);
            //JSONObject weatherResult = jsonData.getJSONObject("result");
            JSONObject weatherRawResult = jsonData;
            // deal with primary color
            String aqiVal = weatherRawResult.getJSONObject("aqi").getString("aqi");
            int primaryColorRes = weatherDataUtils.getAQIColorResource((int) Float.parseFloat(aqiVal));
            mWeatherCardview.setBackgroundColor(primaryColorRes);
            setPrimaryBackground(primaryColorRes);
            JSONObject weatherResult = weatherRawResult.getJSONObject("current");
            String localTemperature = weatherResult.getJSONObject("temperature").getString("value");
            String weatherCondition = weatherResult.getString("weather");
            float fLocalTemperature = Float.parseFloat(localTemperature);
            int intLocalTemperature = (int) fLocalTemperature;
            mDetailedWeatherTempNum.setText(String.format("%s",intLocalTemperature));
            mDetailedWeatherIcon.setImageDrawable(getDrawable(miuiWeatherUtils.getDrawableWeatherByString(weatherCondition)));
            mDetailedWeatherIcon.setColorFilter(getColor(R.color.colorPureWhite));
            mDetailedWeatherDescription.setText(miuiWeatherUtils.getWeatherTextByString(weatherCondition));
            // wind
            JSONObject windCondition = weatherResult.getJSONObject("wind");
            String windSpeed = windCondition.getJSONObject("speed").getString("value");
            String windDirection = windCondition.getJSONObject("direction").getString("value");
            double windSpeedDouble = Double.parseDouble(windSpeed);
            float windDirectionFloat = Float.parseFloat(windDirection);
            mDetailedWeatherWindDirection.setText(getString(weatherDataUtils.getWindDirectionTextResource(windDirectionFloat)));
            mDetailedWeatherWindSpeed.setText(String.format(getString(R.string.wind_scale_format),weatherDataUtils.getWindScaleNumber(windSpeedDouble)));
            // Daily forecast
            List<miuiWeatherData> forecastWeatherDataList = new ArrayList<>();
            JSONObject dailyForecastObj = jsonData.getJSONObject("forecastDaily");
            JSONArray temperatureArray = dailyForecastObj.getJSONObject("temperature").getJSONArray("value");
            JSONArray aqiArray = dailyForecastObj.getJSONObject("aqi").getJSONArray("value");
            JSONArray weatherArray = dailyForecastObj.getJSONObject("weather").getJSONArray("value");
            JSONArray windArray = dailyForecastObj.getJSONObject("wind").getJSONObject("speed").getJSONArray("value");
            JSONArray windDirectionArray = dailyForecastObj.getJSONObject("wind").getJSONObject("direction").getJSONArray("value");
            int lowestTemp = 100, highestTemp=-80;
            // Date

            Date now = new Date();
            Date currentDate = now;
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(now);
            nowCal.add(Calendar.DATE,-1);
            SimpleDateFormat weekendsFormat = new SimpleDateFormat("MMM dd");
            SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
            for(int i=0;i<temperatureArray.length();i++){

                String weekendTitle = "";
                nowCal.add(Calendar.DATE,1);
                String dayString = weekendsFormat.format(nowCal.getTime());
                if (i==0){
                    weekendTitle = getString(R.string.today);
                }
                else {
                    weekendTitle = weekFormat.format(nowCal.getTime());
                }
                JSONObject curTemperatureForecast = (JSONObject) temperatureArray.get(i);
                String highTemp = curTemperatureForecast.getString("from");
                String lowTemp = curTemperatureForecast.getString("to");
                // weather
                if(Integer.parseInt(highTemp) > highestTemp){
                    highestTemp = Integer.parseInt(highTemp);
                }
                if(Integer.parseInt(lowTemp)<lowestTemp){
                    lowestTemp = Integer.parseInt(lowTemp);
                }
                int aqi = Integer.parseInt(aqiArray.get(i).toString());
                Log.d(TAG,"AQI "+aqi);
                JSONObject weatherLabel = (JSONObject) weatherArray.get(i);
                String fromWeatherLabel = weatherLabel.getString("from");
                String toWeatherLabel = weatherLabel.getString("to");

                JSONObject toWindObj = (JSONObject) windArray.get(i);
                String toWind = toWindObj.getString("to");
                JSONObject toWindDirObj = (JSONObject) windDirectionArray.get(i);
                String toWindDir = toWindDirObj.getString("to");
                int windPowerScale = weatherDataUtils.getWindScaleNumber(Float.parseFloat(toWind));
                int WindDirResource = weatherDataUtils.getWindDirectionTextResource(Float.parseFloat(toWindDir));
                String windPowerString = String.format(getString(R.string.wind_scale_format),windPowerScale);
                forecastWeatherDataList.add(new miuiWeatherData(
                        Integer.parseInt(highTemp),
                        Integer.parseInt(lowTemp),
                        aqi,
                        fromWeatherLabel,
                        toWeatherLabel,
                        getString(WindDirResource),
                        windPowerString,

                        String.format("%d",i),
                        weekendTitle,
                        dayString
                ));
            }



            //default WeatherView
            int mScreenWidth = dm.widthPixels;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            miuiDailyForecastRecyclerView.setLayoutManager(linearLayoutManager);

            miuiDailyForecastRecyclerView.setAdapter(new miuiWeatherAdapter(this,
                    forecastWeatherDataList, highestTemp, lowestTemp,
                    mScreenWidth / 6));


            Log.d(TAG,"Finished curve drawing");
            // draw aqi
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            currentWeatherRecylerview.setLayoutManager(manager);

            weatherDetailInfoAdapter adapter = new weatherDetailInfoAdapter(this);
            List<weatherDetailInfoAdapter.weatherDetailInfoBean> mDetailWeatherInfoList = new ArrayList<>();
            adapter.primaryColor = primaryColorRes;
            currentWeatherRecylerview.setAdapter(adapter);

            // alert first
            JSONArray alerts = jsonData.getJSONArray("alerts");
            for(int i=0;i<alerts.length();i++){
                JSONObject alert = (JSONObject) alerts.get(i);
                mDetailWeatherInfoList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                        getString(R.string.emergency_alert_tag),
                        alert.getString("type"),
                        alert.getString("title"),
                        alert.getString("detail"),
                        true,
                        true
                ));
            }
            // deal with aqi
            aqiVal = jsonData.getJSONObject("aqi").getString("aqi");
            String aqiText = getString(weatherDataUtils.getAQITextResource(Integer.parseInt(aqiVal)));
            String aqiSuggest = jsonData.getJSONObject("aqi").getString("suggest");
            mDetailWeatherInfoList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.aqi),
                    String.format("%s / %s",aqiVal,aqiText),
                    aqiSuggest,
                    "",
                    false
            ));
            // feel temperature
            String feelTemperature = jsonData.getJSONObject("current").getJSONObject("feelsLike").getString("value");
            mDetailWeatherInfoList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.somatosensory_temperature),
                    feelTemperature+" ℃",
                    "",
                    "",
                    false
            ));
            // humidity
            String humidity = jsonData.getJSONObject("current").getJSONObject("humidity").getString("value");
            mDetailWeatherInfoList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.relative_humidity),
                    humidity+ " %",
                    "",
                    "",
                    false
            ));

            // air pressure
            String airPressure = jsonData.getJSONObject("current").getJSONObject("pressure").getString("value");
            mDetailWeatherInfoList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.air_pressure_tag),
                    String.format("%s mb",airPressure),
                    "",
                    "",
                    false
            ));
            adapter.mWeatherInfoList = mDetailWeatherInfoList;
            adapter.notifyDataSetChanged();

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
            finish();
        }

        //populate

    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        // 显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 去掉logo图标
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setTitle(R.string.back_label);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }



        return super.onOptionsItemSelected(item);
    }


}
