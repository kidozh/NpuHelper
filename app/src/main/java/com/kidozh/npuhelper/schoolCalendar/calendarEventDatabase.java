package com.kidozh.npuhelper.schoolCalendar;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kidozh.npuhelper.weatherUtils.DateConverter;


@Database(entities = {calendarEventEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class calendarEventDatabase extends RoomDatabase {
    private static String TAG = calendarEventDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "calendarEventDatabase";
    private static calendarEventDatabase sInstance;

    public static calendarEventDatabase getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        calendarEventDatabase.class,
                        calendarEventDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract calendarEventDao calendarEventDao();

}
