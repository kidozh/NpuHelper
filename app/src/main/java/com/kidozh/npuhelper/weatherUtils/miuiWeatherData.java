package com.kidozh.npuhelper.weatherUtils;

import cjh.weatherviewlibarary.IBaseWeatherData;

import java.util.List;

public class miuiWeatherData implements IBaseWeatherData {
    public int highDegree;
    public int lowDegree;
    public String dateString;
    public int aqi;
    public String fromWeatherLabel,toWeatherLabel;
    public String windSpeed,windDire;
    String weekendName;
    String dateName;
    public miuiWeatherData() {
    }

    public miuiWeatherData(int highDegree, int lowDegree,
                           int aqi, String fromWeatherLabel, String toWeatherLabel,
                           String windDire, String windSpeed, String dateString,
                            String weekendName, String dateName) {
        this.highDegree = highDegree;
        this.lowDegree = lowDegree;
        this.aqi = aqi;
        this.dateString = dateString;
        this.fromWeatherLabel = fromWeatherLabel;
        this.toWeatherLabel = toWeatherLabel;
        this.windSpeed = windSpeed;
        this.windDire = windDire;
        this.weekendName = weekendName;
        this.dateName = dateName;
    }

    @Override
    public int getHighDegree() {
        return highDegree;
    }

    @Override
    public int getLowDegree() {
        return lowDegree;
    }
}
