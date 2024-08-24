package com.test.compass;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }

    }

}

