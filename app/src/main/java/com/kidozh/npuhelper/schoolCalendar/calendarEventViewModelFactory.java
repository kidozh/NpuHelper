package com.kidozh.npuhelper.schoolCalendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class calendarEventViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final calendarEventDatabase mDb;

    public calendarEventViewModelFactory(calendarEventDatabase database){ mDb = database; }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new addCalendarEventViewModel(mDb);
    }

}
