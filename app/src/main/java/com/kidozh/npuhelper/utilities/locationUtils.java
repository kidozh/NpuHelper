package com.kidozh.npuhelper.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.xianCityBus.suggestCityLocation;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class locationUtils {
    private final static String TAG = locationUtils.class.getSimpleName();

    private double locLatitude = 34.24626;
    private double locLongitude = 108.91148;
    public static String GEO_LOCATION = "108.91148,34.24626";

    public static Location getLastKnownLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;
        for (String provider : providers) {

            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
