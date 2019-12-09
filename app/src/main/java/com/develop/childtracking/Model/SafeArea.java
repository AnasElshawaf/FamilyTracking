package com.develop.childtracking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SafeArea implements Parcelable {

    private double lat;
    private double lang;
    private int radius;

    public SafeArea(double lat, double lang, int radius) {
        this.lat = lat;
        this.lang = lang;
        this.radius = radius;
    }

    protected SafeArea(Parcel in) {
        lat = in.readDouble();
        lang = in.readDouble();
        radius = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lang);
        dest.writeInt(radius);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SafeArea> CREATOR = new Creator<SafeArea>() {
        @Override
        public SafeArea createFromParcel(Parcel in) {
            return new SafeArea(in);
        }

        @Override
        public SafeArea[] newArray(int size) {
            return new SafeArea[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
