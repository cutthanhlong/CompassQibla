package com.test.compass.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.test.compass.Fragment.FragmentPrayer;
import com.test.compass.R;
import com.test.compass.util.SystemUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PrayerService extends Service {

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "CountdownServiceChannel";
    private static final String TAG = "CountdownService";
    private int currentIndex = 0;
    private Context context;
    private ArrayList<String> prayerTimes;
    private ArrayList<String> prayerNames;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SystemUtil.setLocale(this);
        try {
            if (intent != null) {
                prayerTimes = new ArrayList<>();
                prayerTimes.add(intent.getStringExtra("Fajr"));
                prayerTimes.add(intent.getStringExtra("Sunrise"));
                prayerTimes.add(intent.getStringExtra("Dhuhr"));
                prayerTimes.add(intent.getStringExtra("Asr"));
                prayerTimes.add(intent.getStringExtra("Maghrib"));
                prayerTimes.add(intent.getStringExtra("Isha"));
                prayerNames = new ArrayList<>();
                prayerNames.add("Fajr");
                prayerNames.add("Sunrise");
                prayerNames.add("Dhuhr");
                prayerNames.add("Asr");
                prayerNames.add("Maghrib");
                prayerNames.add("Isha");

                // Bắt đầu đồng hồ đếm ngược cho thời gian đầu tiên
                currentIndex = 0;
                startCountdown(prayerTimes.get(currentIndex), prayerNames.get(currentIndex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startForegroundService();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startCountdown(String time, String name) {
        new CountDownTimer(calculateTimeToPrayer(time), 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                try {
                    int hours = (int) ((millisUntilFinished / 1000) / 3600);
                    int minutes = (int) (((millisUntilFinished / 1000) / 60) % 60);
                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    FragmentPrayer.time_service.setText(timeLeftFormatted);
                    FragmentPrayer.tv_next_step.setText(name + " " + getString(R.string.pray_in));
                    if (currentIndex < prayerTimes.size()) {
                        FragmentPrayer.prayerAdapter.setCheck(prayerNames.get(currentIndex - 1));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFinish() {
                try {
                    showNotification(prayerNames.get(currentIndex));
                    currentIndex++;
                    if (currentIndex < prayerTimes.size()) {
                        startCountdown(prayerTimes.get(currentIndex), prayerNames.get(currentIndex));
                        FragmentPrayer.prayerAdapter.setCheck(prayerNames.get(currentIndex));
                    } else {
                        FragmentPrayer.time_service.setText("00:00:00");
                        FragmentPrayer.prayerAdapter.setCheck("Isha");
                        stopSelf();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long calculateTimeToPrayer(String prayerTime) {
        LocalTime time_now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTimeNow = time_now.format(formatter);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date startTime = null, now = null;
        try {
            startTime = format.parse(prayerTime);
            now = format.parse(formattedTimeNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTime = now.getTime();
        long prayerTimeMillis = startTime.getTime();
        Log.d("bbb", currentTime + "giữa" + prayerTimeMillis);
        return prayerTimeMillis - currentTime;
    }

    private void showNotification(String prayerTime) {
        try {
            RemoteViews remoteViewSmall = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
            RemoteViews remoteViewsBig = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

            remoteViewsBig.setTextViewText(R.id.tv_name_prayer, prayerTime + " " + getString(R.string.fajar_prayer));
            remoteViewSmall.setTextViewText(R.id.tv_name_prayer, prayerTime + " " + getString(R.string.fajar_prayer));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "time_channel_prayer",
                        "Prayer Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "time_channel_prayer")
                            .setSmallIcon(R.drawable.logo_app)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(remoteViewSmall)
                            .setCustomBigContentView(remoteViewsBig)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }

            Notification notification = builder.build();
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(3, notification);
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myApp:notificationLock");
            wl.acquire();
            startForeground(3, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startForegroundService() {
        createNotificationChannel();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setSmallIcon(R.drawable.logo_app)
                .build();

        startForeground(NOTIFICATION_ID, notification);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Countdown Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
