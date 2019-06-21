package com.kidozh.npuhelper.schoolCalendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class calendarEventViewModel extends AndroidViewModel {
    private LiveData<List<calendarEventEntry>> calendarEventTask;

    public calendarEventViewModel(@NonNull Application application){
        super(application);
        calendarEventDatabase database = calendarEventDatabase.getsInstance(this.getApplication());
        calendarEventTask = database.calendarEventDao().loadAllCalendarEvent();
    }

    public LiveData<List<calendarEventEntry>> getCalendarEventTask(){
        return calendarEventTask;
    }
}
