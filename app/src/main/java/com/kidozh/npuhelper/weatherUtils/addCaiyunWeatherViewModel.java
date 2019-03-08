package com.kidozh.npuhelper.weatherUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class addCaiyunWeatherViewModel extends ViewModel {
    private LiveData<caiyunWeatherEntry> caiyunWeatherEntryLiveData;

    public addCaiyunWeatherViewModel(caiyunWeatherDatabase mDb) {
        caiyunWeatherEntryLiveData = mDb.caiyunWeatherDao().getLastWeatherById();
    }

    public LiveData<caiyunWeatherEntry> getCaiyunWeatherEntryLiveData() {
        return caiyunWeatherEntryLiveData;
    }
}
