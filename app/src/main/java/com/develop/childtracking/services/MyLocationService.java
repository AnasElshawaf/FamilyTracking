package com.develop.childtracking.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.develop.childtracking.ui.ChildMapActivity;
import com.develop.childtracking.ui.ParentMapActivity;
import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UODATE_PARENT = "com.develop.childtracking.services.UPDATE_LOCATION_PARENT";
    public static final String ACTION_PROCESS_UODATE_CHILD = "com.develop.childtracking.services.UPDATE_LOCATION_CHILD";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            LocationResult result = LocationResult.extractResult(intent);

            switch (action) {
                case ACTION_PROCESS_UODATE_PARENT:
                    if (result != null) {
                        Location location = result.getLastLocation();
                        try {
                            ParentMapActivity.getInstance().updateLocation(location);
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("error", e.getMessage().toString());
                        }
                    }
                    break;
                case ACTION_PROCESS_UODATE_CHILD:
                    if (result != null) {
                        Location location = result.getLastLocation();
                        try {
                            ChildMapActivity.getInstance().updateLocation(location);
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("error", e.getMessage().toString());
                        }
                    }
                    break;

            }

        }
    }
}
