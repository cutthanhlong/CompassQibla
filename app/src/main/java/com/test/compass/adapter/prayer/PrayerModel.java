package com.test.compass.adapter.prayer;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.test.compass.Service.AlarmBroadcastReceiver;

import java.io.Serializable;
import java.util.Calendar;

public class PrayerModel implements Serializable {
    private int id;
    private String name;
    private String timer;
    private boolean active = false;

    private boolean isAlarm = false;
    private String time_alarm;

    private int hour;
    private int minute;
    private boolean started;
    private boolean vibrate;
    private String title;
    private boolean silent_mode;

    public PrayerModel(int id, String name, String timer, String time_alarm) {
        this.id = id;
        this.name = name;
        this.timer = timer;
        this.time_alarm = time_alarm;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive() {
        return this.active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PrayerModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    public String getTime_alarm() {
        return time_alarm;
    }

    public void setTime_alarm(String time_alarm) {
        this.time_alarm = time_alarm;
    }

    private int alarmid;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAlarmid() {
        return alarmid;
    }

    public void setAlarmid(int alarmid) {
        this.alarmid = alarmid;
    }

    public boolean isSilent_mode() {
        return silent_mode;
    }

    public void setSilent_mode(boolean silent_mode) {
        this.silent_mode = silent_mode;
    }

    @Override
    public String toString() {
        return "PrayerModel{" +
                "name='" + name + '\'' +
                ", timer='" + timer + '\'' +
                ", active=" + active +
                ", isAlarm=" + isAlarm +
                '}';
    }

    @SuppressLint("ScheduleExactAlarm")
    public void schedule(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("AlarmObject", this);
            intent.putExtra("BundleAlarmObject", bundle);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, getAlarmid(), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, getHour());
            calendar.set(Calendar.MINUTE, getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            }
            this.started = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelAlarm(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, getAlarmid(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            alarmManager.cancel(alarmPendingIntent);
            this.started = false;
            @SuppressLint("DefaultLocale") String toastText = String.format("Alarm cancelled for %02d:%02d", hour, minute);
            Log.i("cancel", toastText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
