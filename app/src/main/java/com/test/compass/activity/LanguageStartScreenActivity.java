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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.compass.R;
import com.test.compass.adapter.LanguageStartAdapter;
import com.test.compass.language.Interface.IClickItemLanguage;
import com.test.compass.language.Model.LanguageModel;
import com.test.compass.util.SharePrefUtils;
import com.test.compass.util.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LanguageStartScreenActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView btn_done;
    List<LanguageModel> listLanguage;
    String codeLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_language_start);
        hideNavigation();

        recyclerView = findViewById(R.id.recyclerView);
        btn_done = findViewById(R.id.done);
        codeLang = Locale.getDefault().getLanguage();

        initData();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageStartAdapter languageAdapter = new LanguageStartAdapter(listLanguage, new IClickItemLanguage() {
            @Override
            public void onClickItemLanguage(String code) {
                codeLang = code;
            }
        }, this);


        // set checked default lang local
        String codeLangDefault = Locale.getDefault().getLanguage();
        String[] langDefault = {"hi", "zh", "es", "fr", "pt", "in", "de"};
        if (!Arrays.asList(langDefault).contains(codeLangDefault)) codeLang = "en";

        languageAdapter.setCheck(codeLang);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(languageAdapter);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtil.saveLocale(getBaseContext(), codeLang);
                if (SharePrefUtils.getCountOpenApp(LanguageStartScreenActivity.this) == 1) {
                    startActivity(new Intent(LanguageStartScreenActivity.this, IntroScreenActivity.class));
                } else {
                    if (!hasLocationPermission()) {
                        startActivity(new Intent(LanguageStartScreenActivity.this, PermissionActivity.class));
                    } else {
                        startActivity(new Intent(LanguageStartScreenActivity.this, MainActivity.class));
                    }
                }

                finish();
            }
        });
    }

    private void initData() {
        listLanguage = new ArrayList<>();
        String lang = Locale.getDefault().getLanguage();
        listLanguage.add(new LanguageModel("China", "zh"));
        listLanguage.add(new LanguageModel("English", "en"));
        listLanguage.add(new LanguageModel("French", "fr"));
        listLanguage.add(new LanguageModel("German", "de"));
        listLanguage.add(new LanguageModel("Hindi", "hi"));
        listLanguage.add(new LanguageModel("Indonesia", "in"));
        listLanguage.add(new LanguageModel("Malay", "ms"));
        listLanguage.add(new LanguageModel("Portuguese", "pt"));
        listLanguage.add(new LanguageModel("Turkey", "tr"));
        listLanguage.add(new LanguageModel("Spanish", "es"));
        listLanguage.add(new LanguageModel("Saudi Arabia", "ar"));


        for (int i = 0; i < listLanguage.size(); i++) {
            if (listLanguage.get(i).getCode().equals(lang)) {
                listLanguage.add(0, listLanguage.get(i));
                listLanguage.remove(i + 1);
            }
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_language_start, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_language_start, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }
}
