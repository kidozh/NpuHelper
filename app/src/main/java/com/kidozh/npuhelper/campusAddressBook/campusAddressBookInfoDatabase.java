package com.kidozh.npuhelper.campusAddressBook;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.kidozh.npuhelper.campusBuildingLoc.DateConverter;


@Database(entities = {campusAddressBookInfoEntity.class},version = 2 ,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class campusAddressBookInfoDatabase extends RoomDatabase {


    private static final String TAG = campusAddressBookInfoDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "campusAddressBookInfoDatabase";
    private static campusAddressBookInfoDatabase sInstance;


    public static campusAddressBookInfoDatabase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        campusAddressBookInfoDatabase.class,
                        campusAddressBookInfoDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract campusAddressBookInfoDao campusAddressBookInfoDao();
}
