package com.kidozh.npuhelper.weatherUtils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
