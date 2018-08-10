package com.kidozh.npuhelper.weatherUtils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class caiyunWeatherViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final caiyunWeatherDatabase mDb;

    public caiyunWeatherViewModelFactory(caiyunWeatherDatabase database){
        mDb = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new addCaiyunWeatherViewModel(mDb);
    }
}
