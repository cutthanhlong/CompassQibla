package com.test.compass.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefUtils {
    public static String email = "duongnmad@gmail.com";
    public static String subject = "Qibla Finder: Qibla Compass";
    private static SharedPreferences mSharePref;

    public static void init(Context context) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void increaseCountFirstHelp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("first", pre.getInt("first", 0) + 1);
        editor.apply();
    }

    public static int getCountOpenFirstHelp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        return pre.getInt("first", 0);
    }

    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }

    public static int getCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("counts", 1);
    }

    public static void increaseCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts", pre.getInt("counts", 0) + 1);
        editor.commit();
    }

    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.commit();
    }
}
