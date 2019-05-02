package com.kidozh.npuhelper.weatherUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.kidozh.npuhelper.R;
import es.dmoral.toasty.Toasty;

public class caiyunWeatherLoader extends AsyncTaskLoader<List<caiyunWeatherEntry>> {
    // Package manager
    private PackageManager mPm;
    private List<caiyunWeatherEntry> mApps;

    private String jsonResponse;


    public caiyunWeatherLoader(Context context) {
        super(context);
        mPm = getContext().getPackageManager();
    }

    @Override
    public List<caiyunWeatherEntry> loadInBackground() {

        return null;
    }

}
