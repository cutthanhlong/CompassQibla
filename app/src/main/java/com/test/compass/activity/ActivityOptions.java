package com.test.compass.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.test.compass.Fragment.FragmentSetting;
import com.test.compass.R;
import com.test.compass.adapter.CompassAdapter;
import com.test.compass.databinding.FragmentOptionsBinding;
import com.test.compass.dialog.DialogRating;
import com.test.compass.util.SharePrefUtils;

import java.util.Objects;

public class ActivityOptions extends AppCompatActivity {
    FragmentOptionsBinding binding;

    RelativeLayout rl_read, rl_camera, rl_gps;
    ImageView img_read, img_camera, img_gps;
    private Dialog dialog;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;

    ReviewManager manager;
    ReviewInfo reviewInfo;
    private int count_rate = 0;
    private int locationPermissionDeniedCount = 0;
    private int locationPermissionDeniedCountCamera = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideNavigation();

        binding.toolbar.tvToolbar.setText(getString(R.string.categories));
        binding.toolbar.icStart.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        CompassAdapter compassAdapter = new CompassAdapter(ActivityOptions.this, position -> {

            if (hasLocationPermission()) {
                onNextActivity(position);
            } else {
                showDialogPermission();
            }
        });
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (dialog != null && dialog.isShowing()) {
                    img_camera.setImageResource(R.drawable.switch_on);
                }
                locationPermissionDeniedCountCamera = 0;
            } else {
                if (dialog.isShowing()) {
                    img_camera.setImageResource(R.drawable.switch_off);
                }
                locationPermissionDeniedCountCamera++;
            }
        });

        requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (dialog != null && dialog.isShowing()) {
                    img_read.setImageResource(R.drawable.switch_on);
                }
                locationPermissionDeniedCount = 0;
            } else {
                locationPermissionDeniedCount++;
                img_read.setImageResource(R.drawable.switch_off);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ActivityOptions.this, 2, RecyclerView.VERTICAL, false);
        binding.rcyCompass.setLayoutManager(gridLayoutManager);
        binding.rcyCompass.setHasFixedSize(true);
        binding.rcyCompass.setAdapter(compassAdapter);
    }

    public void showDialogPermission() {
        CardView ok;
        dialog = new Dialog(ActivityOptions.this);
        dialog.setContentView(R.layout.dialog_poup_permission);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        ok = dialog.findViewById(R.id.ok);
        rl_read = dialog.findViewById(R.id.rl_read);
        rl_camera = dialog.findViewById(R.id.rl_camera);
        rl_gps = dialog.findViewById(R.id.rl_gps);
        img_read = dialog.findViewById(R.id.img_read);
        img_camera = dialog.findViewById(R.id.img_camera);
        img_gps = dialog.findViewById(R.id.img_gps);
        if (checkLocationFind()) {
            img_read.setImageResource(R.drawable.switch_on);
        }
        if (checkCameraPermission()) {
            img_camera.setImageResource(R.drawable.switch_on);
        }
        if (checkGPSActivation()) {
            img_gps.setImageResource(R.drawable.switch_on);
        } else {
            img_gps.setImageResource(R.drawable.switch_off);
        }
        rl_read.setOnClickListener(view -> {
            if (!checkLocationFind()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (locationPermissionDeniedCount >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        openAppSettings();
                    } else {
                        locationPermissionDeniedCount++;
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }
            }
        });
        rl_camera.setOnClickListener(view -> {
            try {
                if (!checkCameraPermission()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (locationPermissionDeniedCountCamera >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            openAppSettings();
                        } else {
                            locationPermissionDeniedCountCamera++;
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);

                        }
                    }
                }
            } catch (Exception ignored) {

            }
        });
        rl_gps.setOnClickListener(view -> {
            if (!checkGPSActivation()) {
                try {
                    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                } catch (ActivityNotFoundException ignored) {
                }
            }
        });
        ok.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void onNextActivity(int position) {
        count_rate++;
        Intent intent = new Intent(ActivityOptions.this, ListCompassDetail.class);
        intent.putExtra("position", position);
        intent.putExtra("count_back", count_rate);
        startActivityForResult(intent, 1);
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private boolean hasLocationPermission() {
        boolean cameraPermission = checkCameraPermission();
        boolean gpsPermission = checkGPSActivation();
        boolean hasCoarsePermission = checkLocationFind();
        return hasCoarsePermission && cameraPermission && gpsPermission;
    }

    private boolean checkLocationFind() {
        if (ActivityCompat.checkSelfPermission(ActivityOptions.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityOptions.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(ActivityOptions.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkGPSActivation() {
        Object systemService = getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null && dialog.isShowing()) {
            if (checkLocationFind()) {
                img_read.setImageResource(R.drawable.switch_on);
            }
            if (checkCameraPermission()) {
                img_camera.setImageResource(R.drawable.switch_on);
            }
            if (checkGPSActivation()) {
                img_gps.setImageResource(R.drawable.switch_on);
            } else {
                img_gps.setImageResource(R.drawable.switch_off);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            rateApp(0);
        }
    }

    private void rateApp(int type) {

        DialogRating ratingDialog = new DialogRating(ActivityOptions.this);
        ratingDialog.init(ActivityOptions.this, new DialogRating.OnPress() {
            @Override
            public void send() {
                ratingDialog.dismiss();
                String uriText = "mailto:" + SharePrefUtils.email + "?subject=" + "Review for " + SharePrefUtils.subject + "&body=" + SharePrefUtils.subject + "\nRate : " + ratingDialog.getRating() + "\nContent: ";
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    if (type == 1) {
                        ActivityOptions.this.finishAffinity();
                    }
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.Send_Email)));
                    SharePrefUtils.forceRated(ActivityOptions.this);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ActivityOptions.this, getString(R.string.There_is_no), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void rating() {
                manager = ReviewManagerFactory.create(ActivityOptions.this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<ReviewInfo> task) {

                        if (task.isSuccessful()) {
                            try {
                                FragmentSetting.rl_rate.setVisibility(View.GONE);
                                FragmentSetting.view_rate.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            reviewInfo = task.getResult();
                            Log.e("ReviewInfo", "" + reviewInfo);
                            Task<Void> flow = manager.launchReviewFlow(ActivityOptions.this, reviewInfo);
                            flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    SharePrefUtils.forceRated(ActivityOptions.this);
                                    ratingDialog.dismiss();
                                    if (type == 1) {
                                        ActivityOptions.this.finishAffinity();
                                    }
                                }
                            });
                        } else {
                            ratingDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void later() {
                ratingDialog.dismiss();
            }

        });

        ratingDialog.show();

    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.fragment_options, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.fragment_options, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }
}
