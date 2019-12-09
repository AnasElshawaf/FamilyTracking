package com.develop.childtracking.ui.ChildMap;

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
import com.develop.childtracking.Model.SafeArea;
import com.develop.childtracking.utils.FcmNotifier;
import com.develop.childtracking.services.MyLocationService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChildMapViewModel extends ViewModel {

    LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    MutableLiveData<SafeArea> mutableLiveData = new MutableLiveData<>();
    MutableLiveData<String> mutableLiveUserArea = new MutableLiveData<>();
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");

    private Child child;
    private int radius;
    private Double safeLat, safeLang;
    Context context;

    public ChildMapViewModel() {
    }

    public void displaySafeArea() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    child = dataSnapshot1.getValue(Child.class);
                    if (child.getId() != null) {
                        if (child.getId().equals(firebaseUser.getUid())) {
                            safeLat = Double.valueOf(child.getSafeLat());
                            safeLang = Double.valueOf(child.getSafeLang());
                            radius = Integer.valueOf(child.getSafeRadius());
                            SafeArea safeArea = new SafeArea(safeLat, safeLang, radius);
                            mutableLiveData.setValue(safeArea);
//

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    public void updateLocation(Context mContext) {
        context = mContext;
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UODATE_CHILD);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10f);
    }

    public void saveLastLocation(Location location) {
        String userId = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs").child(userId);
        final GeoFire geoFire = new GeoFire(reference);
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                isLocationInArea(location.getLatitude(), location.getLongitude(),
                        safeLat, safeLang, radius);
            }
        });
    }

    private void isLocationInArea(double myLat, double myLang, double safeLat, double safeLang, int radius) {
        float[] distance = new float[2];

        Location.distanceBetween(myLat, myLang, safeLat, safeLang, distance);

        if (distance[0] > radius) {
            mutableLiveUserArea.setValue("Outside");
            FcmNotifier.sendNotification(child.getUsername() + " Outside Safe Area ", "Warning", child.getParentFcmToken());

        } else if (distance[0] < radius) {
            mutableLiveUserArea.setValue("Inside");
        }
    }


}
