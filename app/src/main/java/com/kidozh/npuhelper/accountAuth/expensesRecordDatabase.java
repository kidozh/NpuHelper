package com.kidozh.npuhelper.accountAuth;

import android.content.Context;
import android.util.Log;

import com.kidozh.npuhelper.campusBuildingLoc.DateConverter;
import com.kidozh.npuhelper.campusBuildingLoc.campusBuildingInfoEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {expensesRecordEntity.class},version = 1 ,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class expensesRecordDatabase extends RoomDatabase {
    private static final String TAG = expensesRecordDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "expensesRecordDatabase";
    private static expensesRecordDatabase sInstance;

    public static expensesRecordDatabase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        expensesRecordDatabase.class,
                        expensesRecordDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract expensesRecordDao expensesRecordDao();
}
