package com.develop.childtracking.ui.ChildMap;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.Model.SafeArea;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.Main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ChildMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static ChildMapActivity instance;
    private Button btLogout;
    private Child child;

    private LatLng lastLocation;
    private SafeArea safeArea;
    private ChildMapViewModel childMapViewModel;

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

        setupViewModel();

        btLogout = findViewById(R.id.bt_logout);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChildMapActivity.this, MainActivity.class));
                finish();
            }
        });

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

        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(lastLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        childMapViewModel.saveLastLocation(location);
        childMapViewModel.mutableLiveUserArea.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Outside")) {
                    Toast.makeText(ChildMapActivity.this, "You are Outside safe area", Toast.LENGTH_LONG).show();
                } else if (s.equals("Inside")) {
                    Toast.makeText(ChildMapActivity.this, "You are Inside safe area", Toast.LENGTH_LONG).show();

                }
            }
        });
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

    private void setupViewModel() {
        childMapViewModel = ViewModelProviders.of(this).get(ChildMapViewModel.class);
        childMapViewModel.displaySafeArea();
        childMapViewModel.mutableLiveData.observe(this, new Observer<SafeArea>() {
            @Override
            public void onChanged(SafeArea s) {
                safeArea = s;
                LatLng latLng = new LatLng(s.getLat(), s.getLang());
                drawSafeArea(latLng, s.getRadius());
            }
        });

        childMapViewModel.updateLocation(this);


    }

    @Override
    protected void onStop() {
        super.onStop();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Childs").child(userId);
//        GeoFire geoFire = new GeoFire(reference);
//        geoFire.removeLocation(userId);
    }

}
