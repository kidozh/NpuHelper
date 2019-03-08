package com.kidozh.npuhelper.xianCityBus;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.help.Tip;
import com.amap.api.services.core.LatLonPoint;

import java.io.Serializable;

public class suggestCityLocation implements Parcelable {
    private static final long serialVersionUID=1L;
    public String name;
    public String description;
    //public LatLonPoint dPoint;
    public Tip locationTip;
    public suggestCityLocation(String name,String  description, Tip tip){
        this.description= description;
        this.name = name;
        //this.dPoint = dPoint;
        this.locationTip = tip;
    }

    protected suggestCityLocation(Parcel in) {
        name = in.readString();
        description = in.readString();
        //dPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
        locationTip = in.readParcelable(Tip.class.getClassLoader());
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        description = in.readString();
        //dPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
        locationTip = in.readParcelable(Tip.class.getClassLoader());
    }

    public static final Creator<suggestCityLocation> CREATOR = new Creator<suggestCityLocation>() {
        @Override
        public suggestCityLocation createFromParcel(Parcel in) {
            return new suggestCityLocation(in);
        }

        @Override
        public suggestCityLocation[] newArray(int size) {
            return new suggestCityLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        //dest.writeString(dPoint.toString());
        dest.writeParcelable(locationTip,flags);
    }
}