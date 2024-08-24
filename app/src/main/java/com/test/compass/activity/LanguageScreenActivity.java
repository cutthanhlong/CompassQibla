package com.test.compass.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.compass.R;
import com.test.compass.adapter.LanguageAdapter;
import com.test.compass.language.Model.LanguageModel;
import com.test.compass.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LanguageScreenActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView btn_back;
    List<LanguageModel> listLanguage;
    String codeLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_language1);
        hideNavigation();

        recyclerView = findViewById(R.id.recyclerView);
        btn_back = findViewById(R.id.back);
        codeLang = Locale.getDefault().getLanguage();
        initData();
        btn_back.setOnClickListener(v -> {
            onBackPressed();
            finish();

        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageAdapter languageAdapter = new LanguageAdapter(listLanguage, code -> codeLang = code, this);


        findViewById(R.id.done).setOnClickListener(view -> {
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            back();
        });

        languageAdapter.setCheck(SystemUtil.getPreLanguage(getBaseContext()));


        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(languageAdapter);
    }

    private void initData() {
        listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("English", "en"));
        listLanguage.add(new LanguageModel("China", "zh"));
        listLanguage.add(new LanguageModel("French", "fr"));
        listLanguage.add(new LanguageModel("German", "de"));
        listLanguage.add(new LanguageModel("Hindi", "hi"));
        listLanguage.add(new LanguageModel("Indonesia", "in"));
        listLanguage.add(new LanguageModel("Malay", "ms"));
        listLanguage.add(new LanguageModel("Portuguese", "pt"));
        listLanguage.add(new LanguageModel("Turkey", "tr"));
        listLanguage.add(new LanguageModel("Spanish", "es"));
        listLanguage.add(new LanguageModel("Saudi Arabia", "ar"));

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void back() {
        startActivity(new Intent(LanguageScreenActivity.this, MainActivity.class));
        finishAffinity();
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_language1, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_language1, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }
}
