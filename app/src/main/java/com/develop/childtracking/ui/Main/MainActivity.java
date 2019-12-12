package com.develop.childtracking.ui.Main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.develop.childtracking.R;
import com.develop.childtracking.ui.ChildLogin.ChildLoginView;
import com.develop.childtracking.ui.ChildMap.ChildMapActivity;
import com.develop.childtracking.ui.ParentChilds.ParentChildsView;
import com.develop.childtracking.ui.ParentLogin.ParentLoginView;
import com.develop.childtracking.utils.App_SharedPreferences;
import com.develop.childtracking.utils.SimpleMultiplePermissionListener;
import com.develop.childtracking.utils.SimplePermissionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;

public class MainActivity extends AppCompatActivity {

    private CardView cdParent;
    private CardView cdChild;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String userType = "none";

    SimpleMultiplePermissionListener simpleMultiplePermissionListener;
    SimplePermissionListener simplePermissionListener;
    private boolean permissionStatus;
    private Boolean gpsStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialViews();


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            setupViewModel();

        }

        cdChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionStatus) {
                    if (checkGPSStatus(MainActivity.this)) {
                        if (userType.equals("child")) {
                            startActivity(new Intent(MainActivity.this, ChildMapActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(MainActivity.this, ChildLoginView.class));
                        }
                    } else {
                        buildAlertMessageNoGps();
                    }

                } else {
                    requestAllPermissions();
                }
            }
        });

        cdParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionStatus) {
                    if (checkGPSStatus(MainActivity.this)) {
                        if (userType.equals("parent")) {
                            startActivity(new Intent(MainActivity.this, ParentChildsView.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, ParentLoginView.class));
                        }
                    } else {
                        buildAlertMessageNoGps();
                    }
                } else {
                    requestAllPermissions();
                }
            }
        });

        requestAllPermissions();
    }

    private void initialViews() {
        simplePermissionListener = new SimplePermissionListener(MainActivity.this);
        simpleMultiplePermissionListener = new SimpleMultiplePermissionListener(MainActivity.this);

        cdParent = findViewById(R.id.cd_parent);
        cdChild = findViewById(R.id.cd_child);
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.checkUserType(firebaseAuth.getCurrentUser());
        mainViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userType = s;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


    private void requestAllPermissions() {
        Dexter.withActivity(this).withPermissions
                (Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(simpleMultiplePermissionListener).check();

    }

    public void showPermissionGranted(String permissionName) {
        switch (permissionName) {

            case Manifest.permission.ACCESS_FINE_LOCATION:
                permissionStatus = true;
                break;

        }
    }

    public void showPermissionDenied(String permissionName) {
    }

    public void showPermissionRational(final PermissionToken token) {


        new AlertDialog.Builder(this).setTitle("We need this permission").
                setMessage("Please allow this permission for do some magic")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        token.continuePermissionRequest();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        token.cancelPermissionRequest();
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                }).show();

    }

    public void handlePermenentDeniedPermission(String permissionName) {

        switch (permissionName) {

            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                Toast.makeText(this, "Permission Denied permenent", Toast.LENGTH_LONG).show();
                break;

        }
        new AlertDialog.Builder(this).setTitle("We need this permission").
                setMessage("Please allow this permission from app settings")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }

    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Check GPS Status true/false
    public static boolean checkGPSStatus(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return (statusOfGPS);
    }
}
