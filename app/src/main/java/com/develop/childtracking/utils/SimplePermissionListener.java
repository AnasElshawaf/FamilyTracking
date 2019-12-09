package com.develop.childtracking.utils;

import com.develop.childtracking.ui.Main.MainActivity;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SimplePermissionListener implements PermissionListener {

    private final MainActivity mainActivity;

    public SimplePermissionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }



    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {

        mainActivity.showPermissionGranted(response.getPermissionName());
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {

        if (response.isPermanentlyDenied()) {
            mainActivity.handlePermenentDeniedPermission(response.getPermissionName());
            return;
        }
        mainActivity.showPermissionDenied(response.getPermissionName());

    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

        mainActivity.showPermissionRational(token);
    }
}
