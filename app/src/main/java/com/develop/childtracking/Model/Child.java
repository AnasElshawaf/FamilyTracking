package com.develop.childtracking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {

    private String id;
    private String parentId;
    private String username;
    private String imageUrl;
    private String safeLang;
    private String safeLat;
    private String safeRadius;
    private String parentFcmToken;

    public Child() {
    }

    protected Child(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        username = in.readString();
        imageUrl = in.readString();
        safeLang = in.readString();
        safeLat = in.readString();
        safeRadius = in.readString();
        parentFcmToken = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSafeLang() {
        return safeLang;
    }

    public void setSafeLang(String safeLang) {
        this.safeLang = safeLang;
    }

    public String getSafeLat() {
        return safeLat;
    }

    public void setSafeLat(String safeLat) {
        this.safeLat = safeLat;
    }

    public String getSafeRadius() {
        return safeRadius;
    }

    public void setSafeRadius(String safeRadius) {
        this.safeRadius = safeRadius;
    }

    public String getParentFcmToken() {
        return parentFcmToken;
    }

    public void setParentFcmToken(String parentFcmToken) {
        this.parentFcmToken = parentFcmToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(parentId);
        parcel.writeString(username);
        parcel.writeString(imageUrl);
        parcel.writeString(safeLang);
        parcel.writeString(safeLat);
        parcel.writeString(safeRadius);
        parcel.writeString(parentFcmToken);
    }
}

