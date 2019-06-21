package com.kidozh.npuhelper.schoolCalendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class addCalendarEventViewModel extends ViewModel {
    private LiveData<List<calendarEventEntry>> calendarEventListLiveData;

    public addCalendarEventViewModel(calendarEventDatabase mDb) {
        calendarEventListLiveData = mDb.calendarEventDao().loadAllCalendarEvent();
    }

    public LiveData<List<calendarEventEntry>> getCalendarEventListLiveData() {
        return calendarEventListLiveData;
    }

}
