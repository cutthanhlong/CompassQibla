package com.test.compass.Service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.test.compass.R;
import com.test.compass.adapter.prayer.PrayerModel;
import com.test.compass.util.SharePreferencesController;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    PrayerModel prayerModel;
    Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getBundleExtra("BundleAlarmObject");
        if (bundle != null) {
            prayerModel = (PrayerModel) bundle.getSerializable("AlarmObject");
            if (prayerModel != null) {
                RemoteViews remoteViewSmall = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
                RemoteViews remoteViewsBig = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

                remoteViewsBig.setTextViewText(R.id.tv_name_prayer, prayerModel.getTitle() + " " + getString(R.string.fajar_prayer));
                remoteViewSmall.setTextViewText(R.id.tv_name_prayer, prayerModel.getTitle() + " " + getString(R.string.fajar_prayer));

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
                                .setSound(null);


                int ringtone = SharePreferencesController.getInstance(context).getInt("sound", -1);
                if (ringtone == 0) {
                    long[] pattern = {0, 100, 1000};
                    vibrator.vibrate(pattern, 0);

                    new CountDownTimer(5000, 1000) {
                        @SuppressLint("SetTextI18n")
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            if (vibrator != null) {
                                vibrator.cancel();
                            }
                        }
                    }.start();
                } else if (ringtone == 1) {
                    playAudio(R.raw.azaan1);
                } else if (ringtone == 2) {
                    playAudio(R.raw.azaan2);
                } else if (ringtone == 3) {
                    playAudio(R.raw.azaan3);
                } else if (ringtone == 5) {
                    playAudio(R.raw.beep);
                }else {
                    playAudio(R.raw.none);
                }


                Notification notification = builder.build();
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(1, notification);
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myApp:notificationLock");
                wl.acquire();
                startForeground(1, notification);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
    }

    private void playAudio(int audioResourceId) {
        try {
            stopMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, audioResourceId);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
