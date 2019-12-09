package com.develop.childtracking.utils;

import com.develop.childtracking.ui.Main.MainActivity;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class SimpleMultiplePermissionListener implements MultiplePermissionsListener {

    private final MainActivity mainActivity;

    public SimpleMultiplePermissionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {

        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            mainActivity.showPermissionGranted(response.getPermissionName());
        }
        for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
            mainActivity.showPermissionDenied(response.getPermissionName());
        }

    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

        mainActivity.showPermissionRational(token);
    }
}
