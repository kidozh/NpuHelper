package com.kidozh.npuhelper.weatherUtils;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

public class caiyunWeatherViewModel extends AndroidViewModel {

    private LiveData<caiyunWeatherEntry> weatherTask;

    public caiyunWeatherViewModel(@NonNull Application application) {
        super(application);
        caiyunWeatherDatabase database = caiyunWeatherDatabase.getsInstance(this.getApplication());
        // cache objects
        weatherTask = database.caiyunWeatherDao().getLastWeatherById();
    }

    public LiveData<caiyunWeatherEntry> getWeatherTask() {
        return weatherTask;
    }
}
