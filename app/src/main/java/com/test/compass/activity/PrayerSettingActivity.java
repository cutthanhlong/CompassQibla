package com.test.compass.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.test.compass.R;
import com.test.compass.Service.SilentModeReceiver;
import com.test.compass.adapter.prayer.PrayerModel;
import com.test.compass.databinding.ActivitySettingAlarmPrayerBinding;
import com.test.compass.util.SharePreferencesController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class PrayerSettingActivity extends AppCompatActivity {

    ActivitySettingAlarmPrayerBinding binding;

    PrayerModel prayerModel;
    boolean isAlarm = false;
    MaterialTimePicker picker;
    private int hour, minute;
    private int REQUEST_CODE_DND_PERMISSION = 124;

    boolean silent = false;
    boolean vibrate = false;
    boolean beep = false;
    boolean azaan1 = false;
    boolean azaan2 = false;
    boolean azaan3 = false;

    private MediaPlayer mediaPlayer;
    Vibrator vibrator;

    private static final int REQUEST_CODE = 123;
    private PendingIntent pendingIntent;
    private boolean isSilent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingAlarmPrayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerReceiver(new SilentModeReceiver(), new IntentFilter("SILENT_MODE_ACTION"));
        hideNavigation();
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) getSystemService(PrayerSettingActivity.this.VIBRATOR_SERVICE);
        prayerModel = (PrayerModel) getIntent().getSerializableExtra("prayer");
        if (prayerModel != null) {
            String name = prayerModel.getName();
            binding.toolbar.tvToolbar.setText(name + " " + getString(R.string.prayer));
            isAlarm = readAlarmState(prayerModel.getId());
            isSilent = prayerModel.isSilent_mode();
            binding.tvTime.setText(readTimeAlarm(prayerModel.getId()));
        }

        boolean isSavedAlarmState = readAlarmState(prayerModel.getId());
        if (isSavedAlarmState) {
            binding.swAlarm.setChecked(true);
            binding.lnAlarm.setVisibility(View.VISIBLE);
        } else {
            binding.swAlarm.setChecked(false);
            binding.lnAlarm.setVisibility(View.GONE);
        }
        if (isSilent) {
            binding.swSilentMode.setChecked(true);
            binding.rlSlientMode.setVisibility(View.VISIBLE);
        } else {
            binding.swSilentMode.setChecked(false);
            binding.rlSlientMode.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(binding.tvTime.getText().toString().trim());
            hour = date.getHours();
            minute = date.getMinutes();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean is_silent = readAlarmStateSilent(prayerModel.getId());

        if (is_silent) {
            binding.swSilentMode.setChecked(true);
        } else {
            binding.swSilentMode.setChecked(false);
        }

        onClickView();

    }


    @SuppressLint("DefaultLocale")
    private void onClickView() {
        binding.toolbar.icStart.setOnClickListener(view -> {

            if (isAlarm) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setAlarm();
                    Log.d("aaa", "true");
                }
            } else {
                saveAlarmState(prayerModel.getId(), isAlarm);
                Log.d("aaa", "false");
                prayerModel.cancelAlarm(PrayerSettingActivity.this);
            }
            prayerModel.setAlarm(isAlarm);
            prayerModel.setTime_alarm(binding.tvTime.getText().toString().trim());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPrayer", prayerModel);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        binding.swAlarm.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.lnAlarm.setVisibility(View.VISIBLE);
                isAlarm = true;
            } else {
                binding.lnAlarm.setVisibility(View.GONE);
                isAlarm = false;
            }
        });

        binding.swSilentMode.setOnClickListener(view -> {

            if (!checkDNDPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_DND_PERMISSION);
                } else {
                    handleDNDPermissionResult(true);
                }
            }
        });
        if (checkDNDPermission()) {
            binding.swSilentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        binding.rlSlientMode.setVisibility(View.VISIBLE);
                        scheduleSilentMode(PrayerSettingActivity.this);
                        prayerModel.setSilent_mode(true);
                        saveAlarmStateSilentMode(prayerModel.getId(), true);
                    } else {
                        binding.rlSlientMode.setVisibility(View.GONE);
                        cancelSilentMode(PrayerSettingActivity.this);
                        prayerModel.setSilent_mode(false);
                        saveAlarmStateSilentMode(prayerModel.getId(), false);
                    }
                }
            });
        }
        binding.tvTime.setOnClickListener(view -> {
            boolean isSystem24Hour = DateFormat.is24HourFormat(this);
            int clockFormat = isSystem24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
            picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(hour)
                    .setMinute(minute)
                    .setTitleText(getString(R.string.select_time))
                    .build();
            picker.show(getSupportFragmentManager(), "tag");

            picker.addOnPositiveButtonClickListener(view1 -> {
                binding.tvTime.setText(String.format("%02d:%02d", picker.getHour(), picker.getMinute()));
            });
        });

        binding.slient.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgSlient.setImageResource(R.drawable.ic_checked_d);
            silent = true;
            stopMediaPlayer();
            if (vibrator != null) {
                vibrator.cancel();
            }
        });

        binding.vibrate.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgVibrate.setImageResource(R.drawable.ic_checked_d);
            vibrate = true;
            long[] pattern = {0, 100, 1000};
            vibrator.vibrate(pattern, 0);
            stopMediaPlayer();
            SharePreferencesController.getInstance(PrayerSettingActivity.this).putInt("sound", 0);
        });
        binding.beep.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgBeep.setImageResource(R.drawable.ic_checked_d);
            beep = true;
            playAudio(R.raw.beep);
            if (vibrator != null) {
                vibrator.cancel();
            }
            SharePreferencesController.getInstance(PrayerSettingActivity.this).putInt("sound", 5);
        });
        binding.azaan1.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgAzaan1.setImageResource(R.drawable.ic_checked_d);
            azaan1 = true;
            playAudio(R.raw.azaan1);
            if (vibrator != null) {
                vibrator.cancel();
            }
            SharePreferencesController.getInstance(PrayerSettingActivity.this).putInt("sound", 1);
        });
        binding.azaan2.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgAzaan2.setImageResource(R.drawable.ic_checked_d);
            azaan2 = true;
            playAudio(R.raw.azaan2);
            if (vibrator != null) {
                vibrator.cancel();
            }
            SharePreferencesController.getInstance(PrayerSettingActivity.this).putInt("sound", 2);
        });
        binding.azaan3.setOnClickListener(view -> {
            getDefaultRingTone();
            binding.imgAzaan3.setImageResource(R.drawable.ic_checked_d);
            azaan3 = true;
            playAudio(R.raw.azaan3);
            if (vibrator != null) {
                vibrator.cancel();
            }
            SharePreferencesController.getInstance(PrayerSettingActivity.this).putInt("sound", 3);
        });
    }


    private void getDefaultRingTone() {
        silent = false;
        vibrate = false;
        beep = false;
        azaan1 = false;
        azaan2 = false;
        azaan3 = false;
        binding.imgSlient.setImageResource(R.drawable.ic_rb_d);
        binding.imgVibrate.setImageResource(R.drawable.ic_rb_d);
        binding.imgBeep.setImageResource(R.drawable.ic_rb_d);
        binding.imgAzaan1.setImageResource(R.drawable.ic_rb_d);
        binding.imgAzaan2.setImageResource(R.drawable.ic_rb_d);
        binding.imgAzaan3.setImageResource(R.drawable.ic_rb_d);
    }

    public boolean checkDNDPermission() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return notificationManager.isNotificationPolicyAccessGranted();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DND_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    boolean granted = notificationManager.isNotificationPolicyAccessGranted();
                    handleDNDPermissionResult(granted);
                }
            } else {
                handleDNDPermissionResult(true);
            }
        }
    }

    private void handleDNDPermissionResult(boolean granted) {
        if (granted) {
            binding.swSilentMode.setChecked(true);
            binding.rlSlientMode.setVisibility(View.VISIBLE);
        } else {
            binding.swSilentMode.setChecked(false);
            binding.rlSlientMode.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarm() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime localTime = LocalTime.parse(binding.tvTime.getText().toString().trim(), formatter);
            int hour = localTime.getHour();
            int minute = localTime.getMinute();
            int alarmId = new Random().nextInt(Integer.MAX_VALUE);
            prayerModel.setAlarmid(alarmId);
            prayerModel.setHour(Integer.parseInt(String.format("%02d", hour)));
            prayerModel.setMinute(Integer.parseInt(String.format("%02d", minute)));
            prayerModel.setTitle(prayerModel.getName());
            prayerModel.setStarted(true);
            prayerModel.setVibrate(vibrate);
            saveAlarmState(prayerModel.getId(), isAlarm);
            saveTimeAlarm(prayerModel.getId(), binding.tvTime.getText().toString().trim());
            prayerModel.schedule(PrayerSettingActivity.this);
        } catch (Exception e) {

        }

    }


    private void hideNavigation() {
        WindowInsetsControllerCompat windowInsetsController;
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        } else
            windowInsetsController = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_setting_alarm_prayer, null));

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
                        windowInsetsController1 = new WindowInsetsControllerCompat(getWindow(), LayoutInflater.from(this).inflate(R.layout.activity_setting_alarm_prayer, null));
                    }
                    Objects.requireNonNull(windowInsetsController1).hide(WindowInsetsCompat.Type.navigationBars());
                }, 3000);
            }
        });
    }

    private void playAudio(int audioResourceId) {
        try {
            stopMediaPlayer();
            mediaPlayer = MediaPlayer.create(PrayerSettingActivity.this, audioResourceId);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMediaPlayer();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        int sound = SharePreferencesController.getInstance(PrayerSettingActivity.this).getInt("sound", 0);
        if (sound == 0) {
            binding.imgVibrate.setImageResource(R.drawable.ic_checked_d);
        } else if (sound == 1) {
            binding.imgAzaan1.setImageResource(R.drawable.ic_checked_d);
        } else if (sound == 2) {
            binding.imgAzaan2.setImageResource(R.drawable.ic_checked_d);
        } else if (sound == 3) {
            binding.imgAzaan3.setImageResource(R.drawable.ic_checked_d);
        } else if (sound == 5) {
            binding.imgBeep.setImageResource(R.drawable.ic_checked_d);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime initialTime = LocalTime.parse(binding.tvTime.getText().toString().trim(), formatter);
        LocalTime modifiedTime = initialTime.plusMinutes(20);
        String resultText = modifiedTime.format(formatter);

        binding.tvTimeStart.setText(binding.tvTime.getText().toString().trim());
        binding.tvTimeEnd.setText(resultText);
    }

    private void scheduleSilentMode(Context context) {

        Intent intent = new Intent("SILENT_MODE_ACTION");
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long triggerTime = SystemClock.elapsedRealtime() + 20 * 60 * 1000;
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
            Toast.makeText(context, getString(R.string.silent_mode_true), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelSilentMode(Context context) {
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
            Toast.makeText(context, getString(R.string.silent_mode_false), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAlarmState(int prayerId, boolean isAlarm) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("alarm_" + prayerId, isAlarm);
        editor.apply();
    }

    private boolean readAlarmState(int prayerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("alarm_" + prayerId, false);
    }

    private void saveTimeAlarm(int prayerId, String timeAlarm) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time_alarm_" + prayerId, timeAlarm);
        editor.apply();
    }

    private String readTimeAlarm(int prayerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("time_alarm_" + prayerId, null);
    }

    private void saveAlarmStateSilentMode(int prayerId, boolean isAlarm) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("alarm_silent_" + prayerId, isAlarm);
        editor.apply();
    }

    private boolean readAlarmStateSilent(int prayerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("alarm_silent_" + prayerId, false);
    }
}