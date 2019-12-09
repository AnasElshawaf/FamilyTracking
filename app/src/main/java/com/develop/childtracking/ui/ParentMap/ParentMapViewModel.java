package com.develop.childtracking.ui.ParentMap;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.services.MyLocationService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ParentMapViewModel extends ViewModel {

    MutableLiveData<LatLng> mutableLiveLatLang = new MutableLiveData<>();
    MutableLiveData<Float> mutableLiveDistance = new MutableLiveData<>();

    private DatabaseReference childLocationRef;
    private ValueEventListener eventListener;

    LocationRequest mLocationRequest;
    public FusedLocationProviderClient fusedLocationProviderClient;
    Context context;
    public Location parentLocation;
    public Child child;

    public void updateLocation(Context mContext) {
        context=mContext;
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UODATE_PARENT);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10f);
    }

    public void saveLocationFcm(Location location, Child mChild) {
        parentLocation = location;
        child = mChild;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Parents").child(userId);
        GeoFire geoFire = new GeoFire(reference);
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                getChildLocation();
            }
        });
    }

    private void getChildLocation() {
        childLocationRef = FirebaseDatabase.getInstance().getReference("Users").child("Childs").child(child.getId()).child(child.getId()).child("l");
        eventListener = childLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng childLatLng = new LatLng(locationLat, locationLng);

                    Location loc1 = new Location("");
                    loc1.setLatitude(parentLocation.getLatitude());
                    loc1.setLongitude(parentLocation.getLongitude());

                    Location loc2 = new Location("");
                    loc2.setLatitude(childLatLng.latitude);
                    loc2.setLongitude(childLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    mutableLiveDistance.setValue(distance);
                    mutableLiveLatLang.setValue(childLatLng);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

}
