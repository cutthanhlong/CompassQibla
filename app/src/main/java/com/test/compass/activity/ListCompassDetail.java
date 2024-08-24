package com.test.compass.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.test.compass.Fragment.FragmentAviation;
import com.test.compass.Fragment.FragmentCamera;
import com.test.compass.Fragment.FragmentDigital;
import com.test.compass.Fragment.FragmentMap;
import com.test.compass.Fragment.FragmentQibla;
import com.test.compass.Fragment.FragmentSatellite;
import com.test.compass.Fragment.FragmentStand;
import com.test.compass.Fragment.FragmentVintage;
import com.test.compass.R;
import com.test.compass.adapter.CompassAdapterViewPager;
import com.test.compass.databinding.ActivityListCompassDetailBinding;
import com.test.compass.util.SharePrefUtils;
import com.test.compass.util.SystemUtil;

import java.util.Objects;


public class ListCompassDetail extends AppCompatActivity {
    ActivityListCompassDetailBinding binding;
    CompassAdapterViewPager compassAdapterViewPager;

    ViewPager2 viewPager2;
    private int position_select = 0;
    private FragmentTransaction transaction;
    public static int select = 0;
    private int count_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityListCompassDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideNavigation();

        viewPager2 = findViewById(R.id.view_pager);
        position_select = getIntent().getIntExtra("position", 0);
        count_rate = getIntent().getIntExtra("count_back", 0);
        transaction = getSupportFragmentManager().beginTransaction();
        if (position_select == 0) {
            binding.toolbar.tvToolbar.setText(getString(R.string.stand_compass));
            FragmentStand fragment1 = new FragmentStand();
            transaction.replace(R.id.content_fragment, fragment1);
        } else if (position_select == 1) {
            binding.toolbar.tvToolbar.setText(getString(R.string.digital_compass));
            FragmentDigital fragment2 = new FragmentDigital();
            transaction.replace(R.id.content_fragment, fragment2);
        } else if (position_select == 2) {
            binding.toolbar.tvToolbar.setText(getString(R.string.camera_compass));
            FragmentCamera fragment2 = new FragmentCamera();
            transaction.replace(R.id.content_fragment, fragment2);
        } else if (position_select == 3) {
            binding.toolbar.tvToolbar.setText(getString(R.string.qibla_compass));
            FragmentQibla fragment2 = new FragmentQibla();
            transaction.replace(R.id.content_fragment, fragment2);
        } else if (position_select == 4) {
            binding.toolbar.tvToolbar.setText(getString(R.string.satellite_compass));
            FragmentSatellite fragment2 = new FragmentSatellite();
            transaction.replace(R.id.content_fragment, fragment2);
        } else if (position_select == 5) {
            binding.toolbar.tvToolbar.setText(getString(R.string.map_compass));
            FragmentMap fragment2 = new FragmentMap();
            transaction.replace(R.id.content_fragment, fragment2);
        } else if (position_select == 6) {
            binding.toolbar.tvToolbar.setText(getString(R.string.aviation_compass));
            FragmentAviation fragment2 = new FragmentAviation();
            transaction.replace(R.id.content_fragment, fragment2);
        } else {
            binding.toolbar.tvToolbar.setText(getString(R.string.vintage_compass));
            FragmentVintage fragment2 = new FragmentVintage();
            transaction.replace(R.id.content_fragment, fragment2);
        }
        transaction.commit();


        compassAdapterViewPager = new CompassAdapterViewPager(this, viewPager2);
        viewPager2.setAdapter(compassAdapterViewPager);

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.30f);
                page.setScaleX(0.85f + r * 0.30f);
            }
        });

        viewPager2.setPageTransformer(transformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                select = position;
                transaction = getSupportFragmentManager().beginTransaction();
                if (position == 0) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.stand_compass));
                    FragmentStand fragment1 = new FragmentStand();
                    transaction.replace(R.id.content_fragment, fragment1);
                } else if (position == 1) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.digital_compass));
                    FragmentDigital fragment2 = new FragmentDigital();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else if (position == 2) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.camera_compass));
                    FragmentCamera fragment2 = new FragmentCamera();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else if (position == 3) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.qibla_compass));
                    FragmentQibla fragment2 = new FragmentQibla();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else if (position == 4) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.satellite_compass));
                    FragmentSatellite fragment2 = new FragmentSatellite();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else if (position == 5) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.map_compass));
                    FragmentMap fragment2 = new FragmentMap();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else if (position == 6) {
                    binding.toolbar.tvToolbar.setText(getString(R.string.aviation_compass));
                    FragmentAviation fragment2 = new FragmentAviation();
                    transaction.replace(R.id.content_fragment, fragment2);
                } else {
                    binding.toolbar.tvToolbar.setText(getString(R.string.vintage_compass));
                    FragmentVintage fragment2 = new FragmentVintage();
                    transaction.replace(R.id.content_fragment, fragment2);
                }
                transaction.commit();
                compassAdapterViewPager.notifyDataSetChanged();
            }
        });

        viewPager2.setCurrentItem(Math.min(position_select, 7));
        binding.toolbar.icStart.setOnClickListener(view -> {
            Log.d("ccc", count_rate + "");
            if ((count_rate == 5 || count_rate == 8) && !SharePrefUtils.isRated(this)) {
                setResult(1);
            }
            onBackPressed();
            finish();
        });
    }

    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_list_compass_detail, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_list_compass_detail, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }

}