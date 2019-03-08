package com.kidozh.npuhelper.weatherUtils;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {caiyunWeatherEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class caiyunWeatherDatabase extends RoomDatabase{
    private static final String TAG = caiyunWeatherDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "caiyunWeatherDatabase";
    private static caiyunWeatherDatabase sInstance;

    public static caiyunWeatherDatabase getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        caiyunWeatherDatabase.class,
                        caiyunWeatherDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract caiyunWeatherDao caiyunWeatherDao();
}
