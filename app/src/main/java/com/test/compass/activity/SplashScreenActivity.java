package com.test.compass.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.test.compass.R;
import com.test.compass.util.SharePrefUtils;
import com.test.compass.util.SystemUtil;

import java.util.Objects;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashh);
        hideNavigation();
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        SharePrefUtils.increaseCountOpenApp(SplashScreenActivity.this);

        new Handler().postDelayed(() -> {
            if (!hasLocationPermission()) {
                startActivity(new Intent(SplashScreenActivity.this, PermissionActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
        }, 3000);
    }

    private boolean   hasLocationPermission() {
        boolean cameraPermission = checkCameraPermission();
        boolean gpsPermission = checkGPSActivation();
        boolean hasCoarsePermission = PermissionChecker.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == PermissionChecker.PERMISSION_GRANTED;
        boolean hasFinePermission = PermissionChecker.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PermissionChecker.PERMISSION_GRANTED;
        return hasCoarsePermission && hasFinePermission && cameraPermission && gpsPermission;
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkGPSActivation() {
        Object systemService = getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    @Override
    public void onBackPressed() {
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_splashh, null));

        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        );
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(i -> {
            if (i == 0) {
                new Handler().postDelayed(() -> {
                    WindowInsetsControllerCompat windowInsetsController1;
                    if (Build.VERSION.SDK_INT >= 30) {
                        windowInsetsController1 = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
                    } else {
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_splashh, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }
}
