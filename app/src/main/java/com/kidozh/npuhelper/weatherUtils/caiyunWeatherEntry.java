package com.kidozh.npuhelper.weatherUtils;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


import java.util.Date;

@Entity(tableName = "caiyunWeather")
public class caiyunWeatherEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String location_temperature;
    private String location_geo;
    private String json_string;
    private Date updatedAt;

    @Ignore
    public caiyunWeatherEntry( String location_temperature, String location_geo, String json_string, Date updatedAt){

        this.location_temperature = location_temperature;
        this.location_geo = location_geo;
        this.json_string = json_string;
        this.updatedAt = updatedAt;
    }

    public caiyunWeatherEntry(int id, String location_temperature, String location_geo, String json_string, Date updatedAt){
        this.location_temperature = location_temperature;
        this.location_geo = location_geo;
        this.json_string = json_string;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getLocation_temperature() {
        return location_temperature;
    }

    public String getLocation_geo() {
        return location_geo;
    }

    public String getJson_string() {
        return json_string;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
