package com.test.compass.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.test.compass.R;
import com.test.compass.databinding.ActivitySettingPrayerBinding;
import com.test.compass.util.SharePreferencesController;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;

public class SettingPrayer extends AppCompatActivity {

    ActivitySettingPrayerBinding binding;

    boolean isClick_date = false;
    boolean isClick_method = false;
    boolean isClick_calculation_time = false;
    boolean isClick_format_time = false;

    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingPrayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.tvToolbar.setText(getString(R.string.prayer_settings));

        hideNavigation();
        binding.toolbar.icStart.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
        binding.cnViewDate.setOnClickListener(view -> {
            if (!isClick_date) {
                binding.rlDay.setVisibility(View.VISIBLE);
                isClick_date = true;
            } else {
                binding.rlDay.setVisibility(View.GONE);
                isClick_date = false;
            }
        });
        binding.cnViewSelectMethod.setOnClickListener(view -> {
            if (!isClick_method) {
                binding.rlMethod.setVisibility(View.VISIBLE);
                isClick_method = true;
            } else {
                binding.rlMethod.setVisibility(View.GONE);
                isClick_method = false;
            }
        });
        binding.lnViewCalculationTime.setOnClickListener(view -> {
            if (!isClick_calculation_time) {
                binding.lnCalculationTimeShow.setVisibility(View.VISIBLE);
                isClick_calculation_time = true;
            } else {
                binding.lnCalculationTimeShow.setVisibility(View.GONE);
                isClick_calculation_time = false;
            }
        });
        binding.lnFormatTime.setOnClickListener(view -> {
            if (!isClick_format_time) {
                binding.lnFormatTimeShow.setVisibility(View.VISIBLE);
                isClick_format_time = true;
            } else {
                binding.lnFormatTimeShow.setVisibility(View.GONE);
                isClick_format_time = false;
            }
        });
        onClickDate();
        onClickMethod();
        onClickAsrTime();
        onClickFormatTime();
    }


    private void onClickDate() {
/*        "adjustment" (number) -
                Number of days to adjust hijri date(s). Example: 1 or 2 or -1 or -2*/
        binding.rlDateTwoAgo.setOnClickListener(view -> {
            binding.tvDateGet.setText(binding.tvTwoDayAgo.getText().toString());
            getDefaultDate();
            binding.imgTwoDayAgo.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("date", -2);
            //number =-2
        });
        binding.rlDateOneAgo.setOnClickListener(view -> {
            binding.tvDateGet.setText(binding.tvOneDayAgo.getText().toString());
            getDefaultDate();
            binding.imgOneDayAgo.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("date", -1);
            //number =-1
        });
        binding.rlDateNone.setOnClickListener(view -> {
            binding.tvDateGet.setText(binding.tvNoneDay.getText().toString());
            getDefaultDate();
            binding.imgNoneDay.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("date", 0);
            //number = 0
        });
        binding.rlDateOneAhead.setOnClickListener(view -> {
            binding.tvDateGet.setText(binding.tvOneDayAhead.getText().toString());
            getDefaultDate();
            binding.imgOneDayAhead.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("date", 1);
            //number = 1
        });
        binding.rlDateTwoAhead.setOnClickListener(view -> {
            binding.tvDateGet.setText(binding.tvTwoDayAhead.getText().toString());
            getDefaultDate();
            binding.imgTwoDayAhead.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("date", 2);
            //number = 2
        });
    }

    private void onClickMethod() {
        binding.rlMedthod1.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow1.getText().toString());
            getDefaultMethod();
            binding.imgMethod1.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 3);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow1.getText().toString());
        });
        binding.rlMedthod2.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow2.getText().toString());
            getDefaultMethod();
            binding.imgMethod2.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 5);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow2.getText().toString());
        });
        binding.rlMethod3.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow3.getText().toString());
            getDefaultMethod();
            binding.imgMethod3.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 4);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow3.getText().toString());
        });
        binding.rlMethod4.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow4.getText().toString());
            getDefaultMethod();
            binding.imgMethod4.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 1);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow4.getText().toString());
        });
        binding.rlMethod5.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow5.getText().toString());
            getDefaultMethod();
            binding.imgMethod5.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 2);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow5.getText().toString());
        });
        binding.rlMethod6.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow6.getText().toString());
            getDefaultMethod();
            binding.imgMethod6.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 12);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow6.getText().toString());
        });
        binding.rlMethod7.setOnClickListener(view -> {
            binding.tvMedthod.setText(binding.tvMethodShow7.getText().toString());
            getDefaultMethod();
            binding.imgMethod7.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("method", 9);
            SharePreferencesController.getInstance(SettingPrayer.this).putString("name_method", binding.tvMethodShow7.getText().toString());
        });
    }

    private void onClickAsrTime() {
        binding.rlTime1.setOnClickListener(view -> {
            binding.tvCalculationTime.setText(binding.tvTimeShow1.getText().toString());
            getDefaultAsrTime();
            binding.imgAsrTime1.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("school", 0);
        });
        binding.rlTime2.setOnClickListener(view -> {
            binding.tvCalculationTime.setText(binding.tvTimeShow2.getText().toString());
            getDefaultAsrTime();
            binding.imgAsrTime2.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putInt("school", 1);
        });
    }

    private void onClickFormatTime() {
        binding.rlFormatTime1.setOnClickListener(view -> {
            binding.tvFormatTime.setText(binding.tvFormatTime1.getText().toString());
            getDefaultFormatTime();
            binding.img24Hour.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putBoolean("format_time",true);
        });
        binding.rlFormatTime2.setOnClickListener(view -> {
            binding.tvFormatTime.setText(binding.tvFormatTime2.getText().toString());
            getDefaultFormatTime();
            binding.img12Hour.setImageResource(R.drawable.ic_checked_d);
            SharePreferencesController.getInstance(SettingPrayer.this).putBoolean("format_time",false);
        });
    }

    private void getDefaultFormatTime() {
        binding.img12Hour.setImageResource(R.drawable.ic_rb_d);
        binding.img24Hour.setImageResource(R.drawable.ic_rb_d);
    }

    private void getDefaultAsrTime() {
        binding.imgAsrTime1.setImageResource(R.drawable.ic_rb_d);
        binding.imgAsrTime2.setImageResource(R.drawable.ic_rb_d);
    }

    private void getDefaultMethod() {
        binding.imgMethod1.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod2.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod3.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod4.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod5.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod6.setImageResource(R.drawable.ic_rb_d);
        binding.imgMethod7.setImageResource(R.drawable.ic_rb_d);
    }

    private void getDefaultDate() {
        binding.imgTwoDayAgo.setImageResource(R.drawable.ic_rb_d);
        binding.imgOneDayAgo.setImageResource(R.drawable.ic_rb_d);
        binding.imgNoneDay.setImageResource(R.drawable.ic_rb_d);
        binding.imgOneDayAhead.setImageResource(R.drawable.ic_rb_d);
        binding.imgTwoDayAhead.setImageResource(R.drawable.ic_rb_d);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.loading.playAnimation();
        isClick_date = false;
        isClick_method = false;
        isClick_calculation_time = false;
        isClick_format_time = false;
        int date = SharePreferencesController.getInstance(SettingPrayer.this).getInt("date", 0);
        if (date == -2) {
            binding.imgTwoDayAgo.setImageResource(R.drawable.ic_checked_d);
            binding.tvDateGet.setText(binding.tvTwoDayAgo.getText().toString());
        } else if (date == -1) {
            binding.imgOneDayAgo.setImageResource(R.drawable.ic_checked_d);
            binding.tvDateGet.setText(binding.tvOneDayAgo.getText().toString());
        } else if (date == 0) {
            binding.imgNoneDay.setImageResource(R.drawable.ic_checked_d);
            binding.tvDateGet.setText(binding.tvNoneDay.getText().toString());
        } else if (date == 1) {
            binding.imgOneDayAhead.setImageResource(R.drawable.ic_checked_d);
            binding.tvDateGet.setText(binding.tvOneDayAhead.getText().toString());
        } else {
            binding.imgTwoDayAhead.setImageResource(R.drawable.ic_checked_d);
            binding.tvDateGet.setText(binding.tvTwoDayAhead.getText().toString());
        }

        int method = SharePreferencesController.getInstance(SettingPrayer.this).getInt("method", 3);
        if (method == 3) {
            binding.tvMedthod.setText(getString(R.string.method1));
            binding.imgMethod1.setImageResource(R.drawable.ic_checked_d);
        } else if (method == 5) {
            binding.tvMedthod.setText(getString(R.string.method2));
            binding.imgMethod2.setImageResource(R.drawable.ic_checked_d);
        } else if (method == 4) {
            binding.tvMedthod.setText(getString(R.string.method3));
            binding.imgMethod3.setImageResource(R.drawable.ic_checked_d);
        } else if (method == 1) {
            binding.tvMedthod.setText(getString(R.string.method4));
            binding.imgMethod4.setImageResource(R.drawable.ic_checked_d);
        } else if (method == 2) {
            binding.tvMedthod.setText(getString(R.string.method5));
            binding.imgMethod5.setImageResource(R.drawable.ic_checked_d);
        } else if (method == 12) {
            binding.tvMedthod.setText(getString(R.string.method6));
            binding.imgMethod6.setImageResource(R.drawable.ic_checked_d);
        } else {
            binding.tvMedthod.setText(getString(R.string.method7));
            binding.imgMethod7.setImageResource(R.drawable.ic_checked_d);
        }

        int school = SharePreferencesController.getInstance(SettingPrayer.this).getInt("school", 0);
        if (school == 0) {
            binding.tvCalculationTime.setText(binding.tvTimeShow1.getText().toString());
            binding.imgAsrTime1.setImageResource(R.drawable.ic_checked_d);
        } else {
            binding.tvCalculationTime.setText(binding.tvTimeShow2.getText().toString());
            binding.imgAsrTime2.setImageResource(R.drawable.ic_checked_d);
        }

        boolean formatime = SharePreferencesController.getInstance(SettingPrayer.this).getBoolean("format_time",true);
        if (formatime){
            binding.tvFormatTime.setText(binding.tvFormatTime1.getText().toString());
            binding.img24Hour.setImageResource(R.drawable.ic_checked_d);
        }else {
            binding.tvFormatTime.setText(binding.tvFormatTime2.getText().toString());
            binding.img12Hour.setImageResource(R.drawable.ic_checked_d);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @SuppressLint("InflateParams")
    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_setting_prayer, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_setting_prayer, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }
}