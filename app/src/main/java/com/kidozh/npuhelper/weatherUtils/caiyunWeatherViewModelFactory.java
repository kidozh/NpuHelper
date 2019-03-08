package com.kidozh.npuhelper.weatherUtils;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
