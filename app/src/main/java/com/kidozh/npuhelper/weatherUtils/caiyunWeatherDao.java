package com.kidozh.npuhelper.weatherUtils;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface caiyunWeatherDao {
    @Query("SELECT * FROM caiyunWeather ORDER BY updatedAt")
    LiveData<List<caiyunWeatherEntry>>loadAllWeatherRecords();

    @Insert
    void insertCaiyunWeatherRecord(caiyunWeatherEntry caiyunWeatherEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCaiyunWeatherRecord(caiyunWeatherEntry caiyunWeatherEntry);

    @Delete
    void deleteCaiyunWeatherRecord(caiyunWeatherEntry caiyunWeatherEntry);

    @Query("SELECT * FROM caiyunWeather WHERE id = :id")
    caiyunWeatherEntry loadWeatherById(int id);

    @Query("SELECT * FROM caiyunWeather ORDER BY id DESC LIMIT 1")
    LiveData<caiyunWeatherEntry> getLastWeatherById();
}
