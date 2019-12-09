package com.develop.childtracking.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.develop.childtracking.FcmNotifier;
import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.develop.childtracking.services.MyLocationService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChildMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static ChildMapActivity instance;
    private Button btLogout;
    private Child child;
    private int radius;
    private Double safeLat, safeLang;
    private LatLng lastLocation;

    public static ChildMapActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        instance = this;

        displaySafeArea();

        btLogout = findViewById(R.id.bt_logout);

        updateLocation();

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChildMapActivity.this, MainActivity.class));
                finish();
            }
        });


    }

    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(ChildMapActivity.this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UODATE_CHILD);
        return PendingIntent.getBroadcast(ChildMapActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Mansoura and move the camera
        LatLng mLatLang = new LatLng(31.037933, 31.381523);
        mMap.addMarker(new MarkerOptions().position(mLatLang).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLang));

    }


    public void updateLocation(final Location location) {
        Log.e("lastLocation", location.getLatitude() + " / " + location.getLongitude());

        // Add a marker in CildLocation and move the camera
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(lastLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs").child(userId);
        final GeoFire geoFire = new GeoFire(reference);
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                isLocationInArea(lastLocation.latitude, lastLocation.longitude,
                        safeLat, safeLang, radius);
            }
        });
    }

//    private void checkUserLocation(LatLng latLng, int radius) {
//        Log.e("latlang", latLng.latitude + "//" + latLng.longitude + "//" + radius);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs").child(FirebaseAuth.getInstance().getUid());
//        final GeoFire geoFire = new GeoFire(reference);
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                Log.e("checkUserLocation", "onKeyEntered");
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//                Log.e("checkUserLocation", "onKeyExited");
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                Log.e("checkUserLocation", "onKeyMoved");
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                Log.e("checkUserLocation", "onGeoQueryReady");
//
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//                Log.e("checkUserLocation", error.getMessage());
//
//            }
//        });
//
//    }

    @Override
    protected void onStop() {
        super.onStop();

//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Childs").child(userId);
//        GeoFire geoFire = new GeoFire(reference);
//        geoFire.removeLocation(userId);
    }

    private void drawSafeArea(LatLng latLng, int radius) {
        if (latLng != null) {
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.GRAY)
                    .strokeWidth(5.0f)
            );
        }

    }

    private void displaySafeArea() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    child = dataSnapshot1.getValue(Child.class);
                    if (child.getId().equals(firebaseUser.getUid())) {
                        safeLat = Double.valueOf(child.getSafeLat());
                        safeLang = Double.valueOf(child.getSafeLang());
                        radius = Integer.valueOf(child.getSafeRadius());
                        LatLng latLng = new LatLng(safeLat, safeLang);
                        drawSafeArea(latLng, radius);
//                        checkUserLocation(latLng, radius);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void isLocationInArea(double myLat, double myLang, double safeLat, double safeLang, int radius) {
        float[] distance = new float[2];

        Location.distanceBetween(myLat, myLang, safeLat, safeLang, distance);

        if (distance[0] > radius) {
            Toast.makeText(ChildMapActivity.this, "Outside", Toast.LENGTH_LONG).show();
            FcmNotifier.sendNotification("aaaaaaa","aaaaaa",child.getParentFcmToken());

        } else if (distance[0] < radius) {
            Toast.makeText(ChildMapActivity.this, "Inside", Toast.LENGTH_LONG).show();
        }
    }


}
