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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;

import com.test.compass.R;
import com.test.compass.adapter.IntroAdapter;
import com.test.compass.util.SystemUtil;

import java.util.Objects;


public class IntroScreenActivity extends AppCompatActivity {
    ImageView[] dots = null;
    int positionPage = 0;
    ViewPager viewPager;
    RelativeLayout btnStart;
    RelativeLayout btnNext;

    IntroAdapter introAdapter;
    boolean isShowStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        hideNavigation();


        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btnNext);
        btnStart = findViewById(R.id.btnStart);

        dots = new ImageView[]{findViewById(R.id.cricle_1), findViewById(R.id.cricle_2), findViewById(R.id.cricle_3)};

        introAdapter = new IntroAdapter(this);

        viewPager.setAdapter(introAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeContentInit(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));

        btnStart.setOnClickListener(v -> {
            goToHome();
        });

    }


    private void changeContentInit(int position) {
        for (int i = 0; i < 3; i++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.ic_dot_choose);
            } else dots[i].setImageResource(R.drawable.ic_dot_not);
        }
        if (isShowStart) {
            switch (position) {
                case 0:
                case 1:
                    btnNext.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.GONE);
                    break;
                case 2:
                    btnNext.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            btnNext.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void goToHome() {
        if (!hasLocationPermission()) {
            startActivity(new Intent(IntroScreenActivity.this, PermissionActivity.class));
        } else {
            startActivity(new Intent(IntroScreenActivity.this, MainActivity.class));
        }
        finish();

    }


    private boolean hasLocationPermission() {
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
    protected void onStart() {
        super.onStart();
        changeContentInit(viewPager.getCurrentItem());
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_intro, null));

        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(i -> {
            if (i == 0) {
                new Handler().postDelayed(() -> {
                    WindowInsetsControllerCompat windowInsetsController1;
                    if (Build.VERSION.SDK_INT >= 30) {
                        windowInsetsController1 = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
                    } else {
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_intro, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }

}