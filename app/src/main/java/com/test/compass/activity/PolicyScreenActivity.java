package com.test.compass.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.test.compass.R;
import com.test.compass.databinding.ActivityPolicyBinding;

import java.util.Objects;

public class PolicyScreenActivity extends AppCompatActivity {

    ActivityPolicyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideNavigation();


        binding.icStart.setOnClickListener(view -> onBackPressed());

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/asy039---compass.appspot.com/o/Privacy%20Policy%20.html?alt=media&token=ff1138f3-7b11-4f1f-922d-3844e32a79b1&_gl=1*la78q1*_ga*MTgyMzg0OTkxMS4xNjc5MDUyMjEy*_ga_CW55HF8NVT*MTY5NjMxNzIxOC4yNi4xLjE2OTYzMTc0ODAuNi4wLjA");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_compass, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_compass, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }

}
