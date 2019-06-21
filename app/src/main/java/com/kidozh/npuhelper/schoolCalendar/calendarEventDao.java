package com.kidozh.npuhelper.schoolCalendar;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface calendarEventDao {
    @Query("SELECT * FROM calendarEvent ORDER BY publishAt DESC")
    LiveData<List<calendarEventEntry>> loadAllCalendarEvent();

    @Insert
    void insertCalendarEvent(calendarEventEntry calendarEvent);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCalendarEvent(calendarEventEntry calendarEvent);

    @Query("SELECT * FROM calendarEvent WHERE id=:id")
    calendarEventEntry getCalendarEventById(int id);

}
