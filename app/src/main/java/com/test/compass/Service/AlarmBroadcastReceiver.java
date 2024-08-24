package com.test.compass.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.test.compass.adapter.prayer.PrayerModel;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    PrayerModel prayerModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("BundleAlarmObject");
        if (bundle != null)
            prayerModel = (PrayerModel) bundle.getSerializable("AlarmObject");
        if (prayerModel != null) {
            startAlarmService(context, prayerModel);
        }

    }

    private void startAlarmService(Context context, PrayerModel prayerModel) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AlarmObject", prayerModel);
        intentService.putExtra("BundleAlarmObject", bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

}

