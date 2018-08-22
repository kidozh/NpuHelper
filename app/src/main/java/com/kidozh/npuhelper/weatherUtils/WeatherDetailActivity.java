package com.kidozh.npuhelper.weatherUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.preference.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDetailActivity extends AppCompatActivity {
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

    @BindView(R.id.detailed_weather_humidity_value)
    TextView mDetailedWeatherHumidityVal;

    @BindView(R.id.detailed_weather_pm25_value)
    TextView mDetailedWeatherPm25Val;

    @BindView(R.id.detailed_weather_rain_tag)
    TextView mDetailedWeatherRainTag;

    @BindView(R.id.detailed_weather_rain_text_1)
    TextView mDetailWeatherRainText1;

    @BindView(R.id.detailed_weather_rain_text_2)
    TextView mDetailWeatherRainText2;



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
        populateWeatherCondition(realtime_json);
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

    public void populateWeatherCondition(String realtimeString){
        //background setting
        int pic_id = getResources().getIdentifier("dark_background","mipmap",getPackageName());
        mWeatherAppBarLayout.setBackgroundResource(pic_id);

        try{
            JSONObject jsonData = new JSONObject(realtimeString);
            JSONObject weatherResult = jsonData.getJSONObject("result");
            String localTemperature = weatherResult.getString("temperature");
            String weatherCondition = weatherResult.getString("skycon");
            float fLocalTemperature = Float.parseFloat(localTemperature);
            int intLocalTemperature = (int) fLocalTemperature;
            mDetailedWeatherTempNum.setText(String.format("%s",intLocalTemperature));
            mDetailedWeatherIcon.setImageDrawable(getDrawable(getDrawableWeatherByString(weatherCondition)));
            mDetailedWeatherIcon.setColorFilter(getColor(R.color.cardview_light_background));
            mDetailedWeatherDescription.setText(getWeatherTextByString(weatherCondition));

            JSONObject windCondition = weatherResult.getJSONObject("wind");
            String windSpeed = windCondition.getString("speed");
            mDetailedWeatherWindSpeed.setText(windSpeed);

            //humidty
            String humidity = weatherResult.getString("humidity");
            float fHumidity = Float.parseFloat(humidity);
            int intHumidity = (int) (fHumidity*100);
            mDetailedWeatherHumidityVal.setText(String.format("%s",intHumidity));


            // pm2.5
            String pm25Val = weatherResult.getString("aqi");
            mDetailedWeatherPm25Val.setText(pm25Val);


            // rain
            JSONObject rainResult = weatherResult.getJSONObject("precipitation");
            JSONObject localRain = rainResult.getJSONObject("local");
            JSONObject nearstRain = rainResult.getJSONObject("nearest");

            if(nearstRain != null && nearstRain.getString("status").equals("ok")){
                mDetailedWeatherRainTag.setText(R.string.nearst_rain_strength);
                String distance = nearstRain.getString("distance");
                float fDistance = Float.parseFloat(distance);
                int intDistance = (int) fDistance;
                mDetailWeatherRainText1.setText(String.format("%s %s",intDistance,getString(R.string.kilometer)));
                String intensity = nearstRain.getString("intensity");
                mDetailWeatherRainText2.setText(intensity);
            }
            else {
                mDetailedWeatherRainTag.setText(R.string.rain_strength);
                String distance = String.format("%s %s",Integer.parseInt(localRain.getString("distance")),getString(R.string.kilometer));
                mDetailWeatherRainText1.setText(distance);
                String intensity = localRain.getString("intensity");
                mDetailWeatherRainText2.setText(intensity);
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        //populate

    }

    private int getDrawableWeatherByString(String skycon_string){
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
        int drawable_id = (int) weather2drawable.get(skycon_string);
        return drawable_id;
    }

    private int getWeatherTextByString(String skycon_string){
        Map weather2drawable = new HashMap<String,Integer>();
        weather2drawable.put("CLEAR_DAY",R.string.CLEAR_DAY);
        weather2drawable.put("CLEAR_NIGHT", R.string.CLEAR_NIGHT);
        weather2drawable.put("PARTLY_CLOUDY_DAY",R.string.PARTLY_CLOUDY_DAY);
        weather2drawable.put("PARTLY_CLOUDY_NIGHT",R.string.PARTLY_CLOUDY_NIGHT);
        weather2drawable.put("CLOUDY",R.string.CLOUDY);
        weather2drawable.put("RAIN",R.string.RAIN);
        weather2drawable.put("SNOW",R.string.SNOW);
        weather2drawable.put("WIND",R.string.WIND);
        weather2drawable.put("FOG",R.string.HAZE);
        weather2drawable.put("HAZE",R.string.HAZE);
        int drawable_id = (int) weather2drawable.get(skycon_string);
        return drawable_id;
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
