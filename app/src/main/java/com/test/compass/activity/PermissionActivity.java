package com.test.compass.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.test.compass.R;
import com.test.compass.databinding.ActivityPermissionBinding;
import com.test.compass.util.SharePrefUtils;
import com.test.compass.util.SystemUtil;

import java.util.Objects;

public class PermissionActivity extends AppCompatActivity {

    ActivityPermissionBinding binding;
    private int REQUEST_CODE_LOCATION_PERMISSION = 125;
    private int REQUEST_CODE_CAMERA_PERMISSION = 126;
    private int locationPermissionDeniedCount = 0;
    private int locationPermissionDeniedCountCamera = 0;
    private int locationPermissionDeniedCountNoti = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideNavigation();

        binding.tvContinue.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        binding.tvNotification.setText(getString(R.string.permission_is_checking));
        binding.tvNotification.setTextColor(getResources().getColor(R.color.color_FFA6A6));


        binding.toolbar.icStart.setVisibility(View.GONE);
        binding.toolbar.tvToolbar.setText(getString(R.string.permission));
        if (checkLocationFind()) {
            binding.imgRead.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgRead.setImageResource(R.drawable.switch_off);
        }
        if (checkCameraPermission()) {
            binding.imgSelect.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgSelect.setImageResource(R.drawable.switch_off);
        }
        if (checkGPSActivation()) {
            binding.imgGps.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgGps.setImageResource(R.drawable.switch_off);
        }
        binding.rlRead.setOnClickListener(view -> {
            if (!checkLocationFind()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (locationPermissionDeniedCount >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        openAppSettings();
                    } else {
                        locationPermissionDeniedCount++;
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE_LOCATION_PERMISSION);
                    }
                }
            }
        });
        binding.rlCamera.setOnClickListener(view -> {
            if (!checkCameraPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (locationPermissionDeniedCountCamera >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        openAppSettings();
                    } else {
                        locationPermissionDeniedCountCamera++;
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
                    }
                }
            }
        });
        binding.rlGps.setOnClickListener(view -> {
            if (!checkGPSActivation()) {
                try {
                    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                } catch (ActivityNotFoundException unused) {
                }
            }
        });

        binding.rlNotification.setOnClickListener(view -> {
            if (!checkNotification()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (locationPermissionDeniedCountNoti >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        openAppSettings();
                    } else {
                        locationPermissionDeniedCountNoti++;
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 111);
                    }
                }

            }
        });

        binding.tvContinue.setOnClickListener(view -> {
            SharePrefUtils.increaseCountFirstHelp(PermissionActivity.this);
            startActivity(new Intent(PermissionActivity.this, MainActivity.class));
            finish();
        });
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public boolean checkGPSActivation() {
        Object systemService = getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    private boolean hasLocationPermission() {
        boolean cameraPermission = checkCameraPermission();
        boolean hasCoarsePermission = PermissionChecker.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == PermissionChecker.PERMISSION_GRANTED;
        boolean hasFinePermission = PermissionChecker.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PermissionChecker.PERMISSION_GRANTED;
        boolean notification = checkNotification();
        return hasCoarsePermission && hasFinePermission && cameraPermission && notification;
    }

    private boolean checkLocationFind() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void handleOverlayPermissionResult(boolean granted) {
        if (granted) {
            binding.imgRead.setImageResource(R.drawable.switch_on);

        } else {
            binding.imgRead.setImageResource(R.drawable.switch_off);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationFind()) {
            binding.imgRead.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgRead.setImageResource(R.drawable.switch_off);
        }
        if (checkCameraPermission()) {
            binding.imgSelect.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgSelect.setImageResource(R.drawable.switch_off);
        }
        if (checkGPSActivation()) {
            binding.imgGps.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgGps.setImageResource(R.drawable.switch_off);
        }

        if (checkNotification()) {
            binding.imgNotification.setImageResource(R.drawable.switch_on);
        } else {
            binding.imgNotification.setImageResource(R.drawable.switch_off);
        }
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_permission, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_permission, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.imgSelect.setImageResource(R.drawable.switch_on);
                locationPermissionDeniedCountCamera = 0;
            } else {
                binding.imgSelect.setImageResource(R.drawable.switch_off);
                locationPermissionDeniedCountCamera++;
            }
        }
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleOverlayPermissionResult(true);
                locationPermissionDeniedCount = 0;
            } else {
                handleOverlayPermissionResult(false);
                locationPermissionDeniedCount++;
            }
        }
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.imgNotification.setImageResource(R.drawable.switch_on);
                locationPermissionDeniedCountNoti = 0;
            } else {
                binding.imgNotification.setImageResource(R.drawable.switch_off);
                locationPermissionDeniedCountNoti++;
            }
        }
        if (hasLocationPermission()) {
            binding.tvNotification.setText(getString(R.string.permission_check_successful_sufficient_permissions));
            binding.tvNotification.setTextColor(getResources().getColor(R.color.color_16f31f));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private boolean checkNotification() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

}