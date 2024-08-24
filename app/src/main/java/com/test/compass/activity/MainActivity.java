package com.test.compass.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.test.compass.Fragment.FragmentHome;
import com.test.compass.Fragment.FragmentPrayer;
import com.test.compass.Fragment.FragmentSetting;
import com.test.compass.Fragment.FragmentWeather;
import com.test.compass.R;
import com.test.compass.databinding.ActivityCompassBinding;
import com.test.compass.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityCompassBinding binding;
    public static int position;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    Animation animation;
    int positionSelect = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityCompassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideNavigation();

        animation = AnimationUtils.loadAnimation(this, R.anim.animation_click);
        addTabs(binding.vpApp);


        binding.vpApp.setCurrentItem(position);
        binding.vpApp.setOffscreenPageLimit(1);

        binding.clTheme.setOnClickListener(view -> {
            if (positionSelect != 0) {
                setPosition(view, 0);
            }
        });

        binding.clPrayer.setOnClickListener(view -> {
            if (positionSelect != 1) {
                setPosition(view, 1);
                mainVPListenerPrayer.onCall_API();
            }
        });

        binding.clFont.setOnClickListener(view -> {
            if (positionSelect != 2) {
                setPosition(view, 2);
            }
        });

        binding.clSetting.setOnClickListener(view -> {
            if (positionSelect != 3) {
                setPosition(view, 3);
            }
        });
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new FragmentHome(), "fragment_home");
        viewPagerAdapter.addFrag(new FragmentPrayer(), "fragment_prayer");
        viewPagerAdapter.addFrag(new FragmentWeather(), "fragment_weather");
        viewPagerAdapter.addFrag(new FragmentSetting(), "fragment_setting");
        viewPager.setAdapter(viewPagerAdapter);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return this.mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String str) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(str);
        }

        @Override
        public CharSequence getPageTitle(int i) {
            return this.mFragmentTitleList.get(i);
        }
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables"})
    public void setPosition(View view, int i) {
        view.startAnimation(animation);
        positionSelect = i;

        binding.vpApp.setCurrentItem(i);
        if (i == 0) {
            getDefaultSelect();
            binding.tvTheme.setTextColor(Color.parseColor("#FFFFFF"));
            binding.imgTheme.setImageResource(R.drawable.ic_home_select);

        } else if (i == 1) {
            getDefaultSelect();
            binding.tvPrayer.setTextColor(Color.parseColor("#FFFFFF"));
            binding.imgPrayer.setImageResource(R.drawable.ic_prayer_select);


        } else if (i == 2) {
            getDefaultSelect();
            binding.tvFont.setTextColor(Color.parseColor("#FFFFFF"));
            binding.imgFont.setImageResource(R.drawable.ic_weather_select);

        } else {
            getDefaultSelect();
            binding.tvSetting.setTextColor(Color.parseColor("#FFFFFF"));
            binding.imgSetting.setImageResource(R.drawable.ic_setting_selects);

        }
    }

    private void getDefaultSelect() {
        binding.imgTheme.setImageResource(R.drawable.ic_home);
        binding.imgPrayer.setImageResource(R.drawable.ic_prayer);
        binding.imgFont.setImageResource(R.drawable.ic_weather);
        binding.imgSetting.setImageResource(R.drawable.ic_setting);
        binding.tvTheme.setTextColor(Color.parseColor("#CED2D6"));
        binding.tvPrayer.setTextColor(Color.parseColor("#CED2D6"));
        binding.tvFont.setTextColor(Color.parseColor("#CED2D6"));
        binding.tvSetting.setTextColor(Color.parseColor("#CED2D6"));
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

    private void dialogExit() {
        CardView stay, quit;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        stay = dialog.findViewById(R.id.stay);
        quit = dialog.findViewById(R.id.quit);
        quit.setOnClickListener(view -> {
            dialog.dismiss();
            finishAffinity();
//            System.exit(0);
        });
        stay.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        dialogExit();
    }

    MainVPListenerPrayer mainVPListenerPrayer;

    public void addMainVPListener(MainVPListenerPrayer mainVPListenerPrayer) {
        this.mainVPListenerPrayer = mainVPListenerPrayer;
    }

    public interface MainVPListenerPrayer {
        void onCall_API();
    }
}