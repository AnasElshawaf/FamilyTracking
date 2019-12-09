package com.develop.childtracking.ui.ParentMap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.Main.MainActivity;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ParentMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;

    Child child;
    private LatLng parentLocation;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private Button btLogout;
    private ParentMapViewModel viewModel;
    private static ParentMapActivity instance;

    public static ParentMapActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        instance = this;

        initialViews();

        setupViewModel();

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ParentMapActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ParentMapViewModel.class);
        viewModel.updateLocation(ParentMapActivity.this);

    }

    private void initialViews() {
        btLogout = findViewById(R.id.bt_logout);
        child = getIntent().getParcelableExtra("child");
        polylines = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(31.037933, 31.381523);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }


    public void updateLocation(Location location) {
        Log.e("lastLocation", location.getLatitude() + " / " + location.getLongitude());

        parentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(parentLocation).title("   "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(parentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        getDataForRoute(location);

    }

    private void getDataForRoute(Location location) {
        viewModel.saveLocationFcm(location, child);
        viewModel.mutableLiveDistance.observe(ParentMapActivity.this, new Observer<Float>() {
            @Override
            public void onChanged(Float distance) {
                if (distance < 100) {
                    Toast.makeText(ParentMapActivity.this, child.getUsername() + " Near You", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ParentMapActivity.this, child.getUsername() + " Far from you : " + String.valueOf(distance / 1000) + " km", Toast.LENGTH_LONG).show();
                }
            }
        });
        viewModel.mutableLiveLatLang.observe(ParentMapActivity.this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng childLatLng) {
                Marker mChildMarker = mMap.addMarker(new MarkerOptions().position(childLatLng).title(child.getUsername()));
                getRouteToMarker(childLatLng);
            }
        });
    }

    private void getRouteToMarker(LatLng childLocation) {
        if (childLocation != null && parentLocation != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(parentLocation, childLocation)
                    .key("AIzaSyC2uAoVMEsYp2MkrW15NCCyIsS1Oe-Rycw")
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(ParentMapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ParentMapActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
//            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[0]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(ParentMapActivity.this, "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }
}

