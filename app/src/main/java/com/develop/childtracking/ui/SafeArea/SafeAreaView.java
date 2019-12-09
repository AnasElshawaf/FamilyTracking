package com.develop.childtracking.ui.SafeArea;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.fragment.app.FragmentActivity;

import com.develop.childtracking.Model.SafeArea;
import com.develop.childtracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SafeAreaView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng pickUpLocation;
    private SeekBar sbProgress;
    private Button btSetArea;
    private int safeRadius = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_area_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //default lcation is mansoura
        pickUpLocation = new LatLng(31.037933, 31.381523);

        sbProgress = findViewById(R.id.sb_progress);
        btSetArea = findViewById(R.id.bt_set_area);


        btSetArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("SAFE_AREA", new SafeArea(pickUpLocation.latitude, pickUpLocation.longitude, safeRadius));
                setResult(2, intent);
                finish();
            }
        });

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                safeRadius = i;
                drawSafeArea(pickUpLocation, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //default location in mansoura
        mMap.addMarker(new MarkerOptions().position(pickUpLocation).title("Marker in Mansoura"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickUpLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        pickUpLocation(mMap);
    }

    private void pickUpLocation(final GoogleMap mMap) {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                pickUpLocation = latLng;
                drawSafeArea(pickUpLocation, safeRadius);
            }
        });


    }

    private void drawSafeArea(LatLng latLng, int radius) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (latLng != null) {
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(markerOptions);

            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.GRAY)
                    .strokeWidth(5.0f)
            );
        }

    }

}
