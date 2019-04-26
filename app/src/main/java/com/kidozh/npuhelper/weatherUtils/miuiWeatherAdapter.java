package com.kidozh.npuhelper.weatherUtils;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cjh.weatherviewlibarary.WeatherView;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;

public class miuiWeatherAdapter extends RecyclerView.Adapter<miuiWeatherAdapter.ViewHolder>{
    private List<miuiWeatherData> datas = new ArrayList<>();

    private int highestDegree, lowestDegree;

    private Context context;

    public static final int DEFAULT_WEATHERVIEW = 0;

    public static final int SETTING_WEATHERVIEW = 1;

    private int style;

    private int weatherViewW;

    public miuiWeatherAdapter(Context context, List<miuiWeatherData> datas, int highestDegree, int lowestDegree, int weatherViewW) {
        this.context = context;
        this.datas = datas;
        this.highestDegree = highestDegree;
        this.lowestDegree = lowestDegree;
        this.weatherViewW = weatherViewW;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                context).inflate(R.layout.miui_weather_banner, parent,
                false);
        return new ViewHolder(view);
    }

    public static int px2dip(int pxValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static int dip2px(float dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        miuiWeatherData weatherData = datas.get(position);
        holder.weatherView.setWH(weatherViewW,weatherViewW*2);
        holder.weatherView.setDatas(datas,highestDegree,lowestDegree,position);
        if(position == 0){
            holder.weatherView.setToday(true);
        }
        else {
            holder.weatherView.setToday(false);
        }
        holder.fromWeatherText.setText(miuiWeatherUtils.getWeatherTextByString(weatherData.fromWeatherLabel));
        holder.toWeatherName.setText(miuiWeatherUtils.getWeatherTextByString(weatherData.toWeatherLabel));
        holder.fromWeatherImage.setImageDrawable(
                context.getDrawable(miuiWeatherUtils.getDrawableWeatherByString(weatherData.fromWeatherLabel)));
        holder.fromWeatherImage.setColorFilter(R.color.colorWetasphalt);
        holder.toWeatherImage.setImageDrawable(
                context.getDrawable(miuiWeatherUtils.getDrawableWeatherByString(weatherData.toWeatherLabel)));
        holder.aqiText.setText(weatherDataUtils.getAQITextResource(weatherData.aqi));
        holder.aqiText.setBackgroundColor(context.getColor(weatherDataUtils.getAQIColorResource(weatherData.aqi)));
        holder.fromWeatherImage.setColorFilter(R.color.colorPureBlack);
        holder.toWeatherImage.setColorFilter(R.color.colorPureBlack);
        holder.weatherWindPower.setText(weatherData.windSpeed);
        holder.weatherWind.setText(weatherData.windDire);
        holder.dateName.setText(weatherData.weekendName);
        holder.dateTime.setText(weatherData.dateName);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

//        public WeatherView weatherView;

        @BindView(R.id.date_name)
        TextView dateName;
        @BindView(R.id.date_time)
        TextView dateTime;
        @BindView(R.id.from_weather_image)
        ImageView fromWeatherImage;
        @BindView(R.id.from_weather_text)
        TextView fromWeatherText;
        @BindView(R.id.to_weather_image)
        ImageView toWeatherImage;
        @BindView(R.id.to_weather_name)
        TextView toWeatherName;
        @BindView(R.id.to_aqi_name)
        TextView aqiText;
        @BindView(R.id.to_weather_wind)
        TextView weatherWind;
        @BindView(R.id.weatherView)
        WeatherView weatherView;
        @BindView(R.id.to_weather_power)
        TextView weatherWindPower;
        @BindView(R.id.root_weather_banner_layout)
        LinearLayout rootWeatherLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }
}
