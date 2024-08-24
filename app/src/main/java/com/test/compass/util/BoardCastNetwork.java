package com.test.compass.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;

import com.test.compass.Fragment.FragmentWeather;

public class BoardCastNetwork extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (isNetWorkAvailable(context)) {
                FragmentWeather.view.setVisibility(View.VISIBLE);
                FragmentWeather.imageView.setVisibility(View.GONE);
            } else {
                FragmentWeather.view.setVisibility(View.GONE);
                FragmentWeather.imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isNetWorkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));

        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
