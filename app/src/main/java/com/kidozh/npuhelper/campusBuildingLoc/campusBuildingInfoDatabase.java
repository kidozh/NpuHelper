package com.kidozh.npuhelper.campusBuildingLoc;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.kidozh.npuhelper.weatherUtils.caiyunWeatherDatabase;

@Database(entities = {campusBuildingInfoEntity.class},version = 1 ,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class campusBuildingInfoDatabase extends RoomDatabase {
    private static final String TAG = caiyunWeatherDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "campusBuildingInfoDatabase";
    private static campusBuildingInfoDatabase sInstance;


    public static campusBuildingInfoDatabase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        campusBuildingInfoDatabase.class,
                        campusBuildingInfoDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract campusBuildingInfoDao campusBuildingInfoDao();

}
