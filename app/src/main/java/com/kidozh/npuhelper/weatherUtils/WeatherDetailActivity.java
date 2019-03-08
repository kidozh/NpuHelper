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

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.weather_recyler_view)
    RecyclerView mRecyclerview;
    @BindView(R.id.weather_detail_total_cardView)
    CardView mWeatherCardview;
    @BindView(R.id.wind_ocean_desciption)
    TextView mWindOceanDiscription;
    @BindView(R.id.wind_land_desciption)
    TextView mWindLandDiscription;
    @BindView(R.id.wind_weather_cardview)
    CardView mWeatherWindCardview;
    @BindView(R.id.weather_deatil_support_cardview)
    CardView mWeatherSupportCardview;
    @BindView(R.id.air_composition_recycelrview)
    RecyclerView airCompositionRecyclerView;

    public int primaryColor = R.color.colorPeterRiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
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
        populateAirRecyclerView(realtime_json);
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
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(colorResource)));
        //getWindow().setStatusBarColor(getColor(colorResource));
        mWeatherCardview.setBackgroundColor(getColor(colorResource));
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setContentScrimColor(getColor(colorResource));
        mWeatherWindCardview.setBackgroundColor(getColor(colorResource));
        mWeatherSupportCardview.setBackgroundColor(getColor(colorResource));
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
            JSONObject weatherRawResult = jsonData.getJSONObject("result");
            JSONObject weatherResult = weatherRawResult.getJSONObject("realtime");
            String localTemperature = weatherResult.getString("temperature");
            String weatherCondition = weatherResult.getString("skycon");
            float fLocalTemperature = Float.parseFloat(localTemperature);
            int intLocalTemperature = (int) fLocalTemperature;
            mDetailedWeatherTempNum.setText(String.format("%s",intLocalTemperature));
            mDetailedWeatherIcon.setImageDrawable(getDrawable(weatherDataUtils.getDrawableWeatherByString(weatherCondition)));
            mDetailedWeatherIcon.setColorFilter(getColor(R.color.colorPureWhite));
            mDetailedWeatherDescription.setText(weatherDataUtils.getWeatherTextByString(weatherCondition));

            JSONObject windCondition = weatherResult.getJSONObject("wind");
            String windSpeed = windCondition.getString("speed");
            String windDirection = windCondition.getString("direction");
            double windSpeedDouble = Double.parseDouble(windSpeed);
            float windDirectionFloat = Float.parseFloat(windDirection);
            mDetailedWeatherWindDirection.setText(getString(weatherDataUtils.getWindDirectionTextResource(windDirectionFloat)));

            mDetailedWeatherWindSpeed.setText(String.format(getString(R.string.wind_scale_format),weatherDataUtils.getWindScaleNumber(windSpeedDouble)));

            List<weatherDetailInfoAdapter.weatherDetailInfoBean> weatherDetailInfoBeanList = parseWeatherRes(jsonData);

            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            mRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            weatherDetailInfoAdapter adapter = new weatherDetailInfoAdapter(this);
            adapter.mWeatherInfoList = weatherDetailInfoBeanList;
            adapter.primaryColor = primaryColor;
            setPrimaryBackground(primaryColor);
            mRecyclerview.setAdapter(adapter);
            // air



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

    private void populateAirRecyclerView(String jsonString){
        List<weatherDetailInfoAdapter.weatherDetailInfoBean> weatherDetailInfoBeanList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject weatherRawResult = jsonObject.getJSONObject("result");
            JSONObject weatherResult = weatherRawResult.getJSONObject("realtime");

            String Val = weatherResult.getJSONObject("air_quality").getString("co");
            if(Val.equals("0")){
                Val = "-";
            }

            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.co),Val,"","",false));

            Val = weatherResult.getJSONObject("air_quality").getString("pm10");
            if(Val.equals("0")){
                Val = "-";
            }
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.pm10),Val,"","",false));

            Val = weatherResult.getJSONObject("air_quality").getString("pm25");
            if(Val.equals("0")){
                Val = "-";
            }
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.pm25),Val,"","",false));

            Val = weatherResult.getJSONObject("air_quality").getString("so2");
            if(Val.equals("0")){
                Val = "-";
            }
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.so2),Val,"","",false));

            Val = weatherResult.getJSONObject("air_quality").getString("o3");
            if(Val.equals("0")){
                Val = "-";
            }
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.o3),Val,"","",false));

            Val = weatherResult.getJSONObject("air_quality").getString("no2");
            if(Val.equals("0")){
                Val = "-";
            }
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.no2),Val,"","",false));


        }
        catch (JSONException e){
            e.printStackTrace();
        }
        airCompositionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        airCompositionRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(6,RecyclerView.VERTICAL));
        weatherDetailInfoAdapter adapter = new weatherDetailInfoAdapter(this);
        adapter.mWeatherInfoList = weatherDetailInfoBeanList;
        adapter.primaryColor = primaryColor;
        setPrimaryBackground(primaryColor);
        airCompositionRecyclerView.setAdapter(adapter);


    }

    private List<weatherDetailInfoAdapter.weatherDetailInfoBean> parseWeatherRes(JSONObject jsonObject){
        List<weatherDetailInfoAdapter.weatherDetailInfoBean> weatherDetailInfoBeanList = new ArrayList<>();
        try{

            JSONObject weatherRawResult = jsonObject.getJSONObject("result");
            JSONObject weatherResult = weatherRawResult.getJSONObject("realtime");

            String aqiVal = weatherResult.getJSONObject("air_quality").getJSONObject("aqi").getString("chn");
            primaryColor = weatherDataUtils.getAQIColorResource((int) Float.parseFloat(aqiVal));

            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.aqi),aqiVal,getString(weatherDataUtils.getAQITextResource((int) Float.parseFloat(aqiVal))),"",true));

            String value = weatherResult.getString("humidity");
            float relative_humidity = Float.parseFloat(value) * 100;
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.relative_humidity),String.valueOf(relative_humidity),"","",false));

            String cloudvalue = weatherResult.getString("cloudrate");
            float cloudrate = (float) Float.parseFloat(cloudvalue) * 100;
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.cloudrate_tag),String.format(getString(R.string.percent_number_format),(int) cloudrate),
                    getString(weatherDataUtils.getCloudRateDescriptionTextResource(Float.parseFloat(cloudvalue))),
                    "",false));

            value = weatherResult.getString("visibility");
            Float visibilityFloat = Float.parseFloat(value);

            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.visibility_tag),String.format(getString(R.string.km_format),value),
                    getString(weatherDataUtils.getVisibilityTextResource(visibilityFloat)),
                    "",false));

            // wind
            JSONObject windCondition = weatherResult.getJSONObject("wind");
            String windSpeed = windCondition.getString("speed");
            double windSpeedDouble = Double.parseDouble(windSpeed);
            int windStrength = weatherDataUtils.getWindScaleNameTextResource(this,windSpeedDouble);
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.wind_strength),getString(windStrength),
                    String.format(getString(R.string.km_per_h_format),windSpeed),"",false));
            mWindLandDiscription.setText(weatherDataUtils.getWindScaleLandTextResource(this,windSpeedDouble));
            mWindOceanDiscription.setText(weatherDataUtils.getWindScaleOceanTextResource(this,windSpeedDouble));



            value = weatherResult.getString("pressure");
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.air_pressure_tag),String.format(getString(R.string.kpa_format), Float.parseFloat(value) / 1000),"",getString(R.string.air_pressure_demonstrate),false));

            // rain
            JSONObject rainResult = weatherResult.getJSONObject("precipitation");
            JSONObject localRain = rainResult.getJSONObject("local");
            JSONObject nearstRain = rainResult.getJSONObject("nearest");

            if(nearstRain != null && nearstRain.getString("status").equals("ok")){

                String distance = nearstRain.getString("distance");
                float fDistance = Float.parseFloat(distance);
                int intDistance = (int) fDistance;
                if(intDistance > 1000){
                    weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                            getString(R.string.nearst_rain_strength),String.format(getString(R.string.km_format),getString(R.string.rain_distance_1000_plus)),"","",true));
                }
                else {
                    String intensity = nearstRain.getString("intensity");
                    weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                            getString(R.string.nearst_rain_strength),String.format(getString(R.string.km_format),
                            String.valueOf(intDistance)),
                            getString(weatherDataUtils.getRainTextResource(Float.parseFloat(intensity))),"",true));
                }
            }
            String descVal = weatherResult.getJSONObject("life_index").getJSONObject("comfort").getString("desc");
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.comfort_text),descVal,"","",false));
            descVal = weatherResult.getJSONObject("life_index").getJSONObject("ultraviolet").getString("desc");
            weatherDetailInfoBeanList.add(new weatherDetailInfoAdapter.weatherDetailInfoBean(
                    getString(R.string.ultraviolet),descVal,"","",false));

        }
        catch (JSONException e){
            e.printStackTrace();

        }
        return weatherDetailInfoBeanList;

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
